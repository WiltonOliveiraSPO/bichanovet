package br.com.bichanovet.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoSQLite {

    private static final String URL = 
        "jdbc:sqlite:C:/bichanovet/bichanovet.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}