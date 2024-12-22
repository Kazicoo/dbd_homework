import java.io.*;


public class Server implements Comm.TcpServerCallback {
  private int readyCount = 0;
  
  public static void main(String[] args) {
    try {
      Server server = new Server();
      // 從這裡開始遊戲(即等待所有玩家準備完成後，伺服器會告訴前端準備完成)
      server.waitGameStart();
    } catch (IOException e) {
      System.out.println("Failed to create server: " + e.getMessage());
    }
  }

  Comm.TcpServer server;
  public Server()
    throws IOException
  {
    server = new Comm.TcpServer(8080, 4, this);
  }

  @Override
  public void onMessage(int id, String message) {
    if (message.startsWith("updateReadyState")) {
      updateReadyState(message, id);
    } 

    // System.out.println("Client " + id + " sent: " + message);
    // server.send(id, "Echo: " + message);
  }

  @Override
  public void onConnect(int id) {
    System.out.println("Client connected: " + id);
  }

  @Override
  public void onDisconnect(int id) {
    System.out.println("Client disconnected: " + id);
  }

  void waitGameStart () {
    // 檢查是否所有玩家都準備遊戲
    while (readyCount != 4);
    // 開始遊戲，告訴前端遊戲開始
    server.broadcast("gameStart");
  }

  void updateReadyState (String message, int id) {
    // 判斷能不能準備，此時收到的訊息封包大概是updateReadyState;ready;is_killer
    String[] parts = message.split(";");
    server.broadcast("ready;" + id);
    readyCount++;
  }
}
