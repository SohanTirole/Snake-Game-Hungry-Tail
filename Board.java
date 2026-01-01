package snakegame;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import javax.sound.sampled.*;
import java.util.List;

public final class Board extends JPanel implements ActionListener {
    /* ---------- IMAGES & SOUNDS ---------- */
    private Image apple, dot, head;
    private Clip eatClip, overClip, levelClip;

    /* ---------- CONSTANTS ---------- */
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    private final int ALL_DOTS = 3600;
    private final int DOT_SIZE = 10;
    private final int RANDOM_POSITION = 59;

    /* ---------- GAME STATE ---------- */
    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];
    int dots, applesEaten, level = 1;
    private int elapsedTime = 0;
    private boolean left = false, right = true, up = false, down = false;
    private boolean inGame = true, paused = false;

    private int apple_x, apple_y;
    private Timer timer, gameTimer;
    private final int MAX_LEVEL = 5;
    private final List<Point>[] levelObstacles = new List[MAX_LEVEL + 1];

    private String playerName = "Anonymous";

    /* ---------- CONSTRUCTOR ---------- */
    Board() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setFocusable(true);
        addKeyListener(new TAdapter());
        loadImages();
        loadSounds();
    }

    /* ---------- RESOURCE LOADING ---------- */
    private void loadImages() {
        apple = new ImageIcon(getClass().getResource("/snakegame/icon/apple.png")).getImage();
        dot   = new ImageIcon(getClass().getResource("/snakegame/icon/dot.png")).getImage();
        head  = new ImageIcon(getClass().getResource("/snakegame/icon/head.png")).getImage();
    }

    private void loadSounds() {
        eatClip   = loadClip("/snakegame/sound/eat.wav");
        overClip  = loadClip("/snakegame/sound/gameover.wav");
        levelClip = loadClip("/snakegame/sound/levelup.wav");
    }

    private Clip loadClip(String path) {
        try {
            java.net.URL soundURL = getClass().getResource(path);
            if (soundURL == null) {
                System.err.println("Sound not found: " + path);
                return null;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL);
            Clip c = AudioSystem.getClip();
            c.open(ais);
            return c;
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.err.println("Failed to load sound: " + path);
            return null;
        }
    }

    private void play(Clip c) {
        if (c != null) {
            c.stop();
            c.setFramePosition(0);
            c.start();
        }
    }

    /* ---------- PUBLIC SETTER ---------- */
    //public void setPlayerName(String name) { this.playerName = name.isEmpty() ? "Anonymous" : name; }
    
    public void setPlayerName(String name) {
        this.playerName = name.isEmpty() ? "Anonymous" : name;
    }

    /* ---------- GAME INITIALISATION ---------- */
    public void initGame() {
        dots = 6; applesEaten = 0; elapsedTime = 0; level = 1;
        left = false; right = true; up = false; down = false;
        inGame = true; paused = false;

        for (int i = 0; i < dots; i++) {
            x[i] = 50 - i * DOT_SIZE;
            y[i] = 50;
        }

        if (gameTimer != null && gameTimer.isRunning()) gameTimer.stop();
        gameTimer = new Timer(1000, e -> { elapsedTime++; repaint(); });
        gameTimer.start();

        for (int l = 1; l <= MAX_LEVEL; l++) levelObstacles[l] = new java.util.ArrayList<>();

        setLevelDifficulty();
        locateApple();

        if (timer != null) timer.stop();
        timer = new Timer(90, this);
        timer.start();
    }
    

    private void restartGame() {
        inGame = true;
        initGame();
    }

    /* ---------- APPLE POSITION ---------- */
    private void locateApple() {
        boolean valid;
        do {
            valid = true;
            apple_x = ((int) (Math.random() * RANDOM_POSITION)) * DOT_SIZE;
            apple_y = ((int) (Math.random() * RANDOM_POSITION)) * DOT_SIZE;

            for (int i = 0; i < dots; i++) {
                if (x[i] == apple_x && y[i] == apple_y) { valid = false; break; }
            }
            List<Point> obs = levelObstacles[level];
            if (valid && obs != null) {
                for (Point p : obs) {
                    if (p.x == apple_x && p.y == apple_y) { valid = false; break; }
                }
            }
        } while (!valid);
    }

    /* ---------- LEVEL DIFFICULTY ---------- */
    public void setLevelDifficulty() {
        int delay = Math.max(30, 90 - (level - 1) * 15);
        if (timer != null) timer.setDelay(delay);

        if (level >= 2 && levelObstacles[2].isEmpty()) {
            for (int xx = 100; xx <= 500; xx += DOT_SIZE) levelObstacles[2].add(new Point(xx, 300));
        }
        if (level >= 3 && levelObstacles[3].isEmpty()) {
            for (int yy = 100; yy <= 500; yy += DOT_SIZE) levelObstacles[3].add(new Point(300, yy));
        }
        if (level >= 4 && levelObstacles[4].isEmpty()) {
            for (int xx = 200; xx <= 400; xx += DOT_SIZE) {
                levelObstacles[4].add(new Point(xx, 150));
                levelObstacles[4].add(new Point(xx, 450));
            }
        }
        if (level >= 5 && levelObstacles[5].isEmpty()) {
            for (int xx = 250; xx <= 350; xx += DOT_SIZE) {
                levelObstacles[5].add(new Point(xx, 250));
                levelObstacles[5].add(new Point(xx, 350));
            }
            for (int yy = 260; yy < 350; yy += DOT_SIZE) {
                levelObstacles[5].add(new Point(250, yy));
                levelObstacles[5].add(new Point(350, yy));
            }
        }
    }

    /* ---------- PAINT ---------- */
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (inGame) {
            g.setColor(Color.GRAY);
            List<Point> obs = levelObstacles[level];
            if (obs != null) obs.forEach((p) -> {
                g.fillRect(p.x, p.y, DOT_SIZE, DOT_SIZE);
            });

            g.drawImage(apple, apple_x, apple_y, this);
            for (int i = 0; i < dots; i++) g.drawImage(i == 0 ? head : dot, x[i], y[i], this);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.BOLD, 25));
            FontMetrics fm = g.getFontMetrics();

            String score = "Score: " + applesEaten;
            g.drawString(score, (SCREEN_WIDTH - fm.stringWidth(score)) / 2, fm.getHeight());
            g.drawString("Level: " + level, 10, 50);

            String time = String.format("Time: %02d:%02d", elapsedTime / 60, elapsedTime % 60);
            g.drawString(time, SCREEN_WIDTH - fm.stringWidth(time) - 10, 50);

            if (paused) {
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Ink Free", Font.BOLD, 60));
                String txt = "PAUSED";
                g.drawString(txt, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(txt)) / 2, SCREEN_HEIGHT / 2);
                g.setFont(new Font("Ink Free", Font.PLAIN, 30));
                g.drawString("Press P to resume", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("Press P to resume")) / 2, SCREEN_HEIGHT / 2 + 60);
            }

        } else {
            showGameOverScreen(g);
        }
    }

    /* ---------- GAME OVER SCREEN (with DB high-scores) ---------- */
    private void showGameOverScreen(Graphics g) {
        if (timer != null && timer.isRunning()) timer.stop();
        if (gameTimer != null && gameTimer.isRunning()) gameTimer.stop();
        play(overClip);

        // Save to DB
        Database.saveRecord(playerName, applesEaten, level, elapsedTime); 
         
        // === ADD THIS INSIDE showGameOverScreen(), after saving to DB ===
//        SwingUtilities.invokeLater(() -> {
//            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(Board.this);
//            topFrame.dispose(); // Close game frame
//            new ClosingPage(playerName, applesEaten, level, elapsedTime);
//        });
        
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        String go = "Game Over";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(go, (SCREEN_WIDTH - fm.stringWidth(go)) / 2, SCREEN_HEIGHT / 3);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        String[] lines = {
                "Player: " + playerName,
                "Score: " + applesEaten,
                "Level: " + level,
                "Time: " + String.format("%02d:%02d", elapsedTime / 60, elapsedTime % 60)
        };
        int y = SCREEN_HEIGHT / 2;
        for (String s : lines) {
            g.drawString(s, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(s)) / 2, y);
            y += 45;
        }
        
        // High-score table from DB
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Ink Free", Font.BOLD, 28));
        g.drawString("Top 5 Scores", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("Top 5 Scores")) / 2, y + 30);
        y += 60;

        g.setFont(new Font("Ink Free", Font.PLAIN, 22));
        List<Database.Record> top = Database.getTopScores(5);
        for (int i = 0; i < top.size(); i++) {
            Database.Record r = top.get(i);
            String row = String.format("%d. %s - %d (Lvl %d) [%02d:%02d]",
                    i + 1, r.name, r.score, r.level, r.time / 60, r.time % 60);
            g.drawString(row, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(row)) / 2, y);
            y += 35;
        }

        g.setColor(Color.GREEN);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        String hint = "Press R to Restart";
        g.drawString(hint, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(hint)) / 2, SCREEN_HEIGHT - 80); 
        
        // === ADD THIS INSIDE showGameOverScreen(), after saving to DB ===
        SwingUtilities.invokeLater(() -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(Board.this);
            topFrame.dispose(); // Close game frame
            new ClosingPage(playerName, applesEaten, level, elapsedTime);
        });
    }

    /* ---------- LOGIC ---------- */
    private void move() {
        for (int i = dots; i > 0; i--) { x[i] = x[i - 1]; y[i] = y[i - 1]; }
        if (left)  x[0] -= DOT_SIZE;
        if (right) x[0] += DOT_SIZE;
        if (up)    y[0] -= DOT_SIZE;
        if (down)  y[0] += DOT_SIZE;
    }

    private void checkApple() {
        if (x[0] == apple_x && y[0] == apple_y) {
            dots++; applesEaten++;
            play(eatClip);
            locateApple();

            if (applesEaten % 25 == 0 && level < MAX_LEVEL) {
                level++;
                setLevelDifficulty();
                play(levelClip);
            }
        }
    }

    private void checkCollision() {
        for (int z = dots; z > 0; z--)
            if (z > 4 && x[0] == x[z] && y[0] == y[z]) inGame = false;

        if (y[0] >= SCREEN_HEIGHT || y[0] < 0 || x[0] >= SCREEN_WIDTH || x[0] < 0)
            inGame = false;

        List<Point> obs = levelObstacles[level];
        if (obs != null)
            for (Point p : obs)
                if (x[0] == p.x && y[0] == p.y) { inGame = false; break; }
    }

    @Override public void actionPerformed(ActionEvent e) {
        if (inGame && !paused) {
            checkApple();
            checkCollision();
            move();
        }
        repaint();
    }

    /* ---------- KEY HANDLER ---------- */
    private class TAdapter extends KeyAdapter {
        @Override 
        public void keyPressed(KeyEvent e) {
            int k = e.getKeyCode();

            if (inGame && !paused) {
                if (k == KeyEvent.VK_LEFT  && !right) { left = true;  up = down = false; }
                if (k == KeyEvent.VK_RIGHT && !left)  { right = true; up = down = false; }
                if (k == KeyEvent.VK_UP    && !down)  { up = true;    left = right = false; }
                if (k == KeyEvent.VK_DOWN  && !up)    { down = true;  left = right = false; }
            }

            if (k == KeyEvent.VK_P) {
                paused = !paused;
                if (paused) {
                    timer.stop(); gameTimer.stop();
                } else {
                    timer.start(); gameTimer.start();
                }
                repaint();
            }

            if (!inGame && k == KeyEvent.VK_R) restartGame();
        }
    }
}