package com.example.booksmart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.booksmart.R;
import com.example.booksmart.models.Message;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    public static final String TAG = "ChatAdapter";
    private static final int MESSAGE_OUTGOING = 123;
    private static final int MESSAGE_INCOMING = 321;
    private static final String NAME_KEY = "name";
    private static final String IMAGE_KEY = "image";

    List<Message> messages;
    Context context;
    ParseUser currentUser;

    public ChatAdapter(Context context, ParseUser user, List<Message> messages) {
        this.messages = messages;
        currentUser = user;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING) {
            View view = inflater.inflate(R.layout.message_incoming, parent, false);
            return new IncomingMessageViewHolder(view);
        } else if (viewType == MESSAGE_OUTGOING) {
            View view = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(view);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatAdapter.MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isCurrentUser(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isCurrentUser(int position) {
        Message message = messages.get(position);
        return message.getUser().getObjectId() != null && message.getUser().getObjectId().equals(currentUser.getObjectId());
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind(Message message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {

        ImageView ivUserPhoto;
        TextView tvBody;
        TextView tvName;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            ivUserPhoto = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
        }

        @Override
        public void bind(Message message) {
            tvBody.setText(message.getBody());

            ParseUser user = message.getUser();
            try {
                user.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvName.setText(user.getString(NAME_KEY));

            ParseFile profileImage = user.getParseFile(IMAGE_KEY);
            if (profileImage != null) {
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .circleCrop()
                        .into(ivUserPhoto);
            }
        }
    }

    public class OutgoingMessageViewHolder extends MessageViewHolder {
        ImageView ivUserPhoto;
        TextView tvBody;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            ivUserPhoto = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bind(Message message) {
            tvBody.setText(message.getBody());

            ParseFile profileImage = currentUser.getParseFile(IMAGE_KEY);
            if (profileImage != null) {
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .circleCrop()
                        .into(ivUserPhoto);
            }
        }
    }
}
