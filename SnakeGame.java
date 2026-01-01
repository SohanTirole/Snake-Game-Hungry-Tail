package snakegame;


import javax.swing.SwingUtilities;

public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HomePage::new);
    }
}
