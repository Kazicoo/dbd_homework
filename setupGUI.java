    import Comm.TcpClient;
    import java.awt.*;
    import java.awt.event.*;
    import javax.swing.*;



    public class setupGUI {
        private boolean[] characterSelected = new boolean[4]; // Tracks character selection
        private JButton[] characterButtons = new JButton[4]; // Character buttons array
        private JButton rulesButton;
        private JLabel statusLabel; // Displays the number of ready players
        private JLabel imageLabel; // Displays the selected character image or name
        private JLabel waitReadyLabel;
        private TcpClient conn;

        // 建構子，初始化所有的GUI组件
        public setupGUI(TcpClient conn) {
            this.conn = conn;
            // 生成主視窗
            JFrame frame = new JFrame("迷途逃生");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);

            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            device.setFullScreenWindow(frame);

            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setLayout(null);
            frame.add(layeredPane);

            // 創建背景圖片的 JLabel
            ImageIcon originalIcon = new ImageIcon("Graphic/GameBackGround.jpg");
            Image scaledImage = originalIcon.getImage().getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon backgroundIcon = new ImageIcon(scaledImage);
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setSize(frame.getWidth(), frame.getHeight());
            backgroundLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight()); 
            // 將背景圖片的 JLabel 添加到 JLayeredPane 的底層 
            layeredPane.add(backgroundLabel, Integer.valueOf(-3));
            
            ImageIcon sparkIcon = new ImageIcon("Graphic/spark.gif");
            JLabel sparkLabel = new JLabel(sparkIcon);
            sparkLabel.setSize(sparkIcon.getIconWidth(), sparkIcon.getIconHeight()); // 使用圖片的原始大小
            sparkLabel.setLocation((frame.getWidth() - sparkIcon.getIconWidth()) / 2, (frame.getHeight() - sparkIcon.getIconHeight()) / 2); // 將 GIF 居中顯示
            layeredPane.add(sparkLabel, Integer.valueOf(-2));

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBounds(0, 0, frame.getWidth(), 100);
            topPanel.setOpaque(false);

            JLabel titleLabel = new JLabel("迷途逃生", SwingConstants.LEFT);
            titleLabel.setFont(new Font("DialogInput", Font.BOLD, 60));
            titleLabel.setForeground(Color.RED);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            topPanel.add(titleLabel, BorderLayout.WEST);

            rulesButton = new JButton("規則");
            rulesButton.setFont(new Font("微軟正黑體", Font.BOLD, 20));
            rulesButton.setMargin(new Insets(5, 40, 5, 40));
            topPanel.add(rulesButton, BorderLayout.EAST);

            layeredPane.add(topPanel, JLayeredPane.DEFAULT_LAYER);

            // 主面板（角色选择和展示）
            JPanel mainPanel = new JPanel(new GridLayout(1, 2));
            mainPanel.setBounds(0, 100, frame.getWidth(), frame.getHeight() - 200);
            layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

            JPanel rolePanel = new JPanel();
            rolePanel.setLayout(new BoxLayout(rolePanel, BoxLayout.Y_AXIS));

            characterButtons[0] = new JButton("killer");
            characterButtons[1] = new JButton("p1");
            characterButtons[2] = new JButton("p2");
            characterButtons[3] = new JButton("p3");

            for (int i = 0; i < characterSelected.length; i++) {
                characterSelected[i] = false;
            }

            for (int i = 0; i < characterButtons.length; i++) {
                JButton button = characterButtons[i];
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.WHITE);
                button.setMaximumSize(new Dimension(500, 100));
                button.setAlignmentX(Component.LEFT_ALIGNMENT);
                int index = i;
                button.addActionListener(e -> handleCharacterSelection(index));
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
            statusLabel.setFont(new Font("微軟正黑體", Font.BOLD, 25));
            statusLabel.setForeground(Color.WHITE);
            bottomPanel.add(statusLabel, BorderLayout.WEST);

            waitReadyLabel = new JLabel("等待玩家到齊...");
            waitReadyLabel.setFont(new Font("微軟正黑體", Font.BOLD, 30));
            waitReadyLabel.setForeground(Color.WHITE);
            bottomPanel.add(waitReadyLabel, BorderLayout.EAST);

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

            // 規則按鈕事件
            rulesButton.addActionListener(e -> rulesPanel.setVisible(true));

            // 規則面板點擊事件
            rulesPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    rulesPanel.setVisible(false);
                }
            });

            // 退出按鍵事件
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
                if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
                return false;
            });

            topPanel.setOpaque(false);
            mainPanel.setOpaque(false);
            rolePanel.setOpaque(false);
            imagePanel.setOpaque(false);
            bottomPanel.setOpaque(false);
            rulesPanel.setOpaque(false);

            // 顯示視窗
            frame.setVisible(true);
        }

        private void handleCharacterSelection(int index) {
            if (characterSelected[index]){
                conn.send("updateReadyState;unready;" + characterButtons[index].getText());
            } else {
                conn.send("updateReadyState;ready;" + characterButtons[index].getText());
            }
        }

        public void updateTotalPlayers(int totalPlayers) {
            if (totalPlayers != 4) {
                waitReadyLabel.setText("等待玩家到齊...("+ (totalPlayers) +"/4)");
            }
            else {
                waitReadyLabel.setText("伺服器人數已達上限4人! 大家都選好角色後即會開始遊戲");
                waitReadyLabel.setFont(new Font("微軟正黑體", Font.BOLD, 40));
                waitReadyLabel.setForeground(Color.RED);
            }
        }

        // 當ready被傳進前端時，畫面更新會進行更新 
        // 封包為 updateReadyState;ready;p1;0
        public void playerReady(Boolean is_ready, String message, int id) {
            // 將數字id轉成字串，檢查該封包是不是自己傳
            String idStr = "" + id;
            String[] parts = message.split(";");

            // 本人按下選擇角色按鈕時，要有的變化
            if (idStr.equals(parts[3])) {
                if (is_ready) {
                    switch (parts[2]) {
                        case "killer" -> {
                            characterButtons[0].setBackground(Color.RED);
                            characterSelected[0] = true;
                            for (int i = 0; i < characterButtons.length; i++) {
                                if (i == 0) continue;
                                characterButtons[i].setEnabled(false);
                            }
                        }
                        case "p1" -> {
                            characterButtons[1].setBackground(Color.GREEN);
                            characterSelected[1] = true;
                            for (int i = 0; i < characterButtons.length; i++) {
                                if (i == 1) continue;
                                characterButtons[i].setEnabled(false);
                            }
                        }
                        case "p2" -> {
                            characterButtons[2].setBackground(Color.GREEN);
                            characterSelected[2] = true;
                            for (int i = 0; i < characterButtons.length; i++) {
                                if (i == 2) continue;
                                characterButtons[i].setEnabled(false);
                            }
                        }
                        case "p3" -> {
                            characterButtons[3].setBackground(Color.GREEN);
                            characterSelected[3] = true;
                            for (int i = 0; i < characterButtons.length; i++) {
                                if (i == 3) continue;
                                characterButtons[i].setEnabled(false);
                            }
                        }
                    }
                } 
                else {
                    switch (parts[2]) {
                        case "killer" -> {
                            characterButtons[0].setBackground(Color.LIGHT_GRAY);
                            characterSelected[0] = false;
                            for (int i = 0; i < characterButtons.length; i++) {
                                if (i == 0) continue;
                                characterButtons[i].setEnabled(true);
                            }
                        }
                        case "p1" -> {
                            characterButtons[1].setBackground(Color.LIGHT_GRAY);
                            characterSelected[1] = false;
                            for (int i = 0; i < characterButtons.length; i++) {
                                if (i == 1) continue;
                                characterButtons[i].setEnabled(true);
                            }
                        }
                        case "p2" -> {
                            characterButtons[2].setBackground(Color.LIGHT_GRAY);
                            characterSelected[2] = false;
                            for (int i = 0; i < characterButtons.length; i++) {
                                if (i == 2) continue;
                                characterButtons[i].setEnabled(true);
                            }
                        }
                        case "p3" -> {
                            characterButtons[3].setBackground(Color.LIGHT_GRAY);
                            characterSelected[3] = false;
                            for (int i = 0; i < characterButtons.length; i++) {
                                if (i == 3) continue;
                                characterButtons[i].setEnabled(true);
                            }
                        }
                    }
                }
            }
            // 非本人按下按鈕應該要有的反應
            if (!idStr.equals(parts[3])) {
                if (is_ready) {
                    switch (parts[2]) {
                        case "killer" -> {
                            characterButtons[0].setBackground(Color.DARK_GRAY);
                            characterButtons[0].setEnabled(false);
                        }
                        case "p1" -> {
                            characterButtons[1].setBackground(Color.DARK_GRAY);
                            characterButtons[1].setEnabled(false);
                        }
                        case "p2" -> {
                            characterButtons[2].setBackground(Color.DARK_GRAY);
                            characterButtons[2].setEnabled(false);
                        }
                        case "p3" -> {
                            characterButtons[3].setBackground(Color.DARK_GRAY);
                            characterButtons[3].setEnabled(false);
                        }
                    }
                }
                else {
                    switch (parts[2]) {
                        case "killer" -> {
                            characterButtons[0].setBackground(Color.LIGHT_GRAY);
                            characterButtons[0].setEnabled(true);
                        }
                        case "p1" -> {
                            characterButtons[1].setBackground(Color.LIGHT_GRAY);
                            characterButtons[1].setEnabled(true);
                        }
                        case "p2" -> {
                            characterButtons[2].setBackground(Color.LIGHT_GRAY);
                            characterButtons[2].setEnabled(true);
                        }
                        case "p3" -> {
                            characterButtons[3].setBackground(Color.LIGHT_GRAY);
                            characterButtons[3].setEnabled(true);
                        }
                    }
                }
            }
        }

        public void startCountdown() {
            for (int i = 0; i < characterButtons.length ; i++) {
                characterButtons[i].setEnabled(false);
                rulesButton.setEnabled(false);
            }
                
            // 倒數計時
            try {
                waitReadyLabel.setText("準備開始...");
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 3; i > 0; i--) {
                try {
                    Thread.sleep(1000);
                    waitReadyLabel.setFont(new Font("微軟正黑體", Font.BOLD, 50));
                    waitReadyLabel.setText("" + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
          
        }
    }