import java.io.*;
import java.util.Scanner;


public class Client implements Comm.TcpClientCallback {
  public static void main(String[] args) {  
    try {
      Client client = new Client();
    } catch (IOException e) {
      System.out.println("Failed to create client: " + e.getMessage());
    }
  }

  Comm.TcpClient client;
  public Client()
    throws IOException
  {
    client = new Comm.TcpClient("localhost", 8080, this);
    Scanner scanner = new Scanner(System.in);

    while (client.isAlive()) {
      System.out.print("Enter message: ");
      String message = scanner.nextLine();
      client.send(message);
    }

    scanner.close();
  }

  @Override
  public void onMessage(String message) {
    System.out.println("Server sent: " + message);
  }

  @Override
  public void onConnect() {
    System.out.println("Connected to server");
  }

  @Override
  public void onDisconnect() {
    System.out.println("Disconnected from server");
  }
}
