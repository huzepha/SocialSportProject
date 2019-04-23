
package com.example.socialsportapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.socialsportapplication.Models.MatchModel;
import com.example.socialsportapplication.Models.RequestEventModel;
import com.example.socialsportapplication.Models.UserModel;

import java.util.List;


public class RequestedEventAdapter extends RecyclerView.Adapter<RequestedEventAdapter.ViewHolder> {

    private List<RequestEventModel> requestedList;
    private ItemListener myListener;
    private Context context;

    public RequestedEventAdapter(Context context, List<RequestEventModel> requestedList, ItemListener myListener) {
        this.context = context;
        this.requestedList = requestedList;
        this.myListener = myListener;
    }

    public void setListener(ItemListener listener) {
        myListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_events_list, parent, false)); // TODO
    }

    @Override
    public int getItemCount() {
        return requestedList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(requestedList.get(position));
    }

    public interface ItemListener {
        void onItemClick(RequestEventModel item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // TODO - Your view members
        public RequestEventModel item;
        public TextView title, desc, address, datetime;
        public Button btnJoinEvent, btnDirection;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            // TODO instantiate/assign view members
            title = itemView.findViewById(R.id.tv_title);
            desc = itemView.findViewById(R.id.tv_desc);
            address = itemView.findViewById(R.id.tv_address);
            datetime = itemView.findViewById(R.id.tv_date_time);
            btnDirection = itemView.findViewById(R.id.btn_direction);
            btnJoinEvent = itemView.findViewById(R.id.btn_join);
        }

        public void setData(RequestEventModel item) {
            this.item = item;
            btnJoinEvent.setText("Accept");
            btnDirection.setText("Decline");
            datetime.setText(item.getMatchName());
            address.setText(item.getUserEmail());
            // TODO set data to view
        }

        @Override
        public void onClick(View v) {
            if (myListener != null) {
                myListener.onItemClick(item);
            }
        }
    }


}
                                