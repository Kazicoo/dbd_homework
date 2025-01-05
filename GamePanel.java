import java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    private ClientGame clientGame;  // 假設 clientGame 中包含玩家陣列
    private Image backgroundImage;
    private int cameraOffsetX = 0;
    private int cameraOffsetY = 0;

    public GamePanel(ClientGame clientGame) {
        this.clientGame = clientGame;
        this.backgroundImage = new ImageIcon("Graphic/GuideLine.png").getImage();
        System.out.println("Background image loaded: " + (backgroundImage != null));
        setLayout(null);
    }

    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // 繪製背景圖，並且根據鏡頭的偏移量來顯示
    if (backgroundImage != null) {
        g.drawImage(backgroundImage, -cameraOffsetX, -cameraOffsetY, 6000, 3600, this);
    }

    // 繪製每一位玩家
    for (ClientPlayer clientPlayer : clientGame.clientPlayers) {
        if (clientPlayer != null && clientPlayer.getCurrentImage() != null) {
            ImageIcon playerIcon = clientPlayer.getCurrentImage();
            int x = clientPlayer.getX() - cameraOffsetX;  // 根據鏡頭偏移量調整 x 坐標
            int y = clientPlayer.getY() - cameraOffsetY;  // 根據鏡頭偏移量調整 y 坐標
            playerIcon.paintIcon(this, g, x, y);  // 繪製玩家圖標
            // System.out.println("Drawing player " + clientPlayer.getId() + " at: (" + x + ", " + y + ")");
        }
    }
}

    // @Override
    // protected void paintComponent(Graphics g) {
    //     super.paintComponent(g);
    //     // 繪製背景圖
    //     if (backgroundImage != null) {
    //         g.drawImage(backgroundImage, -cameraOffsetX, -cameraOffsetY, 6000, 3600, this);
    //     }
    //     // 繪製每一位玩家
    //     for (ClientPlayer clientPlayer : clientGame.clientPlayers) {
    //         if (clientPlayer != null && clientPlayer.getCurrentImage() != null) {  
    //             ImageIcon playerIcon = clientPlayer.getCurrentImage();
    //             int x = clientPlayer.getX();  // 確保 ClientPlayer 類中有這個方法來獲取 x 坐標
    //             int y = clientPlayer.getY();  // 確保 ClientPlayer 類中有這個方法來獲取 y 坐標
    //             playerIcon.paintIcon(this, g, x, y);  // 繪製玩家圖標
    //             // System.out.println("Drawing player " + clientPlayer.getId() + " at: (" + x + ", " + y + ")");
    //         }
    //     }
    // }
    public void setCameraOffset(int cameraOffsetX, int cameraOffsetY) {
        this.cameraOffsetX = cameraOffsetX;
        this.cameraOffsetY = cameraOffsetY;
        System.out.println(cameraOffsetX);
        System.out.println(cameraOffsetY);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(6000, 3600); // 設定畫布大小
    }
}
