import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ServerGame {
    private final Server server;
    private int[] idRole = new int[4];
    private final String[] chars = {"killer", "p1", "p2", "p3"};
    private final ServerPlayer players[] = new ServerPlayer[4];
    private final ServerGenerator[] generators = new ServerGenerator[4];
    public static final int GRID_SIZE   = 60;
    public static final int GRID_WIDTH  = 100;
    public static final int GRID_HEIGHT = 60;
    public static final int collisionSize = GRID_SIZE / 3;
 
    public static final float FRAME_PER_SEC = 20;
    private Timer gameLoopTimer;
    Random rand = new Random();

    private final ServerMapItems[][] grid = new ServerMapItems[GRID_WIDTH][GRID_HEIGHT];

    public ServerGame(Server server) {
        this.server = server;
        this.idRole = server.getidRole();

    }

    //處理分配玩家出生點
    public void loadingPlayerLocation(){
        int count = 0;
        int[][] positionMap = new int[9][2];
        positionMap[0] = new int[]{16*GRID_SIZE, 10*GRID_SIZE};
        positionMap[1] = new int[]{53*GRID_SIZE, 10*GRID_SIZE};
        positionMap[2] = new int[]{83*GRID_SIZE, 14*GRID_SIZE};
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
        positionMap[0] = new int[]{9, 10};
        positionMap[1] = new int[]{48, 7};
        positionMap[2] = new int[]{81, 10};
        positionMap[3] = new int[]{8, 26};
        positionMap[4] = new int[]{44, 27};
        positionMap[5] = new int[]{70, 30};
        positionMap[6] = new int[]{20, 47};
        positionMap[7] = new int[]{54, 40};
        positionMap[8] = new int[]{93, 47};

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
                generators[count] = new ServerGenerator(count, positionMap[position][0], positionMap[position][1]);
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

        for (ServerMapItems[] row : grid) {
            for (ServerMapItems item : row) {
                if (item != null) {
                    item.update();

                    if (item instanceof ServerGenerator && ((ServerGenerator)item).isJustFixed()) {
                        server.broadcastToClient("updateGameObject;generator;" + item.getX() + ";" + item.getY() + ";" + ((ServerGenerator)item).getId());
                    }
                }
            }
        }
    }

    public ServerMapItems getMapItem(int x, int y) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return null;
        }

        return grid[x][y];
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

    public ServerPlayer[] getPlayers() {
        return players;
    }

    public ServerKiller getKillers() {
        return (ServerKiller)players[0];
    }
    
    public ServerHuman[] getHumans() {
        return new ServerHuman[]{(ServerHuman)players[1], (ServerHuman)players[2], (ServerHuman)players[3]};
    }
    
    public void initWall() {
        for (int i = 0; i <= 59; i++) {
            grid[0][i] = new ServerWall(0, i);
            grid[99][i] = new ServerWall(99, i);
        }
        for (int i = 0; i <= 99; i++) {
            grid[i][0] = new ServerWall(i, 0);
            grid[i][59] = new ServerWall(i, 59);
        }
        // 左下、中下
        for (int i = 5; i <= 15; i++) {
            if (i == 9) continue;
            grid[i][49] = new ServerWall(i, 49);
        }
        for (int i = 50; i <= 52; i++) {
            grid[4][i] = new ServerWall(4, i);
        }
        grid[18][50] = new ServerWall(18, 50);
        grid[19][50] = new ServerWall(19, 50);
        for (int i = 19; i <= 26; i++) {
            if (i == 22) continue;
            grid[i][40] = new ServerWall(i, 40);
        }
        for (int i = 41; i <= 43; i++) {
            grid[19][i] = new ServerWall(19,i);
        }
        for (int i = 43; i <= 53; i++) {
            if (i == 48) continue;
            grid[25][i] = new ServerWall(25,i);
        }
        for (int i = 31; i <= 39; i++) {
            if (i == 37) continue;
            grid[i][51] = new ServerWall(i,51);   
        }
        grid[39][50] = new ServerWall(39,50);
        grid[39][49] = new ServerWall(39,49);
        grid[39][48] = new ServerWall(39,48);
        for (int i = 35; i <= 45; i++) {
            if (i == 40) continue;
            grid[35][i] = new ServerWall(35,i);
        }
        grid[52][40] = new ServerWall(52,40);
        grid[52][41] = new ServerWall(52,41);
        grid[53][37] = new ServerWall(53,37);
        grid[54][37] = new ServerWall(54,37);
        grid[53][43] = new ServerWall(53,43);
        grid[54][43] = new ServerWall(54,43);
        for (int i = 35; i <= 43; i++) {
            if (i == 39) continue;
            grid[58][i] = new ServerWall(58,i);
        }
        grid[64][51] = new ServerWall(64,51);
        grid[65][51] = new ServerWall(65,51);
        for (int i = 65; i <= 77; i++) {
            grid[i][38] = new ServerWall(i,38);
        }
        for (int i = 65; i <= 75; i++) {
            if (i == 70) continue;
            grid[i][41] = new ServerWall(i,41);
        }
        for (int i = 70; i <= 80; i++) {
            if (i == 74) continue;
            grid[i][51] = new ServerWall(i,51);
        }
        grid[70][52] = new ServerWall(70,52);
        grid[70][53] = new ServerWall(70,53);
        grid[70][54] = new ServerWall(70,54);
        for (int i = 36; i <= 44; i++) {
            grid[79][i] = new ServerWall(79,i);
        }
        for (int i = 75; i <= 78; i++) {
            grid[i][44] = new ServerWall(i,44);
        }
        grid[80][48] = new ServerWall(80,48);
        grid[81][48] = new ServerWall(81,48);
        // 中
        grid[24][25] = new ServerWall(24,25);
        grid[24][26] = new ServerWall(24,26);
        grid[24][27] = new ServerWall(24,27);
        grid[25][25] = new ServerWall(25,25);
        for (int i = 25; i <= 35; i++) {
            grid[i][25] = new ServerWall(i,25);
        }
        for (int i = 20; i <= 24; i++) {
            grid[39][i] = new ServerWall(39,i);
        }
        for (int i = 40; i <= 43; i++) {
            grid[i][20] = new ServerWall(i,20);
        }
        grid[44][23] = new ServerWall(44,23);
        grid[45][23] = new ServerWall(45,23);
        for (int i = 46; i <= 50; i++) {
            grid[i][20] = new ServerWall(i,20);
        }
        for (int i = 21; i <= 24; i++) {
            grid[50][i] = new ServerWall(50,i);
        }
        for (int i = 27; i <= 31; i++) {
            grid[39][i] = new ServerWall(39,i);
        }
        for (int i = 40; i <= 43; i++) {
            grid[i][31] = new ServerWall(i,31);
        }
        for (int i = 46; i <= 50; i++) {
            grid[i][31] = new ServerWall(i,31);
        }
        for (int i = 27; i <= 30; i++) {
            grid[50][i] = new ServerWall(50,i);
        }
        grid[39][34] = new ServerWall(39,34);
        grid[40][34] = new ServerWall(40,34);
        grid[46][33] = new ServerWall(46,33);
        grid[46][34] = new ServerWall(46,34);
        for (int i = 19; i <= 28; i++) {
            if (i == 24) continue;
            grid[56][i] = new ServerWall(56,i);
        }
        for (int i = 21; i <= 30; i++) {
            if (i == 6) continue;
            grid[61][i] = new ServerWall(61,i);
        }
        //右、右下
        for (int i = 86; i <= 89; i++) {;
            grid[i][29] = new ServerWall(i,29);
        }
        grid[92][29] = new ServerWall(92,29);
        grid[93][29] = new ServerWall(93,29);
        grid[94][29] = new ServerWall(94,29);
        for (int i = 30; i <= 38; i++) {
            if (i == 36) continue;
            grid[86][i] = new ServerWall(86,i);
        }
        grid[87][38] = new ServerWall(87,38);
        grid[88][38] = new ServerWall(88,38);
        grid[91][25] = new ServerWall(91,25);
        grid[92][25] = new ServerWall(92,25);
        for (int i = 17; i <= 21; i++) {
            grid[95][i] = new ServerWall(95,i);
        }
        grid[96][21] = new ServerWall(96,21);
        grid[98][19] = new ServerWall(98,19);
        grid[98][20] = new ServerWall(98,20);
        grid[95][23] = new ServerWall(95,23);
        grid[96][23] = new ServerWall(96,23);
        for (int i = 24; i <= 27; i++) {
            grid[95][i] = new ServerWall(95,i);
        }
        grid[98][24] = new ServerWall(98,24);
        grid[98][25] = new ServerWall(98,25);
        for (int i = 48; i <= 52; i++) {
            grid[85][i] = new ServerWall(85,i);
        }
        grid[86][52] = new ServerWall(86,52);
        grid[88][50] = new ServerWall(88,50);
        grid[88][51] = new ServerWall(88,51);
        grid[86][54] = new ServerWall(86,54);
        for (int i = 54; i <= 58; i++) {
            grid[85][i] = new ServerWall(85,i);
        }
        grid[88][55] = new ServerWall(88,55);
        grid[88][56] = new ServerWall(88,56);
        for (int i = 91; i <= 95; i++) {
            grid[i][48] = new ServerWall(i,48);
        }
        for (int i = 45; i <= 48; i++) {
            grid[96][i] = new ServerWall(96,i);
        }
        //中上
        for (int i = 42; i <= 55; i++) {
            grid[i][4] = new ServerWall(i,4);
        }
        for (int i = 6; i <= 10; i++) {
            grid[55][i] = new ServerWall(55,i);
        }
        for (int i = 7; i <= 17; i++) {
            if (i == 12) continue;
            grid[63][i] = new ServerWall(63,i);
        }
        grid[68][8] = new ServerWall(68,8);
        grid[69][8] = new ServerWall(69,8);
        grid[71][5] = new ServerWall(71,5);
        grid[71][6] = new ServerWall(71,6);
        grid[75][3] = new ServerWall(75,3);
        grid[76][3] = new ServerWall(76,3);
        for (int i = 66; i <= 73; i++) {
            if(i == 72) continue;
            grid[i][18] = new ServerWall(i,18);
        }
        for (int i = 18; i <= 21; i++) {
            grid[74][i] = new ServerWall(74,i);
        }
        for (int i = 77; i <= 87; i++) {
            grid[i][6] = new ServerWall(i,6);
        }
        for (int i = 7; i <= 12; i++) {
            grid[77][i] = new ServerWall(77,i);
        }
        for (int i = 77; i <= 81; i++) {
            grid[i][14] = new ServerWall(i,14);
        }
        for (int i = 83; i <= 87; i++) {
            grid[i][14] = new ServerWall(i,14);
        }
        for (int i = 8; i <= 13; i++) {
            grid[87][i] = new ServerWall(87,i);
        }
        //左上
        for (int i = 5; i <= 13; i++) {
            if(i == 10) continue;
            grid[i][4] = new ServerWall(i,4);
        }
        for (int i = 5; i <= 13; i++) {
            if(i == 11) continue;
            grid[5][i] = new ServerWall(5,i);
        }
        grid[6][13] = new ServerWall(6,13);
        for (int i = 9; i <= 15; i++) {
            grid[i][7] = new ServerWall(i,7);
        }
        grid[7][13] = new ServerWall(7,3);
        for (int i = 5; i <= 15; i++) {
            grid[i][23] = new ServerWall(i,23);
        }
        for (int i = 24; i <= 31; i++) {
            grid[5][i] = new ServerWall(5,i);
        }
        for (int i = 6; i <= 15; i++) {
            if(i == 10) continue;
            grid[i][31] = new ServerWall(i,31);
        }
        grid[12][33] = new ServerWall(12,33);
        grid[12][34] = new ServerWall(12,34);
    }

    public void initHook() {
        grid[13][8] = new ServerHook(0, 13, 8);
        server.broadcastToClient("initGameObject;hook;3;8;0");
        grid[30][16] = new ServerHook(1, 30, 16);
        server.broadcastToClient("initGameObject;hook;30;16;1");
        grid[44][5] = new ServerHook(2, 44, 5);
        server.broadcastToClient("initGameObject;hook;44;5;2");
        grid[76][19] = new ServerHook(3, 76, 19);
        server.broadcastToClient("initGameObject;hook;13;8;3");
        grid[94][19] = new ServerHook(4, 94, 19);
        server.broadcastToClient("initGameObject;hook;94;19;4");
        grid[48][23] = new ServerHook(5, 48, 23);
        server.broadcastToClient("initGameObject;hook;48;23;5");
        grid[12][46] = new ServerHook(6, 12, 46);
        server.broadcastToClient("initGameObject;hook;12;46;6");
        grid[48][43] = new ServerHook(7, 48, 43);
        server.broadcastToClient("initGameObject;hook;48;43;7");
        grid[76][46] = new ServerHook(8, 76, 46);
        server.broadcastToClient("initGameObject;hook;76;46;8");
    }
    

    public static boolean aabb_collision(  // axis-aligned bounding box
        int x11, int y11,       // Top-left 1
        int x12, int y12,       // Bottom-right 1
        int x21, int y21,       // Top-left 2
        int x22, int y22        // Bottom-right 2
    ) {
        if (x12 < x21 || x11 > x22) return false;
        return !(y12 < y21 || y11 > y22);
    }
}
