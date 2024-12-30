import java.awt.*;
import javax.swing.*;

public class ClientGame {

    private JFrame frame;

    public ClientGame() {
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
        JPanel topPanel = new JPanel();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGame::new);
    }
}
