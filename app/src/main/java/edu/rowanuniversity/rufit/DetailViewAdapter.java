package edu.rowanuniversity.rufit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by shiv on 3/31/2017.
 */

public class DetailViewAdapter extends RecyclerView.Adapter<DetailViewAdapter.WorkoutsViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    List<WorkoutsData> data = Collections.emptyList();

    public DetailViewAdapter(Context context, List<WorkoutsData> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }
    @Override
    public WorkoutsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.detail_workout_item, parent, false);
        WorkoutsViewHolder holder = new WorkoutsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WorkoutsViewHolder holder, int position) {
        WorkoutsData currentData = data.get(position);

        holder.DateTitle.setText(currentData.Date);
        holder.CaloriesBurned.setText(currentData.calorie);
        holder.DistanceRan.setText(currentData.Distance);
        holder.TimeWorkout.setText(currentData.Time);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class WorkoutsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView DateTitle;
        TextView CaloriesBurned;
        TextView DistanceRan;
        TextView TimeWorkout;
        CardView cardView;


        public WorkoutsViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            DateTitle = (TextView) itemView.findViewById(R.id.dateofworkout_Title);
            CaloriesBurned = (TextView) itemView.findViewById(R.id.calories_burned);
            DistanceRan = (TextView) itemView.findViewById(R.id.distance);
            TimeWorkout = (TextView) itemView.findViewById(R.id.time_workout);
            cardView = (CardView) itemView.findViewById(R.id.detail_workout_cardView);


        }

        @Override
        public void onClick(View v) {
            Intent detailWorkout = new Intent(context, DetailWorkouts.class);
            detailWorkout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(detailWorkout);
        }
    }
}
