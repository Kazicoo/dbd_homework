import Comm.TcpClient;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class ClientGame {
    private TcpClient conn;
    private JFrame frame;
    private JLabel generatorLabel; // 用於顯示發電機數量
    private JLabel healthLabel1,healthLabel2,healthLabel3; // 用於顯示玩家血量
    private int generatorCount = 4; // 初始發電機數量
    private int healthcount = 2; // 初始玩家血量
    private String role;
    JPanel middlePanel;

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
    
        // 獲取螢幕解析度
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
    
        // 計算中部面板的高度（不縮放地圖）
        int topBarHeight = screenHeight / 20; // 頂部狀態欄高度
        int bottomBarHeight = screenHeight / 20; // 底部狀態欄高度
    
        // 上部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(screenWidth, topBarHeight));
        topPanel.setBackground(Color.GRAY); // 可自定義背景顏色
        topPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    
        // 中部面板
        middlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 假設你有背景圖片或地圖圖片，可以在這裡繪製
                Image backgroundImage = new ImageIcon("Graphic/GuideLine.png").getImage();
                g.drawImage(backgroundImage, 0, 0, 6000, 3600, this);
            }
        };
        middlePanel.setLayout(null); // 使用絕對佈局
        middlePanel.setPreferredSize(new Dimension(6000, 3600));
        middlePanel.setBackground(Color.WHITE); // 可自定義背景顏色
        middlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        // 新增 healthLabel
        healthLabel1 = createHealthLabel("p1", healthcount, 10, 5);
        healthLabel2 = createHealthLabel("p2", healthcount, 10, 40);
        healthLabel3 = createHealthLabel("p3", healthcount, 10, 75);
        middlePanel.add(healthLabel1);
        middlePanel.add(healthLabel2);
        middlePanel.add(healthLabel3);
    
        // 下部面板
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(screenWidth, bottomBarHeight));
        bottomPanel.setBackground(Color.GRAY); // 可自定義背景顏色
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    

        // 發電機數量顯示 (右上角)
        generatorLabel = createGeneratorLabel("Generator", generatorCount, screenWidth - 200, 5);
        topPanel.add(generatorLabel, BorderLayout.EAST);
        // 添加面板到框架
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(middlePanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        
        // 顯示框架
        frame.setVisible(true);
        
    }


    private JLabel createHealthLabel(String role, int healthcount, int x, int y) {
        JLabel healthLabel = new JLabel(role + " Health: " + healthcount);
        healthLabel.setFont(new Font("Arial", Font.BOLD, 18));
        healthLabel.setBounds(x, y, 200, 30); // 設定位置和大小
        healthLabel.setForeground(Color.RED); // 可自定義文字顏色
        return healthLabel;
    }
    private JLabel createGeneratorLabel(String role, int healthcount, int x, int y) {
        JLabel generatorLabel = new JLabel(role + " Health: " + healthcount);
        generatorLabel.setFont(new Font("Arial", Font.BOLD, 18));
        generatorLabel.setBounds(x, y, 200, 30); // 設定位置和大小
        generatorLabel.setForeground(Color.RED); // 可自定義文字顏色
        return generatorLabel;
    }
    
    //正確waitGameStart封包傳送邏輯
    // public void waitGameStart() {
    //     synchronized (this) {
    //         while (generatorTotal != 4 || playerTotal != 4) {
    //             try {
    //                 wait();
    //             } catch (InterruptedException e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //     }
        
    //     conn.send("startGame");
    // 


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
        
        try {
            int id = Integer.parseInt(parts[4]);
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            
            for (int i = 0; i < generators.length; i++) {
                if (generators[i] == null) {
                    // 初始化發電機物件
                    generators[i] = new ClientGenerator(id);
                    generators[i].setRelativeLocation(9 * (i+1) * x, 10 * (i+1) * y);
                    
                    // 初始化按鈕
                    // 載入圖片作為按鈕背景
                    ImageIcon generatorIcon = new ImageIcon("Graphic/generator.PNG");
                    JButton generatorButton = new JButton(generatorIcon);

                    int imageWidth = generatorIcon.getIconWidth();
                    int imageHeight = generatorIcon.getIconHeight();

                    // 設定按鈕的位置和大小
                    generatorButton.setBounds(generators[i].getX(), generators[i].getY(), imageWidth, imageHeight);
                    generatorButton.setOpaque(false);     // 讓按鈕背景透明
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
                    
                    System.out.println("Generator initialized: ID " + id + " at (" + generators[i].getX() + ", " + generators[i].getY() + ")");
                    
                    if (generatorTotal == generators.length) {
                        System.out.println("Maximum generators reached.");
                    }
                    
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
    private final ClientHuman[] players = new ClientHuman[3];
    
    public void initHuman(String message) {
        
        String parts[];
        
        try {
            parts = message.split(";");
            if (parts.length < 5 || !"player".equals(parts[1])) {
                throw new IllegalArgumentException("Invalid player message format.");
            }
        } catch (Exception e) {
            System.out.println("Error parsing player message: " + e.getMessage());
            return;
        }

        if (playerTotal >= players.length) {
            System.out.println("Maximum players reached.");
            return;
        }

        try {
            int id = Integer.parseInt(parts[4]);
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);

            for (int i = 0; i < players.length; i++) {
                if (players[i] == null) {
                players[i] = new ClientHuman(id);
                players[i].setRelativeLocation(x, y);
            
                ImageIcon playerIcon = new ImageIcon("");
                players[i].setIcon(playerIcon);

                playerTotal++;
                System.out.println("Human initialized: ID " + id + " at (" + x + ", " + y + ")");
                break;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Error parsing coordinates or ID: " + e.getMessage());
        }
    }
    
    
    public ClientKiller clientKiller;

    public void initKiller(String message) {
        String[] parts;
        
        try {
            parts = message.split(";");
            if (parts.length < 5 || !"player".equals(parts[1])) {
                throw new IllegalArgumentException("Invalid player message format.");
            }
        } catch (Exception e) {
            System.out.println("Error parsing player message: " + e.getMessage());
            return;
        } 

        try {
            int id = Integer.parseInt(parts[4]);
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            
            clientKiller = new ClientKiller(id);
            clientKiller.setRelativeLocation(x, y);

            ImageIcon killerIcon = new ImageIcon("");
            clientKiller.setIcon(killerIcon);

            playerTotal++;
            System.out.println("killer initialized: ID " + id + " at (" + x + ", " + y + ")");
        } catch (NumberFormatException e) {
            System.out.println("Error parsing coordinates or ID: " + e.getMessage());
        }
    }
    
    public void draw(Graphics g, JPanel panel) {
        if(clientKiller != null && clientKiller.getIcon() != null){
            ImageIcon killerIcon = clientKiller.getIcon();
            int x = clientKiller.getX();
            int y = clientKiller.getY();
            killerIcon.paintIcon(panel, g, x, y);
        }
        for (int i = 0; i < players.length; i++)
            if (players[i] != null && players[i].getIcon() != null) {
                ImageIcon playerIcon = players[i].getIcon();
                int x = players[i].getX();
                int y = players[i].getY();
                playerIcon.paintIcon(panel, g, x, y);
        }
    }



    private int count = 0;

// 收到 initGameObject 時呼叫此方法
    public void initstatusbar(String[] part, int id, JPanel panel) {
        String[] chars = {"killer", "p1", "p2", "p3"};

        if (part[4].equals("" + id)) {
            role = chars[count];
            int yPosition = 5 + count * 35; // 動態調整標籤位置，避免重疊

            JLabel healthLabel = createHealthLabel(role, healthcount, 10, yPosition);
            JLabel generatorLabel = createGeneratorLabel(role, generatorCount, 10, yPosition);
            panel.add(healthLabel); // 將標籤添加到指定面板
            panel.add(generatorLabel);
            panel.revalidate();
            panel.repaint();

            count++;
        }
    }



}
    
    
    

