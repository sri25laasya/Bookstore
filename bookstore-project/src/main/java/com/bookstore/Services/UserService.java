package com.bookstore.Services;

import com.bookstore.Models.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    private static final String URL = "jdbc:mysql://localhost:3306/BookStoreDB"; // Your database URL
    private static final String USER = "root"; // Your database username
    private static final String PASSWORD = "WJ28@krhps"; // Your database password

    private Connection connection;

    public UserService() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a new user
    public void createUser(String username, String password, String email) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password); // Store hashed password in a real application
            preparedStatement.setString(3, email);
            preparedStatement.executeUpdate();
            System.out.println("User created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read a user by username
    public User readUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                return new User(id, username, password, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if user not found
    }

    // Update user details
    public void updateUser(int id, String username, String password, String email) {
        String sql = "UPDATE users SET username = ?, password = ?, email = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password); // Store hashed password in a real application
            preparedStatement.setString(3, email);
            preparedStatement.setInt(4, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User updated successfully!");
            } else {
                System.out.println("No user found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a user
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("No user found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Close the connection
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
