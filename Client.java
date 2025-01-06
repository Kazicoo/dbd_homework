import java.io.*;
import java.util.Scanner;


public class Client implements Comm.TcpClientCallback {
  private Comm.TcpClient client;
  private final setupGUI initialGUI;
  private int id;
  private ClientGame ClientGame;
  private int generatorCount = 0;
  private int hookCount = 0;
  public static String[] chars = {"killer", "p1", "p2", "p3"};
  
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
    
    synchronized (this) {
      initialGUI = new setupGUI(client);
      notifyAll();
    }
    scanner.close();
  }

  @Override
  public void onMessage(String message) {
    String[] parts = message.split(";");
    
    if (message.startsWith("id")) {
      String idStr = parts[1];
      id = Integer.parseInt(idStr);
    }
    
    
    synchronized (this) {
      while (initialGUI == null) {
        try {
          wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }

    if (message.startsWith("totalPlayers")) {
      int totalPlayers = Integer.parseInt(parts[1]);
      initialGUI.updateTotalPlayers(totalPlayers);
    }
    
    if (message.startsWith("updateReadyState")) {
      if ("ready".equals(parts[1])) {
        initialGUI.playerReady(true, message, id);
        if ("killer".equals(parts[2])) {
            Integer.parseInt(parts[3]);  
        }
      }
      if ("unready".equals(parts[1])) {
        initialGUI.playerReady(false, message, id);
        if ("killer".equals(parts[2])) {
        }
      }
    }

    if ("startLoading".equals(message)) {
      initialGUI.startCountdown();
      initialGUI.closeFrame();
      ClientGame = new ClientGame(client);
      // 等 ClientGame 初始化完成後再執行後續操作
    }

    // initGameObject;player;<x: int>;<y: int>;<id: "0" | "1" | "2" | "3">
    if (message.startsWith("initGameObject")) {

      if (parts.length == 5) { // 確保格式正確
          String type = parts[1]; // 對象類型
          int x = Integer.parseInt(parts[2]); // X 座標
          int y = Integer.parseInt(parts[3]); // Y 座標
          if ("generator".equals(type)) {
            // 初始化發電機
            ClientGame.initGenerator(message);
            System.out.println("Initializing generator at (" + x + ", " + y + ") with ID " + ClientGame.generators[generatorCount].getId());
            generatorCount++;  
          } if ("hook".equals(type)) {
            // 初始化發電機
            ClientGame.initHook(message);
            System.out.println("Initializing hook at (" + x + ", " + y + ") with ID " + ClientGame.Hook[hookCount].getId());
            hookCount++;
          }else if ("player".equals(type)) {
            ClientGame.initPlayer(message,id);
            // System.out.println("Initializing player at (" + x + ", " + y + ") with ID " + id);
          }
      } 
    }

    if(message.startsWith("updateGameObject"))  {
      if (parts[1].equals("player")){
        ClientGame.updatePlayerPosition(message, id);
      } else if (parts[1].equals("health")) {
        ClientGame.HealthStatus(message);
      }
    }

    if(message.startsWith("animated")) {
      if(parts[1].equals("attack"))  
      ClientGame.attackFacing(message);
      if(parts[1].equals("animated"))
      ClientGame.moveAnimation(message);
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
