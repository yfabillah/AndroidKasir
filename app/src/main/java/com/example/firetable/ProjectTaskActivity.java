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

import com.example.firetable.Adapters.ProjectsAdapter;
import com.example.firetable.Adapters.TasksAdapter;
import com.example.firetable.Models.Projects;
import com.example.firetable.Models.Tasks;
import com.example.firetable.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProjectTaskActivity extends AppCompatActivity {

    private String projectKey;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser currentUser;
    private FloatingActionButton addTaskFAB;

    private ArrayList<Tasks> tasksList;
    private RecyclerView taskRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_task);
        Bundle extras = getIntent().getExtras();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        projectKey = extras.getString("PROJECTS_KEY");

        tasksList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        addTaskFAB = findViewById(R.id.add_task_fab);
        taskRecyclerView = findViewById(R.id.task_recycler_view);

        addTaskFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        mDatabase.getReference().child("projects").child(projectKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                actionBar.setTitle(task.getResult().getValue(Projects.class).name);
            }
        });
    }

    private void showBottomSheetDialog(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.add_tasks);

        EditText taskNameInput = bottomSheetDialog.findViewById(R.id.input_task_name);
        EditText emailInput = bottomSheetDialog.findViewById(R.id.pic_email);
        EditText progressInput = bottomSheetDialog.findViewById(R.id.status);

        final Button addTaskBtn = bottomSheetDialog.findViewById(R.id.add_task_btn);



        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("users").orderByChild("email").equalTo(emailInput.getText().toString())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                                        String email = mDataSnapshot.getValue(User.class).email;
                                        DatabaseReference taskRef = mDatabase.getReference().child("projects").child(projectKey).child("tasks");
                                        String key =  taskRef.push().getKey();
                                        Tasks tasks = new Tasks();

                                        tasks.taskName = taskNameInput.getText().toString();
                                        tasks.progressStatus = progressInput.getText().toString();
                                        tasks.assignedTo = new HashMap<>();
                                        tasks.assignedTo.put("email", email);
                                        tasks.assignedTo.put("user_id", mDataSnapshot.getKey());

                                        Map<String, Object> childUpdates = new HashMap<>();
                                        childUpdates.put(key, tasks);
                                        taskRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Map<String, Object> members = new HashMap<>();
                                                members.put(mDataSnapshot.getKey(), true);

                                                DatabaseReference projectsRef = mDatabase.getReference()
                                                .child("projects").child(projectKey).child("members");
                                                projectsRef.updateChildren(members);
                                                showSuccessSnackbar("Add Task Success");
                                                bottomSheetDialog.hide();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showErrorSnackbar(e.toString());
                                                bottomSheetDialog.hide();
                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                });



            }
        });

        bottomSheetDialog.show();

    }

    @Override
    protected void onStart(){
        super.onStart();
        mDatabase.getReference().child("projects").child(projectKey).child("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tasksList = new ArrayList<>();
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    Tasks task = new Tasks(
                            dataSnapshot.getKey(),
                            mDataSnapshot.getValue(Tasks.class).taskName,
                            new Date(),
                            new Date(),
                            mDataSnapshot.getValue(Tasks.class).assignedTo,
                            mDataSnapshot.getValue(Tasks.class).progressStatus
                    );
                    tasksList.add(task);
                }
                TasksAdapter tasksAdapter = new TasksAdapter(ProjectTaskActivity.this, tasksList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProjectTaskActivity.this);
                taskRecyclerView.setLayoutManager(layoutManager);
                taskRecyclerView.setAdapter(tasksAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showSuccessSnackbar(String message){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.project_task_root_container), message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_green_light));
        snackbar.show();
    }

    private void showErrorSnackbar(String message){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.project_task_root_container), message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(getResources().getColor(R.color.error_color));
        snackbar.show();
    }

}