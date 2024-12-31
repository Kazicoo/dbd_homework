import java.util.Random;

public class ServerGame {
    private final Server server;
    private int[] idRole = new int[4];
    private final ServerPlayer players[] = new ServerPlayer[4];
    private final ServerGenerator[] generators = new ServerGenerator[4];
    Random rand = new Random();

    public ServerGame(Server server) {
        this.server = server;
        this.idRole = server.getidRole();
    }

    //處理分配玩家出生點
    public void loadingPlayerLocation(){
        int count = 0;
        players[0] = new ServerKiller(idRole[0]);

        for (int i = 1; i < 4; i++) {
            players[i] = new ServerHuman(idRole[i]);
        }

        while(count < 4){
            int position = rand.nextInt(9);
            boolean isValid = true;

            for (int i = 0; i < count; i++) {
                if (players[i].getRelativeLocation() == position) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                players[count].setRelativeLocation(position);
                count++;
            }
        }
        int index = 0;
        for (ServerPlayer player : players) {
            server.broadcastToClient("initGameObject;player;" + player.getRelativeLocation() + ";" + idRole[index]);
            index++;
        }
    }

    //處理分配發動機出生點
    public void loadingGeneratorLocation(){
        int count = 0;
        int[][] positionMap = new int[9][2];
        positionMap[0] = new int[]{1000, 1000};
        positionMap[1] = new int[]{1000, 1000};
        positionMap[2] = new int[]{1000, 1000};
        positionMap[3] = new int[]{1000, 1000};
        positionMap[4] = new int[]{1000, 1000};
        positionMap[5] = new int[]{1000, 1000};
        positionMap[6] = new int[]{1000, 1000};
        positionMap[7] = new int[]{1000, 1000};
        positionMap[8] = new int[]{1000, 1000};

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
