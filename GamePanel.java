import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private ClientGame clientGame;
    private Image backgroundImage;

    public GamePanel(ClientGame clientGame) {
        this.clientGame = clientGame;
        this.backgroundImage = new ImageIcon(("Graphic/GuideLine.png")).getImage();
        System.out.println("Background image loaded: " + (backgroundImage != null));
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, 6000, 3600, this);
        }
        if (clientGame.clientKiller != null && clientGame.clientKiller.getIcon() != null) {
            ImageIcon killerIcon = clientGame.clientKiller.getIcon();
            int x = clientGame.clientKiller.getX();
            int y = clientGame.clientKiller.getY();
            killerIcon.paintIcon(this, g, x, y);
        }

         // 繪製玩家
    for (int i = 0; i < clientGame.players.length; i++) {
        if (clientGame.players[i] != null && clientGame.players[i].getIcon() != null) {
            ImageIcon playerIcon = clientGame.players[i].getIcon();
            int x = clientGame.players[i].getX();
            int y = clientGame.players[i].getY();
            playerIcon.paintIcon(this, g, x, y);
        }
    }
        System.out.println("paintComponent called");
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(6000, 3600); // 地图的完整尺寸
    }
}


