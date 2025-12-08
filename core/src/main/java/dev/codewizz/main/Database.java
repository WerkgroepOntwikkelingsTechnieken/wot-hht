package dev.codewizz.main;

import java.sql.*;

public class Database {

    public static Connection connection;

    public static void insert(String query) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet query(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wot_hht", "root", "TomToon2015!"
            );
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
