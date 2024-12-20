import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class dbd_client {
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) {
        // 創建主框架
        JFrame frame = new JFrame("迷途逃生");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true); // 移除標題欄，必要條件

        // 設置為全螢幕模式
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        device.setFullScreenWindow(frame);

        // 創建 JLayeredPane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null); // 使用null佈局管理
        frame.add(layeredPane);

        // 上方標題和規則按鈕區域
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBounds(0, 0, frame.getWidth(), 100);
        topPanel.setOpaque(false);

        // 左上角標題
        JLabel titleLabel = new JLabel("迷途逃生", SwingConstants.LEFT);
        titleLabel.setFont(new Font("DialogInput", Font.BOLD, 50));
        titleLabel.setForeground(Color.RED);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // 右上角規則按鈕
        JButton rulesButton = new JButton("規則");
        rulesButton.setFont(new Font("Dialog", Font.PLAIN, 20));
        rulesButton.setMargin(new Insets(5, 15, 5, 15));
        topPanel.add(rulesButton, BorderLayout.EAST);

        layeredPane.add(topPanel, JLayeredPane.DEFAULT_LAYER);

        // 中間主面板
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBounds(0, 100, frame.getWidth(), frame.getHeight() - 200);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        // 左側角色選擇區
        JPanel rolePanel = new JPanel(new GridLayout(4, 1));

        JButton ghostButton = new JButton("Ghost");
        ghostButton.setBackground(Color.RED);
        ghostButton.setForeground(Color.WHITE);
        rolePanel.add(ghostButton);

        JButton humanButton1 = new JButton("Character 1");
        JButton humanButton2 = new JButton("Character 2");
        JButton humanButton3 = new JButton("Character 3");
        rolePanel.add(humanButton1);
        rolePanel.add(humanButton2);
        rolePanel.add(humanButton3);

        mainPanel.add(rolePanel);

        // 右側圖片展示區
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createTitledBorder("Character Display"));

        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        imageLabel.setText("Select a character to display");
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        mainPanel.add(imagePanel);

        // 下方操作區
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBounds(0, frame.getHeight() - 100, frame.getWidth(), 100);

        JLabel statusLabel = new JLabel("Ready players: 2/4", SwingConstants.LEFT);
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

        // 角色按鈕點擊事件
        ghostButton.addActionListener(e -> {
            imageLabel.setText("Ghost Selected");
            sendMessage("Ghost Selected");
        });
        humanButton1.addActionListener(e -> {
            imageLabel.setText("Character 1 Selected");
            sendMessage("Character 1 Selected");
        });
        humanButton2.addActionListener(e -> {
            imageLabel.setText("Character 2 Selected");
            sendMessage("Character 2 Selected");
        });
        humanButton3.addActionListener(e -> {
            imageLabel.setText("Character 3 Selected");
            sendMessage("Character 3 Selected");
        });

        // 規則按鈕事件
        rulesButton.addActionListener(e -> rulesPanel.setVisible(true));

        // 規則面板關閉事件
        rulesPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                rulesPanel.setVisible(false);
            }
        });

        // 全局鍵盤事件
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

    private static void connectToServer() {
        try {
            Socket socket = new Socket("localhost",12345); // 連接本地伺服器
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
