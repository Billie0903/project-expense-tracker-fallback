package com.example.projectexpensetrackerv2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import java.util.List;

/**
 * LIST ACTIVITY: Shows all projects in a list.
 * Includes Search and Cloud Backup features.
 * THE ADAPTER IS INSIDE THIS FILE TO KEEP IT SIMPLE.
 */
public class ProjectListActivity extends AppCompatActivity {

    private ListView listView;
    private DbHelper db;
    private FirebaseHelper firebase;
    private ProjectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_list);

        db = new DbHelper(this);
        firebase = new FirebaseHelper(this, db);
        listView = findViewById(R.id.listViewProjects);
        listView.setEmptyView(findViewById(R.id.tvEmpty));

        setupSearch();

        // ADD BUTTON: Go to Add/Edit screen in "Add" mode
        findViewById(R.id.btnAddProject).setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditProjectActivity.class));
        });

        // Set Toolbar
        setSupportActionBar(findViewById(R.id.toolbarList));
    }

    private void setupSearch() {
        SearchView searchView = findViewById(R.id.searchViewProjects);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) { updateList(q); return true; }
            @Override
            public boolean onQueryTextChange(String q) { updateList(q); return true; }
        });
    }

    private void updateList(String query) {
        List<Models.Project> list = (query == null || query.isEmpty()) 
                ? db.getAllProjects() : db.searchProjects(query);
        adapter = new ProjectAdapter(this, list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList(null); // Refresh list when returning to this screen
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_backup) {
            Toast.makeText(this, "Backing up...", Toast.LENGTH_SHORT).show();
            firebase.uploadAllData(new FirebaseHelper.BackupCallback() {
                @Override public void onSuccess() { Toast.makeText(ProjectListActivity.this, "Backup Success!", Toast.LENGTH_SHORT).show(); }
                @Override public void onFailure(String err) { Toast.makeText(ProjectListActivity.this, "Error: " + err, Toast.LENGTH_LONG).show(); }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- INNER ADAPTER CLASS ---
    private class ProjectAdapter extends ArrayAdapter<Models.Project> {
        public ProjectAdapter(Context context, List<Models.Project> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Models.Project p = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.project_item, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.tvProjectName)).setText(p.getName());
            ((TextView) convertView.findViewById(R.id.tvProjectCode)).setText("Code: " + p.getProjectCode());
            ((TextView) convertView.findViewById(R.id.tvStatus)).setText("Status: " + p.getStatus());
            ((TextView) convertView.findViewById(R.id.tvBudget)).setText("£" + String.format("%.2f", p.getBudget()));

            // VIEW/EDIT: Clicking the item opens the form in Edit mode
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddEditProjectActivity.class);
                intent.putExtra("ID", p.getId());
                getContext().startActivity(intent);
            });

            // EDIT ICON: Also goes to same screen
            convertView.findViewById(R.id.ivEdit).setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddEditProjectActivity.class);
                intent.putExtra("ID", p.getId());
                getContext().startActivity(intent);
            });

            // DELETE ICON: Show confirmation
            convertView.findViewById(R.id.ivDelete).setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete?")
                        .setMessage("Delete " + p.getName() + "?")
                        .setPositiveButton("Yes", (d, w) -> {
                            db.deleteProject(p.getId());
                            updateList(null);
                        })
                        .setNegativeButton("No", null).show();
            });

            return convertView;
        }
    }
}