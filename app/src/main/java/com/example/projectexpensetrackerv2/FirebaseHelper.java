package com.example.projectexpensetrackerv2; // This tells the computer this file is part of our project's folder

// Import the tools we need to talk to Android and Firebase (Google's online database)
import android.content.Context; // Provides context about the app
import com.google.firebase.firestore.FirebaseFirestore; // The main tool to connect to Firestore
import com.google.firebase.firestore.WriteBatch;       // Used to send many pieces of data at once to the internet
import java.util.HashMap;                             // A container to store data as "Labels" and "Values"
import java.util.List;                                // A list to hold our projects and expenses
import java.util.Map;                                 // An interface for Map containers like HashMap

/**
 * FIREBASE HELPER: This file handles backing up your local data to the cloud (Internet).
 * It takes everything saved on the phone and sends a copy to Google's online database.
 */
public class FirebaseHelper {

    private FirebaseFirestore db; // The connection to the online Firebase database
    private DbHelper sqlite;      // The connection to the local database on the phone

    // Constructor: Sets up the helper when it is created
    public FirebaseHelper(Context context, DbHelper sqlite) {
        // Connect to Firebase Firestore
        this.db = FirebaseFirestore.getInstance();
        // Use the existing local database helper
        this.sqlite = sqlite;
    }

    // A Callback is like a "phone call" back to the main app to say if the backup worked or failed
    public interface BackupCallback {
        void onSuccess();             // Runs if the backup worked perfectly
        void onFailure(String error);  // Runs if something went wrong (like no internet)
    }

    // The main function that uploads all projects and expenses to the internet
    public void uploadAllData(BackupCallback callback) {
        // 1. Get all projects from the phone's local database
        List<Models.Project> projects = sqlite.getAllProjects();

        // 2. If there are no projects, warn the user and stop
        if (projects.isEmpty()) {
            callback.onFailure("No data to backup");
            return;
        }

        // 3. Create a "Batch". This is like a big box where we put many updates to send at once.
        WriteBatch batch = db.batch();

        // 4. Loop through every single project found on the phone
        for (Models.Project p : projects) {
            // Create a Map (list of labels and data) for the project
            Map<String, Object> pMap = new HashMap<>();
            pMap.put("name", p.getName());          // Label "name" = Project Name
            pMap.put("code", p.getProjectCode());    // Label "code" = Project Code
            pMap.put("budget", p.getBudget());      // Label "budget" = Project Budget
            pMap.put("status", p.getStatus());      // Label "status" = Project Status
            pMap.put("priority", p.getPriority());  // Label "priority" = Project Priority
            pMap.put("urgent", p.isUrgent());        // Label "urgent" = Is it urgent?

            // Tell the batch: "Prepare to save this project map to the online 'projects' collection"
            batch.set(db.collection("projects").document(String.valueOf(p.getId())), pMap);

            // 5. For each project, also get its expenses from the local database
            List<Models.Expense> expenses = sqlite.getExpensesForProject(p.getId());
            
            // Loop through each expense belonging to this project
            for (Models.Expense e : expenses) {
                // Create a Map for the expense data
                Map<String, Object> eMap = new HashMap<>();
                eMap.put("amount", e.getAmount());   // Label "amount" = Amount spent
                eMap.put("type", e.getType());       // Label "type" = Category (Food, etc)
                eMap.put("claimant", e.getClaimant()); // Label "claimant" = Person name
                
                // Prepare to save this expense inside the project's own folder in the cloud
                batch.set(db.collection("projects").document(String.valueOf(p.getId()))
                        .collection("expenses").document(String.valueOf(e.getId())), eMap);
            }
        }

        // 6. Finally, "Commit" the batch. This actually sends all the data to the internet.
        batch.commit()
                // If it works, tell the callback it was a success
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                // If it fails, tell the callback what the error was
                .addOnFailureListener(err -> callback.onFailure(err.getMessage()));
    }
}