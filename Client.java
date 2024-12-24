import java.io.*;
import java.util.Scanner;

public class Client implements Comm.TcpClientCallback {
  private Comm.TcpClient client;
  private setupGUI initialGUI;
  private int id;

  public static void main(String[] args) {  
    try {
      Client client = new Client();
    } catch (IOException e) {
      System.out.println("Failed to create client: " + e.getMessage());
    }
  }

  public Client() throws IOException {
    client = new Comm.TcpClient("localhost", 8080, this);
    Scanner scanner = new Scanner(System.in);

    // 等待伺服器連接成功後開始接受指令
    while (!client.isAlive()) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    initialGUI = new setupGUI(client);
    scanner.close();
  }

  @Override
  public void onMessage(String message) {
    String[] parts = message.split(";");
    // 如果message一開始是id的話，代表封包為id;<id; "0" | "1" | "2" | "3">的格式
    if (message.startsWith("id")) {
      String idStr = parts[1];
      id = Integer.parseInt(idStr);
    }
    
    // 按下更新的角色按鈕後，會獲得 updateReadyState;ready;p1;0 的封包
    // 主視窗的更新畫面
    if (message.startsWith("updateReadyState")) {
      if ("ready".equals(parts[1])) initialGUI.playerReady(true, message, id);
      if ("unready".equals(parts[1])) initialGUI.playerReady(false, message, id);
    }
    
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
