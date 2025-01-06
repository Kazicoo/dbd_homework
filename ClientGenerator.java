import javax.swing.*;
import java.awt.event.*;

public class ClientGenerator extends ClientMapItems {
    int id;
    private String status;
    private JButton button;
    private JLabel progressLabel;
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
        }if("fixing".equals(status)){
            int progress = 0;
            progressLabel = new JLabel();
            
        }

        return new ImageIcon(imagePath);
    }

    public void setButton(JButton button) {
        ImageIcon generatorBrokenIcon = new ImageIcon("Graphic/Object/generator-broken.png");
        button = new JButton(generatorBrokenIcon);
        button.setBounds(getX(), getY(), 120, 60);
        button.setOpaque(false); // 讓按鈕背景透明
        button.setContentAreaFilled(false); // 移除按鈕預設的背景
        button.setBorderPainted(false); // 移除按鈕邊框

        // 添加 MouseListener 來偵測左鍵點擊
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {  // 判斷是否為左鍵點擊
                    System.out.println("Left mouse button clicked on generator " + getId());
                    // 在這裡執行對應的操作
                }
            }
        });

        this.button = button;
    }

    public JButton getButton() {
        return button;
    }
}
