package edu.rowanuniversity.rufit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import edu.rowanuniversity.rufit.rufitObjects.Shoe;

/**
 * Created by catherine on 4/8/2017.
 * LastUpdated: 04.08.2017
 *
 * RecyclerView Adapter for user's shoe display.
 */

public class ShoeAdapter extends RecyclerView.Adapter<ShoeAdapter.ViewHolder> {
    private HashMap<String,Shoe> shoes;
    private String[] keys;

    public ShoeAdapter(HashMap<String,Shoe> shoes) {
        this.shoes = shoes;
        keys = shoes.keySet().toArray(new String[getItemCount()]);
    }

    @Override
    public ShoeAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shoe, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShoeAdapter.ViewHolder viewHolder, int i) {
        viewHolder.shoeName.setText(shoes.get(keys[i]).getName());
        viewHolder.shoeMileage.setText((Math.round(shoes.get(keys[i]).getMileage() * 100.0)/100.0) +" miles");
    }

    @Override
    public int getItemCount() {
        return shoes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView shoeName;
        private TextView shoeMileage;
        public ViewHolder(View view) {
            super(view);
            shoeName = (TextView)view.findViewById(R.id.shoe_name);
            shoeMileage = (TextView)view.findViewById(R.id.shoe_mileage);
        }
    }
}