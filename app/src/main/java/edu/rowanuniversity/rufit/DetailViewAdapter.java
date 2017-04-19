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

import edu.rowanuniversity.rufit.rufitObjects.Run;

/**
 * Created by shiv on 3/31/2017.
 */

public class DetailViewAdapter extends RecyclerView.Adapter<DetailViewAdapter.WorkoutsViewHolder>  {

    private LayoutInflater inflater;
    private Context context;
    List<Run> data = Collections.emptyList();

    public DetailViewAdapter(Context context, List<Run> data){
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
    public void onBindViewHolder(WorkoutsViewHolder holder, final int position) {
        Run currentData = data.get(position);

        holder.DateTitle.setText(" " +currentData.getDate());
        holder.CaloriesBurned.setText(" " +currentData.getCalories());
        holder.DistanceRan.setText(" " + currentData.getMileage());

        holder.TimeWorkout.setText(String.format("%02d", currentData.getTime()/3600) + ":"
                + String.format("%02d",currentData.getTime()/60) + ":"
                + String.format("%02d",currentData.getTime()%60));


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailWorkout = new Intent(context, DetailWorkouts.class);
                detailWorkout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                detailWorkout.putExtra("Key", data.get(position));
                context.startActivity(detailWorkout);
            }
        });
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

           // itemView.setOnClickListener(this);
            DateTitle = (TextView) itemView.findViewById(R.id.dateofworkout_Title);
            CaloriesBurned = (TextView) itemView.findViewById(R.id.calories_burned);
            DistanceRan = (TextView) itemView.findViewById(R.id.distance);
            TimeWorkout = (TextView) itemView.findViewById(R.id.time_workout);
            cardView = (CardView) itemView.findViewById(R.id.detail_workout_cardView);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
