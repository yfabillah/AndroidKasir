package com.example.firetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.firetable.Adapters.ProjectsAdapter;
import com.example.firetable.Models.Projects;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.firetable.Models.User;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private Button logout, addProjectBtn;
    private EditText projectNameInput;
    private ListView projectListView;
    private TextView greetingText;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser currentUser;
    private ArrayList<Projects> projectList;
    private RecyclerView recyclerView;
    private FloatingActionButton addFloatingActionButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar actionBar = getSupportActionBar();

        greetingText = findViewById(R.id.greeting_text);
        recyclerView = findViewById(R.id.recycler_view);


        projectList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        addFloatingActionButton = findViewById(R.id.add_fab);
        addFloatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showBottomSheetDialog(v);
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        currentUser = mAuth.getCurrentUser();

        mDatabase.getReference().child("users").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    greetingText.setText("Halo, " + task.getResult().getValue(User.class).name);
                }
            }
        });

        DatabaseReference projectsRef = mDatabase.getReference().child("projects");
        projectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                projectList = new ArrayList<>();
                for(DataSnapshot mDataSnapShot : dataSnapshot.getChildren()){
                    Log.d("data snapshot : ", mDataSnapShot.toString());
                    Projects projects = new Projects(mDataSnapShot.getValue(Projects.class).name, mDataSnapShot.getKey(), mDataSnapShot.getValue(Projects.class).members);
                    Log.d("projects result", projects.name);
                    if(mDataSnapShot.getValue(Projects.class).members.containsKey(currentUser.getUid())){
                        projectList.add(projects);
                    }
                }
                Log.d("project List", projectList.toString());
                ProjectsAdapter projectsAdapter = new ProjectsAdapter(HomeActivity.this, projectList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(projectsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showBottomSheetDialog(View v){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.add_projects);

        projectNameInput = bottomSheetDialog.findViewById(R.id.input_project_name);
        addProjectBtn = bottomSheetDialog.findViewById(R.id.add_projects_btn);

        addProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(projectNameInput.getText().toString().isEmpty()){
                    showErrorSnackbar("Project Name is required");
                    return;
                }
                Map<String, String> data = new HashMap<>();
                data.put("name", projectNameInput.getText().toString());

                Projects projects = new Projects();
                projects.name = projectNameInput.getText().toString();
                projects.members = new HashMap<String ,Boolean>();
                projects.members.put(currentUser.getUid(), true);

                String key = mDatabase.getReference().child("projects").push().getKey();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/projects/" + key, projects);
                mDatabase.getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        bottomSheetDialog.hide();
                        showSuccessSnackbar("Projects Added");
                    }
                });
            }
        });


        bottomSheetDialog.show();

    }

    private void showSuccessSnackbar(String message){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.home_root_container), message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_green_light));
        snackbar.show();
    }

    private void showErrorSnackbar(String message){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.home_root_container), message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(getResources().getColor(R.color.error_color));
        snackbar.show();
    }


}