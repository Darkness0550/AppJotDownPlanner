package pe.upn.AppJotDownPlanner.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import pe.upn.AppJotDownPlanner.R;
import pe.upn.AppJotDownPlanner.models.TaskModel;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public List<TaskModel> tasks;
    private final Context context;

    public TaskAdapter(List<TaskModel> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        TaskModel taskModel = tasks.get(position);

        // Check if task is completed
        if (taskModel.isCompleted()) {
            holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvName.setText(taskModel.getName());
        } else {
            holder.tvName.setText(taskModel.getName());
        }

        // Set task desc
        holder.tvDesc.setText(taskModel.getDesc());

        // Format LocalDateTime to string
        LocalDateTime dateTime = TaskModel.convertToLocalDateTime(taskModel.getDateTime());
        holder.tvDateTime.setText(dateTime.toString().replace("T", " "));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvDesc, tvDateTime;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvTaskName);
            this.tvDesc = itemView.findViewById(R.id.tvTaskDesc);
            this.tvDateTime = itemView.findViewById(R.id.tvTaskDateTime);
        }
    }
}
