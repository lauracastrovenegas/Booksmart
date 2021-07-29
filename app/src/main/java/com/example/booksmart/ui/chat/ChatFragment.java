package com.example.booksmart.ui.chat;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.booksmart.R;
import com.example.booksmart.adapters.ChatAdapter;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;
import com.example.booksmart.models.Message;
import com.example.booksmart.ui.listings.ListingDetailFragment;
import com.example.booksmart.ui.listings.ListingsFragment;
import com.example.booksmart.viewmodels.ChatViewModel;
import com.example.booksmart.viewmodels.ListingDetailViewModel;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class ChatFragment extends Fragment {

    public static final String TAG = "ChatFragment";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";

    ChatViewModel chatViewModel;
    ListingDetailViewModel listingDetailViewModel;
    RecyclerView rvMessages;
    ChatAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    EditText etInput;
    ImageButton ibSend;
    ImageView ivBack;
    ImageView ivUserProfilePhoto;
    TextView tvUserName;
    ImageView ivListingPreview;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);

        etInput = view.findViewById(R.id.etMessage);
        ibSend = view.findViewById(R.id.ibSend);
        ivBack = view.findViewById(R.id.ibChatBack);
        ivUserProfilePhoto = view.findViewById(R.id.ivOtherUserProfileImageChat);
        tvUserName = view.findViewById(R.id.tvOtherUserNameChat);
        ivListingPreview = view.findViewById(R.id.ivListingImagePreview);
        rvMessages = view.findViewById(R.id.rvMessages);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        rvMessages.setLayoutManager(linearLayoutManager);

        setViewModel();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFragment(new ConversationsFragment());
            }
        });

        return view;
    }

    private void setViewModel() {
        listingDetailViewModel = new ViewModelProvider((requireActivity())).get(ListingDetailViewModel.class);
        chatViewModel = new ViewModelProvider((requireActivity())).get(ChatViewModel.class);

        chatViewModel.getSelected().observe(getViewLifecycleOwner(), conversation -> {
            setUI(conversation);
        });

        chatViewModel.getMessages().observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                adapter = new ChatAdapter(getContext(), ParseUser.getCurrentUser(), messages);
                rvMessages.setAdapter(adapter);
            }
        });
    }

    private void setUI(Conversation conversation){
        ParseUser currentUser = ParseUser.getCurrentUser();

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
        String name = (otherUser.getString(KEY_NAME).split(" "))[0];
        tvUserName.setText(name);

        ParseFile profileImage = otherUser.getParseFile(KEY_IMAGE);
        Glide.with(getContext())
                .load(profileImage.getUrl())
                .circleCrop()
                .into(ivUserProfilePhoto);

        // Set preview photo for listings
        Listing listing = conversation.getListing();
        ParseFile listingImage = listing.getImage();
        if (listingImage != null) {
            Glide.with(getActivity())
                    .load(((ParseFile) listingImage).getUrl())
                    .transform(new MultiTransformation(new CenterCrop(), new GranularRoundedCorners(15, 15, 15, 15)))
                    .into(ivListingPreview);
        }

        // set on click listener for listing preview image
        ivListingPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listingDetailViewModel.select(listing);
                listingDetailViewModel.setPreviousFragment(new ChatFragment());
                goToDetail();
            }
        });

        // set on click listener for send button
        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = etInput.getText().toString();
                Message message = new Message();
                message.setBody(body);
                message.setUser(ParseUser.getCurrentUser());
                message.setConversation(conversation);
                chatViewModel.saveMessage(message);
                etInput.setText(null);
            }
        });
    }

    private void goToDetail(){
        Fragment fragment = new ListingDetailFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out_left);
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void goToFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out);
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}