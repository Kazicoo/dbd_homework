import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class setupGUI {
    private static PrintWriter out;
    private static BufferedReader in;
    private static Socket socket;
    private static int readyPlayers = 0; // Tracks the number of ready players
    private static int maxPlayers = 4; // Maximum number of players
    private static boolean[] characterSelected = new boolean[4]; // Tracks character selection
    private static boolean isReady = false; // Tracks if the player is ready
    private static JButton[] characterButtons = new JButton[4]; // Character buttons array
    private static JLabel statusLabel; // Displays the number of ready players
    private static String selectedCharacter = null; // Tracks the selected character
    private static JLabel imageLabel; // Displays the selected character image or name
    private static JButton readyButton; // Ready button

    // 构造器，初始化所有的GUI组件
    public setupGUI() {
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
        characterButtons[1] = new JButton("Character 1");
        characterButtons[2] = new JButton("Character 2");
        characterButtons[3] = new JButton("Character 3");

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

        statusLabel = new JLabel("已準備玩家: 0/4", SwingConstants.LEFT);
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        readyButton = new JButton("選擇角色");
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
        characterButtons[0].addActionListener(e -> selectCharacter("Ghost", characterButtons[0], 0));
        characterButtons[1].addActionListener(e -> selectCharacter("Character 1", characterButtons[1], 1));
        characterButtons[2].addActionListener(e -> selectCharacter("Character 2", characterButtons[2], 2));
        characterButtons[3].addActionListener(e -> selectCharacter("Character 3", characterButtons[3], 3));

        // 規則按鈕事件
        rulesButton.addActionListener(e -> rulesPanel.setVisible(true));

        // 規則面板點擊事件
        rulesPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                rulesPanel.setVisible(false);
            }
        });

        // 選擇角色按鈕事件
        readyButton.addActionListener(e -> handleReadyButton());

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

    // 角色選擇方法
    private static void selectCharacter(String character, JButton button, int index) {
        if (!isReady) {
            if (!characterSelected[index]) {
                // 如果角色未被選擇，選擇它
                for (int i = 0; i < characterButtons.length; i++) {
                    if (characterSelected[i]) {
                        characterButtons[i].setBackground(Color.LIGHT_GRAY);
                        characterSelected[i] = false;
                        sendMessage("DESELECT " + characterButtons[i].getText());  // 取消之前選擇的角色
                    }
                }
                characterSelected[index] = true;
                selectedCharacter = character;
                sendMessage("SELECT " + character);  // 發送選擇角色的封包
                button.setBackground(Color.GRAY);
                imageLabel.setText(character + " 已被選擇");
                readyButton.setEnabled(true);
            } else {
                // 如果角色已經選擇，取消選擇
                characterSelected[index] = false;
                sendMessage("DESELECT " + character);  // 發送取消選擇角色的封包
                selectedCharacter = null;
                button.setBackground(Color.LIGHT_GRAY);
                imageLabel.setText("請選擇角色");
                readyButton.setEnabled(false);
            }
        }
    }

    // 處理準備按鈕的邏輯
    private static void handleReadyButton() {
        if (isReady) {
            // 玩家取消準備
            readyPlayers--;
            statusLabel.setText("已準備玩家: " + readyPlayers + "/4");
            readyButton.setText("選擇角色");
            isReady = false;
            if (selectedCharacter != null) {
                sendMessage("CANCEL_READY " + selectedCharacter);  // 發送取消準備的封包
            }
            for (int i = 0; i < characterButtons.length; i++) {
                characterButtons[i].setEnabled(!characterSelected[i]);
            }
        } else if (readyPlayers < maxPlayers && selectedCharacter != null) {
            // 玩家準備
            readyPlayers++;
            statusLabel.setText("已準備玩家: " + readyPlayers + "/4");
            readyButton.setText("取消選擇");
            isReady = true;
            sendMessage("READY " + selectedCharacter);  // 發送準備訊息給伺服器
    
            // 傳送角色封包，包含所有角色的狀態
            String roles = getRoleStatus();  // 取得角色狀態
            sendMessage("ROLE " + roles);  // 發送角色封包
    
            for (int i = 0; i < characterButtons.length; i++) {
                characterButtons[i].setEnabled(characterSelected[i]);
            }
    
        }
        if (readyPlayers == maxPlayers) {
            readyButton.setEnabled(false);
        }
    }

    // 取得角色狀態
    private static String getRoleStatus() {
        StringBuilder roleStatus = new StringBuilder();
        roleStatus.append(characterSelected[0] ? "killer" : "p1");
        roleStatus.append(",");
        roleStatus.append(characterSelected[1] ? "p2" : "p3");
        roleStatus.append(",");
        roleStatus.append(characterSelected[2] ? "p2" : "p3");
        roleStatus.append(",");
        roleStatus.append(characterSelected[3] ? "p2" : "p3");
    
        return roleStatus.toString();
    }
    
    // 發送訊息到伺服器
    private static void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
