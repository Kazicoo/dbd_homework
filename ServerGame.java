import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ServerGame {
    private final Server server;
    private int[] idRole = new int[4];
    private final String[] chars = {"killer", "p1", "p2", "p3"};
    private final ServerPlayer players[] = new ServerPlayer[4];
    private final ServerGenerator[] generators = new ServerGenerator[4];
    public static final int GRID_SIZE = 60;
    public static final float FRAME_PER_SEC = 20;
    private Timer gameLoopTimer;
    Random rand = new Random();

    private static final int GRID_WIDTH = 100;
    private static final int GRID_HEIGHT = 60;
    private final ServerGameObject[][] grid = new ServerGameObject[GRID_WIDTH][GRID_HEIGHT];

    public ServerGame(Server server) {
        this.server = server;
        this.idRole = server.getidRole();
    }

    //處理分配玩家出生點
    public void loadingPlayerLocation(){
        int count = 0;
        int[][] positionMap = new int[9][2];
        positionMap[0] = new int[]{16*GRID_SIZE, 10*GRID_SIZE};
        positionMap[1] = new int[]{53*GRID_SIZE, 5*GRID_SIZE};
        positionMap[2] = new int[]{83*GRID_SIZE, 10*GRID_SIZE};
        positionMap[3] = new int[]{29*GRID_SIZE, 28*GRID_SIZE};
        positionMap[4] = new int[]{48*GRID_SIZE, 28*GRID_SIZE};
        positionMap[5] = new int[]{74*GRID_SIZE, 31*GRID_SIZE};
        positionMap[6] = new int[]{23*GRID_SIZE, 42*GRID_SIZE};
        positionMap[7] = new int[]{50*GRID_SIZE, 50*GRID_SIZE};
        positionMap[8] = new int[]{89*GRID_SIZE, 43*GRID_SIZE};

        int[] usedPosition = new int[4];

        players[0] = new ServerKiller(idRole[0], this);

        for (int i = 1; i < 4; i++) {
            players[i] = new ServerHuman(idRole[i], this);
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
        for (ServerPlayer player : players) {
            server.broadcastToClient("initGameObject;player;" + 
                player.getX() + ";" + player.getY() + ";" + idRole[index]);
            index++;
        }
    }

    //處理分配發動機出生點
    public void loadingGeneratorLocation(){
        int count = 0;
        int[][] positionMap = new int[9][2];
        positionMap[0] = new int[]{9*GRID_SIZE, 10*GRID_SIZE};
        positionMap[1] = new int[]{48*GRID_SIZE, 7*GRID_SIZE};
        positionMap[2] = new int[]{81*GRID_SIZE, 10*GRID_SIZE};
        positionMap[3] = new int[]{8*GRID_SIZE, 26*GRID_SIZE};
        positionMap[4] = new int[]{44*GRID_SIZE, 27*GRID_SIZE};
        positionMap[5] = new int[]{70*GRID_SIZE, 30*GRID_SIZE};
        positionMap[6] = new int[]{20*GRID_SIZE, 47*GRID_SIZE};
        positionMap[7] = new int[]{54*GRID_SIZE, 40*GRID_SIZE};
        positionMap[8] = new int[]{93*GRID_SIZE, 47*GRID_SIZE};

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
                generator.getX() + ";" + generator.getY() + ";" + generator.getId());
        }
    }
    
    public void initHealthStatus() {
        for (int i = 1; i < idRole.length; i++) {
            server.broadcastToClient("updateGameObject;health;2;" + idRole[i]);
        }
    }
    // 血量改變時
    public void setHealthStatus(int health, int id) {
        for (int i = 1; i < idRole.length; i++) {
            if (id == idRole[i]) {
                server.broadcastToClient("updateGameObject;health;" + health + ";" + id);
            }
        }
    }
    
    public void handleKeyInput(int id, String key, boolean isKeyDown) {
        for (ServerPlayer player : players) {
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
                player.setDirection(dx, dy);
                break;
            }
        }
    }
    
    public void sendMessage(String message) {
        server.broadcastToClient(message);
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
        }, 0, 1000 / (int)FRAME_PER_SEC); // 每50毫秒执行一次
    }
    
    private void updateGameLogic() {
        for (ServerPlayer player : players) {
            player.update();
        }
    }

    public int[] validateMovement(int x, int y) {
        int[] result = new int[2];
        int px = x / 60;
        int py = y / 60;

        // if (grid[px][py] != null && grid[px][py].isColliding(this)) {
        //     result[0] = 0;
        //     result[1] = 0;
        //     return result;
        // }
        
        // TODO: 檢查是否合法
        result[0] = x;
        result[1] = y;
        
        return result;
    }
    
    public ServerKiller getKiller() {
        return (ServerKiller)players[0];
    }
    
    public ServerHuman[] getHumans() {
        return new ServerHuman[]{(ServerHuman)players[1], (ServerHuman)players[2], (ServerHuman)players[3]};
    }
    
    public void initWall() {
        // 左上、中下
        for (int i = 5; i <= 15; i++) {
            if (i == 9) continue;
            grid[i][49] = new ServerWall();
        }
        for (int i = 50; i <= 52; i++) {
            grid[4][i] = new ServerWall();
        }
        grid[18][50] = new ServerWall();
        grid[19][50] = new ServerWall();
        for (int i = 19; i <= 26; i++) {
            if (i == 22) continue;
            grid[i][40] = new ServerWall();
        }
        for (int i = 41; i <= 43; i++) {
            grid[19][i] = new ServerWall();
        }
        for (int i = 43; i <= 53; i++) {
            if (i == 48) continue;
            grid[25][i] = new ServerWall();
        }
        for (int i = 31; i <= 39; i++) {
            if (i == 37) continue;
            grid[i][51] = new ServerWall();   
        }
        grid[39][50] = new ServerWall();
        grid[39][49] = new ServerWall();
        grid[39][48] = new ServerWall();
        for (int i = 35; i <= 45; i++) {
            if (i == 40) continue;
            grid[35][i] = new ServerWall();
        }
        grid[52][40] = new ServerWall();
        grid[52][41] = new ServerWall();
        grid[53][37] = new ServerWall();
        grid[54][37] = new ServerWall();
        grid[53][43] = new ServerWall();
        grid[54][43] = new ServerWall();
        for (int i = 35; i <= 43; i++) {
            if (i == 39) continue;
            grid[58][i] = new ServerWall();
        }
        grid[64][51] = new ServerWall();
        grid[65][51] = new ServerWall();
        for (int i = 65; i <= 77; i++) {
            grid[i][38] = new ServerWall();
        }
        for (int i = 65; i <= 75; i++) {
            if (i == 70) continue;
            grid[i][41] = new ServerWall();
        }
        for (int i = 70; i <= 80; i++) {
            if (i == 74) continue;
            grid[i][51] = new ServerWall();
        }
        grid[70][52] = new ServerWall();
        grid[70][53] = new ServerWall();
        grid[70][54] = new ServerWall();
        for (int i = 36; i <= 44; i++) {
            grid[79][i] = new ServerWall();
        }
        for (int i = 75; i <= 78; i++) {
            grid[i][44] = new ServerWall();
        }
        grid[80][48] = new ServerWall();
        grid[81][48] = new ServerWall();
        // 中
        grid[24][25] = new ServerWall();
        grid[24][26] = new ServerWall();
        grid[24][27] = new ServerWall();
        grid[25][25] = new ServerWall();
        for (int i = 25; i <= 35; i++) {
            grid[i][25] = new ServerWall();
        }
        for (int i = 20; i <= 24; i++) {
            grid[39][i] = new ServerWall();
        }
        for (int i = 40; i <= 43; i++) {
            grid[i][20] = new ServerWall();
        }
        grid[44][23] = new ServerWall();
        grid[45][23] = new ServerWall();
        for (int i = 46; i <= 50; i++) {
            grid[i][20] = new ServerWall();
        }
        for (int i = 21; i <= 24; i++) {
            grid[50][i] = new ServerWall();
        }
        for (int i = 27; i <= 31; i++) {
            grid[39][i] = new ServerWall();
        }
        for (int i = 40; i <= 43; i++) {
            grid[i][31] = new ServerWall();
        }
        for (int i = 46; i <= 50; i++) {
            grid[i][31] = new ServerWall();
        }
        for (int i = 27; i <= 30; i++) {
            grid[50][i] = new ServerWall();
        }
        grid[39][34] = new ServerWall();
        grid[40][34] = new ServerWall();
        grid[46][33] = new ServerWall();
        grid[46][34] = new ServerWall();
        for (int i = 19; i <= 28; i++) {
            if (i == 24) continue;
            grid[56][i] = new ServerWall();
        }
        for (int i = 21; i <= 30; i++) {
            if (i == 6) continue;
            grid[61][i] = new ServerWall();ㄕ
        }
    }
}
