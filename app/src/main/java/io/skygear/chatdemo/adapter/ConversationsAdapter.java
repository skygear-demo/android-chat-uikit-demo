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

import io.skygear.chatdemo.R;
import io.skygear.plugins.chat.Conversation;
import io.skygear.plugins.chat.ui.ConversationActivity;

/**
 * Created by carmenlau on 10/17/17.
 */

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {
    List<Conversation> mConversationList = new ArrayList<Conversation>();
    Context context;

    public ConversationsAdapter(Context context) {
        this.context = context;
    }

    public void setConversationList(List<Conversation> conversationList) {
        this.mConversationList = conversationList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mConversationList.size();
    }

    @Override
    public ConversationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ConversationsAdapter.ViewHolder(layoutInflater.inflate(R.layout.item_conversation, parent, false));
    }

    @Override
    public void onBindViewHolder(ConversationsAdapter.ViewHolder holder, int position) {
        final Conversation c = mConversationList.get(position);
        holder.textView.setText(c.getTitle());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent i = new Intent(context, ConversationActivity.class);
                i.putExtra("CONVERSATION", c.toJson().toString());
                context.startActivity(i);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView textView;

        public ViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.conversation_tv);
        }

    }
}
