import java.util.Random;

public class ServerGame {
    private final Server server;
    private int[] idRole = new int[4];
    private final ServerPlayer players[] = new ServerPlayer[4];
    private ServerGenerator[] generators = new ServerGenerator[4];
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
            int relativeLocation = rand.nextInt(9);
            boolean isValid = true;

            for (int i = 0; i < count; i++) {
                if (players[i].getRelativeLocation() == relativeLocation) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                players[count].setRelativeLocation(relativeLocation);
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

        while (count < 4) {
            int relativeLocation = rand.nextInt(9); 
            boolean isValid = true;

            for (int i = 0; i < count; i++) {
                if (generators[i].getRelativeLocation() == relativeLocation) { 
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                generators[count] = new ServerGenerator(count);
                generators[count].setRelativeLocation(relativeLocation);
                count++;
            }
        }
        for (ServerGenerator generator : generators) {
            server.broadcastToClient("GameObject;generator;" + generator.getRelativeLocation() + ";" + generator.getId());
        }
    }
}
