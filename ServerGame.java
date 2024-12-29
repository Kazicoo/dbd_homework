import java.util.Random;

public class ServerGame {
    private int[] area = {0,1,2,3,4,5,6,7,8};
    private Player players[] = new Player[4];
    Random rand = new Random();

    public void loadingPlayerLocation(){
        int count = 0;

        players[0] = new Killer();

        for(int i=1; i<4; i++){
            players[i] = new Human();
        }

        while(count < 4){
            int relativeLocation = rand.nextInt(9);
            boolean isValid = true;

            for(int i = 0; i < count; i++){
                if(players[i].relativeLocation == relativeLocation){
                    isValid = false;
                    break;
                }
            }

            if(isValid){
                players[count].relativeLocation = relativeLocation;
                count ++;
            }
        }
    }
}
