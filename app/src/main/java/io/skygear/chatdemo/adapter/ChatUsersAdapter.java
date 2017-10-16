package io.skygear.chatdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.skygear.chatdemo.R;
import io.skygear.plugins.chat.ChatUser;

/**
 * Created by carmenlau on 10/16/17.
 */

public class ChatUsersAdapter extends RecyclerView.Adapter<ChatUsersAdapter.ViewHolder> {

    List<ChatUser> mChatUserList = new ArrayList<ChatUser>();
    String mCurrentUserId;

    public ChatUsersAdapter(String mCurrentUserId) {
        this.mCurrentUserId = mCurrentUserId;
    }

    public void setChatUserList(List<ChatUser> chatUserList) {
        this.mChatUserList = new ArrayList<ChatUser>();
        for (ChatUser user: chatUserList) {
            if (user.getId() != mCurrentUserId) {
                this.mChatUserList.add(user);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mChatUserList.size();
    }

    @Override
    public ChatUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(layoutInflater.inflate(R.layout.item_chat_user, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatUsersAdapter.ViewHolder holder, int position) {
        ChatUser chatUser = mChatUserList.get(position);
        String userLabelText = chatUser.getId();
        if (chatUser.getRecord().get("name") != null && chatUser.getRecord().get("name") instanceof String) {
            userLabelText = (String) chatUser.getRecord().get("name");
        } else if (chatUser.getRecord().get("username") != null) {
            userLabelText = (String) chatUser.getRecord().get("username");
        }
        holder.textView.setText(userLabelText);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView textView;

        public ViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.username_tv);
        }

    }
}
