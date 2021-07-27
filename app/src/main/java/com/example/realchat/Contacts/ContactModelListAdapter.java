package com.example.realchat.Contacts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realchat.Chat.CreateChatActivity;
import com.example.realchat.R;

import java.util.ArrayList;

public class ContactModelListAdapter extends RecyclerView.Adapter<ContactModelListAdapter.ContactModelViewHolder> {
    private ArrayList<ContactModel> mContactList;
    private LayoutInflater mInflater;
    private Context mContext;

    public ContactModelListAdapter(ArrayList<ContactModel> mUserList, Context context) {
       this.mContactList = mUserList;
       mInflater = LayoutInflater.from(context);
       mContext=context;

  }

    @NonNull
    @Override
    public ContactModelListAdapter.ContactModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.contact_list_item,parent,false);
      return new ContactModelViewHolder(mItemView,this,mContext);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull ContactModelListAdapter.ContactModelViewHolder holder, int position) {
        String mCurrent = mContactList.get(position).getName();
        holder.contactItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    public class ContactModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView contactItemView;
        public Context context;
        public ContactModelListAdapter mAdapter;
        public ContactModelViewHolder(@NonNull View itemView, ContactModelListAdapter adapter, Context mcontext) {
            super(itemView);

           contactItemView= (TextView) itemView.findViewById(R.id.contact_item);
           mAdapter = adapter;
           context = mcontext;
           contactItemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent =new Intent (context, CreateChatActivity.class);
            intent.putExtra("User ID",mContactList.get(getLayoutPosition()).getUid());
            context.startActivity(intent);

        }
    }

}