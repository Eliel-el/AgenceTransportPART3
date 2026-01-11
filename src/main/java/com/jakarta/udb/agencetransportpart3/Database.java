/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jakarta.udb.agencetransportpart3;

/**
 *
 * @author eliel
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/reservationdb"; // change selon ton nom de BDD
    private static final String USER = "TEST"; // ton user
    private static final String PASSWORD = "TEST"; // ton mot de passe

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // driver MySQL
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
