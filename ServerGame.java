import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ServerGame {
    private final Server server;
    private int[] idRole = new int[4];
    private final String[] chars = {"killer", "p1", "p2", "p3"};
    private final serverPlayer players[] = new serverPlayer[4];
    private final ServerGenerator[] generators = new ServerGenerator[4];
    private final int SIZE = 60;
    private Timer gameLoopTimer;
    Random rand = new Random();

    private static final int GRID_WIDTH = 100;
    private static final int GRID_HEIGHT = 60;
    private final int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT];

    public ServerGame(Server server) {
        this.server = server;
        this.idRole = server.getidRole();
    }

    //處理分配玩家出生點
    public void loadingPlayerLocation(){
        int count = 0;
        int[][] positionMap = new int[9][2];
        positionMap[0] = new int[]{16*SIZE, 10*SIZE};
        positionMap[1] = new int[]{53*SIZE, 5*SIZE};
        positionMap[2] = new int[]{83*SIZE, 10*SIZE};
        positionMap[3] = new int[]{29*SIZE, 28*SIZE};
        positionMap[4] = new int[]{48*SIZE, 28*SIZE};
        positionMap[5] = new int[]{74*SIZE, 31*SIZE};
        positionMap[6] = new int[]{23*SIZE, 42*SIZE};
        positionMap[7] = new int[]{50*SIZE, 50*SIZE};
        positionMap[8] = new int[]{89*SIZE, 43*SIZE};

        int[] usedPosition = new int[4];

        players[0] = new ServerKiller(idRole[0]);

        for (int i = 1; i < 4; i++) {
            players[i] = new ServerHuman(idRole[i]);
        }

        while(count < 4){
            int position = rand.nextInt(9);
            boolean isValid = true;

            for (int i = 0; i < count; i++) {
                if (usedPosition[i] == position) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                usedPosition[count] = position;
                players[count].setX(positionMap[position][0]);
                players[count].setY(positionMap[position][1]);
                count++;
            }
        }
        int index = 0;
        for (serverPlayer player : players) {
            server.broadcastToClient("initGameObject;player;" + 
            player.getX() + ";" + player.getX() + ";" +
            idRole[index]);
            index++;
        }
    }

    //處理分配發動機出生點
    public void loadingGeneratorLocation(){
        int count = 0;
        int[][] positionMap = new int[9][2];
        positionMap[0] = new int[]{9*SIZE, 10*SIZE};
        positionMap[1] = new int[]{48*SIZE, 7*SIZE};
        positionMap[2] = new int[]{81*SIZE, 10*SIZE};
        positionMap[3] = new int[]{8*SIZE, 26*SIZE};
        positionMap[4] = new int[]{44*SIZE, 27*SIZE};
        positionMap[5] = new int[]{70*SIZE, 30*SIZE};
        positionMap[6] = new int[]{20*SIZE, 47*SIZE};
        positionMap[7] = new int[]{54*SIZE, 40*SIZE};
        positionMap[8] = new int[]{93*SIZE, 47*SIZE};

        int[] usedPosition = new int[4];

        while (count < 4) {
            int position = rand.nextInt(9); 
            boolean isValid = true;

            for (int i = 0; i < count; i++) {
                if (usedPosition[i] == position) { 
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                usedPosition[count] = position;
                generators[count] = new ServerGenerator(count);
                generators[count].setX(positionMap[position][0]);
                generators[count].setY(positionMap[position][1]);
                count++;
            }
        }
        for (ServerGenerator generator : generators) {
            server.broadcastToClient("initGameObject;generator;" +
            generator.getX() + ";" + generator.getY() + ";" + 
            generator.getId());
        }
    }

    public void initHealthStatus() {
        for (int i = 1; i < chars.length; i++) {
            server.broadcastToClient("updateGameObject;health;2;" + chars[i]);
        }
    }
    // 血量改變時
    public void setHealthStatus(int health, int id) {
        for (int i = 1; i < idRole.length; i++) {
            if (id == idRole[i]) {
                server.broadcastToClient("updateGameObject;health;" + health + ";" + chars[i]);
            }
        }
    }

    public void handleKeyInput(int id, String key, boolean isKeyDown) {
        for (serverPlayer player : players) {
            if (player != null && player.getId() == id) {
                int dx = player.getDx();
                int dy = player.getDy();
                if (isKeyDown) {
                    switch (key) {
                        case "W" -> dy = -1;
                        case "S" -> dy = 1;
                        case "A" -> dx = -1;
                        case "D" -> dx = 1;
                    }
                } else {
                    switch (key) {
                        case "W", "S" -> dy = 0;
                        case "A", "D" -> dx = 0;
                    }
                }
                break;
            }
        }
    } 
    
    public void startGameLoop() {
        gameLoopTimer = new Timer();
        gameLoopTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    updateGameLogic();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 50); // 每50毫秒执行一次
    }

    private void updateGameLogic() {
        for (serverPlayer player : players) {
            if (player != null && (player.getDx() != 0 || player.getDy() != 0)) {
                player.updatePosition();
                // 廣播玩家的新位置
                server.broadcastToClient("updateGameObject;player;" + player.getX() + ";" + player.getY() + ";" + player.getId());
            }
        }
    }
}
