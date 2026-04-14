package com.example.projectexpensetrackerv2; // This defines which package (folder) this file belongs to

/**
 * MODELS FILE: This file contains all the data structures (Classes) used in the app.
 * By putting them all here, we make the project structure much simpler to read.
 * These are like "containers" for our data.
 */

public class Models { // The main container class for all our data models

    // --- PROJECT MODEL ---
    // This class represents a single Project in the app
    public static class Project { 
        private int id;             // The unique ID number used for the database
        private String projectCode; // A short code for the project (e.g., "PROJ001")
        private String name;        // The full name of the project
        private String description; // A longer text describing what the project is about
        private String startDate;   // The date when the project begins
        private String endDate;     // The date when the project is finished
        private String manager;     // The person in charge of the project
        private String status;      // Current state (e.g., "In Progress", "Completed")
        private double budget;      // The total amount of money allocated for this project
        
        // New fields for demo requirement 9 (Radio, Toggle, Checkbox)
        private String priority;   // Stores "High", "Medium", or "Low" (from Radio buttons)
        private boolean isUrgent;  // Stores true or false for urgency (from a Toggle switch)
        private boolean isChecked; // Stores true or false for a simple checkbox marking

        // Empty constructor: Required by some tools to create a project object without data
        public Project() {} 

        // Main constructor: Used to create a project and fill in all its details at once
        public Project(int id, String projectCode, String name, String description,
                       String startDate, String endDate, String manager,
                       String status, double budget, String priority, boolean isUrgent, boolean isChecked) {
            this.id = id;                     // Set the ID
            this.projectCode = projectCode;   // Set the project code
            this.name = name;                 // Set the name
            this.description = description;   // Set the description
            this.startDate = startDate;       // Set the start date
            this.endDate = endDate;           // Set the end date
            this.manager = manager;           // Set the manager's name
            this.status = status;             // Set the status
            this.budget = budget;             // Set the budget amount
            this.priority = priority;         // Set the priority level
            this.isUrgent = isUrgent;         // Set the urgency status
            this.isChecked = isChecked;       // Set the checkbox status
        }

        // --- GETTERS AND SETTERS ---
        // These are standard "helper" functions to get or set (change) the data in the project.

        public int getId() { return id; } // Returns the project's ID
        public void setId(int id) { this.id = id; } // Changes the project's ID
        
        public String getProjectCode() { return projectCode; } // Returns the code
        public void setProjectCode(String projectCode) { this.projectCode = projectCode; } // Changes the code
        
        public String getName() { return name; } // Returns the name
        public void setName(String name) { this.name = name; } // Changes the name
        
        public String getDescription() { return description; } // Returns the description
        public void setDescription(String description) { this.description = description; } // Changes the description
        
        public String getStartDate() { return startDate; } // Returns start date
        public void setStartDate(String startDate) { this.startDate = startDate; } // Changes start date
        
        public String getEndDate() { return endDate; } // Returns end date
        public void setEndDate(String endDate) { this.endDate = endDate; } // Changes end date
        
        public String getManager() { return manager; } // Returns manager name
        public void setManager(String manager) { this.manager = manager; } // Changes manager name
        
        public String getStatus() { return status; } // Returns status (e.g. Open/Closed)
        public void setStatus(String status) { this.status = status; } // Changes status
        
        public double getBudget() { return budget; } // Returns budget amount
        public void setBudget(double budget) { this.budget = budget; } // Changes budget amount
        
        public String getPriority() { return priority; } // Returns priority level
        public void setPriority(String priority) { this.priority = priority; } // Changes priority
        
        public boolean isUrgent() { return isUrgent; } // Returns if project is urgent
        public void setUrgent(boolean urgent) { isUrgent = urgent; } // Changes urgency status
        
        public boolean isChecked() { return isChecked; } // Returns if checkbox is checked
        public void setChecked(boolean checked) { isChecked = checked; } // Changes checkbox status
    }

    // --- EXPENSE MODEL ---
    // This class represents a single Expense (money spent) belonging to a project
    public static class Expense {
        private int id;              // Unique ID for the expense in the database
        private int projectId;       // The ID of the Project this expense belongs to (the "link")
        private String date;         // The date when money was spent
        private double amount;       // How much money was spent
        private String currency;     // The type of money (e.g., "USD", "EUR", "GBP")
        private String type;         // Category of expense (e.g., "Travel", "Food")
        private String paymentMethod;// How it was paid (e.g., "Cash", "Card")
        private String claimant;      // The person who spent the money
        private String paymentStatus;// Status of payment (e.g., "Paid", "Pending")
        private String description;  // Notes about what was bought
        private String location;     // Where the expense happened

        // Empty constructor: Creates an empty expense object
        public Expense() {}

        // Main constructor: Fills in all expense details at once when created
        public Expense(int id, int projectId, String date, double amount, String currency,
                       String type, String paymentMethod, String claimant,
                       String paymentStatus, String description, String location) {
            this.id = id;                     // Set ID
            this.projectId = projectId;       // Set the project link
            this.date = date;                 // Set date
            this.amount = amount;             // Set amount
            this.currency = currency;         // Set currency
            this.type = type;                 // Set category type
            this.paymentMethod = paymentMethod; // Set payment method
            this.claimant = claimant;         // Set person's name
            this.paymentStatus = paymentStatus; // Set status
            this.description = description;   // Set notes
            this.location = location;         // Set location
        }

        // --- GETTERS AND SETTERS ---
        // These allow the app to read and write data to the expense object.

        public int getId() { return id; } // Gets expense ID
        public void setId(int id) { this.id = id; } // Sets expense ID
        
        public int getProjectId() { return projectId; } // Gets the linked project's ID
        public void setProjectId(int projectId) { this.projectId = projectId; } // Changes the project link
        
        public String getDate() { return date; } // Gets date
        public void setDate(String date) { this.date = date; } // Sets date
        
        public double getAmount() { return amount; } // Gets amount spent
        public void setAmount(double amount) { this.amount = amount; } // Sets amount spent
        
        public String getCurrency() { return currency; } // Gets currency code (USD, etc.)
        public void setCurrency(String currency) { this.currency = currency; } // Sets currency code
        
        public String getType() { return type; } // Gets expense category
        public void setType(String type) { this.type = type; } // Sets expense category
        
        public String getPaymentMethod() { return paymentMethod; } // Gets how it was paid
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; } // Sets payment method
        
        public String getClaimant() { return claimant; } // Gets person who spent money
        public void setClaimant(String claimant) { this.claimant = claimant; } // Sets person's name
        
        public String getPaymentStatus() { return paymentStatus; } // Gets payment status
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; } // Sets payment status
        
        public String getDescription() { return description; } // Gets description notes
        public void setDescription(String description) { this.description = description; } // Sets description notes
        
        public String getLocation() { return location; } // Gets location
        public void setLocation(String location) { this.location = location; } // Sets location

        // --- SPECIAL CALCULATIONS ---
        // Simplified Currency conversion used for the project demo
        public double getAmountInGBP() {
            // If currency is USD, multiply by 0.8 to get estimated GBP
            if ("USD".equalsIgnoreCase(currency)) return amount * 0.8;
            // If currency is EUR, multiply by 0.9 to get estimated GBP
            if ("EUR".equalsIgnoreCase(currency)) return amount * 0.9;
            // Otherwise, assume it is already GBP or another default
            return amount; 
        }
    }
}
