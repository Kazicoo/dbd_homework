public class ClientGameObject {
    private final int id;
    private int relativeLocationX;
    private int relativeLocationY;  

    public ClientGameObject(int id) {
        this.id = id;
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

    public int getX() {
        return relativeLocationX;
    }

    public int getY() {
        return relativeLocationY;
    }
}
