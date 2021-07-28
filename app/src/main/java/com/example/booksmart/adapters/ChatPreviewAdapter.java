package com.example.booksmart.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.booksmart.R;
import com.example.booksmart.helpers.ParseMessageClient;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Listing;
import com.example.booksmart.models.Message;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
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

    public void clear(){
        conversations.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Conversation> newConversations){
        conversations.addAll(newConversations);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private static final String KEY_NAME = "name";
        private static final String KEY_IMAGE = "image";

        ImageView ivProfilePhoto;
        ImageView ivListingPreview;
        TextView tvUserName;
        TextView tvLastMessage;
        TextView tvDate;
        ParseUser currentUser;
        String name;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivProfilePhoto = itemView.findViewById(R.id.ivOtherUserProfileImage);
            ivListingPreview = itemView.findViewById(R.id.ivListingPreviewImage);
            tvUserName = itemView.findViewById(R.id.tvOtherUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvDate = itemView.findViewById(R.id.tvTime);
        }

        public void bind(Conversation conversation) {
            currentUser = ParseUser.getCurrentUser();

            // Get other user
            List<ParseUser> users = conversation.getUsers();
            ParseObject otherUser;
            if (currentUser.getObjectId().equals(users.get(0).getObjectId())){
                otherUser = users.get(1);
            } else {
                otherUser = users.get(0);
            }

            try {
                otherUser = otherUser.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Set name and profile photo of other user
            name = (otherUser.getString(KEY_NAME).split(" "))[0];
            tvUserName.setText(name);

            ParseFile profileImage = otherUser.getParseFile(KEY_IMAGE);
            Glide.with(context)
                    .load(profileImage.getUrl())
                    .circleCrop()
                    .into(ivProfilePhoto);

            // get last message and set textview
            ParseMessageClient client = setParseClient(conversation);
            client.getLastMessage(conversation);

            // Set preview photo for listings
            Listing listing = conversation.getListing();
            ParseFile listingImage = listing.getImage();
            if (listingImage != null) {
                Glide.with(context)
                        .load(((ParseFile) listingImage).getUrl())
                        .transform(new MultiTransformation(new CenterCrop(), new GranularRoundedCorners(15, 15, 15, 15)))
                        .into(ivListingPreview);
            }
        }

        private ParseMessageClient setParseClient(Conversation conversation){
            ParseMessageClient parseMessageClient = new ParseMessageClient(context) {
                @Override
                protected void onMessageFetched(Message message) {
                    // Set Text for last message preview and date
                    if (message != null){
                        if (message.getUser().getObjectId().equals(currentUser.getObjectId())){
                            tvLastMessage.setText("You: " + message.getBody());
                        } else {
                            tvLastMessage.setText(name + ": " + message.getBody());
                        }

                        tvDate.setText(message.getCreatedAtDate());
                    } else {
                        tvDate.setText(conversation.getCreatedAtDate());
                    }
                }
            };

            return parseMessageClient;
        }
    }
}
