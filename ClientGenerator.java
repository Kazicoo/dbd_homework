import javax.swing.ImageIcon;


public class ClientGenerator extends ClientMapItems {
    int id;
    private String status;

    public ClientGenerator(int id) {
        super(id);
        this.status = "broken";
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    // 設置狀態
    public void setStatus(String status) {
        this.status = status;
    }

     // 根據狀態返回相應的圖片
    public ImageIcon getGeneratorImage() {
        String imagePath = "Graphic/Object/generator-broken.png"; // 默認圖片

        if ("fixed".equals(status)) {
            imagePath = "Graphic/Object/generator-fixed.png"; // 通電狀態圖片
        }

        return new ImageIcon(imagePath);
    }
}