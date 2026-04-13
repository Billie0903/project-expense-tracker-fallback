package com.example.projectexpensetrackerv2;

/**
 * MODELS FILE: This file contains all the data structures (Classes) used in the app.
 * By putting them all here, we make the project structure much simpler to read.
 */

public class Models {

    // --- PROJECT MODEL ---
    public static class Project {
        private int id;
        private String projectCode;
        private String name;
        private String description;
        private String startDate;
        private String endDate;
        private String manager;
        private String status;
        private double budget;
        
        // New fields for demo requirement 9 (Radio, Toggle, Checkbox)
        private String priority;   // For Radio buttons (High, Medium, Low)
        private boolean isUrgent;  // For Toggle/Switch
        private boolean isChecked; // For Checkbox

        public Project() {}

        public Project(int id, String projectCode, String name, String description,
                       String startDate, String endDate, String manager,
                       String status, double budget, String priority, boolean isUrgent, boolean isChecked) {
            this.id = id;
            this.projectCode = projectCode;
            this.name = name;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.manager = manager;
            this.status = status;
            this.budget = budget;
            this.priority = priority;
            this.isUrgent = isUrgent;
            this.isChecked = isChecked;
        }

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getProjectCode() { return projectCode; }
        public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public String getManager() { return manager; }
        public void setManager(String manager) { this.manager = manager; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public double getBudget() { return budget; }
        public void setBudget(double budget) { this.budget = budget; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public boolean isUrgent() { return isUrgent; }
        public void setUrgent(boolean urgent) { isUrgent = urgent; }
        public boolean isChecked() { return isChecked; }
        public void setChecked(boolean checked) { isChecked = checked; }
    }

    // --- EXPENSE MODEL ---
    public static class Expense {
        private int id;
        private int projectId;
        private String date;
        private double amount;
        private String currency;
        private String type;           
        private String paymentMethod;  
        private String claimant;       
        private String paymentStatus;  
        private String description;    
        private String location;       

        public Expense() {}

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

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getProjectId() { return projectId; }
        public void setProjectId(int projectId) { this.projectId = projectId; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getClaimant() { return claimant; }
        public void setClaimant(String claimant) { this.claimant = claimant; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        // Simplified Currency conversion for the demo
        public double getAmountInGBP() {
            if ("USD".equalsIgnoreCase(currency)) return amount * 0.8;
            if ("EUR".equalsIgnoreCase(currency)) return amount * 0.9;
            return amount; // Default
        }
    }
}
