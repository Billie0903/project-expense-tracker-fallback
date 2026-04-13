package com.example.projectexpensetrackerv2;

public class Expense {
    private int id;
    private int projectId; // The ID of the project this expense belongs to
    private String date;
    private double amount;
    private String currency;
    private String type;            // Travel, Materials, etc.
    private String paymentMethod;   // Cash, Credit Card, etc.
    private String claimant;        // Person making the claim
    private String paymentStatus;   // Paid, Pending, Reimbursed
    private String description;     // Optional
    private String location;        // Optional

    // Constructor for adding new expenses
    public Expense(int projectId, String date, double amount, String currency,
                   String type, String paymentMethod, String claimant,
                   String paymentStatus, String description, String location) {
        this.projectId = projectId;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.claimant = claimant;
        this.paymentStatus = paymentStatus;
        this.description = description;
        this.location = location;
    }

    // Constructor for fetching from DB (with ID)
    public Expense(int id, int projectId, String date, double amount, String currency,
                   String type, String paymentMethod, String claimant,
                   String paymentStatus, String description, String location) {
        this.id = id;
        this.projectId = projectId;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.claimant = claimant;
        this.paymentStatus = paymentStatus;
        this.description = description;
        this.location = location;
    }

    // Getters
    public int getId() { return id; }
    public int getProjectId() { return projectId; }
    public String getDate() { return date; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getType() { return type; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getClaimant() { return claimant; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }

    // Helper method for currency conversion
    public double getAmountInGBP() {
        if (currency == null) return amount;
        switch (currency.toUpperCase()) {
            case "USD":
                return amount * 0.79; // Approximate conversion rate
            case "EUR":
                return amount * 0.85;
            case "VND":
                return amount * 0.000031;
            case "GBP":
            default:
                return amount;
        }
    }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    public void setDate(String date) { this.date = date; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setType(String type) { this.type = type; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setClaimant(String claimant) { this.claimant = claimant; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
}