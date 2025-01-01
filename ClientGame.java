import Comm.TcpClient;
import java.awt.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ClientGame {
    private TcpClient conn;
    private JFrame frame;
    private JPanel topPanel;
    private JPanel leftPanel; // 用於顯示血量
    private JLabel generatorLabel; // 用於顯示發電機數量
    private int generatorCount = 4; // 初始發電機數量
    private Map<Integer, JLabel> playerHealthLabels; // 儲存玩家血量提示
    private Map<Integer, Integer> playerHealth; // 儲存玩家的血量

    public ClientGame(TcpClient conn) {
        this.conn = conn;
        this.playerHealthLabels = new HashMap<>();
        this.playerHealth = new HashMap<>();
        initGame();
        
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
