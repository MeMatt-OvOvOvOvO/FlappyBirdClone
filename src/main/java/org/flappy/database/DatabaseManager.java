package org.flappy.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
}