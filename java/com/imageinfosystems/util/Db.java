package com.imageinfosystems.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {

    private static final String URL = "jdbc:postgresql://localhost:5432/imageinfosystems";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres123";

    public static Connection getConnection() throws SQLException {
        Connection c = null;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver missing from classpath",e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}