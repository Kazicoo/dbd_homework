public class serverPlayer extends ServerGameObject {
    private int dx = 0;
    private int dy = 0;
    private int speed = 0;

    public serverPlayer(int id) {
        super(id);
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void updatePosition() {
        setX(getX() + dx * speed); 
        setY(getY() + dy * speed);
    }
}
