

public class ServerGenerator extends ServerMapItems {       
    private final int[][] positionMap = new int[9][2];

    public ServerGenerator(int id) {
        super(id);
        initPositionMap();
    }

    private void initPositionMap() {
        positionMap[0] = new int[]{1000, 1000};
        positionMap[1] = new int[]{2000, 1000};
        positionMap[2] = new int[]{3000, 1000};
        positionMap[3] = new int[]{1000, 2000};
        positionMap[4] = new int[]{2000, 3000};
        positionMap[5] = new int[]{3000, 2000};
        positionMap[6] = new int[]{1000, 2000};
        positionMap[7] = new int[]{2000, 2300};
        positionMap[8] = new int[]{3000, 1130};
    }

    public int[] getPosition(int position) {
        return positionMap[position];
    }
}