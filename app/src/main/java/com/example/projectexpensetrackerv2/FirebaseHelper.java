package com.example.projectexpensetrackerv2;

import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FIREBASE HELPER: Handles cloud backup.
 * Simple logic: Loop through all projects and expenses, and save them to Firestore.
 */
public class FirebaseHelper {

    private FirebaseFirestore db;
    private DbHelper sqlite;

    public FirebaseHelper(Context context, DbHelper sqlite) {
        this.db = FirebaseFirestore.getInstance();
        this.sqlite = sqlite;
    }

    public interface BackupCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void uploadAllData(BackupCallback callback) {
        List<Models.Project> projects = sqlite.getAllProjects();

        if (projects.isEmpty()) {
            callback.onFailure("No data to backup");
            return;
        }

        WriteBatch batch = db.batch();

        for (Models.Project p : projects) {
            Map<String, Object> pMap = new HashMap<>();
            pMap.put("name", p.getName());
            pMap.put("code", p.getProjectCode());
            pMap.put("budget", p.getBudget());
            pMap.put("status", p.getStatus());
            pMap.put("priority", p.getPriority());
            pMap.put("urgent", p.isUrgent());

            // Save project
            batch.set(db.collection("projects").document(String.valueOf(p.getId())), pMap);

            // Save expenses for this project
            List<Models.Expense> expenses = sqlite.getExpensesForProject(p.getId());
            for (Models.Expense e : expenses) {
                Map<String, Object> eMap = new HashMap<>();
                eMap.put("amount", e.getAmount());
                eMap.put("type", e.getType());
                eMap.put("claimant", e.getClaimant());
                
                batch.set(db.collection("projects").document(String.valueOf(p.getId()))
                        .collection("expenses").document(String.valueOf(e.getId())), eMap);
            }
        }

        batch.commit()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(err -> callback.onFailure(err.getMessage()));
    }
}