package com.example.realchat.Chat;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageModelViewHolder> {
    ArrayList<MessageModel>MessageList;
    Context mContext;
    LayoutInflater mInflater;


    public MessageListAdapter(ArrayList<MessageModel> messageList, Context mContext) {
        MessageList = messageList;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }



    @NonNull
    @Override
    public MessageModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView;
        switch(viewType){
            case 1:
                mItemView = mInflater.inflate(R.layout.message_list_item_outgoing,parent,false);
                return new MessageListAdapter.MessageModelViewHolder(mItemView,this,mContext,viewType);


            default:
                mItemView= mInflater.inflate(R.layout.message_list_item_incoming,parent,false);
                return new MessageListAdapter.MessageModelViewHolder(mItemView,this,mContext,viewType);

        }

    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser currUser =FirebaseAuth.getInstance().getCurrentUser();
        return (MessageList.get(position).getFrom().equals(currUser.getUid()))?1:0;

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull MessageModelViewHolder holder, int position) {

        String mCurrent =MessageList.get(position).getMessage();
        String time = MessageList.get(position).getTime().substring(11,16);
        String message =mCurrent+" "+time;
        SpannableString ss1=  new SpannableString(mCurrent);
        ss1.setSpan(new AbsoluteSizeSpan(44),0,mCurrent.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString ss2=  new SpannableString(time);
        ss2.setSpan(new AbsoluteSizeSpan(30),0,5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss2.setSpan(new ForegroundColorSpan(Color.DKGRAY),0,5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        CharSequence finalText = TextUtils.concat(ss1, " ", ss2);
        holder.contactItemView.setText(finalText);
    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }

    public class MessageModelViewHolder extends RecyclerView.ViewHolder {
        public TextView contactItemView;
        public Context context;
        public MessageListAdapter mAdapter;
        public MessageModelViewHolder(@NonNull View itemView, MessageListAdapter adapter, Context mcontext,int viewType) {
            super(itemView);
            if(viewType==1)
            contactItemView= (TextView) itemView.findViewById(R.id.outgoing_message);
            else contactItemView= (TextView) itemView.findViewById(R.id.incoming_message);
            mAdapter = adapter;
            context = mcontext;

        }


    }
}
