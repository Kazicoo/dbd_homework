

public class ServerGenerator extends ServerMapItems {   
    private final int x;
    private final int y;

    public static final double FIX_TARGET = 60;
    public static final double FIX_RATE   = 1;

    private double fixStatus = 0;
    private ServerHuman fixers[] = new ServerHuman[3];

    private boolean justFixed = false;

    public ServerGenerator(int id, int x, int y) {
        super(id);
        this.x = x;
        this.y = y;
    }

    public ServerHuman[] getFixers() { 
        return this.fixers; 
    }

    @Override
    public int getX() {
        return x * ServerGame.GRID_SIZE;
    }

    @Override
    public int getY() {
        return y * ServerGame.GRID_SIZE;
    }

    @Override
    public boolean isColliding(ServerPlayer serverPlayer) {
        return ServerGame.aabb_collision(
            // self top left
            x * ServerGame.GRID_SIZE, 
            y * ServerGame.GRID_SIZE, 
            // self bottom right
            (x+2) * ServerGame.GRID_SIZE,
            (y+1) * ServerGame.GRID_SIZE,
            // other top left
            serverPlayer.getX() - ServerGame.collisionSize,
            serverPlayer.getY() - ServerGame.collisionSize,
            // other bottom right
            serverPlayer.getX() + ServerGame.collisionSize,
            serverPlayer.getY() + ServerGame.collisionSize);
    }

    public void fix(ServerHuman serverPlayer) {
        for (int i = 0; i < fixers.length; i++) {
            if (fixers[i] == serverPlayer)
                return;

            if (fixers[i] == null) {
                fixers[i] = serverPlayer;
                return;
            }
        }
    }

    public boolean isFixed() {
        return fixStatus >= FIX_TARGET;
    }

    public boolean isJustFixed() {
        return justFixed;
    }

    private int playerId = -1;

    public void setSomeoneLeft(int playerId) {
        this.playerId = playerId;
    }

    public int getSomeoneLeft() {
        return this.playerId;
    }

    @Override
    public void update() {
        justFixed = false;

        if (isFixed()) 
            return;

        int count = 0;
        ServerHuman leftFixers[] = new ServerHuman[3];
        int leftFixersCount = 0;
        for (ServerHuman human : fixers) {
            if (human == null)
                continue;

            if (human.canInteractGenerator(this)) {
                count++;
            }

            else {
                leftFixers[leftFixersCount++] = human;
                setSomeoneLeft(human.getId());
            }
        }

        for (int i = 0; i < fixers.length; i++) {
            for (ServerHuman human : leftFixers) {
                if (human == null)
                    continue;

                fixers[i] = null;
                break;
            }
        }

        double fixRate = switch (count) {
            case 1 -> FIX_RATE;
            case 2 -> FIX_RATE * 1.5;
            case 3 -> FIX_RATE * 2;
            default -> 0;
        };

        fixStatus += fixRate / ServerGame.FRAME_PER_SEC;
        justFixed = isFixed();
    }

    public int getFixPercentage() { 
        return (int) ((fixStatus / FIX_TARGET) * 100); 
    }
}
