import Comm.TcpClient;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientGame {
    private TcpClient conn;
    private JFrame frame;
    private JLayeredPane layeredPane;
    private  ClientPlayer clientPlayers[];
    private Image generatorImage;
    private JPanel middlePanel;

    public ClientGame(TcpClient conn) {
        this.conn = conn;
        initGame();
        waitGameStart();
    }

    public void initGame() {
        frame = new JFrame("迷途逃生");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setLayout(new BorderLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        // 上部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(width, height / 20));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
         

        // 中部面板
        middlePanel = new JPanel();
        middlePanel.setLayout(null);  // 設定為絕對佈局
        middlePanel.setPreferredSize(new Dimension(width, 2 * height / 3));
        middlePanel.setBackground(Color.WHITE);
        middlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        

        // 下部面板
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(width, height / 20));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // 添加面板到框架
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(middlePanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void waitGameStart() {
        synchronized (this) {
            while (generatorTotal == 4 && playerTotal == 4) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        conn.send("startGame");
    }
    // public void drawPlayer() {
    //     ImageIcon playerImage = new ImageIcon("");
    // }

    private int generatorTotal = 0;

// 確保 generators 陣列只初始化一次
private final ClientGenerator[] generators = new ClientGenerator[4];

public void initGenerator(String message) {
    String[] parts;
    try {
        parts = message.split(";");
        if (parts.length < 5 || !"generator".equals(parts[1])) {
            throw new IllegalArgumentException("Invalid generator message format.");
        }
    } catch (Exception e) {
        System.out.println("Error parsing generator message: " + e.getMessage());
        return;
    }

    if (generatorTotal >= generators.length) {
        System.out.println("Maximum generators reached.");
        return;
    }

    try {
        int id = Integer.parseInt(parts[4]);
        int x = Integer.parseInt(parts[2]);
        int y = Integer.parseInt(parts[3]);

        for (int i = 0; i < generators.length; i++) {
            if (generators[i] == null) {
                // 初始化發電機物件
                generators[i] = new ClientGenerator(id);
                generators[i].setRelativeLocation(x, y);

                    // 初始化按鈕
                    // 載入圖片作為按鈕背景
                ImageIcon generatorIcon = new ImageIcon("Graphic/generator.PNG");
                JButton generatorButton = new JButton(generatorIcon);

                // 設定按鈕的位置和大小
                generatorButton.setBounds(x, y, 100, 50);
                generatorButton.setOpaque(false);            // 讓按鈕背景透明
                generatorButton.setContentAreaFilled(false); // 移除按鈕預設的背景
                generatorButton.setBorderPainted(false);     // 移除按鈕邊框

                // 添加到面板
                middlePanel.add(generatorButton);
                middlePanel.revalidate();
                middlePanel.repaint();

                    // 添加互動邏輯
                    int index = i;
                    generatorButton.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                conn.send("Player clicked generator ID: " + generators[index].getId());
                            }
                        }
                    });

                generatorTotal++;
                System.out.println("Generator initialized: ID " + id + " at (" + x + ", " + y + ")");
                break;
            }
        }
    } catch (NumberFormatException e) {
        System.out.println("Error parsing coordinates or ID: " + e.getMessage());
    }
}

    
    public void updateGenerator() {

    }

    int playerTotal = 0;
    public void initPlayer(String message) {
        
        playerTotal++;
    }
    

    // update;health;1
    // update;generator;fixed;2
    // update;totalGenerator;3
    public void updatehealth(int totalhealth) {
        JLabel healthLabel = new JLabel("血量: "+totalhealth);
        healthLabel.add(healthLabel, BorderLayout.WEST);
        healthLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        if(totalhealth == 2) {

        }if(totalhealth == 1) {
            
        }
        else if (totalhealth == 0) {
            JOptionPane.showMessageDialog(frame, "You are dead!");
            frame.setFont(new Font("Serif", Font.PLAIN, 20));
            
        }
    }
    private final String[] chars = {"killer", "p1", "p2", "p3"};
    private int count = 0;
    // 收到initGameObject時 count+1
    public void initGameObject(String[] part, int id) {
    count++;
    // 每一次都判斷 part[4].equals(""+id)
    if (part[4].equals("" + id)) {
        // 成立的話 就讓role = chars[count];
        String role = chars[count];
        // 這裡可以加入其他處理邏輯
        }
    }
}
