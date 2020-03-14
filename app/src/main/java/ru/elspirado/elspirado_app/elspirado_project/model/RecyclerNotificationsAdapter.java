package ru.elspirado.elspirado_app.elspirado_project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.elspirado.elspirado_app.elspirado_project.R;

public class RecyclerNotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Notification> notificationArrayList;

    Context context;

    public RecyclerNotificationsAdapter(ArrayList<Notification> notificationArrayList, Context context){
        this.notificationArrayList = notificationArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_notificications_recycler_item, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Notification notification = notificationArrayList.get(position);

        ((ViewHolder) holder).notificationtime.setText("12:28");
        ((ViewHolder) holder).notificationText.setText(notification.getText());
        ((ViewHolder) holder).checkBoxIsRepeat.setChecked(notification.isRepeat());
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout mainConstraint;

        TextView notificationText;

        TextView notificationtime;

        CheckBox checkBoxIsRepeat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mainConstraint = itemView.findViewById(R.id.notificationsConstraint);
            notificationText = itemView.findViewById(R.id.notificationText);
            notificationtime = itemView.findViewById(R.id.notificationTime);
            checkBoxIsRepeat = itemView.findViewById(R.id.checkBoxRepeat);
        }
    }
}
