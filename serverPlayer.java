

public abstract class ServerPlayer extends ServerGameObject {
    private String role;

    protected ServerGame game;

    private int dx = 0;
    private int dy = 0;
    private int moveSpeed;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UP_LEFT,
        UP_RIGHT,
        DOWN_LEFT,
        DOWN_RIGHT,
    }

    private Direction facing = Direction.DOWN;

    public ServerPlayer(int id, ServerGame game, String role) {
        super(id);
        this.game = game;
        this.role = role;
    }

    public int getDx() {
        return this.dx;
    }

    public int getDy() {
        return this.dy;
    }

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;

        if (dx < 0 && dy < 0) {
            this.facing = Direction.UP_LEFT;
        } 
        
        else if (dx < 0 && dy > 0) {
            this.facing = Direction.DOWN_LEFT;
        } 
        
        else if (dx > 0 && dy < 0) {
            this.facing = Direction.UP_RIGHT;
        } 
        
        else if (dx > 0 && dy > 0) {
            this.facing = Direction.DOWN_RIGHT;
        } 
        
        else if (dx < 0) {
            this.facing = Direction.LEFT;
        } 
        
        else if (dx > 0) {
            this.facing = Direction.RIGHT;
        } 
        
        else if (dy < 0) {
            this.facing = Direction.UP;
        } 
        
        else if (dy > 0) {
            this.facing = Direction.DOWN;
        }
    }

    public Direction getFacing() {
        return this.facing;
    }

    public abstract int defaultSpeed();

    public void resetMoveSpeed() {
        this.moveSpeed = defaultSpeed();
    }

    public int getMoveSpeed() {
        return this.moveSpeed;
    }

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public void update() {
        if (dx != 0 || dy != 0) {
            for (int i = 0; i < getMoveSpeed(); i++) {
                setRelativeLocation(
                    relativeLocationX + dx, 
                    relativeLocationY + dy);

                ServerMapItems items[] = getNearbyMapItems();
                boolean colliding = false;

                for (int j=0; !colliding && j<25; j++) {
                    if (items[j] == null) { continue; }
                    if (!items[j].isColliding(this)) { continue; }
                    colliding = true;
                }

                ServerPlayer players[] = game.getPlayers();
                for (int j=0; !colliding && j<players.length; j++) {
                    if (players[j] == null) { continue; }
                    if (players[j] == this) { continue; }
                    if (!players[j].isColliding(this)) { continue; }
                    colliding = true;
                }

                if (colliding) {
                    setRelativeLocation(
                        relativeLocationX - dx, 
                        relativeLocationY - dy);
                    break;
                }
            }

            game.sendMessage("updateGameObject;player;" + getX() + ";" + getY() + ";" + getId());
        }
    }

    public boolean inRange(int x, int y, double range, double angle) {
        int vx = x - getX();
        int vy = y - getY();

        double distance = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));

        if (distance > range) {
            return false;
        }

        double base_angle = 0;
        double theta = Math.toDegrees(Math.acos(vx / distance));

        while (theta > 360) { theta -= 360; }
        while (theta < 0  ) { theta += 360; }

        switch (getFacing()) {
            case UP         -> { base_angle = 90;  }
            case DOWN       -> { base_angle = 270; }
            case LEFT       -> { base_angle = 180; }
            case RIGHT      -> { base_angle = 0;   }
            case UP_LEFT    -> { base_angle = 135; }
            case UP_RIGHT   -> { base_angle = 45;  }
            case DOWN_LEFT  -> { base_angle = 225; }
            case DOWN_RIGHT -> { base_angle = 315; }
        }
        
        return 
            (theta >= base_angle - angle / 2) &&
            (theta <= base_angle + angle / 2);
    }

    public ServerMapItems[] getNearbyMapItems() {
        int gx = getX() / ServerGame.GRID_SIZE;
        int gy = getY() / ServerGame.GRID_SIZE;

        ServerMapItems items[] = new ServerMapItems[25];
        int count = 0;

        for (int _x=-2; _x<=2; _x++) {
            for (int _y=-2; _y<=2; _y++) {
                ServerMapItems item = game.getMapItem(gx + _x, gy + _y);
                if (item == null) { continue; }
                items[count++] = item;                
            }
        }

        return items;
    }

    public boolean canInteractWindow(ServerWindow win) {
        return ServerGame.aabb_collision(
            // self top left
            getX() - ServerGame.GRID_SIZE / 2, 
            getY() - ServerGame.GRID_SIZE / 2, 
            // self bottom right
            getX() + ServerGame.GRID_SIZE / 2,
            getY() + ServerGame.GRID_SIZE / 2,
            // other top left
            win.getX() - ServerGame.GRID_SIZE,
            win.getY() - ServerGame.GRID_SIZE,
            // other bottom right
            win.getX() + 2*ServerGame.GRID_SIZE,
            win.getY() + 2*ServerGame.GRID_SIZE);
    }

    public String getRole() {
        return this.role;
    }

    // public void crossWindow(ServerWindow win) {
    //     if (getY() > win.getY()) {
    //         setRelativeLocation(win.getX(), win.getY()-50);
    //         game.sendMessage("updateGameObject;player;" + getX() + ";" + getY() + ";" + getId());
    //         game.sendMessage("crossing;player;back;" + getId());
    //         try {
    //             if ("killer".equals(game.getRole())) {
    //                 Thread.sleep(2000);
    //                 System.out.println("");
    //             } else {
    //                 Thread.sleep(1000);
    //             }
    //         } catch (Exception e) {
    //         }
    //         setRelativeLocation(win.getX(), win.getY()-100);
    //         game.sendMessage("updateGameObject;player;" + getX() + ";" + getY() + ";" + getId());
    //     } else {
    //         setRelativeLocation(win.getX(), win.getY()+50);
    //         game.sendMessage("updateGameObject;player;" + getX() + ";" + getY() + ";" + getId());
    //         game.sendMessage("crossing;player;front;" + getId());
    //         try {
    //             if ("killer".equals(game.getRole())) {
    //                 Thread.sleep(2000);
    //             } else {
    //                 Thread.sleep(1000);
    //             }
    //         } catch (Exception e) {
    //         }
    //         setRelativeLocation(win.getX(), win.getY()+100);
    //         game.sendMessage("updateGameObject;player;" + getX() + ";" + getY() + ";" + getId());
    //     }
    // }

    @Override
    public boolean isColliding(ServerPlayer serverPlayer) {
        return ServerGame.aabb_collision(
            // self top left
            getX() - ServerGame.collisionSize, 
            getY() - ServerGame.collisionSize, 
            // self bottom right
            getX() + ServerGame.collisionSize,
            getY() + ServerGame.collisionSize,
            // other top left
            serverPlayer.getX() - ServerGame.collisionSize,
            serverPlayer.getY() - ServerGame.collisionSize,
            // other bottom right
            serverPlayer.getX() + ServerGame.collisionSize,
            serverPlayer.getY() + ServerGame.collisionSize);
    }
}
