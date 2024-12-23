import java.io.*;


public class Server implements Comm.TcpServerCallback {
  private int readyCount = 0;
  
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
    String chosenCharacter = null;

    // 确保消息格式是以 "updateIfChoseState" 开头
    if (message.startsWith("updateIfChoseState")) {
        // 提取角色部分，假设消息格式为 "updateIfChoseState:CharacterX"
        chosenCharacter = message.split(":")[1]; // 获取 "Character1", "Character2", ...

        // 使用 switch-case 处理不同的角色
        switch (chosenCharacter) {
            case "Ghost":
                // 如果是 ghost，不需要进一步的数字解析
                id = 0;  // 假设 ghost 对应 id 为 0
                break;
            case "Character1":
            case "Character2":
            case "Character3":
                // 提取 "Character" 后的数字部分
                try {
                    String playerIDStr = chosenCharacter.replaceAll("[^0-9]", ""); // 去除非数字字符
                    if (!playerIDStr.isEmpty()) {
                        id = Integer.parseInt(playerIDStr);  // 将数字转换为整数，并更新 id
                    }
                } catch (NumberFormatException e) {
                    // 处理数字解析错误
                    System.out.println("Error: Invalid player ID in message: " + chosenCharacter);
                }
                break;
            default:
                // 处理未知的角色
                System.out.println("Error: Unknown character in message: " + chosenCharacter);
                return;
        }
    }

    // 更新游戏状态，传入角色和 id
    if (chosenCharacter != null) {
        updateReadyState(chosenCharacter, id);
    } else {
        System.out.println("Error: No valid character chosen in the message.");
    }
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

  void updateReadyState (String chosenCharacter, int playerID) {
    // 主視窗負責處理是否準備的邏輯處理
    // 判斷能不能準備，此時收到的訊息封包大概是兩組
    // 1.玩家選擇角色，封包為updateReadyState;ready;<role>
    // 2.玩家取消選擇角色，封包為updateReadyState;unready;<role>
    // String[] parts = message.split(";");
    // // if ("ready".equals(parts[1])) {
    // //   // 封包是準備，執行準備的動作
    // //   readyCount++;
    // // } 
    // // else if ("unready".equals(parts[1])) {
    // //   // 封包是取消準備，執行取消準備的動作 
    // //   readyCount--;
    // // }
    // server.broadcast(message + ";" + id);

    
    System.out.println("Player " + playerID + " chose: " + chosenCharacter);
    server.broadcast("Player " + playerID + " chose: " + chosenCharacter);
  }
}
