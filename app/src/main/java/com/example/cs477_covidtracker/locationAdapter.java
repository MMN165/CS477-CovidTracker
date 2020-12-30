package com.example.cs477_covidtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Custom Adapter to be used with our card view recycle view. Sets all values here for the card.
 */
public class locationAdapter extends RecyclerView.Adapter<locationAdapter.CardViewHolder> {
    private ArrayList<cardLocation> mLocationInfo;
    private onItemClickListener clickListener;

    /**
     * Nested class that finds the card view and holds it.
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder{
        public TextView location;
        public TextView mCases, mDeaths;

        public CardViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            location = itemView.findViewById(R.id.location_text2);
            mCases = itemView.findViewById(R.id.currentCases2);
            mDeaths = itemView.findViewById(R.id.currentDeaths2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            listener.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }

    /**
     * OnItemClickListener Interface. For onItemClick
     */
    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        clickListener = listener;
    }

    public locationAdapter(ArrayList<cardLocation> lister){
        mLocationInfo = lister;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_information_card, parent, false);
        CardViewHolder evh = new CardViewHolder(v, clickListener);
        return evh;
    }

    /**
     * Setting values into a card.
     */
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
            cardLocation currentItem = mLocationInfo.get(position);

            holder.location.setText("" + currentItem.getCounty() + ", " + currentItem.getState());
            holder.mDeaths.setText("" + currentItem.getCurrentDeath());
            holder.mCases.setText("" + currentItem.getCurrentCase());
    }

    @Override
    public int getItemCount() {
        return mLocationInfo.size();
    }
}
