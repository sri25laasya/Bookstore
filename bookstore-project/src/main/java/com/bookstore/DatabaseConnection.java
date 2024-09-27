package com.bookstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/BookStoreDB"; // Use your database name here
    private static final String USER = "root";
    private static final String PASSWORD = "WJ28@krhps";

    private static Connection connection;
    private static Statement statement; 
    private static ResultSet resultSet;
    private static PreparedStatement preparedStatement;
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try {
            // Establish connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            createRecord();  // Insert a new book record
            readRecords();   // Read all book records
            updateRecord();  // Update an existing book record by ID
            deleteRecord();  // Delete a book record by ID
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Create table statement for reference
    /*
    CREATE TABLE books (
        id INT PRIMARY KEY,
        name VARCHAR(100),
        author VARCHAR(100),
        price DECIMAL(10, 2)
    );
    */

    // Create a new record in the 'books' table
    private static void createRecord() {
        String sql = "INSERT INTO books (id, name, author, price) VALUES (?, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 21); // Set unique ID for each new record
            preparedStatement.setString(2, "The Alchemist");
            preparedStatement.setString(3, "Paulo Coelho");
            preparedStatement.setDouble(4, 9.99);
            preparedStatement.executeUpdate();
            System.out.println("Record created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read all records from the 'books' table
    private static void readRecords() {
        String sql = "SELECT * FROM books";
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            System.out.println("Reading all records from 'books' table:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String author = resultSet.getString("author");
                double price = resultSet.getDouble("price");

                System.out.printf("ID: %d, Name: %s, Author: %s, Price: %.2f\n", id, name, author, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update a record in the 'books' table
    private static void updateRecord() {
        String sql = "UPDATE books SET price = ? WHERE id = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, 12.99); // New price
            preparedStatement.setInt(2, 21); // ID of the record to update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Record updated successfully!");
            } else {
                System.out.println("No record found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a record from the 'books' table
    private static void deleteRecord() {
        String sql = "DELETE FROM books WHERE id = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 21); // ID of the record to delete
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Record deleted successfully!");
            } else {
                System.out.println("No record found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
