import javax.swing.ImageIcon;

public class ClientGameObject {
    private final int id;
    private int relativeLocationX;
    private int relativeLocationY;  
    private ImageIcon icon;

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

    public void setX(int x) {
        this.relativeLocationX = x;
    }

    public void setY(int y) {
        this.relativeLocationY = y;
    }

    public int getX() {
        return this.relativeLocationX;
    }

    public int getY() {
        return this.relativeLocationY;
    }

     public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public ImageIcon getIcon() {
        return icon;
    }
}
