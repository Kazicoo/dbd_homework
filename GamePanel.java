import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private ClientGame clientGame;

    public GamePanel(ClientGame clientGame) {
        this.clientGame = clientGame;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);  // 確保背景被清除

        // 繪製發電機、玩家、殺手等物件
        clientGame.draw(g, this);
    }
}