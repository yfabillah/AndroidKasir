package com.example.firetable.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firetable.Models.Projects;
import com.example.firetable.ProjectTaskActivity;
import com.example.firetable.R;

import java.util.ArrayList;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectsViewHolder> {

    public ArrayList<Projects> projectsArrayList;
    public Context context;

    public ProjectsAdapter(Context context, ArrayList<Projects> dataList) {
        this.context = context;
        this.projectsArrayList = dataList;
    }

    @NonNull
    @Override
    public ProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.projects_item, parent, false);
        return new ProjectsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String projectName = projectsArrayList.get(position).name;
        Log.d("adapter", projectsArrayList.get(position).name);
        holder.projectNameText.setText(projectName);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProjectTaskActivity.class);
                intent.putExtra("PROJECTS_KEY", projectsArrayList.get(position).key);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (projectsArrayList != null) ? projectsArrayList.size() : 0;
    }

    public class ProjectsViewHolder extends RecyclerView.ViewHolder {
        private TextView projectNameText;
        private CardView cardView;

        public ProjectsViewHolder(View itemView){
            super(itemView);
            projectNameText = itemView.findViewById(R.id.projects_name);
            cardView = itemView.findViewById(R.id.item_card);
        }
    }
}
