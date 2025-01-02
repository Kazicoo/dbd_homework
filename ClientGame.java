import Comm.TcpClient;
import java.awt.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.*;
import javax.swing.*;   

public class ClientGame {
    private TcpClient conn;
    private JFrame frame;
    private JPanel topPanel;
    private JPanel leftPanel; // 用於顯示血量
    private JLabel generatorLabel; // 用於顯示發電機數量
    private int generatorCount = 4; // 初始發電機數量
    private Map<Integer, JLabel> playerHealthLabels; // 儲存玩家血量提示
    private Map<Integer, Integer> playerHealth; // 儲存玩家的血量
    JPanel middlePanel;

    public ClientGame(TcpClient conn) {
        this.conn = conn;
        this.playerHealthLabels = new HashMap<>();
        this.playerHealth = new HashMap<>();
        initGame();
        waitGameStart();
    }

    public void initGame() {
        frame = new JFrame("迷途逃生");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setLayout(new BorderLayout());
        
        GamePanel gamePanel = new GamePanel(this);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        // 上部面板
        topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(width, height / 20));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // 左側面板 (顯示血量)
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        topPanel.add(leftPanel, BorderLayout.WEST);

        // 發電機數量顯示 (右上角)
        generatorLabel = new JLabel("Generators to fix: " + generatorCount);
        generatorLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(generatorLabel, BorderLayout.EAST);

        // 中部面板
        JPanel middlePanel = new JPanel();
         

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
        frame.add(gamePanel, BorderLayout.CENTER);
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
                    frame.add(generatorButton);
                    frame.revalidate();
                    frame.repaint();

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



    // update;health;1
    // update;generator;fixed;2
    // update;totalGenerator;3
    public void updatehealth(int totalhealth) {
        JLabel healthLabel = new JLabel("血量: "+totalhealth);
        healthLabel.add(healthLabel, BorderLayout.WEST);
        healthLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        if(totalhealth == 2) {

        }
    }

    // 處理伺服器發來的封包
    public void initGameObject(String[] part) {
        String objectType = part[1]; // 封包中的物件類型
        int objectId = Integer.parseInt(part[4]); // 封包中的 ID

        if (objectType.equals("generator")) {
            // 更新發電機數量
            generatorCount--;
            updateGeneratorLabel();
        } else if (objectType.equals("player")) {
            // 生成或更新玩家血量提示
            if (!playerHealth.containsKey(objectId)) {
                playerHealth.put(objectId, 100); // 初始血量 100
            }
            addPlayerHealthHint(objectId);
        }
    }

    // 更新發電機數量顯示
    private void updateGeneratorLabel() {
        generatorLabel.setText("Generators to fix: " + generatorCount);
    }

    // 添加或更新玩家血量提示
    private void addPlayerHealthHint(int playerId) {
        // 如果尚未為該玩家創建血量提示，則創建
        if (!playerHealthLabels.containsKey(playerId)) {
            JLabel playerLabel = new JLabel("Player " + playerId + " HP: " + playerHealth.get(playerId));
            playerLabel.setFont(new Font("Arial", Font.BOLD, 18));
            leftPanel.add(playerLabel); // 添加到左側面板
            playerHealthLabels.put(playerId, playerLabel); // 記錄到 Map 中
        } else {
            // 更新已存在的血量提示
            JLabel existingLabel = playerHealthLabels.get(playerId);
            existingLabel.setText("Player " + playerId + " HP: " + playerHealth.get(playerId));
        }

        // 刷新左側面板
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    
}
