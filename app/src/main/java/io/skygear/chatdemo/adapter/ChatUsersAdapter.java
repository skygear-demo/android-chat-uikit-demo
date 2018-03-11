package io.skygear.chatdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.skygear.chatdemo.LoginActivity;
import io.skygear.chatdemo.R;
import io.skygear.plugins.chat.ChatUser;

/**
 * Created by carmenlau on 10/16/17.
 */

public class ChatUsersAdapter extends RecyclerView.Adapter<ChatUsersAdapter.ViewHolder> {

    List<ChatUser> mChatUserList = new ArrayList<ChatUser>();
    String mCurrentUserId;
    ArrayList<ChatUser> mSelectedChatUsers = new ArrayList<ChatUser>();
    Context context;

    public ChatUsersAdapter(Context context, String mCurrentUserId) {
        this.context = context;
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

    public ArrayList<ChatUser> getSelectedChatUsers() {
        return mSelectedChatUsers;
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

    private void refreshChatUserBackgroundColor(ChatUser chatUser, TextView textView) {
        refreshChatUserBackgroundColor(chatUser, textView, null);
    }

    private void refreshChatUserBackgroundColor(ChatUser chatUser, View view, Boolean alreadySelected) {
        alreadySelected = alreadySelected == null ?
                            isChatUserSelected(chatUser) :
                            alreadySelected;
        int backgroundColor = alreadySelected ?
                ContextCompat.getColor(context, R.color.gray_light) :
                ContextCompat.getColor(context, R.color.transparent);
        view.setBackgroundColor(backgroundColor);
    }

    private boolean isChatUserSelected(ChatUser chatUser) {
        return mSelectedChatUsers.indexOf(chatUser) != -1;
    }

    @Override
    public void onBindViewHolder(ChatUsersAdapter.ViewHolder holder, int position) {
        final ChatUser chatUser = mChatUserList.get(position);
        String userLabelText = chatUser.getId();
        if (chatUser.getRecord().get("name") != null && chatUser.getRecord().get("name") instanceof String) {
            userLabelText = (String) chatUser.getRecord().get("name");
        } else if (chatUser.getRecord().get("username") != null) {
            userLabelText = (String) chatUser.getRecord().get("username");
        }

        holder.textView.setText(userLabelText);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean alreadySelected = isChatUserSelected(chatUser);
                if (alreadySelected) {
                    mSelectedChatUsers.remove(chatUser);
                } else {
                    mSelectedChatUsers.add(chatUser);
                }
                refreshChatUserBackgroundColor(chatUser, view, !alreadySelected);
            }
        });
        refreshChatUserBackgroundColor(chatUser, holder.textView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView textView;

        public ViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.username_tv);
        }

    }
}
