package com.example.firetable.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firetable.Models.Tasks;
import com.example.firetable.ProjectTaskActivity;
import com.example.firetable.R;
import com.google.android.material.chip.Chip;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    public ArrayList<Tasks> tasksArrayList;
    public Context context;

    public TasksAdapter(Context context, ArrayList<Tasks> dataList) {
        this.context = context;
        this.tasksArrayList = dataList;
    }

    @NonNull
    @Override
    public TasksAdapter.TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tasks_item, parent, false);
        return new TasksAdapter.TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String taskName = tasksArrayList.get(position).taskName;
        Tasks tasks = tasksArrayList.get(position);
        holder.taskNameText.setText(taskName);
        holder.progressStatusText.setText(tasks.progressStatus);
        holder.picText.setText("PIC : " + tasks.assignedTo.get("email"));
    }

    @Override
    public int getItemCount() {
        return (tasksArrayList != null) ? tasksArrayList.size() : 0;
    }

    public class TasksViewHolder extends RecyclerView.ViewHolder {
        private TextView taskNameText;
        private TextView picText;
        private Chip progressStatusText;
        private CardView cardView;

        public TasksViewHolder(View itemView){
            super(itemView);
            taskNameText = itemView.findViewById(R.id.task_name);
            progressStatusText = itemView.findViewById(R.id.task_progress_status);
            picText = itemView.findViewById(R.id.pic);
            cardView = itemView.findViewById(R.id.item_card);
        }
    }
}

