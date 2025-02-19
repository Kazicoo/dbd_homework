import Comm.TcpClient;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ClientGame {
    private TcpClient conn;
    private JFrame frame;
    private JLabel generatorLabel; // 用於顯示發電機數量
    private JLabel humanLabel1,humanLabel2,humanLabel3; // 用於顯示玩家血量
    private int generatorCount = 6; // 初始發電機數量
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
            frame.add(gamePanel);
            // 顯示框架
            frame.setVisible(true);
        }

    public JLabel createGeneratorLabel(String role, int generatorCount, int x, int y) {
        JLabel generatorLabel = new JLabel(" 未修理發電機: " + generatorCount);
        generatorLabel.setFont(new Font("DialogInput", Font.BOLD, 18));
        generatorLabel.setBounds(x, y, 200, 30); // 設定位置和大小
        generatorLabel.setForeground(Color.RED); // 可自定義文字顏色
        return generatorLabel;
    }
    
    public void waitGameStart() {
        new Thread(() -> {
            while (true) {
                synchronized (this) {
                    if (initGeneratorTotal == 6 && playerTotal == 4) {
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
    public final ClientGenerator[] generators = new ClientGenerator[6];

    public void initGenerator(String message) {
        String[] parts;
        parts = message.split(";");
           
        try {
            int id = Integer.parseInt(parts[4]);
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            // 初始化發電機物件
            generators[generatorTotal] = new ClientGenerator(id);
            generators[generatorTotal].setRelativeLocation(x, y);
            generators[generatorTotal].setButton(new JButton());
                    
            synchronized (this) {
                generatorTotal++;
                initGeneratorTotal++;
                if (initGeneratorTotal == 6 && playerTotal == 4) {
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
        
        
        int imageWidth = hookIcon.getIconWidth();
        int imageHeight = hookIcon.getIconHeight();

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

    
    private int WindowTotal = 0;
    public final ClientWindow[] Window = new ClientWindow[11];
    
    public void initWindow(String message) {
        String[] parts;
        try {
            // 解析封包
            parts = message.split(";");
            if (parts.length != 5 || !"window".equals(parts[1])) {
                throw new IllegalArgumentException("Invalid window message format.");
            }
        } catch (Exception e) {
            System.out.println("Error parsing window message: " + e.getMessage());
            return;
        }
    
        try {
            // 提取座標和 ID
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            int id = Integer.parseInt(parts[4]);
    
            // 初始化物件
            Window[WindowTotal] = new ClientWindow(id);
            Window[WindowTotal].setRelativeLocation(x, y);
    
            ImageIcon windowIcon = new ImageIcon("Graphic/Object/window.png");
            if (windowIcon.getIconWidth() == -1) {
                System.out.println("Error loading image: " + windowIcon);
                return; // 圖片加載失敗，退出方法
            }
    
            JButton windowButton = new JButton(windowIcon);
    
            int imageWidth = windowIcon.getIconWidth();
            int imageHeight = windowIcon.getIconHeight();
    
            // 設定按鈕位置和大小
            windowButton.setBounds(x, y, imageWidth, imageHeight);
            windowButton.setOpaque(false);
            windowButton.setContentAreaFilled(false);
            windowButton.setBorderPainted(false);
    
            WindowTotal++; 
    
            // 初始化物件
            Window[id] = new ClientWindow(id);
            Window[id].setRelativeLocation(x, y);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing coordinates or ID: " + e.getMessage());
        }
    }
    


    private int boardTotal = 0;
    public final ClientBoard[] Board = new ClientBoard[13];

    public void initBoard(String message) {
        // 解析伺服器傳送的訊息
        String[] parts = message.split(";");
        if (parts.length < 5 || !parts[0].equals("initGameObject") || !parts[1].equals("board")) {
            System.err.println("Invalid message format: " + message);
            return;
        }

        int id = Integer.parseInt(parts[2].split(" ")[1]); // 提取 id
        int x = Integer.parseInt(parts[3].split(" ")[1]);  // 提取 x 座標
        int y = Integer.parseInt(parts[4].split(" ")[1]);  // 提取 y 座標

    // 初始化物件
        ClientBoard board = new ClientBoard(id);
        board.setRelativeLocation(x, y);
        Board[id] = new ClientBoard(id);
        Board[id].setRelativeLocation(x, y);

        ImageIcon boardIcon = new ImageIcon("Graphic/Object/board-notUsed.png");
        

        int imageWidth = boardIcon.getIconWidth();
        int imageHeight = boardIcon.getIconHeight();

  



        synchronized (this) {
            if (boardTotal == 13) {
                System.out.println("Maximum broads reached.");
                return;
            }
            boardTotal++; // 自增板子數量
            System.out.println("Hook initialized: ID " + id + " at (" + x + ", " + y + ")");
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
        if (playerTotal == 4 && initGeneratorTotal ==6) {
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
                                conn.send("animated;KeyDown;W");
                                isMovingUp = true;
                            }
                            case 'A' -> {
                                // 向左移動
                                player.updateMovement("A");
                                conn.send("KeyDown;A");
                                conn.send("animated;KeyDown;A");
                                isMovingLeft = true;
                            }
                            case 'S' -> {
                                // 向下移動
                                player.updateMovement("S");
                                conn.send("KeyDown;S");
                                conn.send("animated;KeyDown;S");
                                isMovingDown = true;
                            }
                            case 'D' -> {
                                // 向右移動
                                player.updateMovement("D");
                                conn.send("KeyDown;D");
                                conn.send("animated;KeyDown;D");
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

                if (key == KeyEvent.VK_J) {
                    for (ClientPlayer player : clientPlayers) {
                        if (player != null && "killer".equals(player.getRole()) && player.getIsSelf()) {
                            conn.send("animated;attack");
                        } else {
                        }
                    }
                }

                // 檢查是否為空白鍵
                if (key == KeyEvent.VK_SPACE) {
                    for (ClientPlayer player : clientPlayers) {
                        if (player != null && player.getIsSelf()) {
                            if ("killer".equals(player.getRole())) {
                                conn.send("Activated;window");
                            }
                        } else {
                            conn.send("Activated;window");
                            conn.send("clicked;generator");
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
        if(parts.length == 4) {
            int id = Integer.parseInt(parts[3]);
            String direction = parts[2];
            for (ClientPlayer clientPlayer : clientPlayers) {
                if (clientPlayer != null && clientPlayer.getHp() == 2 && id == clientPlayer.getId()) {
                    clientPlayer.updateMovement(direction);
                }
            }
        }

    }
    
   
    
    
    public void attackFacing(String message) {
        String[] parts = message.split(";");
        for (ClientPlayer clientPlayer : clientPlayers) {
            if (clientPlayer != null && clientPlayer.getRole().equals("killer")) {
                clientPlayer.setAction(parts[1]);
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
    
                if (clientPlayer.getHp() == 2) {
                    currentLabel.setForeground(Color.GREEN);
                } else if (clientPlayer.getHp() == 1){
                    currentLabel.setForeground(Color.RED);
                } else if ((clientPlayer.getHp() == 0)) {
                    currentLabel.setForeground(Color.BLACK);
                    clientPlayer.setDownImage();
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
