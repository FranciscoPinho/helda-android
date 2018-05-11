package com.organon.helda.app.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.organon.helda.R;
import com.organon.helda.app.activities.DisassemblyActivity;
import com.organon.helda.core.entities.Task;

import java.util.List;
import java.util.Locale;

public class PickSkippedTaskListAdapter extends RecyclerView.Adapter<PickSkippedTaskListAdapter.ViewHolder> {
    private List<Task> tasks;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;

        public ViewHolder(LinearLayout layout) {
            super(layout);
            this.layout = layout;
        }
    }

    public PickSkippedTaskListAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout layout = (LinearLayout)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pick_skipped_task_list_item, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView description = holder.layout.findViewById(R.id.pick_skipped_task_list_item_description);
        description.setText(tasks.get(position).getDescription());

        TextView id = holder.layout.findViewById(R.id.pick_skipped_task_list_item_offset);
        id.setText(String.format(Locale.getDefault(), "Tarea %d", tasks.get(position).getOffset()));

        final Activity activity = (Activity)holder.layout.getContext();
        final int offset = tasks.get(position).getOffset();

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = activity.getIntent();
                intent.putExtra("task", offset);
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
