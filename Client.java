import java.io.*;
import java.util.Scanner;
import javax.swing.SwingUtilities;


public class Client implements Comm.TcpClientCallback {
  private Comm.TcpClient client;
  private setupGUI initialGUI;
  private int id;
  private ClientGame ClientGame;

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
      if ("ready".equals(parts[1])) initialGUI.playerReady(true, message, id);
      if ("unready".equals(parts[1])) initialGUI.playerReady(false, message, id);
    }

    if ("startLoading".equals(message)) {
      initialGUI.startCountdown();
      initialGUI.closeFrame();
      SwingUtilities.invokeLater(() -> {
          ClientGame = new ClientGame(client);
      }); 
    }
    
    if("updatehealth".equals(message)){
      ClientGame.updateheaith(id);
      if("minus".equals(parts[1])){
        int totalhealth = Integer.parseInt(parts[2]);
        ClientGame.updateheaith(totalhealth);
      }
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
