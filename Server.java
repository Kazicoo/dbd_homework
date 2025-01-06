import java.io.*;


public class Server implements Comm.TcpServerCallback { 
  private int readyCount = 0;
  private int startCount = 0;
  private final String[] chars = {"killer", "p1", "p2", "p3"};
  private final boolean[] characterSelected = new boolean[4];
  private final int[] idRole = new int[4];
  private int totalPlayers = 0;
  private final Comm.TcpServer server;
  private static ServerGame serverGame;
  
  
    
    public static void main(String[] args) {
      try {
        Server server = new Server();
        System.out.println("Launching the server");
        // 從這裡開始遊戲(即等待所有玩家準備完成後，伺服器會告訴前端準備完成)
        server.waitGameStart();
        serverGame = new ServerGame(server);
        // 當伺服器發送startLoading給客戶端後，要初始化遊戲資訊
        serverGame.loadingGeneratorLocation();
        serverGame.loadingPlayerLocation();
        serverGame.initHealthStatus();
        serverGame.startGameLoop();
    } catch (IOException e) {
      System.out.println("Failed to create server: " + e.getMessage());
    }
  }

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

    if (message.startsWith("startGame")) {
      startCount++;
      if (startCount == 4) {
        server.broadcast("startGame");
      }
    }

    if (message.startsWith("KeyDown")) {
      String key = message.split(";")[1];
      serverGame.handleKeyInput(id, key, true);
    } else if (message.startsWith("KeyUp")) {
      String key = message.split(";")[1];
      serverGame.handleKeyInput(id, key, false);
    }

    if (message.startsWith("attack")) {
      serverGame.getKiller().attack();
    }

    if (message.startsWith("animated")) {
      server.broadcast(message);
    }

    // if (message.startsWith("fix_gen")) {
    //   ServerGenerator gen;
    //   ServerPlayer player;

    //   if (player.canInteractGenerator(gen)) {
    //     gen.fix();
    //   }
    // }

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
    totalPlayers ++;
    server.broadcast("totalPlayers;" + (totalPlayers));
  }

  @Override
  public void onDisconnect(int id) {
    System.out.println("Client disconnected: " + id);
    totalPlayers --;
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

    try {
      Thread.sleep(60);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // 開始遊戲，告訴前端遊戲開始
    server.broadcast("startLoading");
  }
  
  public void updateReadyState (String message, int id) {
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
            idRole[i] = id;
          }
        }
      } else if ("unready".equals(parts[1])) {
        if (readyCount > 0) readyCount--;
        for (int i = 0; i < chars.length; i++) {
          if (parts[2].equals(chars[i])) {
              characterSelected[i] = false;
              idRole[i] = -1;
          }
        }
      }
      notifyAll(); // 通知等待的執行緒
  }
    server.broadcast(message + ";" + id);
  }

  public void broadcastToClient(String message) {
    server.broadcast(message);
  }

  public void sendToClient(int id, String message) { 
    server.send(id, message); 
  }

  public int[] getidRole() {
    return this.idRole;
  }
}
