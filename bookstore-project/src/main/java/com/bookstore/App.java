package com.bookstore;

import com.bookstore.Services.BookService;
import com.bookstore.Services.OrderService;
import com.bookstore.Models.Order;
// import com.bookstore.Models.Book;
// import com.bookstore.Models.User;
// import com.bookstore.DatabaseConnection;
// import com.bookstore.Services.UserService;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BookService bookService = new BookService(); // Assuming you have a BookService class
        OrderService orderService = new OrderService();

        while (true) {
            System.out.println("\nWelcome to the BookStore Application");
            System.out.println("1. Manage Books");
            System.out.println("2. Manage Orders");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    manageBooks(scanner, bookService);
                    break;
                case 2:
                    manageOrders(scanner, orderService);
                    break;
                case 3:
                    System.out.println("Exiting the application...");
                    orderService.closeResources(); // Close resources before exiting
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void manageBooks(Scanner scanner, BookService bookService) {
        while (true) {
            System.out.println("\nManage Books");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Update Book");
            System.out.println("4. Delete Book");
            System.out.println("5. Go Back");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Add book logic
                    System.out.print("Enter Book ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter Book Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Author Name: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter Price: ");
                    double price = scanner.nextDouble();
                    bookService.createRecord(id, name, author, price);
                    break;
                case 2:
                    bookService.readRecords();
                    break;
                case 3:
                    // Update book logic
                    System.out.print("Enter Book ID to Update: ");
                    int updateId = scanner.nextInt();
                    System.out.print("Enter New Name: ");
                    scanner.nextLine(); // Consume newline
                    String newName = scanner.nextLine();
                    System.out.print("Enter New Author Name: ");
                    String newAuthor = scanner.nextLine();
                    System.out.print("Enter New Price: ");
                    double newPrice = scanner.nextDouble();
                    bookService.updateRecord(updateId, newName, newAuthor, newPrice);
                    break;
                case 4:
                    // Delete book logic
                    System.out.print("Enter Book ID to Delete: ");
                    int deleteId = scanner.nextInt();
                    bookService.deleteRecord(deleteId);
                    break;
                case 5:
                    return; // Go back to the main menu
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void manageOrders(Scanner scanner, OrderService orderService) {
        while (true) {
            System.out.println("\nManage Orders");
            System.out.println("1. Add Order");
            System.out.println("2. View Order");
            System.out.println("3. Update Order");
            System.out.println("4. Delete Order");
            System.out.println("5. Go Back");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Add order logic
                    System.out.print("Enter Book ID: ");
                    int bookId = scanner.nextInt();
                    System.out.print("Enter Quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter Customer Name: ");
                    String customerName = scanner.nextLine();
                    orderService.createOrder(bookId, quantity, customerName);
                    break;
                case 2:
                    // View order logic
                    System.out.print("Enter Order ID to View: ");
                    int orderId = scanner.nextInt();
                    Order order = orderService.readOrder(orderId);
                    if (order != null) {
                        System.out.println("Order Details: " + order);
                    } else {
                        System.out.println("Order not found.");
                    }
                    break;
                case 3:
                    // Update order logic
                    System.out.print("Enter Order ID to Update: ");
                    int updateOrderId = scanner.nextInt();
                    System.out.print("Enter New Book ID: ");
                    int newBookId = scanner.nextInt();
                    System.out.print("Enter New Quantity: ");
                    int newQuantity = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter New Customer Name: ");
                    String newCustomerName = scanner.nextLine();
                    orderService.updateOrder(updateOrderId, newBookId, newQuantity, newCustomerName);
                    break;
                case 4:
                    // Delete order logic
                    System.out.print("Enter Order ID to Delete: ");
                    int deleteOrderId = scanner.nextInt();
                    orderService.deleteOrder(deleteOrderId);
                    break;
                case 5:
                    return; // Go back to the main menu
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
}
