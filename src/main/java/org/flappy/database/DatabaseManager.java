package org.flappy.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:flappy.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = """
            CREATE TABLE IF NOT EXISTS scores (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                score INTEGER NOT NULL,
                difficulty TEXT NOT NULL
            );
            """;
            stmt.execute(sql);

            String createCoinsTable = """
            CREATE TABLE IF NOT EXISTS coins (
                id INTEGER PRIMARY KEY CHECK (id = 0),
                amount INTEGER NOT NULL
            );
            """;
            stmt.execute(createCoinsTable);

            String createSkinsTable = """
            CREATE TABLE IF NOT EXISTS unlocked_skins (
                skin_name TEXT PRIMARY KEY
            );
            """;
            stmt.execute(createSkinsTable);

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM coins");
            if (rs.next() && rs.getInt("count") == 0) {
                stmt.execute("INSERT INTO coins (id, amount) VALUES (0, 0)");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveScore(String name, int score, String difficulty) {
        String sql = "INSERT INTO scores(name, score, difficulty) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, score);
            pstmt.setString(3, difficulty);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTopScores(String difficulty, int limit) {
        List<String> results = new ArrayList<>();
        String sql = "SELECT name, score FROM scores WHERE difficulty = ? ORDER BY score DESC LIMIT ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, difficulty);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("name") + ": " + rs.getInt("score"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static int getCoins() {
        String sql = "SELECT amount FROM coins WHERE id = 0";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setCoins(int value) {
        String sql = "UPDATE coins SET amount = ? WHERE id = 0";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unlockSkin(String skinName) {
        String sql = "INSERT OR IGNORE INTO unlocked_skins(skin_name) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, skinName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getUnlockedSkins() {
        Set<String> skins = new HashSet<>();
        String sql = "SELECT skin_name FROM unlocked_skins";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                skins.add(rs.getString("skin_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return skins;
    }
}