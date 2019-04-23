package com.example.socialsportapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.socialsportapplication.Models.MatchModel;
import com.example.socialsportapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//References used:
//https://www.youtube.com/watch?v=Vyqz_-sJGFk
//https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
//https://stackoverflow.com/questions/tagged/android-recyclerview

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {
    private static final String TAG = "EventsListAdapter";
    private List<MatchModel> matchesList;
    private ItemListener myListener;
    private DetailListner myDetailListner;
    private Context context;
    private String currentUserId;

    public EventsListAdapter(Context context, List<MatchModel> matchesList, String currentUserId, ItemListener myListener, DetailListner myDetailListner) {
        this.context = context;
        this.matchesList = matchesList;
        this.currentUserId = currentUserId;
        this.myListener = myListener;
        this.myDetailListner = myDetailListner;
    }

    public void setListener(ItemListener listener) {
        myListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_events_list, parent, false));
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(matchesList.get(position));


    }

    public interface ItemListener {
        void onItemClick(MatchModel item);
    }

    public interface DetailListner {
        void onViewClick(MatchModel item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public MatchModel item;
        public TextView title, desc, address, datetime;
        public Button btnJoinEvent, btnDirection;
        public LinearLayout llDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            //
            title = itemView.findViewById(R.id.tv_title);
            desc = itemView.findViewById(R.id.tv_desc);
            address = itemView.findViewById(R.id.tv_address);
            datetime = itemView.findViewById(R.id.tv_date_time);
            btnDirection = itemView.findViewById(R.id.btn_direction);
            btnJoinEvent = itemView.findViewById(R.id.btn_join);
            llDetails = itemView.findViewById(R.id.llDetails);


            btnJoinEvent.setOnClickListener(this);
            btnDirection.setOnClickListener(this);

        }

        //TODO set Events Data
        public void setData(final MatchModel item) {
            this.item = item;

            title.setText(item.getName());
            desc.setText(item.getDescription());
            address.setText(item.getAddress());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            try {
                Date date = sdf.parse(item.getDatetime());
                SimpleDateFormat sdf4 = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                datetime.setText(sdf4.format(date));
            } catch (Exception e) {
                Log.e(TAG, "setData:==>" + e.getMessage());
            }

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_join:
                    if (myListener != null) {
                        myListener.onItemClick(item);
                    }
                    break;
                case R.id.btn_direction:
                    if (myDetailListner != null) {
                        myDetailListner.onViewClick(item);
                    }
                    break;
            }


        }


    }


}
                                