
package com.example.assignment112_1;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment112_1.model.VisitData;

import java.util.List;

public class VisitAdapter extends RecyclerView.Adapter<VisitAdapter.View_Holder>  {
    private List<VisitData> visits;
    private final VisitListener mVisitListener;

    public VisitAdapter(List<VisitData> visits, VisitListener visitListener) {
        this.visits = visits;
        this.mVisitListener = visitListener;
    }


    @NonNull
    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_visit,
                    parent, false);

        return new View_Holder(v, mVisitListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final View_Holder holder, final int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the
        // current row on the RecyclerView
        Log.e("VISITS?", "Here!");
        if (holder != null && visits.get(position) != null) {
            Log.e("VISITS?", "Here!!");
            String title = visits.get(position).getTitle();
            holder.titleView.setText(title);
        }
        //animate(holder);
    }


    // convenience method for getting data at click position
    VisitData getItem(int id) {
        return visits.get(id);
    }

    @Override
    public int getItemCount() {
        return visits.size();
    }


    public interface VisitListener {
        void onVisitClick(int position);
    }

    public class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleView;
        VisitListener viewListener;

        View_Holder(View itemView, VisitListener visitListener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.path_title);
            itemView.setOnClickListener(this);
            this.viewListener = visitListener;
        }

        @Override
        public void onClick(View view) {
            viewListener.onVisitClick(getAdapterPosition());
        }
    }

    public List<VisitData> getVisits() {
        return visits;
    }

    public void setVisits(List<VisitData> visits) {
        this.visits = visits;
    }
}