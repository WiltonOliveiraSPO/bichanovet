package br.com.bichanovet.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoSQLite {

    private static final String URL =
        "jdbc:sqlite:C:/bichanovet/bichanovet.db";

    public static Connection conectar() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }
}
