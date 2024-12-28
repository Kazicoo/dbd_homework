import java.io.*;


public class Server implements Comm.TcpServerCallback { 
  private int readyCount = 0;
  private String[] chars = {"killer", "p1", "p2", "p3"};
  private boolean[] characterSelected = new boolean[4];
  
  public static void main(String[] args) {
    try {
      Server server = new Server();
      System.out.println("Launching the server");
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
    System.out.println(readyCount);
    System.out.println("Client " + id + " sent: " + message);
    server.send(id, "Echo: " + message); 
  }

  @Override
  public void onConnect(int id) {
    System.out.println("Client connected: " + id);
    // 告訴連進來的那位client的id是多少，使用字串串接
    server.send(id, "id;" + id);
    for (int i = 0; i < chars.length; i++) {
      if (characterSelected[i] == true) {

        server.send(id, "updateReadyState;ready;" + chars[i] + ";" + (id - 1));
      }
    }
  }

  @Override
  public void onDisconnect(int id) {
    System.out.println("Client disconnected: " + id);
  }

  void waitGameStart () {
    // 初始化角色都尚未被選擇
    for (int i = 0; i < characterSelected.length; i++) {
      characterSelected[i] = false;
    }


    // 檢查是否所有玩家都準備遊戲
    synchronized (this) {
      while (readyCount < 4) {
          try {
              wait();
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
    } 
    // 開始遊戲，告訴前端遊戲開始
    server.broadcast("gameStart");
    // 4.5秒後開始遊戲
    try {
      Thread.sleep(4500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  void updateReadyState (String message, int id) {
    // 主視窗負責處理是否準備的邏輯處理
    // 判斷能不能準備，此時收到的訊息封包大概是兩組
    // 1.玩家選擇角色，封包為updateReadyState;ready;<role>
    // 2.玩家取消選擇角色，封包為updateReadyState;unready;<role>
    String[] parts = message.split(";");

    synchronized (this) {
      if ("ready".equals(parts[1])) {
          if (readyCount < 4) readyCount++;
          for (int i = 0; i < chars.length; i++) {
              if (parts[2].equals(chars[i])) {
                  characterSelected[i] = true;
              }
          }
      } else if ("unready".equals(parts[1])) {
          if (readyCount > 0) readyCount--;
          for (int i = 0; i < chars.length; i++) {
              if (parts[2].equals(chars[i])) {
                  characterSelected[i] = false;
              }
          }
      }
      notifyAll(); // 通知等待的執行緒
  }
    server.broadcast(message + ";" + id);
  }
}
