import javax.swing.ImageIcon;


public class ClientBoard extends ClientMapItems {
    int id;
    private String status;

    public ClientBoard(int id) {
        super(id);
        this.status = "broken";
        this.status = "used";
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
    public ImageIcon getBroadImage() {
        String imagePath = "Graphic/Object/broad-notUsed.png"; // 默認圖片
        if ("Used".equals(status)) {
            imagePath = "Graphic/Object/broad-blocked.png"; // 阻擋狀態圖片
        }if("broken".equals(status)){
            imagePath = "Graphic/Object/broad-broken.png"; // 壞掉狀態圖片
        }

        return new ImageIcon(imagePath);
    }
}