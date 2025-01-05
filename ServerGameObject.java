public class ServerGameObject {
    private final int id;
    protected int relativeLocationX;
    protected int relativeLocationY;
    private boolean isCollisionStatus; 

    public ServerGameObject(int id, boolean isCollisionStatus) {
        this.id = id;
        this.isCollisionStatus = isCollisionStatus;
        this.relativeLocationX = -1;
        this.relativeLocationY = -1; 
    }

    public int getId() {
        return id;
    }

    public void setRelativeLocation(int x, int y) {
        this.relativeLocationX = x;
        this.relativeLocationY = y;
    }

    public void setX(int x) {
        this.relativeLocationX = x;
    }

    public void setY(int y) {
        this.relativeLocationY = y;
    }

    public int getX() {
        return relativeLocationX;
    }

    public int getY() {
        return relativeLocationY;
    }

    public void setCollisionStatus(boolean isCollisionStatus) {
        this.isCollisionStatus = isCollisionStatus;
    }

    public boolean getCollisionStatus() {
        return this.isCollisionStatus;
    }
}