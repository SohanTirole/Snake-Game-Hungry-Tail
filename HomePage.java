//package snakegame;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import static javax.swing.JFrame.EXIT_ON_CLOSE;
//
//public class HomePage extends JFrame implements ActionListener {
//    private final JTextField nameField;
//    private final JComboBox<String> levelBox;
//    private final JButton startButton, exitButton;
//
//    public HomePage() {
//        setTitle("Hungry Tail");
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setSize(600, 600);
//        setLocationRelativeTo(null);
//        setResizable(false);
//        setLayout(new BorderLayout());
//
//        JPanel bg = new JPanel() {
//            @Override protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                Graphics2D g2 = (Graphics2D) g;
//                GradientPaint gp = new GradientPaint(0, 0, Color.BLACK, 0, getHeight(), new Color(40,40,40));
//                g2.setPaint(gp);
//                g2.fillRect(0, 0, getWidth(), getHeight());
//            }
//        };
//        bg.setLayout(new BoxLayout(bg, BoxLayout.Y_AXIS));
//
//        JLabel title = new JLabel("Hungry Tail");
//        title.setFont(new Font("Ink Free", Font.BOLD, 60));
//        title.setForeground(Color.GREEN);
//        title.setAlignmentX(Component.CENTER_ALIGNMENT);
//        title.setBorder(BorderFactory.createEmptyBorder(50, 0, 40, 0));
//
//        JLabel nameLabel = new JLabel("Enter Your Name:");
//        nameLabel.setFont(new Font("Ink Free", Font.BOLD, 25));
//        nameLabel.setForeground(Color.WHITE);
//        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        nameField = new JTextField(15);
//        nameField.setMaximumSize(new Dimension(200, 30));
//        nameField.setFont(new Font("Ink Free", Font.PLAIN, 20));
//        nameField.setHorizontalAlignment(JTextField.CENTER);
//        nameField.setBackground(Color.DARK_GRAY);
//        nameField.setForeground(Color.WHITE);
//        nameField.setCaretColor(Color.GREEN);
//        nameField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
//        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JLabel levelLabel = new JLabel("Select Level:");
//        levelLabel.setFont(new Font("Ink Free", Font.BOLD, 25));
//        levelLabel.setForeground(Color.WHITE);
//        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        String[] levels = {"1", "2", "3", "4", "5"};
//        levelBox = new JComboBox<>(levels);
//        levelBox.setFont(new Font("Ink Free", Font.BOLD, 20));
//        levelBox.setBackground(Color.BLACK);
//        levelBox.setForeground(Color.GREEN);
//        levelBox.setMaximumSize(new Dimension(160, 30));
//        levelBox.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        startButton = new JButton("Start Game");
//        startButton.setFont(new Font("Ink Free", Font.BOLD, 22));
//        startButton.setBackground(Color.GREEN);
//        startButton.setForeground(Color.BLACK);
//        startButton.setFocusPainted(false);
//        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//        startButton.addActionListener(this);
//
//        exitButton = new JButton(" Exit Game ");
//        exitButton.setFont(new Font("Ink Free", Font.BOLD, 22));
//        exitButton.setBackground(Color.RED);
//        exitButton.setForeground(Color.WHITE);
//        exitButton.setFocusPainted(false);
//        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//        exitButton.addActionListener(this);
//
//        bg.add(title);
//        bg.add(Box.createVerticalStrut(20));
//        bg.add(nameLabel); bg.add(Box.createVerticalStrut(10));
//        bg.add(nameField); bg.add(Box.createVerticalStrut(20));
//        bg.add(levelLabel); bg.add(Box.createVerticalStrut(10));
//        bg.add(levelBox); bg.add(Box.createVerticalStrut(40));
//        bg.add(startButton); bg.add(Box.createVerticalStrut(20));
//        bg.add(exitButton);
//
//        add(bg, BorderLayout.CENTER);
//        setVisible(true);
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == exitButton) {
//            System.exit(0);
//        }
//        if (e.getSource() == startButton) {
//            String name = nameField.getText().trim();
//            if (name.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Please enter your name!", "Warning", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            int selectedLevel = Integer.parseInt((String) levelBox.getSelectedItem());
//
//            JFrame gameFrame = new JFrame("Hungry Tail – " + name);
//            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            gameFrame.setResizable(false);
//
//            Board board = new Board();
//            board.setPlayerName(name);           // PASS NAME
//            board.level = selectedLevel;
//            board.initGame();                    // CALL initGame() FIRST
//            board.setLevelDifficulty();          // THEN apply difficulty
//
//            gameFrame.add(board);
//            gameFrame.pack();
//            gameFrame.setLocationRelativeTo(null);
//            gameFrame.setVisible(true);
//
//            dispose(); // Close HomePage
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(HomePage::new);
//    }
//}
