package com.bookstore.Services;

import com.bookstore.Models.Order;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OrderService {
    private static final String URL = "jdbc:mysql://localhost:3306/BookStoreDB"; // Your database URL
    private static final String USER = "root"; // Your database username
    private static final String PASSWORD = "WJ28@krhps"; // Your database password

    private Connection connection;
    private Cache<Integer, Order> orderCache; // Cache to store order records

    // Create an ExecutorService for handling tasks
    private ExecutorService executorService;

    public OrderService() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            // Initialize the cache with a maximum size and an expiration time
            orderCache = CacheBuilder.newBuilder()
                    .maximumSize(100) // Maximum of 100 entries
                    .expireAfterWrite(10, TimeUnit.MINUTES) // Expire entries after 10 minutes
                    .build();

            // Initialize the ExecutorService with a fixed thread pool
            executorService = Executors.newFixedThreadPool(4); // Number of threads
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a new order
    public void createOrder(int bookId, int quantity, String customerName) {
        String sql = "INSERT INTO orders (book_id, quantity, customer_name) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, bookId);
            preparedStatement.setInt(2, quantity);
            preparedStatement.setString(3, customerName);
            preparedStatement.executeUpdate();
            System.out.println("Order created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read an order by ID with caching
    public Order readOrder(int orderId) {
        // Check if the order is in the cache
        Order order = orderCache.getIfPresent(orderId);
        if (order != null) {
            return order; // Return the cached order
        }

        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                int quantity = resultSet.getInt("quantity");
                String customerName = resultSet.getString("customer_name");
                order = new Order(orderId, bookId, quantity, customerName);
                // Store the order in the cache
                orderCache.put(orderId, order);
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if order not found
    }

    // Update an order
    public void updateOrder(int orderId, int bookId, int quantity, String customerName) {
        String sql = "UPDATE orders SET book_id = ?, quantity = ?, customer_name = ? WHERE order_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, bookId);
            preparedStatement.setInt(2, quantity);
            preparedStatement.setString(3, customerName);
            preparedStatement.setInt(4, orderId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Update the cache if the order is updated
                orderCache.put(orderId, new Order(orderId, bookId, quantity, customerName));
                System.out.println("Order updated successfully!");
            } else {
                System.out.println("No order found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete an order
    public void deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Remove the order from the cache
                orderCache.invalidate(orderId);
                System.out.println("Order deleted successfully!");
            } else {
                System.out.println("No order found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Run a task using Executors
    public void executeTask(Runnable task) {
        executorService.submit(task);
    }

    // Close the connection and shutdown executor
    public void closeResources() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (executorService != null) {
                executorService.shutdown(); // Shutdown the executor service
                executorService.awaitTermination(5, TimeUnit.SECONDS); // Wait for tasks to finish
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
