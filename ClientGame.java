import Comm.TcpClient;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientGame implements ActionListener{
    private TcpClient conn;
    private JFrame frame;
    private final ClientPlayer clientPlayers[];
    private final ClientGenerator generators[];
    private Image generatorImage;

    public ClientGame(TcpClient conn) {
        this.conn = conn;
        initGame();
        waitGameStart();
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

    public void waitGameStart() {
        synchronized (this) {
            while (generatorTotal < 4 && playerTotal < 4 && status) {
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

    int generatorTotal = 0;
    public void initGenerator(String message) {
        String[] parts = message.split(";");

        generators = new ClientGenerator[generatorTotal];

        for (int i = 0;i < generatorTotal;i++) {
            generators[i] = new ClientGenerator();

            generators[i].setRelativeLocation(Integer.parseInt(parts[2]),Integer.parseInt(parts[3]));
            
            JButton generatorButton = new JButton("Generator " + generators[i].getId());
            generatorButton.setPreferredSize(new Dimension(generators[i].getX(),generators[i].getY()));
            
        generatorTotal++;
        }
        
        JButton generatorButton = new JButton();
        generatorButton.setPreferredSize(new Dimension(50,120));

        generatorButton.addMouseListener(new MouseAdapter() {
            if (SwingUtilities.isLeftMouseButton(e)) {
                //傳送玩家左鍵點擊發電機的封包給伺服器，伺服器判斷玩家是否在發電機可操作範圍內。
                conn.send("");
                
            }
        });
    }
    public void updateGenerator() {

    }

    int playerTotal = 0;
    public void initPlayer(String message) {
        
        playerTotal++;
    }
    

}
