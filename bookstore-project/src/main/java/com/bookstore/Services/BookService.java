package com.bookstore.Services;

import com.bookstore.DatabaseConnection;
import com.bookstore.Models.Book;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class BookService {
    private DatabaseConnection databaseConnection;

    // Create a Guava Cache for books
    private Cache<Integer, Book> bookCache;

    public BookService() {
        this.databaseConnection = new DatabaseConnection();
        this.bookCache = CacheBuilder.newBuilder()
                .maximumSize(100) // Maximum number of entries in the cache
                .expireAfterWrite(10, TimeUnit.MINUTES) // Expire entries after 10 minutes
                .build();
    }

    // Create a new record in the 'books' table
    public void createRecord(int id, String name, String author, double price) {
        String sql = "INSERT INTO books (id, name, author, price) VALUES (?, ?, ?, ?)";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id); // Set unique ID for each new record
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, author);
            preparedStatement.setDouble(4, price);
            preparedStatement.executeUpdate();
            System.out.println("Book record created successfully!");

            // Cache the new book
            bookCache.put(id, new Book(id, name, author, price));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read all records from the 'books' table
    public void readRecords() {
        String sql = "SELECT * FROM books";
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Reading all records from 'books' table:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String author = resultSet.getString("author");
                double price = resultSet.getDouble("price");

                // Cache the book if not already cached
                bookCache.put(id, new Book(id, name, author, price));
                System.out.printf("ID: %d, Name: %s, Author: %s, Price: %.2f\n", id, name, author, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve a book by ID (with caching)
    public Book getBookById(int id) {
        // Check if the book is in the cache
        Book cachedBook = bookCache.getIfPresent(id);
        if (cachedBook != null) {
            System.out.println("Retrieved book from cache: " + cachedBook);
            return cachedBook;
        }

        // If not in cache, fetch from the database
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String author = resultSet.getString("author");
                double price = resultSet.getDouble("price");

                // Cache the fetched book
                Book book = new Book(id, name, author, price);
                bookCache.put(id, book);
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Book not found
    }

    // Update a record in the 'books' table
    public void updateRecord(int id, String newName, String newAuthor, double newPrice) {
        String sql = "UPDATE books SET name = ?, author = ?, price = ? WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newAuthor);
            preparedStatement.setDouble(3, newPrice);
            preparedStatement.setInt(4, id); // ID of the record to update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book record updated successfully!");
                // Update the cache
                bookCache.put(id, new Book(id, newName, newAuthor, newPrice));
            } else {
                System.out.println("No record found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a record from the 'books' table
    public void deleteRecord(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id); // ID of the record to delete
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book record deleted successfully!");
                // Remove from cache
                bookCache.invalidate(id);
            } else {
                System.out.println("No record found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
