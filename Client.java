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
  private String selectedRole = null;  // Tracks the selected character/role
  private boolean isReady = false;     // Tracks if the player is ready

  public Client() throws IOException {
    client = new Comm.TcpClient("localhost", 8080, this);
    Scanner scanner = new Scanner(System.in);

    // 等待伺服器連接成功後開始接受指令
    while (client.isAlive()) {
        setupGUI initialGUI = new setupGUI();
    }

    scanner.close();
  }

  // 設定角色
  private void selectRole(String role) {
    selectedRole = role;
    System.out.println("Selected role: " + role);
    sendMessage("updateReadyState;ready;" + role); // 發送選擇角色的準備訊息
  }

  // 設定準備狀態
  private void setReady(boolean ready) {
    if (ready) {
      isReady = true;
      sendMessage("updateReadyState;ready;" + selectedRole); // 發送準備訊息
    } else {
      isReady = false;
      sendMessage("updateReadyState;unready;" + selectedRole); // 發送取消準備訊息
    }
  }

  // 發送訊息到伺服器
  private void sendMessage(String message) {
    // if (client != null) {
    //   client.send(message);
    // }
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
