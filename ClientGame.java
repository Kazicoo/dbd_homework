import Comm.TcpClient;
import java.awt.*;
import javax.swing.*;

public class ClientGame {
    private TcpClient conn;
    private JFrame frame;
    private JPanel statusPanel; // 用於顯示角色血量的面板
    private String role;
    private int count = 0;
    private int totalHealth = 2; // 血量預設值

    public ClientGame(TcpClient conn) {
        this.conn = conn;
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

        // 上部面板 (狀態欄)
        statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // 左對齊
        statusPanel.setPreferredSize(new Dimension(width, height / 20));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // 中部面板
        JPanel middlePanel = new JPanel();
        middlePanel.setPreferredSize(new Dimension(width, height * 9 / 10));
        middlePanel.setBackground(Color.WHITE);
        middlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // 下部面板
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(width, height / 20));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // 添加面板到框架
        frame.add(statusPanel, BorderLayout.NORTH);
        frame.add(middlePanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // 初始化角色
    public void initGameObject(String[] part, int id) {
        SwingUtilities.invokeLater(() -> {
            String[] chars = {"killer", "p1", "p2", "p3"};
            if (part[4].equals("" + id)) {
                role = chars[count];
                if (!role.equals("killer")) { // 排除 "killer"
                    addHealthStatus(role, totalHealth); // 根據角色新增血量狀態
                }
                count++;
            }
        });
    }

    // 新增角色血量狀態
    private void addHealthStatus(String role, int health) {
        JLabel healthLabel = new JLabel(role + " 血量: " + health);
        healthLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        healthLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // 添加內邊距
        statusPanel.add(healthLabel); // 將標籤加入狀態面板
        statusPanel.revalidate(); // 重新佈局
        statusPanel.repaint();   // 刷新畫面
    }

   
}
