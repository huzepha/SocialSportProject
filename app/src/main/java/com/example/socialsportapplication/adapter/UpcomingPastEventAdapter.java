
package com.example.socialsportapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.socialsportapplication.Models.MatchModel;
import com.example.socialsportapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class UpcomingPastEventAdapter extends RecyclerView.Adapter<UpcomingPastEventAdapter.ViewHolder> {

    private List<MatchModel> eventList;
    private ItemListener myListener;
    private Context context;

    public UpcomingPastEventAdapter(Context context,List<MatchModel> eventList, ItemListener myListener) {
        this.context = context;
        this.eventList = eventList;
        this.myListener = myListener;
    }

    public void setListener(ItemListener listener) {
        myListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_upcoming_past_event_list, parent, false)); // TODO
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(eventList.get(position));
    }

    public interface ItemListener {
        void onItemClick(MatchModel item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public MatchModel item;
        public TextView title, desc, address, datetime;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.tv_title);
            desc = itemView.findViewById(R.id.tv_desc);
            address = itemView.findViewById(R.id.tv_address);
            datetime = itemView.findViewById(R.id.tv_date_time);

        }

        public void setData(MatchModel item) {
            this.item = item;

            title.setText(item.getName());
            desc.setText(item.getDescription());
            address.setText(item.getAddress());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            try {
                Date date = sdf.parse(item.getDatetime());
                SimpleDateFormat sdf4 = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                datetime.setText(sdf4.format(date));
            }catch (Exception e){
            }
        }

        @Override
        public void onClick(View v) {
            if (myListener != null) {
                myListener.onItemClick(item);
            }
        }
    }


}
                                