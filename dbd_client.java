import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.net.*;

public class dbd_client {
    private static PrintWriter out;
    private static BufferedReader in;
    private static int readyPlayers = 0; // 跟蹤已準備的玩家數量
    private static int maxPlayers = 4; // 最大玩家數量
    private static boolean[] characterSelected = new boolean[4]; // 角色是否已被選擇
    private static boolean isReady = false; // 玩家是否已經準備
    private static JButton[] characterButtons = new JButton[4]; // 角色按鈕陣列
    private static JLabel statusLabel; // 用於顯示準備玩家數量的Label
    private static String selectedCharacter = null; // 用於記錄玩家選擇的角色
    private static JLabel imageLabel; // 顯示選擇角色的圖片或名稱

    public static void main(String[] args) {
        // 創建主框架
        JFrame frame = new JFrame("迷途逃生");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true); // 移除標題欄以便全螢幕顯示

        // 設置為全螢幕模式
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        device.setFullScreenWindow(frame);

        // 創建 JLayeredPane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        frame.add(layeredPane);

        // 上方標題及規則按鈕區域
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBounds(0, 0, frame.getWidth(), 100);
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("迷途逃生", SwingConstants.LEFT);
        titleLabel.setFont(new Font("DialogInput", Font.BOLD, 50));
        titleLabel.setForeground(Color.RED);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton rulesButton = new JButton("規則");
        rulesButton.setFont(new Font("Dialog", Font.PLAIN, 20));
        rulesButton.setMargin(new Insets(5, 15, 5, 15));
        topPanel.add(rulesButton, BorderLayout.EAST);

        layeredPane.add(topPanel, JLayeredPane.DEFAULT_LAYER);

        // 主面板設置
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBounds(0, 100, frame.getWidth(), frame.getHeight() - 200);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        // 左側角色選擇區
        JPanel rolePanel = new JPanel(new GridLayout(4, 1));

        characterButtons[0] = new JButton("Ghost");
        characterButtons[1] = new JButton("Character 1");
        characterButtons[2] = new JButton("Character 2");
        characterButtons[3] = new JButton("Character 3");

        // 設置角色按鈕的初始顏色，並添加到面板
        for (JButton button : characterButtons) {
            button.setBackground(Color.LIGHT_GRAY); // 默認顏色
            button.setForeground(Color.WHITE);
            rolePanel.add(button);
        }

        mainPanel.add(rolePanel);

        // 右側角色展示區
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createTitledBorder("Character Display"));

        imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        imageLabel.setText("Select a character to display");
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        mainPanel.add(imagePanel);

        // 下方操作區
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBounds(0, frame.getHeight() - 100, frame.getWidth(), 100);

        statusLabel = new JLabel("Ready players: 0/4", SwingConstants.LEFT);
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        JButton readyButton = new JButton("Ready");
        bottomPanel.add(readyButton, BorderLayout.EAST);

        layeredPane.add(bottomPanel, JLayeredPane.DEFAULT_LAYER);

        // 規則面板
        JPanel rulesPanel = new JPanel();
        rulesPanel.setLayout(new BorderLayout());
        rulesPanel.setBounds(frame.getWidth() / 2 - 200, frame.getHeight() / 2 - 150, 400, 300);
        rulesPanel.setBackground(new Color(0, 0, 0, 150));
        rulesPanel.setBorder(BorderFactory.createTitledBorder("遊戲規則"));
        JLabel rulesLabel = new JLabel("<html>遊戲規則:<br>1. 選擇角色<br>2. 按下Ready開始遊戲</html>");
        rulesPanel.add(rulesLabel, BorderLayout.CENTER);
        rulesPanel.setVisible(false);

        layeredPane.add(rulesPanel, JLayeredPane.PALETTE_LAYER);

        // 角色選擇按鈕事件
        characterButtons[0].addActionListener(e -> selectCharacter("Ghost", characterButtons[0], 0));
        characterButtons[1].addActionListener(e -> selectCharacter("Character 1", characterButtons[1], 1));
        characterButtons[2].addActionListener(e -> selectCharacter("Character 2", characterButtons[2], 2));
        characterButtons[3].addActionListener(e -> selectCharacter("Character 3", characterButtons[3], 3));

        // 規則按鈕事件
        rulesButton.addActionListener(e -> rulesPanel.setVisible(true));

        // 規則面板點擊隱藏事件
        rulesPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                rulesPanel.setVisible(false);
            }
        });

        // "準備" 按鈕事件
        readyButton.addActionListener(e -> {
            if (isReady) {
                // 取消準備
                readyPlayers--;
                statusLabel.setText("Ready players: " + readyPlayers + "/4");
                readyButton.setText("Ready"); // 恢復為"準備"
                isReady = false;

                // 取消準備後恢復角色按鈕可用狀態
                for (int i = 0; i < characterButtons.length; i++) {
                    characterButtons[i].setEnabled(true); // 恢復所有按鈕可用
                    characterButtons[i].setBackground(Color.LIGHT_GRAY); // 恢復顏色
                    characterSelected[i] = false; // 取消所有角色選擇
                }
                selectedCharacter = null; // 清空選擇的角色
                imageLabel.setText("Select a character to display"); // 清空顯示的角色名稱
            } else if (readyPlayers < maxPlayers) {
                // 準備
                readyPlayers++;
                statusLabel.setText("Ready players: " + readyPlayers + "/4");
                readyButton.setText("Cancel Ready"); // 改為"取消準備"
                isReady = true;

                // 準備後禁用所有未選擇的角色按鈕
                for (int i = 0; i < characterButtons.length; i++) {
                    if (!characterSelected[i]) {
                        characterButtons[i].setEnabled(false); // 禁用未選擇的角色按鈕
                    }
                }
            }

            if (readyPlayers == maxPlayers) {
                readyButton.setEnabled(false); // 當達到最大玩家數量時禁用準備按鈕
            }
        });

        // ESC退出功能
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0); // 退出程式
            }
            return false;
        });

        // 顯示框架
        frame.setVisible(true);

        // 連接伺服器
        connectToServer();
    }

    private static void selectCharacter(String character, JButton button, int index) {
        if (!isReady) {
            if (!characterSelected[index]) {
                // 如果角色未被選擇，選擇該角色並鎖定
                characterSelected[index] = true; // 標記該角色為已選
                selectedCharacter = character; // 記錄選擇的角色
                sendMessage(character + " Selected");
                button.setBackground(Color.GRAY); // 當選擇角色後，按鈕變為灰色
                button.setEnabled(false); // 禁用已選擇角色的按鈕

                // 更新imagePanel顯示所選角色的名稱
                imageLabel.setText(character + " Selected");
            } else {
                // 如果角色已被選擇，則取消選擇並恢復為可選狀態
                characterSelected[index] = false; // 取消選擇
                sendMessage(character + " Deselected");
                button.setBackground(Color.LIGHT_GRAY); // 恢復為默認顏色
                button.setEnabled(true); // 恢復按鈕可用
                selectedCharacter = null; // 清除選擇的角色
                imageLabel.setText("Select a character to display"); // 清空顯示的角色名稱
            }
        }
    }

    private static int getCharacterIndex(String character) {
        switch (character) {
            case "Ghost":
                return 0;
            case "Character 1":
                return 1;
            case "Character 2":
                return 2;
            case "Character 3":
                return 3;
            default:
                return -1;
        }
    }

    private static void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345); // 連接本地伺服器
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println("伺服器回應: " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
