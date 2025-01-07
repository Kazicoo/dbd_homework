public class ServerWindow extends ServerMapItems {
    private final int x;
    private final int y;

    public ServerWindow(int id, int x, int y) {
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
            (y+1) * ServerGame.GRID_SIZE,
            // other top left
            serverPlayer.getX() - ServerGame.GRID_SIZE / 2,
            serverPlayer.getY() - ServerGame.GRID_SIZE / 2,
            // other bottom right
            serverPlayer.getX() + ServerGame.GRID_SIZE / 2,
            serverPlayer.getY() + ServerGame.GRID_SIZE / 2);
    }

    public void cross(ServerGame game, ServerPlayer player) {
        if (getY() + ServerGame.GRID_SIZE < player.getY()) {
            new Thread(() -> {
                player.setY(getY() - ServerGame.GRID_SIZE / 2);
                game.sendMessage("updateGameObject;player;" + getX() + ";" + getY() + ";" + getId());
                game.sendMessage("crossing;player;back;" + getId());

                try {
                    Thread.sleep(switch (player.getRole()) {
                        case "killer" -> 2000;
                        default -> 1000;
                    });
                } catch (Exception e) {}
                
                player.setY(getY() - ServerGame.GRID_SIZE / 2);
                game.sendMessage("updateGameObject;player;" + getX() + ";" + getY() + ";" + getId());
            }).start();

            return;
        }

        new Thread(() -> {
            player.setY(getY() + ServerGame.GRID_SIZE / 2);
            game.sendMessage("updateGameObject;player;" + getX() + ";" + getY() + ";" + getId());
            game.sendMessage("crossing;player;front;" + getId());

            try {
                Thread.sleep(switch (player.getRole()) {
                    case "killer" -> 2000;
                    default -> 1000;
                });
            } catch (Exception e) {}
            
            player.setY(getY() + ServerGame.GRID_SIZE / 2);
            game.sendMessage("updateGameObject;player;" + getX() + ";" + getY() + ";" + getId());
        }).start();
    }
}
