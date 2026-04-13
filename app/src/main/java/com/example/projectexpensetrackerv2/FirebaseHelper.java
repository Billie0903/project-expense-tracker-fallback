package com.example.projectexpensetrackerv2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {

    private FirebaseFirestore db;
    private ProjectDatabaseHelper sqliteHelper;
    private Context context;

    public FirebaseHelper(Context context, ProjectDatabaseHelper sqliteHelper) {
        this.db = FirebaseFirestore.getInstance();
        this.sqliteHelper = sqliteHelper;
        this.context = context;
    }

    public interface BackupCallback {
        void onSuccess();
        void onFailure(String error);
    }

    /**
     * Checks if the device has an active internet connection.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    /**
     * Uploads all local projects and expenses to Firestore.
     * Uses the project ID as the document ID to ensure synchronization/overwriting of existing data.
     */
    public void uploadAllData(BackupCallback callback) {
        // 1. Check for Network Connectivity
        if (!isNetworkAvailable()) {
            callback.onFailure("No internet connection available. Please check your network settings.");
            return;
        }

        List<Project> projects = sqliteHelper.getAllProjects();

        if (projects.isEmpty()) {
            callback.onFailure("No data in local database to backup.");
            return;
        }

        // Use a WriteBatch for efficient uploading and to ensure atomic operations where possible
        WriteBatch batch = db.batch();

        for (Project project : projects) {
            // Create a Map for the project data
            Map<String, Object> projectMap = new HashMap<>();
            projectMap.put("id", project.getId());
            projectMap.put("name", project.getName());
            projectMap.put("code", project.getProjectCode());
            projectMap.put("description", project.getDescription());
            projectMap.put("startDate", project.getStartDate());
            projectMap.put("endDate", project.getEndDate());
            projectMap.put("budget", project.getBudget());
            projectMap.put("status", project.getStatus());
            projectMap.put("manager", project.getManager());

            // By using String.valueOf(project.getId()), we ensure that if the local
            // data is changed, it overwrites the specific document in the cloud (Sync).
            batch.set(db.collection("projects").document(String.valueOf(project.getId())), projectMap);

            // Handle Expenses sub-collection
            List<Expense> expenses = sqliteHelper.getExpensesForProject(project.getId());
            for (Expense expense : expenses) {
                Map<String, Object> expMap = new HashMap<>();
                expMap.put("id", expense.getId());
                expMap.put("amount", expense.getAmount());
                expMap.put("type", expense.getType());
                expMap.put("date", expense.getDate());
                expMap.put("currency", expense.getCurrency());
                expMap.put("paymentMethod", expense.getPaymentMethod());
                expMap.put("claimant", expense.getClaimant());
                expMap.put("paymentStatus", expense.getPaymentStatus());
                expMap.put("description", expense.getDescription());
                expMap.put("location", expense.getLocation());

                // Nested collection for better relational mapping in Firestore
                batch.set(db.collection("projects")
                        .document(String.valueOf(project.getId()))
                        .collection("expenses")
                        .document(String.valueOf(expense.getId())), expMap);
            }
        }

        // Commit all changes to the cloud
        batch.commit()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure("Backup failed: " + e.getMessage()));
    }
}