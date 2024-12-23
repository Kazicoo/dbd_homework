import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.net.*;

public class dbd_client extends setupGUI {
    private static PrintWriter out;
    private static BufferedReader in;
    private static Socket socket;

    public static void main(String[] args) {
        // 創建 setupGUI 實例並初始化視窗
        setupGUI initialGUI = new setupGUI();

        // 呼叫connectToServer來連接伺服器
        connectToServer();
    }

    private static void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
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
            JOptionPane.showMessageDialog(null, "無法連接到伺服器: " + e.getMessage(), "連線錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }
}
