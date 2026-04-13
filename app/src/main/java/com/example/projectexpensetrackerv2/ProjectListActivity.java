package com.example.projectexpensetrackerv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import androidx.appcompat.widget.SearchView; // search bar


public class ProjectListActivity extends AppCompatActivity {

    private ListView listView;
    private ProjectDatabaseHelper dbHelper;
    private ProjectAdapter adapter;
    private SearchView searchView;
    private FirebaseHelper firebaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_list);

        listView = findViewById(R.id.listViewProjects);
        searchView = findViewById(R.id.searchViewProjects); 
        dbHelper = new ProjectDatabaseHelper(this);
        firebaseHelper = new FirebaseHelper(this, dbHelper);

        listView.setEmptyView(findViewById(R.id.tvEmpty));

        setupToolbar();
        setupSearch();

        findViewById(R.id.btnAddProject).setOnClickListener(v -> {
            Intent intent = new Intent(ProjectListActivity.this, MainActivity.class);
            startActivity(intent);
        });

        refreshList();
    }

    private void setupToolbar() {
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbarList);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_backup) {
            performBackup();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performBackup() {
        Toast.makeText(this, "Backing up data to cloud...", Toast.LENGTH_SHORT).show();
        
        firebaseHelper.uploadAllData(new FirebaseHelper.BackupCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ProjectListActivity.this, "Backup Successful!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                new AlertDialog.Builder(ProjectListActivity.this)
                        .setTitle("Backup Failed")
                        .setMessage(error)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

// Search logic here
    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });
    }

    private void performSearch(String query) {
        List<Project> filteredList = dbHelper.searchProjects(query);
        adapter = new ProjectAdapter(this, filteredList);
        listView.setAdapter(adapter);
    }

    private void refreshList() {
        // 1. Get all projects from the DatabaseHelper
        List<Project> projectList = dbHelper.getAllProjects();

        // 2. Setup the adapter with the list of projects
        adapter = new ProjectAdapter(this, projectList);

        // 3. Attach the adapter to the ListView
        listView.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list every time we come back to this screen
        refreshList();
    }

    public void showDeleteDialog(Project project) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Project")
                .setMessage("Are you sure you want to delete \"" + project.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteProject(project.getId());
                    Toast.makeText(this, "Project deleted", Toast.LENGTH_SHORT).show();
                    refreshList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}