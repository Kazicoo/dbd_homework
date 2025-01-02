import java.util.Random;

public class ServerGame {
    private final Server server;
    private int[] idRole = new int[4];
    private final ServerPlayer players[] = new ServerPlayer[4];
    private final ServerGenerator[] generators = new ServerGenerator[4];
    private final int SIZE = 60;
    Random rand = new Random();

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
        for (ServerPlayer player : players) {
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
        positionMap[0] = new int[]{SIZE, SIZE};
        positionMap[1] = new int[]{SIZE, SIZE};
        positionMap[2] = new int[]{SIZE, SIZE};
        positionMap[3] = new int[]{SIZE, SIZE};
        positionMap[4] = new int[]{SIZE, SIZE};
        positionMap[5] = new int[]{SIZE, SIZE};
        positionMap[6] = new int[]{SIZE, SIZE};
        positionMap[7] = new int[]{SIZE, SIZE};
        positionMap[8] = new int[]{SIZE, SIZE};

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
}
