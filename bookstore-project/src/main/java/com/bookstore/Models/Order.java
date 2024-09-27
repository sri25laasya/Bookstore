package com.bookstore.Models;

public class Order {
    private int orderId;
    private int bookId;
    private int quantity;
    private String customerName;

    public Order(int orderId, int bookId, int quantity, String customerName) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.customerName = customerName;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCustomerName() {
        return customerName;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", bookId=" + bookId +
                ", quantity=" + quantity +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}
