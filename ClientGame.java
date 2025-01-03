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
    GamePanel gamePanel = new GamePanel(this);

    
    public ClientGame(TcpClient conn) {
        this.conn = conn;
        
        initGame();
        initKeyListener();
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
        middlePanel = new JPanel();
        middlePanel.setLayout(null); // 使用絕對佈局
        middlePanel.setPreferredSize(new Dimension(20,80 ));
        middlePanel.setBackground(Color.WHITE); // 可自定義背景顏色
        middlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        middlePanel.setOpaque(false);
        // 新增 healthLabel
        //將healthbar移動到gamepanel
        healthLabel1 = createHealthLabel("p1", healthcount, 10, 5);
        healthLabel2 = createHealthLabel("p2", healthcount, 10, 40);
        healthLabel3 = createHealthLabel("p3", healthcount, 10, 75);
        gamePanel.add(healthLabel1);
        gamePanel.add(healthLabel2);
        gamePanel.add(healthLabel3);
    
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
        //將healthbar移動到gamepanel
        frame.add(middlePanel);

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(gamePanel, BorderLayout.CENTER);
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
    
    public void waitGameStart() {
        new Thread(() -> {
            while (true) {
                synchronized (this) {
                    if (generatorTotal >= 4 && PlayerCount () >= 4) {
                        try {
                            conn.send("startGame");
                            System.out.println("Game started!");
                            break; // 成功發送後跳出迴圈
                        } catch (Exception e) {
                            System.out.println("Failed to send startGame: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Waiting for generators and players to be initialized...");
                    }
                }
                try {
                    Thread.sleep(500); // 避免過度佔用 CPU，增加延遲
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted: " + e.getMessage());
                }
            }
        }).start();
    }
    
    
    

    // public void waitGameStart() {
    //     synchronized (this) {
    //         while (generatorTotal == 4 && playerTotal == 4) {
    //             try {
    //                 wait();
    //             } catch (InterruptedException e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //     }
        
    //     conn.send("startGame");
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
                    generators[i].setRelativeLocation(x, y);
                    
                    // 初始化按鈕
                    // 載入圖片作為按鈕背景
                    ImageIcon generatorIcon = new ImageIcon("Graphic/Generator360180.png");
                    JButton generatorButton = new JButton(generatorIcon);

                    int imageWidth = generatorIcon.getIconWidth();
                    int imageHeight = generatorIcon.getIconHeight();

                    // 設定按鈕的位置和大小
                    generatorButton.setBounds(generators[i].getX(), generators[i].getY(), imageWidth, imageHeight);
                    generatorButton.setOpaque(false);     // 讓按鈕背景透明
                    generatorButton.setContentAreaFilled(false); // 移除按鈕預設的背景
                    generatorButton.setBorderPainted(false);     // 移除按鈕邊框
                    
                    // 添加到面板
                    gamePanel.add(generatorButton);
                    gamePanel.revalidate();
                    gamePanel.repaint();
                    
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

                    synchronized (this) {
                        generatorTotal++;
                        if (generatorTotal == 4 && playerTotal == 4) {
                            notifyAll(); // 通知等待的線程
                        }
                    }
                    
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
    int humanTotal = 0;
    int killerTotal = 0;
    int killerID = 0;
    public final ClientHuman[] players = new ClientHuman[3];
    
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

        if (playerTotal == players.length) {
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
                
                
                if (players[i].getId() == 1) {
                    players[i].setIcon(new ImageIcon("Graphic/p1.png"));
                }
                if (players[i].getId() == 2) {
                    players[i].setIcon(new ImageIcon("Graphic/p2.png"));
                }
                if (players[i].getId() == 3) {
                    players[i].setIcon(new ImageIcon("Graphic/p3.png"));
                }
                synchronized (this) {
                    humanTotal++;
                    if (generatorTotal == 4 && playerTotal == 4) {
                        notifyAll();
                    }
                }
                
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
    
                killerID = id;
                clientKiller = new ClientKiller(id);
                clientKiller.setRelativeLocation(x, y);
    
                // 設置圖片
                ImageIcon killerIcon = new ImageIcon("Graphic/p0.png");
                clientKiller.setIcon(killerIcon);
    
                synchronized (this) {
                    killerTotal++;
                    if (generatorTotal == 4 && playerTotal == 4) {
                        notifyAll();
                    }
                }
                
                System.out.println("Killer initialized: ID " + id + " at (" + x + ", " + y + ")");

        } catch (NumberFormatException e) {
            System.out.println("Error parsing coordinates or ID: " + e.getMessage());
        }
    }
    
    public int PlayerCount () {
        playerTotal = killerTotal + humanTotal;
        return playerTotal;
    }

    public void updatePlayerPosition(String message) {
        String[] parts;
        parts = message.split(";");
           
        try {
            int x = Integer.parseInt(parts[2]); // 新的 x 座標
            int y = Integer.parseInt(parts[3]); // 新的 y 座標
            int id = Integer.parseInt(parts[4]); // 玩家或殺手 ID
        
            // 檢查是否更新玩家或殺手位置
            synchronized (this) {
                if (id == killerID && clientKiller != null) {
                    clientKiller.setRelativeLocation(x, y);
                } else {
                    for (ClientHuman player : players) {
                        if (player != null && player.getId() == id) {
                            player.setRelativeLocation(x, y);
                            break;
                        }
                    }
                }
            }
        
            // 重繪遊戲畫面
            gamePanel.repaint();
            if(id == killerID) {
                System.out.println("killer Position updated: ID " + id + " to (" + x + ", " + y + ")");
            } else System.out.println("player Position updated: ID " + id + " to (" + x + ", " + y + ")");
        } catch (NumberFormatException e) {
            System.out.println("Error parsing coordinates or ID: " + e.getMessage());
        }
    }        
    
    public void initKeyListener() {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char key = Character.toLowerCase(e.getKeyChar());
                
                switch (key) {
                    case 'w':
                        conn.send("KeyDown;W");
                        break;
                    case 'a':
                        conn.send("KeyDown;A");
                        break;
                    case 's':
                        conn.send("KeyDown;S");
                        break;
                    case 'd':
                        conn.send("KeyDown;D");
                        break;
                    default:
                        System.out.println("Unhandled key press: " + key);
                }
            }
    
            @Override
            public void keyReleased(KeyEvent e) {
                char key = Character.toLowerCase(e.getKeyChar());
                
                switch (key) {
                    case 'w':
                        conn.send("KeyUp;W");
                        break;
                    case 'a':
                        conn.send("KeyUp;A");
                        break;
                    case 's':
                        conn.send("KeyUp;S");
                        break;
                    case 'd':
                        conn.send("KeyUp;D");
                        break;
                    default:
                        System.out.println("Unhandled key release: " + key);
                }
            }
        });
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
    
    
    

