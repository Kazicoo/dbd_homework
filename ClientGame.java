import Comm.TcpClient;
import java.awt.*;
import javax.swing.*;

public class ClientGame {
    private TcpClient conn;
    private JFrame frame;

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

        // 上部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(width, height / 20));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        

        // 中部面板
        JPanel middlePanel = new JPanel();
        middlePanel.setPreferredSize(new Dimension(width, 2 * height / 1));
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
    // update;health;1
    // update;generator;fixed;2
    // update;totalGenerator;3
    public void updateheaith(int totalhealth) {
        if(totalhealth == 2) {
            JLabel healthLabel = new JLabel("血量: I I");
            healthLabel.add(healthLabel, BorderLayout.WEST);
            healthLabel.setFont(new Font("Serif", Font.PLAIN, 20));
            
        }
        else if (totalhealth == 0) {
            JOptionPane.showMessageDialog(frame, "You are dead!");
            frame.setFont(new Font("Serif", Font.PLAIN, 20));
            
        }
    }

}
