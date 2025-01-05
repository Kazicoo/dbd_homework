import Comm.TcpClient;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ClientGame {
    
    private TcpClient conn;
    private JFrame frame;
    private JLabel generatorLabel; // 用於顯示發電機數量
    private JLabel healthLabel1,healthLabel2,healthLabel3; // 用於顯示玩家血量
    private int generatorCount = 4; // 初始發電機數量
    private int healthcount = 2; // 初始玩家血量
    private int cameraOffsetX = 0;
    private int cameraOffsetY = 0;
    JPanel middlePanel;
    int playerTotal = 0;
    int killerId = 0;
    int clientId = 0;
    private final String[] chars = {"killer", "p1", "p2", "p3"};
    int idCount = 0;
    private final int idRole[] = new int[4];
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
        System.out.println(screenWidth+"|"+screenHeight);
        
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
        JLabel healthLabel1 = new JLabel();
        JLabel healthLabel2 = new JLabel();
        JLabel healthLabel3 = new JLabel();
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
    

    public JLabel createHealthLabel(String role, int healthcount, int x, int y) {
        JLabel healthLabel = new JLabel(role + " Health: " + healthcount);
        healthLabel.setFont(new Font("Arial", Font.BOLD, 18));
        healthLabel.setBounds(x, y, 200, 30); // 設定位置和大小
        healthLabel.setForeground(Color.RED); // 可自定義文字顏色
        return healthLabel;
    }
    public JLabel createGeneratorLabel(String role, int healthcount, int x, int y) {
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
                    if (initGeneratorTotal == 4 && playerTotal == 4) {
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
    
    public void updateCameraPosition(int playerX, int playerY) {
        // 獲取當前裝置的螢幕解析度
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
    
        // 計算偏移量
        cameraOffsetX = playerX - (screenWidth / 2);
        cameraOffsetY = playerY - (screenHeight / 2);
        
        //更新
        int NEWX = playerX + cameraOffsetX , NEWY = playerY + cameraOffsetY;
        int dx = NEWX - playerX;
        int dy = NEWY - playerY;
        cameraOffsetX -= dx;
        cameraOffsetY -= dy;
    
        // 限制鏡頭不要超過地圖範圍
        cameraOffsetX = Math.max(0, Math.min(cameraOffsetX, 6000 - screenWidth)); // 6000 是地圖的寬度
        cameraOffsetY = Math.max(0, Math.min(cameraOffsetY, 3600 - screenHeight)); // 3600 是地圖的高度
    
        // 更新遊戲面板的顯示範圍
        gamePanel.setCameraOffset(cameraOffsetX, cameraOffsetY);
    }
    

    private int initGeneratorTotal = 0; //確保waitgamestart()正確啟動
    private int generatorTotal = 0;
    public final ClientGenerator[] generators = new ClientGenerator[4];

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
            // 初始化發電機物件
            generators[generatorTotal] = new ClientGenerator(id);
            generators[generatorTotal].setRelativeLocation(x, y);
                    
            // 初始化按鈕
            // 載入圖片作為按鈕背景
            ImageIcon generatorIcon = new ImageIcon("Graphic/Generator-broken.png");
            JButton generatorButton = new JButton(generatorIcon);

            int imageWidth = generatorIcon.getIconWidth();
            int imageHeight = generatorIcon.getIconHeight();

            // 設定按鈕的位置和大小
            generatorButton.setBounds(generators[generatorTotal].getX(), generators[generatorTotal].getY(), imageWidth, imageHeight);
            generatorButton.setOpaque(false);     // 讓按鈕背景透明
            generatorButton.setContentAreaFilled(false); // 移除按鈕預設的背景
            generatorButton.setBorderPainted(false);     // 移除按鈕邊框
                    
            // 添加到面板
            gamePanel.add(generatorButton);
            gamePanel.revalidate();
            gamePanel.repaint();
                    
            // 添加互動邏輯
                    generatorButton.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                conn.send("Clicked;generator;" + generators[generatorTotal].getId());
                            }
                        }
                    });
                    
                    synchronized (this) {
                        generatorTotal++;
                        initGeneratorTotal++;
                        if (initGeneratorTotal == 4 && playerTotal == 4) {
                            notifyAll(); // 通知等待的線程
                        }
                    }
                    if (generatorTotal == generators.length) {
                        System.out.println("Maximum generators reached.");
                    }
                    System.out.println("Generator initialized: ID " + id + " at (" + x + ", " + y + ")");
                    
                    
        } catch (NumberFormatException e) {
            System.out.println("Error parsing coordinates or ID: " + e.getMessage());
        }
    }

    int playerCount = 0;
    ClientPlayer[] clientPlayers = new ClientPlayer[4];
    
    
    public void initPlayer(String message, int ClientId) {
        String[] parts = message.split(";");
        int x = Integer.parseInt(parts[2]);
        int y = Integer.parseInt(parts[3]);
        int id = Integer.parseInt(parts[4]);
    
    
        // 創建新的 ClientPlayer 並初始化
        clientPlayers[playerCount] = new ClientPlayer(id);
        clientPlayers[playerCount].setRelativeLocation(x, y);
        clientPlayers[playerCount].setRole(chars[playerCount]);
        clientPlayers[playerCount].setIsSelf(ClientId == id);

        if (playerCount >= 0 && playerCount < clientPlayers.length && clientPlayers[playerCount] != null) {
            if (clientPlayers[playerCount].getRole() != null) {
                playerIcon();
            }
        }
        

        playerCount++;
    
        // 更新playerTotal並進行同步通知
        playerTotal++;
        System.out.println("Player initialized: ID: " + ClientId + " at (" + x + ", " + y + ")");
    
        if (playerCount >= clientPlayers.length) {
            System.out.println("Maximum number of players reached.");
            return;
        }
        // 如果已經達到4個玩家並且生成器數量也是4，則通知所有等待的線程
        if (playerTotal == initGeneratorTotal) {
            notifyAll();
        }
    }

    public void updatePlayerPosition(String message) {
        String[] parts = message.split(";");
        try {
            int x = Integer.parseInt(parts[2]); // 新的 x 座標
            int y = Integer.parseInt(parts[3]); // 新的 y 座標
            int id = Integer.parseInt(parts[4]); // 玩家
    
            // 檢查是否更新玩家或殺手位置
            synchronized (this) {
                for (ClientPlayer clientPlayer : clientPlayers) {
                    if (clientPlayer != null && clientPlayer.getId() == id) {
                        clientPlayer.setRelativeLocation(x, y);
                    }
                }
                // 重繪遊戲畫面
                gamePanel.repaint();
            }
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

            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
                
                // 檢查是否為空白鍵
                if (key == KeyEvent.VK_SPACE) {
                    for (int i = 0; i < clientPlayers.length ; i++) {
                        if (clientPlayers[i] != null 
                        && "killer".equals(clientPlayers[i].getRole()) 
                        && clientPlayers[i].getIsSelf()== true) {
                        conn.send("Attack");
                        
                        }
                }
                } 
            }
            
        });

        frame.setFocusable(true);
        frame.requestFocusInWindow();

    }
    
    public void updateHealth(String message) {
        String[] parts = message.split(";");
        
        if (parts.length < 4 || !"updateGameObject".equals(parts[0]) || !"health".equals(parts[1])) {
            System.out.println("無效的血量更新訊息格式。");
            return;
        }
    
        String healthValue = parts[2];
        String role = parts[3];
    
        // 解析血量值
        int health = Integer.parseInt(healthValue.split(":")[1]);
        String status = "";
    
        // 根據血量設定狀態
        switch (health) {
            case 2:
                status = "(健康)";
                break;
            case 1:
                status = "(受傷)";
                break;
            case 0:
                status = "(倒地)";
                break;
           
        }
    
        // 更新對應角色的血量和狀態
        switch (role) {
            case "p1":
                healthLabel1.setText("p1 Health: " + health + " " + status);
                break;
            case "p2":
                healthLabel2.setText("p2 Health: " + health + " " + status);
                break;
            case "p3":
                healthLabel3.setText("p3 Health: " + health + " " + status);
                break;
           
        }
    
        // 刷新面板以顯示更新內容
        middlePanel.revalidate();
        middlePanel.repaint();
    } 

    // 收到 initGameObject 時呼叫此方法
    // updateGameObject;health;<hp: 0, 1, 2>;<human: p1, p2, p3>

    public void initHealthStatus(String message) {
        String[] parts = message.split(";");
        String healthValue = parts[2];
        String role = parts[3];
        
        // 解析血量值
        int health = Integer.parseInt(healthValue);
        
        String status;
        
        // 根據血量設定狀態
        switch (health) {
            case 2 -> status = "(健康)";
            case 1 -> status = "(受傷)";
            case 0 -> status = "(倒地)";
           
        }
    }
    // 定義角色列表
    //     String[] chars = {"killer", "p1", "p2", "p3"};

    //     // 根據角色初始化狀態欄
    //     switch (role) {
    //         case "p1":
    //             healthLabel1.setText((role + health + " ") + " " + status);
    //             healthLabel1.setBounds(10 , 5 , 200 ,30);
    //             gamePanel.add(healthLabel1);                     
    //             break;
    //         case "p2":
    //             healthLabel2.setText((role + health + " ") + " " + status);
    //             healthLabel2.setBounds(10 , 40 , 200 ,30);
    //             gamePanel.add(healthLabel2);                      
    //             break;
    //         case "p3":
    //             healthLabel3.setText((role + health + " ") + " " + status);
    //             healthLabel3.setBounds(10 , 75 , 200 ,30);                        
    //             gamePanel.add(healthLabel3);
    //             break;
    //         default:
    //             System.out.println("未知的角色: " + role);
    //             break;
    //     }

    //     // 更新面板以顯示狀態
    //     gamePanel.revalidate();
    //     gamePanel.repaint();
    // }
    
    public void playerIcon() {
        ImageIcon p1Icon = new ImageIcon("Graphic/Human/p1/p1-front.png");
        ImageIcon p2Icon = new ImageIcon("Graphic/Human/p2/p2-front.png");
        ImageIcon p3Icon = new ImageIcon("Graphic/Human/p3/p3-front.png");
        ImageIcon killerIcon = new ImageIcon("Graphic/Killer/killer-left.png");
        for (ClientPlayer clientPlayer1 : clientPlayers) {
            if (clientPlayer1 != null && clientPlayer1.getRole().equals("p1")) {
                clientPlayer1.setIcon(p1Icon);
            }
            if (clientPlayer1 != null && clientPlayer1.getRole().equals("p2")) {
                clientPlayer1.setIcon(p2Icon);
            }
            if (clientPlayer1 != null && clientPlayer1.getRole().equals("p3")) {
                clientPlayer1.setIcon(p3Icon);
            }
            if (clientPlayer1 != null && clientPlayer1.getRole().equals("killer")) {
                clientPlayer1.setIcon(killerIcon);
            } 
        }
    }          
}
    
    

