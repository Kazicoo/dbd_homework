import java.io.*;
import java.net.*;
import java.util.*;

public class dbd_server {
    private static final int PORT = 12345;
    private static final int MAX_PLAYERS = 4;

    private static List<PlayerHandler> players = Collections.synchronizedList(new ArrayList<>());
    private static Map<String, Boolean> playerReadyStatus = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        System.out.println("伺服器啟動中...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                if (players.size() < MAX_PLAYERS) {
                    Socket clientSocket = serverSocket.accept();
                    PlayerHandler player = new PlayerHandler(clientSocket);
                    players.add(player);
                    new Thread(player).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 開始監聽準備狀態
    private static void startReadyMonitor() {
        new Thread(() -> {
            while (true) {
                synchronized (playerReadyStatus) {
                    // 檢查所有玩家是否都準備完成
                    if (playerReadyStatus.size() == MAX_PLAYERS && playerReadyStatus.values().stream().allMatch(status -> status)) {
                        notifyAllPlayers("所有玩家已準備，遊戲開始！");
                        break;
                    }
                }

                try {
                    Thread.sleep(1000); // 每秒檢查一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 通知所有玩家訊息
    private static void notifyAllPlayers(String message) {
        synchronized (players) {
            for (PlayerHandler player : players) {
                player.sendMessage(message);
            }
        }
    }

    private static class PlayerHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName;

        public PlayerHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                playerReadyStatus.put(playerName, false);
                System.out.println(playerName + " 已連線。");
                notifyAllPlayers(playerName + " 已加入遊戲。");

                if (players.size() == MAX_PLAYERS) {
                    notifyAllPlayers("所有玩家已連線！等待玩家準備...");
                    startReadyMonitor(); // 啟動準備監聽
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(playerName + " 發送: " + message);

                    if (message.startsWith("SELECT")) {
                        notifyAllPlayers(playerName + " 選擇了角色: " + message.split(" ")[1]);
                    } else if (message.startsWith("DESELECT")) {
                        notifyAllPlayers(playerName + " 取消選擇角色: " + message.split(" ")[1]);
                    } else if (message.startsWith("READY")) {
                        playerReadyStatus.put(playerName, true);
                        notifyAllPlayers(playerName + " 已準備完成！");
                    } else if (message.startsWith("CANCEL_READY")) {
                        playerReadyStatus.put(playerName, false);
                        notifyAllPlayers(playerName + " 取消準備。");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                players.remove(this);
                playerReadyStatus.remove(playerName);
                System.out.println(playerName + " 已離線。");
                notifyAllPlayers(playerName + " 已離開遊戲。");
            }
        }

        // 傳送訊息給玩家
        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }
    }
}
