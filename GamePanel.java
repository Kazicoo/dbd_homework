import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private ClientGame clientGame;
    private Image backgroundImage;
    private int cameraOffsetX = 0;
    private int cameraOffsetY = 0;

    public GamePanel(ClientGame clientGame) {
        this.clientGame = clientGame;
        this.backgroundImage = new ImageIcon("Graphic/GuideLine.png").getImage();
        System.out.println("Background image loaded: " + (backgroundImage != null));
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 繪製背景圖
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, 6000, 3600, this);
        }

        // 繪製玩家
        for (int i = 0; i < clientGame.clientPlayer.length; i++) {
            if (clientGame.clientPlayer[i] != null && clientGame.clientPlayer[i].getIcon() != null) {
                ImageIcon playerIcon = clientGame.clientPlayer[i].getIcon();
                int x = clientGame.clientPlayer[i].getX();
                int y = clientGame.clientPlayer[i].getY();
                playerIcon.paintIcon(this, g, x, y);
                System.out.println("Drawing player " + clientGame.clientPlayer[i].getId() + " at: (" + x + ", " + y + ")");
            }
        }
    }

    public void setCameraOffset(int offsetX, int offsetY) {
        this.cameraOffsetX = offsetX;
        this.cameraOffsetY = offsetY;
        repaint(); // 更新畫面
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(6000, 3600); // 地圖的完整尺寸
    }
}
