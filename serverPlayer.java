public class serverPlayer extends ServerGameObject {
    private int dx = 0;
    private int dy = 0;
    private int speed = 0;

    public serverPlayer(int id) {
        super(id);
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
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void updatePosition() {
        setX(getX() + this.dx * speed); 
        setY(getY() + this.dy * speed);
    }
}
