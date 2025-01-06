import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel {
    private ClientGame clientGame; // 假設 clientGame 中包含玩家陣列
    private Image backgroundImage;
    private int cameraOffsetX = 0;
    private int cameraOffsetY = 0;
    private Image generatorIcon;
    private Image hookIcon;
    
    public GamePanel(ClientGame clientGame) {
        this.clientGame = clientGame;
        this.backgroundImage = new ImageIcon("Graphic/mapWithWall.png").getImage();
        this.generatorIcon = new ImageIcon("Graphic/Object/generator-broken.png").getImage();
        this.hookIcon = new ImageIcon("Graphic/Object/hook.png").getImage();
        System.out.println("Background image loaded: " + (backgroundImage != null));
        System.out.println("generator image loaded: " + (generatorIcon != null));
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 繪製背景圖，根據鏡頭的偏移量來顯示
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, -cameraOffsetX, -cameraOffsetY, 6000, 3600, this);
        }

        // for (int i = 0; i < clientGame.generators.length; i++) {
        //     g.drawImage(generatorIcon,clientGame.generators[i].getX(),clientGame.generators[i].getY(),120,60,this);
        // }
        // 繪製每一位玩家
        for (ClientPlayer clientPlayer : clientGame.clientPlayers) {
            if (clientPlayer != null && clientPlayer.getCurrentImage() != null) {
                ImageIcon playerIcon = clientPlayer.getCurrentImage();
                int x = clientPlayer.getX() - cameraOffsetX ; // 根據鏡頭偏移量調整 x 坐標
                int y = clientPlayer.getY() - cameraOffsetY ; // 根據鏡頭偏移量調整 y 坐標
                playerIcon.paintIcon(this, g, x, y); // 繪製玩家圖標
            }
        }
        // System.out.println("generator drawn");
        
        drawGenerator(g);
        drawHooks(g);
    }
    // 獨立的鉤子繪製方法
    private void drawHooks(Graphics g) {
        for (ClientHook clientHook : clientGame.Hook) {
            if(clientHook != null) {
                int x = clientHook.getX() - cameraOffsetX;
                int y = clientHook.getY() - cameraOffsetY;
                g.drawImage(hookIcon, x, y, 60, 120, this);  // 繪製鉤子圖標
            // System.out.println("Drawing hook " + clientHook.getId() + " at: (" + x + ", " + y + ")");
            }
        }
    }

    public ImageIcon applyBloodEffect(ImageIcon baseImage, ImageIcon bloodImage) {
        if (baseImage == null || bloodImage == null) {
            return baseImage; // 如果沒有圖片資源，返回原始圖片
        }

        Image base = baseImage.getImage();
        Image blood = bloodImage.getImage();

        // 建立一個新的 BufferedImage 來儲存合成結果
        BufferedImage combined = new BufferedImage(
            base.getWidth(null), 
            base.getHeight(null), 
            BufferedImage.TYPE_INT_ARGB
        );

        // 繪製合成圖片
        Graphics2D g = combined.createGraphics();
        g.drawImage(base, 0, 0, null); // 繪製玩家的基本圖片
        g.drawImage(blood, 0, 0, null); // 疊加血的圖片
        g.dispose();

        return new ImageIcon(combined);
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
        for (ClientGenerator clientGenerator : clientGame.generators) {
            if (clientGenerator != null) {
                // 獲取發電機的真實位置
                int x = clientGenerator.getX() - cameraOffsetX; // 根據鏡頭偏移量調整 x 坐標
                int y = clientGenerator.getY() - cameraOffsetY; // 根據鏡頭偏移量調整 y 坐標
                
                // 繪製發電機圖標
                g.drawImage(generatorIcon, x, y, 120, 60, this);
            }
        }
    }  
}
