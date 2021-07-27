package com.example.realchat.User;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realchat.Chat.CreateChatActivity;
import com.example.realchat.R;
import com.example.realchat.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ConnectionsListAdapter extends RecyclerView.Adapter<ConnectionsListAdapter.ConnnectionsListViewHolder> {
    ArrayList<UserModel> connectionslist;
    private LayoutInflater mInflater;
    private Context mContext;
    public ConnectionsListAdapter(ArrayList<UserModel> userlist, Context context){

        this.connectionslist=userlist;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public ConnnectionsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.connection_item,parent,false);
//        RecyclerView.LayoutParams lp= new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        LayoutView.setLayoutParams(lp);
//        ConnnectionsListViewHolder rcv = new ConnnectionsListViewHolder(LayoutView);
        return new ConnnectionsListViewHolder(itemView,this,mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ConnnectionsListViewHolder holder, int position) {
           // holder.mName.setText(connectionslist.get(position).getUesrID());
            //holder.mPhone.setText(connectionslist.get(position).getPhoneNumber());
            holder.mName.setText(connectionslist.get(position).getName());
            holder.mPhone.setText(connectionslist.get(position).getPhoneNumber());


    }

    @Override
    public int getItemCount() {
        return connectionslist.size();
    }

    public class ConnnectionsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mName,mPhone;
        public CardView connectionItemView;
        private Context mContext;
        ConnectionsListAdapter mAdapter;
        public ConnnectionsListViewHolder(@NonNull View view,ConnectionsListAdapter mAdapter,Context context) {

            super(view);
            connectionItemView=  itemView.findViewById(R.id.card_view_connections);
            //mName= view.findViewById(R.id.name);
            //mPhone = view.findViewById(R.id.phone);
            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            this.mAdapter = mAdapter;
            mContext = context;
            connectionItemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            Intent intent =new Intent (mContext, CreateChatActivity.class);
            intent.putExtra("User ID",connectionslist.get(getLayoutPosition()).getUid());
            mContext.startActivity(intent);
        }
    }
}
