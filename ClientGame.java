import Comm.TcpClient;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ClientGame {
    private TcpClient conn;
    private JFrame frame;
    private JLabel generatorLabel; // 用於顯示發電機數量
    private JLabel humanLabel1,humanLabel2,humanLabel3, healthLabel4; // 用於顯示玩家血量
    private int generatorCount = 4; // 初始發電機數量
    private int healthcount = 2; // 初始玩家血量
    private String status;
    int playerCount = 0;
    ClientPlayer[] clientPlayers = new ClientPlayer[4];
    JPanel middlePanel;
    int playerTotal = 0;
    private final String[] chars = {"killer", "p1", "p2", "p3"};
    private GamePanel gamePanel;

    public ClientGame(TcpClient conn) {
        this.conn = conn;
        initGame();
        initKeyListener();
        waitGameStart();
    }


    public void initGame() { 
        this.gamePanel = new GamePanel(this);
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
        humanLabel1 = new JLabel();
        humanLabel2 = new JLabel();
        humanLabel3 = new JLabel();
        gamePanel.add(humanLabel1);
        gamePanel.add(humanLabel2);
        gamePanel.add(humanLabel3);
    
    

        // 發電機數量顯示 (右上角)
        generatorLabel = createGeneratorLabel("Generator", generatorCount, screenWidth - 200, 5);
        topPanel.add(generatorLabel, BorderLayout.EAST);
        // 添加面板到框架
        frame.add(topPanel, BorderLayout.NORTH);
        //將healthbar移動到gamepanel
        frame.add(middlePanel);
        frame.add(gamePanel, BorderLayout.CENTER);
        // 顯示框架
        frame.setVisible(true);
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
        int cameraOffsetX = playerX - (screenWidth / 2 );
        int cameraOffsetY = playerY - (screenHeight / 2 );

        // 限制鏡頭不要超過地圖範圍
        cameraOffsetX = Math.max(0, Math.min(cameraOffsetX, 6000 - screenWidth)); // 6000 是地圖的寬度
        cameraOffsetY = Math.max(0, Math.min(cameraOffsetY, 3600 - screenHeight)); // 3600 是地圖的高度
    
        // 更新遊戲面板的顯示範圍
        gamePanel.setCameraOffset(cameraOffsetX, cameraOffsetY);
    }

    private int initGeneratorTotal = 0; //確保waitgamestart正確啟動
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
            ImageIcon generatorIcon = new ImageIcon("Graphic/Object/generator-broken.png");
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

    
    
    private int hookTotal = 0;
    public final ClientHook[] Hook = new ClientHook[9];
    
    public void initHook(String message) {
        String[] parts;
        try {
            // 解析封包
            parts = message.split(";");
            if (parts.length != 5 || !"hook".equals(parts[1])) {
                throw new IllegalArgumentException("Invalid hook message format.");
            }
        } catch (Exception e) {
            System.out.println("Error parsing hook message: " + e.getMessage());
            return;
        }
    
        try {
            // 提取座標和 ID
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            int id = Integer.parseInt(parts[4]);
    
            if (id < 0 || id > 8) {
                throw new IllegalArgumentException("Invalid ID. Must be '0' to '8'.");
            }
    
            // 初始化物件
            Hook[id] = new ClientHook(id);
            Hook[id].setRelativeLocation(x, y);
    
            ImageIcon hookIcon = new ImageIcon("Graphic/Object/hook.png");
            if (hookIcon.getIconWidth() == -1) {
                System.out.println("Error loading image: " + hookIcon);
                return; // 圖片加載失敗，退出方法
            }
    
            JButton hookButton = new JButton(hookIcon);
    
            int imageWidth = hookIcon.getIconWidth();
            int imageHeight = hookIcon.getIconHeight();
    
            // 設定按鈕位置和大小
            hookButton.setBounds(x, y, imageWidth, imageHeight);
            hookButton.setOpaque(false);
            hookButton.setContentAreaFilled(false);
            hookButton.setBorderPainted(false);
    
            // 添加按鈕到 JPanel
            gamePanel.add(hookButton);  // 確保gamePanel已正確初始化
            gamePanel.revalidate();
            gamePanel.repaint();
    
            // 添加互動邏輯
            hookButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        System.out.println("Hook clicked: ID " + Hook[id].getId());
                        // 可選：發送封包邏輯
                        // conn.send("Clicked;hook;" + Hook[id].getId());
                    }
                }
            });
    
            synchronized (this) {
                if (hookTotal == 9) {
                    System.out.println("Maximum hooks reached.");
                    return;
                }
                hookTotal++; // 自增鉤子數量
                System.out.println("Hook initialized: ID " + id + " at (" + x + ", " + y + ")");
            }
    
        } catch (NumberFormatException e) {
            System.out.println("Error parsing coordinates or ID: " + e.getMessage());
        }
    }
    
    
        
    
    
    public void initPlayer(String message, int clientId) {
        String[] parts = message.split(";");
        int x = Integer.parseInt(parts[2]);
        int y = Integer.parseInt(parts[3]);
        int id = Integer.parseInt(parts[4]);
    
    
        // 創建新的 ClientPlayer 並初始化
        clientPlayers[playerCount] = new ClientPlayer(id);
        clientPlayers[playerCount].setRelativeLocation(x, y);
        clientPlayers[playerCount].setRole(chars[playerCount]);
        clientPlayers[playerCount].setIsSelf(clientId == id);
        clientPlayers[playerCount].initImage();

        if (clientId == id) {
            updateCameraPosition(x, y);
            gamePanel.repaint();
        }
        System.out.println(clientPlayers[playerCount].getRole());
        playerCount++;
        
        
        // 更新playerTotal並進行同步通知
        playerTotal++;
        System.out.println("Player initialized: ID: " + id + " at (" + x + ", " + y + ")");
    
        if (playerCount >= clientPlayers.length) {
            System.out.println("Maximum number of players reached.");
            return;
        }
        // 如果已經達到4個玩家並且生成器數量也是4，則通知所有等待的線程
        if (playerTotal == initGeneratorTotal) {
            notifyAll();
        }
    }

    
    public void updatePlayerPosition(String message, int clientId) {
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
                        if (clientId == id) {
                            updateCameraPosition(x, y);
                        }
                    }
                }
                // clientPlayer.moveIcon(x,y);
                // 重繪遊戲畫面
                gamePanel.repaint();
            }
        } catch (NumberFormatException e) {
            System.out.println("Error parsing coordinates or ID: " + e.getMessage());
        }
    }

    private boolean isMovingUp = false; 
    private boolean isMovingLeft = false;
    private boolean isMovingDown = false; 
    private boolean isMovingRight = false;
    
    public void initKeyListener() {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char key = Character.toUpperCase(e.getKeyChar()); // 將小寫字母轉換為大寫
    
                // 確認玩家是當前玩家
                for (ClientPlayer player : clientPlayers) {
                    if (player != null && player.getIsSelf()) {
                        // 使用傳入的方向來更新玩家的移動
                        switch (key) {
                            case 'W' -> {
                                // 向上移動
                                player.updateMovement("W");
                                conn.send("KeyDown;W");
                                conn.send("aminated;KeyDown;W");
                                isMovingUp = true;
                            }
                            case 'A' -> {
                                // 向左移動
                                player.updateMovement("A");
                                conn.send("KeyDown;A");
                                conn.send("aminated;KeyDown;A");
                                isMovingLeft = true;
                            }
                            case 'S' -> {
                                // 向下移動
                                player.updateMovement("S");
                                conn.send("KeyDown;S");
                                conn.send("aminated;KeyDown;S");
                                isMovingDown = true;
                            }
                            case 'D' -> {
                                // 向右移動
                                player.updateMovement("D");
                                conn.send("KeyDown;D");
                                conn.send("aminated;KeyDown;D");
                                isMovingRight = true;
                            }
                            default -> System.out.println("Unhandled key press: " + key);
                        }
                    }
                }
            }
    
            @Override
            public void keyReleased(KeyEvent e) {
                char key = Character.toUpperCase(e.getKeyChar());
    
                for (ClientPlayer player : clientPlayers) {
                    if (player != null && player.getIsSelf()) {
                        // 當按鍵被釋放時更新玩家狀態
                        switch (key) {
                            case 'W' -> {
                                conn.send("KeyUp;W");
                                conn.send("animated;KeyUp;W");
                                isMovingUp = false;
                                if (isMovingDown) { 
                                    player.updateMovement("S");
                                } else if (isMovingRight) { 
                                    player.updateMovement("D"); 
                                } else if (isMovingLeft) { 
                                    player.updateMovement("A"); 
                                }
                            }
                            case 'A' -> {
                                conn.send("KeyUp;A");
                                conn.send("animated;KeyUp;A");
                                isMovingLeft = false;
                                if (isMovingRight) { 
                                    player.updateMovement("D");
                                } else if (isMovingUp) { 
                                    player.updateMovement("W");
                                } else if (isMovingDown) { 
                                    player.updateMovement("S");
                                }
                            }
                            case 'S' -> {
                                conn.send("KeyUp;S");
                                conn.send("animated;KeyUp;S");
                                isMovingDown = false;
                                if (isMovingUp) {
                                    player.updateMovement("W");
                                } else if (isMovingRight) {
                                    player.updateMovement("D");
                                } else if (isMovingLeft) {
                                    player.updateMovement("A");
                                }
                            }
                            case 'D' -> {
                                conn.send("KeyUp;D");
                                conn.send("animated;KeyUp;D");
                                isMovingRight = false;
                                if (isMovingLeft) {
                                    player.updateMovement("A");
                                } else if (isMovingUp) {
                                    player.updateMovement("W"); 
                                } else if (isMovingDown) {
                                    player.updateMovement("S");
                                }
                            }
                            default -> System.out.println("Unhandled key release: " + key);
                        }

                        if (!isMovingUp && !isMovingLeft && !isMovingDown && !isMovingRight) {
                            player.updateMovement("");
                            conn.send("animated;\"\"");
                            gamePanel.repaint();
                        }
                    }
                }
            }
    
            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
    
                // 檢查是否為空白鍵
                if (key == KeyEvent.VK_SPACE) {
                    for (ClientPlayer player : clientPlayers) {
                        if (player != null && "killer".equals(player.getRole()) && player.getIsSelf()) {
                            conn.send("attack");
                            // conn.send("animated;attack")
                        }
                    }
                }
            }
    
        });
    
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }
    
    
    public void moveAnimation(String message) {
        String[] parts = message.split(";");
        int id = Integer.parseInt(parts[2]);
        String direction = parts[1];
        for (ClientPlayer clientPlayer : clientPlayers) {
            if (clientPlayer != null && clientPlayer.getHp() == 2 && id == clientPlayer.getId()) {
                clientPlayer.updateMovement(direction);
            }
        }

    }
    

    
    public void attackFacing(String message) {
        String[] parts = message.split(";");
        for (ClientPlayer clientPlayer : clientPlayers) {
            if (clientPlayer != null && clientPlayer.getRole().equals("killer")) {
                clientPlayer.setAction(parts[2]);
            }
        }
        gamePanel.repaint();
    }
    // 收到 initGameObject 時呼叫此方法
    // updateGameObject;health;<hp: 0, 1, 2>;<human: p1, p2, p3>
    public void HealthStatus(String message) {
        String[] parts = message.split(";");
        int hp = Integer.parseInt(parts[2]); // 解析血量
        String role = parts[3]; // 解析角色
        Font largeFont = new Font("微軟正黑體", Font.BOLD, 20);
    
        // 用於動態分配 Label 的指標
        int labelIndex = 0;
    
        // 遍歷所有玩家
        for (ClientPlayer clientPlayer : clientPlayers) {
            if (clientPlayer == null) {
                continue; // 跳過空的玩家
            }
    
            // 跳過殺手
            if ("killer".equals(clientPlayer.getRole())) {
                continue;
            }
    
            // 找到匹配的角色，更新其血量
            if (clientPlayer.getRole().equals(role)) {
                clientPlayer.setHp(hp);
            }
    
            // 動態分配到對應的 Label
            JLabel currentLabel = switch (labelIndex) {
                case 0 -> humanLabel1;
                case 1 -> humanLabel2;
                case 2 -> humanLabel3;
                default -> null;
            };
    
            if (currentLabel != null) {
                currentLabel.setText(clientPlayer.getRole() + "  血量： " + clientPlayer.getHp() + "     " + clientPlayer.getStatus());
                currentLabel.setBounds(10, 5 + labelIndex * 35, 300, 30);
                currentLabel.setFont(largeFont);
    
                if (clientPlayer.getHp() < 2) {
                    currentLabel.setForeground(Color.RED);
                } else {
                    currentLabel.setForeground(Color.GREEN);
                }
    
                // 每處理一個玩家，移動到下一個 Label
                labelIndex++;
            }
    
            // 如果已分配完 3 個 Label，就結束處理
            if (labelIndex >= 3) {
                break;
            }
        }
    }
    
    

     
        
}
