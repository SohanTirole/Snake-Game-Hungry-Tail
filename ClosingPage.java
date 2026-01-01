package snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Closing Page for Hungry Tail Snake Game
 * Shows game results and high score from MySQL
 */
public class ClosingPage extends JFrame implements ActionListener {
    private final String playerName;
    private final int score, level, timePlayed;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ClosingPage(String player, int score, int level, int timePlayed) {
        this.playerName = player;
        this.score = score;
        this.level = level;
        this.timePlayed = timePlayed;

        setTitle("Game Over – Hungry Tail");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Gradient background panel
        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(20, 20, 20),
                        0, getHeight(), new Color(60, 0, 0)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel title = new JLabel("Game Over!");
        title.setFont(new Font("Ink Free", Font.BOLD, 52));
        title.setForeground(Color.RED);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        bg.add(title, c);

        // Player Stats
        String timeStr = String.format("%02d:%02d", timePlayed / 60, timePlayed % 60);
        JLabel[] stats = {
                new JLabel("Player: " + playerName),
                new JLabel("Score : " + score),
                new JLabel("Level : " + level),
                new JLabel("Time  : " + timeStr)
        };
        for (JLabel l : stats) {
            l.setFont(new Font("Ink Free", Font.PLAIN, 28));
            l.setForeground(Color.WHITE);
            c.gridy++;
            bg.add(l, c);
        }

        // Save to DB
        Database.saveRecord(playerName, score, level, timePlayed);

        // High Score Title
        JLabel hsTitle = new JLabel("=== TOP 5 HIGH SCORES ===");
        hsTitle.setFont(new Font("Ink Free", Font.BOLD, 30));
        hsTitle.setForeground(Color.YELLOW);
        c.gridy++; c.insets = new Insets(20, 10, 10, 10);
        bg.add(hsTitle, c);

        // Load Top 5 from DB
        List<Database.Record> topScores = Database.getTopScores(5);

        if (topScores.isEmpty()) {
            JLabel noData = new JLabel("No high scores yet!");
            noData.setFont(new Font("Ink Free", Font.PLAIN, 24));
            noData.setForeground(Color.GRAY);
            c.gridy++;
            bg.add(noData, c);
        } else {
            for (int i = 0; i < topScores.size(); i++) {
                Database.Record r = topScores.get(i);
                String rank = (i + 1) + ".";
                String name = r.name;
                String scr = String.valueOf(r.score);
                String lvl = "Lvl " + r.level;
                String tm = String.format("%02d:%02d", r.time / 60, r.time % 60);

                JPanel row = new JPanel(new GridLayout(1, 5, 10, 0));
                row.setOpaque(false);

                JLabel[] cells = {
                        createLabel(rank, 24, Color.CYAN),
                        createLabel(name, 24, Color.WHITE),
                        createLabel(scr, 24, Color.GREEN),
                        createLabel(lvl, 22, Color.ORANGE),
                        createLabel(tm, 22, Color.LIGHT_GRAY)
                };
                for (JLabel cell : cells) row.add(cell);

                c.gridy++;
                bg.add(row, c);
            }
        }

        // Buttons
        JButton again = createButton("Play Again", Color.GREEN, Color.BLACK);
        JButton exit = createButton("Exit", Color.RED, Color.WHITE);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(again);
        btnPanel.add(exit);

        c.gridy++; c.insets = new Insets(30, 10, 10, 10);
        bg.add(btnPanel, c);

        add(bg);
        setVisible(true);
    }

    private JLabel createLabel(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Ink Free", Font.PLAIN, size));
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Ink Free", Font.BOLD, 24));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("Play Again".equals(cmd)) {
            dispose();
            SwingUtilities.invokeLater(HomePage::new);
        } else if ("Exit".equals(cmd)) {
            System.exit(0);
        }
    }

    // === TEST MAIN ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClosingPage("TestPlayer", 150, 5, 245));
    }
}