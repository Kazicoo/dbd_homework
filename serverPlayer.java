

public abstract class serverPlayer extends ServerGameObject {
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

    public serverPlayer(int id, ServerGame game) {
        super(id);
        this.game = game;
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
            int newX = relativeLocationX + (dx * getMoveSpeed());
            int newY = relativeLocationX + (dy * getMoveSpeed());
            setX(newX);
            setY(newY);
            game.sendMessage("updateGameObject;player;" + getX() + ";" + getY() + ";" + getId());
        }
        // int move[] = game.validateMovement(
        //     relativeLocationX + (dx * getMoveSpeed()),
        //     relativeLocationY + (dy * getMoveSpeed()));
        
        // if (move[0] != 0 || move[1] != 0) {
        //     relativeLocationX = move[0];
        //     relativeLocationY = move[1];
        // }
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
            case UP         -> { base_angle = 0;   }
            case DOWN       -> { base_angle = 45;  }
            case LEFT       -> { base_angle = 90;  }
            case RIGHT      -> { base_angle = 135; }
            case UP_LEFT    -> { base_angle = 180; }
            case UP_RIGHT   -> { base_angle = 225; }
            case DOWN_LEFT  -> { base_angle = 270; }
            case DOWN_RIGHT -> { base_angle = 315; }
        }

        return 
            (theta >= base_angle - angle / 2) &&
            (theta <= base_angle + angle / 2);
    }
}
