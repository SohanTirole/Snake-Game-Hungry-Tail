package snakegame;

import java.io.*;
import java.util.*;

public final class HighScore {
    private static final String FILE = "highscore.dat";

    void maybeSave(int score, String playerName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    String getBestPlayer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    String getBestScore() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    String getBestDate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class Entry implements Serializable {
        final String name;
        final int score, level;
        final int time;               // seconds

        Entry(String name, int score, int level, int time) {
            this.name = name;
            this.score = score;
            this.level = level;
            this.time = time;
        }
    }

    private static List<Entry> load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            return (List<Entry>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void save(Entry e) {
        List<Entry> list = load();
        list.add(e);
        list.sort((a, b) -> Integer.compare(b.score, a.score)); // highest first
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(list);
        } catch (IOException ex) {
        }
    }

    public static List<Entry> getTop(int n) {
        List<Entry> all = load();
        return all.subList(0, Math.min(n, all.size()));
    }
}