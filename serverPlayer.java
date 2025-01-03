public class ServerPlayer extends ServerGameObject {
    private int dx = 0; // 水平方向速度
    private int dy = 0; // 垂直方向速度
    private final int SPEED = 3;

    public ServerPlayer(int id) {
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

    public void updatePosition() {
        setX(getX() + dx * SPEED); 
        setY(getY() + dy * SPEED);
    }
}
