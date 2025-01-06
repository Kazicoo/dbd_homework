import javax.swing.ImageIcon;

public class ClientHook extends ClientMapItems {
    private int id;
    private int x;
    private int y;
    private ImageIcon currentImage = new ImageIcon("Graphic/Object/hook.png");
    public ClientHook(int id) {
        super(id);
    }

    public int getId() {
        return id;
    }
    public void setRelativeLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public ImageIcon getCurrentImage() {
        return currentImage;
    }
}