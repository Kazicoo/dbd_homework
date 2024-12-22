package Comm;

import java.io.*;
import java.net.*;


public class TcpClient {
  private Socket         s;
  private PrintWriter    o;
  private BufferedReader i;
  private boolean        alive;

  public TcpClient(String host, int port, TcpClientCallback callback)
    throws IOException
  {
    s = new Socket(host, port);
    o = new PrintWriter(s.getOutputStream(), true);
    i = new BufferedReader(new InputStreamReader(s.getInputStream()));

    alive = true;

    callback.onConnect();

    new Thread(() -> {
      while (true) {
        try {
          String message = i.readLine();
          if (message == null) { break; }
          callback.onMessage(message);
        }

        catch (IOException e) {
          System.out.println("Error reading message: " + e.getMessage());
          break;
        }
      }

      callback.onDisconnect();
      this.close();
    }).start();
  }

  public boolean isAlive() {
    return alive;
  }

  public void send(String message) {
    o.println(message);
  }

  void close() {
    try {
      o.close();
      i.close();
      s.close();
      alive = false;

    } catch (IOException e) {
      System.out.println("Error closing client: " + e.getMessage());
    }
  }
}
