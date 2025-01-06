public class ServerWall extends ServerMapItems {
    private final int x;
    private final int y;

    public ServerWall(int x, int y) {
        super(0);

        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isColliding(ServerPlayer serverPlayer) {
        return ServerGame.aabb_collision(
            // self top left
            x * ServerGame.GRID_SIZE, 
            y * ServerGame.GRID_SIZE, 
            // self bottom right
            (x+1) * ServerGame.GRID_SIZE,
            (y+1) * ServerGame.GRID_SIZE,
            // other top left
            serverPlayer.getX() - ServerGame.GRID_SIZE / 2,
            serverPlayer.getY() - ServerGame.GRID_SIZE / 2,
            // other bottom right
            serverPlayer.getX() + ServerGame.GRID_SIZE / 2,
            serverPlayer.getY() + ServerGame.GRID_SIZE / 2);
    }
}
