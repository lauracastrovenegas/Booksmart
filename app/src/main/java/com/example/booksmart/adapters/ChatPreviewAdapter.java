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
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Listing;
import com.example.booksmart.models.Message;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatPreviewAdapter extends RecyclerView.Adapter<ChatPreviewAdapter.ViewHolder> {

    public static final String TAG = "ChatPreviewAdapter";

    List<Conversation> conversations;
    Context context;

    public ChatPreviewAdapter(Context context, List<Conversation> conversations){
        this.context = context;
        this.conversations = conversations;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private static final String KEY_NAME = "name";
        private static final String KEY_IMAGE = "image";

        ImageView ivProfilePhoto;
        ImageView ivListingPreview;
        TextView tvUserName;
        TextView tvLastMessage;
        TextView tvDate;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivProfilePhoto = itemView.findViewById(R.id.ivOtherUserProfileImage);
            ivListingPreview = itemView.findViewById(R.id.ivListingPreviewImage);
            tvUserName = itemView.findViewById(R.id.tvOtherUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvDate = itemView.findViewById(R.id.tvTime);
        }

        public void bind(Conversation conversation) {
            ParseUser currentUser = ParseUser.getCurrentUser();

            // Get other user
            ParseUser[] users = conversation.getUsers();
            ParseUser otherUser;
            if (currentUser.equals(users[0])){
                otherUser = users[1];
            } else {
                otherUser = users[0];
            }

            try {
                otherUser = otherUser.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Set name and profile photo of other user
            String name = otherUser.getString(KEY_NAME);
            tvUserName.setText(name);

            ParseFile profileImage = otherUser.getParseFile(KEY_IMAGE);
            Glide.with(context)
                    .load(profileImage.getUrl())
                    .circleCrop()
                    .into(ivProfilePhoto);

            // Set Text for last message preview and date
            Message lastMessage = conversation.getLastMessage();
            if (lastMessage.getUser().equals(currentUser)){
                tvLastMessage.setText("You: " + lastMessage.getBody());
            } else {
                tvLastMessage.setText(name + ": " + lastMessage.getBody());
            }

            tvDate.setText(lastMessage.getCreatedAt().toString());

            // Set preview photo for listings
            Listing listing = conversation.getListing();
            try {
                listing = listing.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ParseFile listingImage = listing.getImage();
            Glide.with(context)
                    .load(listingImage.getUrl())
                    .centerCrop()
                    .into(ivListingPreview);
        }
    }
}
