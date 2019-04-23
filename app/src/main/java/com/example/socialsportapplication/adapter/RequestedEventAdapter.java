
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
import android.widget.Toast;

import com.example.socialsportapplication.Models.MatchModel;
import com.example.socialsportapplication.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class RequestedEventAdapter extends RecyclerView.Adapter<RequestedEventAdapter.ViewHolder> {

    private static final String TAG = "RequestedEventAdapter";
    private List<MatchModel> requestedList;
    private ItemListener myListener;
    private DeclineListner myDeclineListner;
    private Context context;
    FirebaseAuth firebaseAuth;

    public RequestedEventAdapter(Context context, List<MatchModel> requestedList, ItemListener myListener,DeclineListner myDeclineListner) {
        this.context = context;
        this.requestedList = requestedList;
        this.myListener = myListener;
        this.myDeclineListner = myDeclineListner;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void setListener(ItemListener listener) {
        myListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_events_list_requested, parent, false)); // TODO
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "getItemCount:===> " +requestedList.size() );
        return requestedList.size();

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder: " + position );
        holder.setData(requestedList.get(position));
    }

    public interface ItemListener {
        void onItemClick(MatchModel item,int position);
    }

    public interface DeclineListner{
        void onDeclineClick(MatchModel item,int position);
    }

    public void removeAt(int position) {
        requestedList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, requestedList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public MatchModel item;
        public TextView title, desc, address, datetime,message,noNoti;
        public Button btnJoinEvent, btnDirection;
        public LinearLayout llitems;
        View decview;
        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_title);
            desc = itemView.findViewById(R.id.tv_desc);
            address = itemView.findViewById(R.id.tv_address);
            datetime = itemView.findViewById(R.id.tv_date_time);
            message = itemView.findViewById(R.id.tvMessage);
            btnDirection = itemView.findViewById(R.id.btn_direction);
            btnJoinEvent = itemView.findViewById(R.id.btn_join);
            llitems = itemView.findViewById(R.id.llItems);
            decview = itemView.findViewById(R.id.decview);
            noNoti = itemView.findViewById(R.id.tvNonoti);

            btnJoinEvent.setOnClickListener(this);
            btnDirection.setOnClickListener(this);

        }


        public void setData(MatchModel item) {
            this.item = item;
            btnJoinEvent.setText("Accept");
            btnDirection.setText("Decline");
            datetime.setText(item.getDatetime());
            address.setText(item.getUser_name());
            title.setText("Someone wants to join "+item.getName());
            message.setText("You have been accepted to join "+item.getName());
            desc.setText(item.getDescription());
            Log.e(TAG, "setData:decs==>  " + item.getDescription() );
            Log.e(TAG, "setData:===> " + firebaseAuth.getCurrentUser().getUid()+" " + item.getOwnerid() + " " + item.getU_Id() + " " + item.isStatus() );
            if (firebaseAuth.getCurrentUser().getUid().equals(item.getOwnerid())){
                if (item.isStatus()){
                    message.setVisibility(View.GONE);
                    llitems.setVisibility(View.GONE);
                }else {
                    llitems.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                }
            }else {
                message.setVisibility(View.GONE);
                llitems.setVisibility(View.GONE);
            }

            if (item.getU_Id()==null){
                message.setVisibility(View.GONE);
                llitems.setVisibility(View.GONE);
            }

            if (firebaseAuth.getCurrentUser().getUid().equals(item.getU_Id())){
                if (item.isStatus()){
                    llitems.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                }else {
                    llitems.setVisibility(View.GONE);
                    message.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_join:
                    if (myListener != null) {
                        myListener.onItemClick(item,getAdapterPosition());
                    }
                    break;
                case R.id.btn_direction:
                    if (myDeclineListner!=null){
                        myDeclineListner.onDeclineClick(item,getAdapterPosition());
                    }
                    break;
            }

        }
    }


}
                                