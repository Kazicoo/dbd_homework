import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class dbd_client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Matching Program");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // Set main layout
        frame.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Matching Program", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Gender
        JLabel genderLabel = new JLabel("Gender:");
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(genderLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(genderCombo, gbc);

        // Age
        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(ageLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(ageField, gbc);

        // Phone
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        frame.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addUserButton = new JButton("Add User");
        JButton findMatchesButton = new JButton("Find Matches");
        buttonPanel.add(addUserButton);
        buttonPanel.add(findMatchesButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Add User Button Action
        addUserButton.addActionListener(e -> {
            String name = nameField.getText();
            String gender = (String) genderCombo.getSelectedItem();
            String ageText = ageField.getText();
            String phone = phoneField.getText();

            if (name.isEmpty() || ageText.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Age must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Send add user request to server
                out.println("ADD_USER");
                out.println(name);
                out.println(gender);
                out.println(age);
                out.println(phone);

                // Receive server response
                String serverResponse = in.readLine();
                JOptionPane.showMessageDialog(frame, serverResponse, "Response", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error connecting to server.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Find Matches Button Action
        findMatchesButton.addActionListener(e -> {
            String name = nameField.getText();
            String gender = (String) genderCombo.getSelectedItem();
            String ageText = ageField.getText();
            String phone = phoneField.getText();

            if (name.isEmpty() || ageText.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Age must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Send find matches request to server
                out.println("FIND_MATCHES");
                out.println(name);
                out.println(gender);
                out.println(age);
                out.println(phone);

                // Receive matches from server
                StringBuilder matches = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    matches.append(line).append("\n");
                }

                JOptionPane.showMessageDialog(frame, matches.toString(), "Matches", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error connecting to server.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }
}
