import java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    private ClientGame clientGame; // 假設 clientGame 中包含玩家陣列
    private Image backgroundImage;
    private int cameraOffsetX = 0;
    private int cameraOffsetY = 0;
    private Image generatorIcon;
    private Image hookIcon;
    private Image boardIcon;
    private Image windowIcon;
    
    public GamePanel(ClientGame clientGame) {
        this.clientGame = clientGame;
        this.backgroundImage = new ImageIcon("Graphic/mapWithWall.png").getImage();
        this.generatorIcon = new ImageIcon("Graphic/Object/generator-broken.png").getImage();
        this.hookIcon = new ImageIcon("Graphic/Object/hook.png").getImage();
        this.boardIcon = new ImageIcon("Graphic/Object/board.png").getImage();
        System.out.println("Background image loaded: " + (backgroundImage != null));
        System.out.println("generator image loaded: " + (generatorIcon != null));
        System.out.println("hook image loaded: " + (hookIcon != null));
        System.out.println("board image loaded: " + (boardIcon != null));
        setLayout(null);  // 讓按鈕可以自由放置
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 繪製背景圖，根據鏡頭的偏移量來顯示
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
            }
        }

        drawGenerator(g);
        drawHooks(g);
        drawBoard(g);
        drawWindow(g);
    }

    // 繪製每一個鉤子
    private void drawHooks(Graphics g) {
        for (ClientHook clientHook : clientGame.Hook) {
            if (clientHook != null && clientHook.getCurrentImage() != null) {
                ImageIcon hookIcon = clientHook.getCurrentImage();
                int x = clientHook.getX()- cameraOffsetX;
                int y = clientHook.getY() - cameraOffsetY;
                hookIcon.paintIcon(this, g, x, y);  // 繪製鉤子圖標
            }
        }
    }

    private void drawBoard(Graphics g) {
        for (ClientBoard clientboard : clientGame.Board) {
            if (clientboard != null) {
                int x = clientboard.getX() - cameraOffsetX;
                int y = clientboard.getY() - cameraOffsetY;
                g.drawImage(hookIcon, x, y, 60, 120, this);  // 繪製板子圖標
            }
        }
    }

    private void drawWindow(Graphics g) {
        for (ClientWindow clientWindow : clientGame.Window) {
            if (clientWindow != null) {
                int x = clientWindow.getX() - cameraOffsetX;
                int y = clientWindow.getY() - cameraOffsetY;
                g.drawImage(windowIcon, x, y, 60, 120, this);  // 繪製窗口圖標
            }
        }
    }


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

    private void drawGenerator(Graphics g) {
        // 遍歷所有的發電機
        for (int i = 0; i < clientGame.generators.length; i++) {
            if (clientGame.generators[i] != null) {
                int x = clientGame.generators[i].getX() - cameraOffsetX; // 根據鏡頭偏移量調整 x 坐標
                int y = clientGame.generators[i].getY() - cameraOffsetY; // 根據鏡頭偏移量調整 y 坐標
                g.drawImage(generatorIcon, x, y, 120, 60, this); // 繪製發電機圖標
            }
        }
    }
}
