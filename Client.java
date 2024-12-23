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

  public Client() throws IOException {
    client = new Comm.TcpClient("localhost", 8080, this);
    Scanner scanner = new Scanner(System.in);

    // 等待伺服器連接成功後開始接受指令
    while (!client.isAlive());
    setupGUI initialGUI = new setupGUI(client);

    scanner.close();
  }
  
  // 發送訊息到伺服器
  private void sendMessage(String message) {
    if (client != null) {
      client.send(message);
    }
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
