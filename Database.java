package snakegame;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/snakegame?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";        // CHANGE ME
    private static final String PASS = "root"; // CHANGE ME

    private Database() {}

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static class Record {
        public final String name;
        public final int score, level, time;
        public final Timestamp timestamp;

        Record(String name, int score, int level, int time, Timestamp ts) {
            this.name = name; this.score = score; this.level = level;
            this.time = time; this.timestamp = ts;
        }
    }

    // Save a new high-score
    public static void saveRecord(String name, int score, int level, int time) {
        String sql = "INSERT INTO highscores (player_name, score, level, play_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, score);
            ps.setInt(3, level);
            ps.setInt(4, time);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save record: " + e.getMessage());
        }
    }

    // Get top N scores
    public static List<Record> getTopScores(int limit) {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT player_name, score, level, play_time, played_at " +
                     "FROM highscores ORDER BY score DESC, played_at ASC LIMIT ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Record(
                    rs.getString("player_name"),
                    rs.getInt("score"),
                    rs.getInt("level"),
                    rs.getInt("play_time"),
                    rs.getTimestamp("played_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load scores: " + e.getMessage());
        }
        return list;
    }
}