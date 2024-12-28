import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class ClientGame{
    public ClientGame() {
        JFrame frame = new JFrame("迷途逃生");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        device.setFullScreenWindow(frame);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new BorderLayout());
        frame.add(layeredPane);

        JPanel leftPanel = new JPanel(new BorderLayout());
        layeredPane.add(leftPanel, BorderLayout.WEST);
        JPanel wall1 = new JPanel();
        wall1.setBackground(Color.CYAN);
        wall1.setPreferredSize(new Dimension(50, 770));
        JPanel door1 = new JPanel();
        door1.setBackground(Color.MAGENTA);
        door1.setPreferredSize(new Dimension(50, 100));
        leftPanel.add(wall1, BorderLayout.SOUTH); 
        leftPanel.add(door1, BorderLayout.NORTH);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        layeredPane.add(rightPanel, BorderLayout.EAST);
        JPanel wall2 = new JPanel();
        wall2.setBackground(Color.CYAN);
        wall2.setPreferredSize(new Dimension(50, 770));
        JPanel door2 = new JPanel();
        door2.setBackground(Color.MAGENTA);
        door2.setPreferredSize(new Dimension(50, 100));
        rightPanel.add(wall2, BorderLayout.NORTH); 
        rightPanel.add(door2, BorderLayout.SOUTH);
        
        JPanel ground = new JPanel(new GridLayout(7, 7));
        ground.setBackground(Color.RED);
        layeredPane.add(ground);
        
        JPanel dynamo = new JPanel();
        dynamo.setBackground(Color.YELLOW);
        dynamo.setPreferredSize(new Dimension(50, 50));                 

        Random rand = new Random();
        // 創建並隨機選擇偶數格位置
        int randomIndex = rand.nextInt(25); // 7x7 共 49 個格子，隨機選擇 0 至 48
        // 確保選擇的格子是偶數位置
        while (randomIndex % 2 != 0) {
            randomIndex = rand.nextInt(25);
        }
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                // 初始化每個小面板
                JPanel panel = new JPanel();
                panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                // 計算格子的索引
                int index = row * 7 + col;

                // 如果是隨機選中的偶數格，則在這個格子上放置 Dynamo
                if (index == randomIndex) {
                    panel.add(dynamo); // 把 Dynamo 放到這個格子裡
                }

                // 將每個小面板添加到 ground 面板
                ground.add(panel);
            }
        }



        frame.setVisible(true);
        // 退出按鍵事件
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
            return false;
        });

    }

}
