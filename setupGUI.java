import Comm.TcpClient;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class setupGUI {
    private boolean[] characterSelected = new boolean[4]; // Tracks character selection
    private JButton[] characterButtons = new JButton[4]; // Character buttons array
    private JLabel statusLabel; // Displays the number of ready players
    private String selectedCharacter = null; // Tracks the selected character
    private JLabel imageLabel; // Displays the selected character image or name
    private JButton readyButton; // Ready button
    private TcpClient conn;

    // 建構子，初始化所有的GUI组件
    public setupGUI(TcpClient conn) {
        this.conn = conn;

        JFrame frame = new JFrame("迷途逃生");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        device.setFullScreenWindow(frame);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        frame.add(layeredPane);

        // 顶部面板（标题和规则按钮）
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBounds(0, 0, frame.getWidth(), 100);
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("迷途逃生", SwingConstants.LEFT);
        titleLabel.setFont(new Font("DialogInput", Font.BOLD, 60));
        titleLabel.setForeground(Color.RED);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton rulesButton = new JButton("規則");
        rulesButton.setFont(new Font("DialogInput", Font.PLAIN, 20));
        rulesButton.setMargin(new Insets(5, 15, 5, 15));
        topPanel.add(rulesButton, BorderLayout.EAST);

        layeredPane.add(topPanel, JLayeredPane.DEFAULT_LAYER);

        // 主面板（角色选择和展示）
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBounds(0, 100, frame.getWidth(), frame.getHeight() - 200);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        JPanel rolePanel = new JPanel();
        rolePanel.setLayout(new BoxLayout(rolePanel, BoxLayout.Y_AXIS));

        characterButtons[0] = new JButton("Ghost");
        characterButtons[1] = new JButton("Character1");
        characterButtons[2] = new JButton("Character2");
        characterButtons[3] = new JButton("Character3");

        for (JButton button : characterButtons) {
            button.setBackground(Color.LIGHT_GRAY);
            button.setForeground(Color.WHITE);
            button.setMaximumSize(new Dimension(500, 100));
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            rolePanel.add(button);
            rolePanel.add(Box.createRigidArea(new Dimension(0, 75)));
        }
        rolePanel.remove(rolePanel.getComponentCount() - 1);
        mainPanel.add(rolePanel);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createTitledBorder("角色展示"));

        imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setFont(new Font("DialogInput", Font.ITALIC, 18));
        imageLabel.setText("請選擇角色");
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        mainPanel.add(imagePanel);

        // 底部面板（顯示已準備玩家數和選擇角色按鈕）
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBounds(0, frame.getHeight() - 100, frame.getWidth(), 100);

        statusLabel = new JLabel("所有角色被選定後將直接開始遊戲", SwingConstants.LEFT);
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        readyButton = new JButton("取消選擇");
        readyButton.setEnabled(false);
        bottomPanel.add(readyButton, BorderLayout.EAST);

        layeredPane.add(bottomPanel, JLayeredPane.DEFAULT_LAYER);

        // 規則面板
        JPanel rulesPanel = new JPanel();
        rulesPanel.setLayout(new BorderLayout());
        rulesPanel.setBounds(frame.getWidth() / 2 - 200, frame.getHeight() / 2 - 150, 400, 300);
        rulesPanel.setBackground(new Color(100, 0, 0, 150));
        rulesPanel.setBorder(BorderFactory.createTitledBorder("遊戲規則"));
        JLabel rulesLabel = new JLabel("<html>遊戲規則:<br>1. 選擇角色<br>2. 當所有玩家準備完畢後開始遊戲</html>");
        rulesPanel.add(rulesLabel, BorderLayout.CENTER);
        rulesPanel.setVisible(false);

        layeredPane.add(rulesPanel, JLayeredPane.PALETTE_LAYER);

        // 添加角色選擇事件
        for (int i = 0; i < characterButtons.length; i++) {
            int index = i;
            characterButtons[i].addActionListener(_ -> {
                // 設定選擇的角色
                characterSelected[index] = true;
                String selectedCharacter = characterButtons[index].getText(); // 取得選中的角色名稱
        
                // 發送選中的角色給伺服器
                try {
                    // 只發送一次更新角色的訊息，根據選擇的角色來傳遞
                    conn.send("updateIfChoseState:"+selectedCharacter);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        
                // 顯示選擇的角色名稱
                imageLabel.setText("選擇的角色: " + selectedCharacter);
            });
        }
        
        // 規則按鈕事件
        rulesButton.addActionListener(_ -> rulesPanel.setVisible(true));

        // 規則面板點擊事件
        rulesPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                rulesPanel.setVisible(false);
            }
        });

        // 選擇角色按鈕事件
        

        // 退出按鍵事件
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
            return false;
        });

        // 顯示視窗
        frame.setVisible(true);
    }

    public void playerReady(Boolean ready) {
        // 改視窗
    }
    public void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
  
}
