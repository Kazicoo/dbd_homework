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
            g.drawImage(backgroundImage, -cameraOffsetX, -cameraOffsetY, 6000, 3600, this);
        }

        // 繪製玩家
        for (int i = 0; i < clientGame.players.length; i++) {
            if (clientGame.players[i] != null && clientGame.players[i].getIcon() != null) {
                ImageIcon playerIcon = clientGame.players[i].getIcon();
                int x = clientGame.players[i].getX() - cameraOffsetX;
                int y = clientGame.players[i].getY() - cameraOffsetY;
                playerIcon.paintIcon(this, g, x, y);
            }
        }

        // 繪製殺手
        if (clientGame.clientKiller != null && clientGame.clientKiller.getIcon() != null) {
            ImageIcon killerIcon = clientGame.clientKiller.getIcon();
            int x = clientGame.clientKiller.getX() - cameraOffsetX;
            int y = clientGame.clientKiller.getY() - cameraOffsetY;
            killerIcon.paintIcon(this, g, x, y);
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
