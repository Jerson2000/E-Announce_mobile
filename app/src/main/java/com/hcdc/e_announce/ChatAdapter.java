package com.hcdc.e_announce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter{

    ArrayList<ChatModal> chats;
    Context context;

    public ChatAdapter(Context context, ArrayList<ChatModal> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_holder,parent, false);
                return new UserViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_user_holder,parent, false);
                return new OtherUserViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModal chat = chats.get(position);
        switch(chats.get(position).getSender()){
            case "user":
                ((UserViewHolder)holder).userBubble.setText(chat.getMessage());
                break;
            case "other_user":
                ((OtherUserViewHolder)holder).otherUserBubble.setText(chat.getMessage());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (chats.get(position).getSender()) {
            case "user":
                return 0;
            case "other_user":
                return 1;
            default:
                return -1;
        }
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userBubble;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userBubble = itemView.findViewById(R.id.userBubble);
        }
    }

    public class OtherUserViewHolder extends RecyclerView.ViewHolder {

        TextView otherUserName, otherUserBubble;

        public OtherUserViewHolder(@NonNull View itemView) {
            super(itemView);
            otherUserName = itemView.findViewById(R.id.otherUserName);
            otherUserBubble = itemView.findViewById(R.id.otherUserBubble);
        }
    }

}
