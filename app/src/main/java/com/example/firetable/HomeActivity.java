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
import android.widget.ListView;
import android.widget.TextView;

import com.example.firetable.Adapters.ProjectsAdapter;
import com.example.firetable.Models.Projects;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class HomeActivity extends AppCompatActivity {

    private Button logout;
    private ListView projectListView;
    private TextView greetingText;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private ArrayList<Projects> projectList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        greetingText = findViewById(R.id.greeting_text);
        recyclerView = findViewById(R.id.recycler_view);
        projectList = new ArrayList<>();
//        projectListView = findViewById(R.id.projects_listview);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mDatabase.getReference().child("users").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    greetingText.setText("Halo, " + task.getResult().getValue(User.class).name);
                }
            }
        });

        DatabaseReference projectsRef = mDatabase.getReference().child("projects");
//        FirebaseListOptions<String> options = new FirebaseListOptions.Builder<String>()
//                .setQuery(projectsRef, String.class)
//                .setLayout(android.R.layout.simple_list_item_1)
//                .build();
//        FirebaseListAdapter<String> adapter = new FirebaseListAdapter<String>(options) {
//            @Override
//            protected void populateView(@NonNull View v, @NonNull String model, int position) {
//                TextView projectNameTxt = v.findViewById(R.id.projects_name);
//                DatabaseReference itemRef = getRef(position);
//                Log.d("snapshot", "model " + model.toString() + "item" + itemRef);
//
////                projectNameTxt.setText(model.name);
//            }
//        };
//        adapter.startListening();
//        projectListView.setAdapter(adapter);
        projectsRef.limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                projectList = new ArrayList<>();
                for(DataSnapshot mDataSnapShot : dataSnapshot.getChildren()){
                    Log.d("data snapshot : ", mDataSnapShot.toString());
                    Projects projects = new Projects(mDataSnapShot.getValue(Projects.class).name, mDataSnapShot.getKey());
                    Log.d("projects result", projects.name);
                    projectList.add(projects);

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
//        mDatabase.getReference().child("projects").limitToLast(10).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                projectList = new ArrayList<>();
//                for(DataSnapshot mDataSnapShot : dataSnapshot.getChildren()){
//                    Log.d("data snapshot : ", mDataSnapShot.toString());
//                    Projects projects = new Projects(mDataSnapShot.getValue(Projects.class).name, mDataSnapShot.getKey());
//                    projectList.add(projects);
//
//                }
////                ProjectsAdapter projectsAdapter = new ProjectsAdapter(HomeActivity.this, projectList);
//                projectListView.setAdapter(adapter);
////                projectsAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        logout = (Button) findViewById(R.id.logout);
//
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(HomeActivity.this, MainActivity.class));
//            }
//        });
    }

}