import java.io.*;


public class Server implements Comm.TcpServerCallback {
  public static void main(String[] args) {
    try {
      Server server = new Server();
    } catch (IOException e) {
      System.out.println("Failed to create server: " + e.getMessage());
    }
  }

  Comm.TcpServer server;
  public Server()
    throws IOException
  {
    server = new Comm.TcpServer(8080, 2, this);
  }

  @Override
  public void onMessage(int id, String message) {
    System.out.println("Client " + id + " sent: " + message);
    server.send(id, "Echo: " + message);
    server.broadcast("move;player" + id + ";up");
  }

  @Override
  public void onConnect(int id) {
    System.out.println("Client connected: " + id);
  }

  @Override
  public void onDisconnect(int id) {
    System.out.println("Client disconnected: " + id);
  }
}
