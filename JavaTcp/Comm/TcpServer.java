package Comm;

import java.io.*;
import java.net.*;


public class TcpServer {
  private ServerSocket      serverSocket;
  private Client            clients[];
  private TcpServerCallback callback;

  public TcpServer(int port, int maxClients, TcpServerCallback callback)
    throws IOException
  {
    clients       = new Client[maxClients];
    serverSocket  = new ServerSocket(port);
    this.callback = callback;

    new Thread(() -> {
      int count = 0;

      try {
        while (true) {
          if (clients[count] == null) {
            this.connect(count);
          }

          count = (count + 1) % maxClients;
          Thread.sleep(50);
        }
      }

      catch (InterruptedException e) {
        System.out.println("Error accepting client: " + e.getMessage());
      }

      catch (IOException e) {
        System.out.println("Error accepting client: " + e.getMessage());
      }
    }).start();

    new Thread(() -> {
      while (true) {
        for (int i = 0; i < clients.length; i++) {
          if (clients[i] != null && !clients[i].isAlive()) {
            callback.onDisconnect(i);
            this.disconnect(i);
          }
        }

        try {
          Thread.sleep(50);
        }

        catch (InterruptedException e) {
          System.out.println("Error checking client status: " + e.getMessage());
        }
      }
    }).start();
  }

  void connect(int id)
    throws IOException
  {
    clients[id] = new Client(serverSocket.accept(), id, callback);
  }

  void disconnect(int id) {
    clients[id] = null;
  }

  public void send(int id, String message) {
    System.out.println(message);
    if (id >= 0 && id < clients.length && clients[id] != null) {
      clients[id].send(message);
    }
  }

  public void broadcast(String message) {
    for (int i = 0; i < clients.length; i++) {
      if (clients[i] != null) {
        clients[i].send(message);
      }
    }
  }
}


class Client {
  private Socket         s;
  private PrintWriter    o;
  private BufferedReader i;
  private int            id;
  private boolean        alive;

  public Client(Socket socket, int id, TcpServerCallback server)
    throws IOException
  {
    s = socket;
    o = new PrintWriter(socket.getOutputStream(), true);
    i = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    alive   = true;
    this.id = id;

    
    new Thread(() -> {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      server.onConnect(id);
      
      while (true) {
        try {
          String message = i.readLine();
          if (message == null) { break; }
          server.onMessage(id, message);
        }

        catch (IOException e) {
          System.out.println("Error reading message: " + e.getMessage());
          break;
        }
      }

      this.close();
    }).start();
  }

  public int getId() {
    return id;
  }

  public boolean isAlive() {
    return alive;
  }

  public void send(String message) {
    o.println(message);
  }

  public void close() {
    try {
      o.close();
      i.close();
      s.close();
      alive = false;
    }

    catch (IOException e) {
      System.out.println("Error closing client: " + e.getMessage());
    }
  }
}
