

public class ServerHook extends ServerMapItems{
    private final int x;
    private final int y;

    public ServerHook(int id, int x, int y) {
        super(id);
        this.x = x;
        this.y = y;
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
            (x+1) * ServerGame.GRID_SIZE,
            (y+2) * ServerGame.GRID_SIZE,
            // other top left
            serverPlayer.getX() - ServerGame.collisionSize,
            serverPlayer.getY() - ServerGame.collisionSize,
            // other bottom right
            serverPlayer.getX() + ServerGame.collisionSize,
            serverPlayer.getY() + ServerGame.collisionSize);
    }

    @Override
    public void update() {

    }
}
