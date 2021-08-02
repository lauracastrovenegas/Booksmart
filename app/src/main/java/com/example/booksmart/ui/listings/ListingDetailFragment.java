package com.example.booksmart.ui.listings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.booksmart.R;
import com.example.booksmart.helpers.DeviceDimensionsHelper;
import com.example.booksmart.helpers.ParseMessageClient;
import com.example.booksmart.models.Book;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Message;
import com.example.booksmart.ui.chat.ChatFragment;
import com.example.booksmart.viewmodels.ChatViewModel;
import com.example.booksmart.viewmodels.ConversationsViewModel;
import com.example.booksmart.viewmodels.ListingDetailViewModel;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.example.booksmart.models.Listing;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListingDetailFragment extends Fragment {

    public static final String KEY = "detail_listing";
    public static final String FAIL_MSG = "Failed to retrieve listing";
    public static final String TAG = "ListingDetailFragment";
    public static final int IMAGE_HEIGHT = 800;

    ChatViewModel chatViewModel;
    ListingDetailViewModel listingDetailViewModel;
    ImageView ivImage;
    ImageView ivUserProfileImage;
    TextView tvTitle;
    TextView tvPrice;
    TextView tvUserUsername;
    TextView tvAuthors;
    ImageView ivClose;
    Button btnCourse;
    TextView tvDescription;
    Button btnSave;
    Button btnMessageSeller;
    Button btnLinkToGoogle;
    Button btnRemove;
    Button btnSold;
    ProgressBar progressBar;

    public ListingDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_listing_detail, container, false);

        ivImage = itemView.findViewById(R.id.ivListingImageDetail);
        ivUserProfileImage = itemView.findViewById(R.id.ivListingUserDetail);
        tvTitle = itemView.findViewById(R.id.tvListingTitleDetail);
        tvAuthors = itemView.findViewById(R.id.tvListingAuthorDetail);
        tvPrice = itemView.findViewById(R.id.tvListingPriceDetail);
        tvUserUsername = itemView.findViewById(R.id.tvListingUserDetail);
        btnCourse = itemView.findViewById(R.id.btnListingCourse);
        tvDescription = itemView.findViewById(R.id.tvListingDescription);
        btnMessageSeller = itemView.findViewById(R.id.btnMessageSeller);
        btnLinkToGoogle = itemView.findViewById(R.id.btnGoogleBooksLink);
        btnSave = itemView.findViewById(R.id.btnSaveItem);
        btnRemove = itemView.findViewById(R.id.btnRemove);
        btnSold = itemView.findViewById(R.id.btnSold);
        ivClose = itemView.findViewById(R.id.ivClose);
        progressBar = itemView.findViewById(R.id.pbListingDetail);
        progressBar.setVisibility(View.VISIBLE);

        return itemView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatViewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);
        listingDetailViewModel = new ViewModelProvider(requireActivity()).get(ListingDetailViewModel.class);

        listingDetailViewModel.getPreviousFragment().observe(getViewLifecycleOwner(), fragment -> {
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToFragment(fragment);
                }
            });
        });

        listingDetailViewModel.getSelected().observe(getViewLifecycleOwner(), item -> {
            if (item.getType() == Item.TYPE_LISTING){
                ParseUser user = ((Listing) item).getParseUser("user");
                try {
                    user = user.fetchIfNeeded();
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }

                ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ParseFile image = ((Listing) item).getParseFile(Listing.KEY_IMAGE);
                if (image != null){
                    int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());

                    Glide.with(getContext())
                            .load(image.getUrl())
                            .centerCrop()
                            .into(ivImage);
                }

                ParseFile profileImage = ((Listing) item).getParseUser(Listing.KEY_USER).getParseFile(Listing.KEY_IMAGE);
                if (profileImage != null){
                    Glide.with(getContext())
                            .load(profileImage.getUrl())
                            .circleCrop()
                            .into(ivUserProfileImage);
                }

                tvTitle.setText(((Listing) item).getString(Listing.KEY_TITLE));
                tvPrice.setText("$" + String.valueOf(((Listing) item).getInt(Listing.KEY_PRICE)));
                tvUserUsername.setText(((Listing) item).getParseUser(Listing.KEY_USER).getUsername());
                btnCourse.setText(((Listing) item).getString(Listing.KEY_COURSE));
                tvDescription.setText(((Listing) item).getString(Listing.KEY_DESCRIPTION));
                tvAuthors.setVisibility(View.GONE);

                if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    // This listing belongs to the current user
                    btnSold.setVisibility(View.VISIBLE);
                    btnRemove.setVisibility(View.VISIBLE);

                    btnSold.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Sold!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    btnRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Remove Listing!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    btnMessageSeller.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);

                    btnMessageSeller.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startConversation((Listing) item);
                        }
                    });

                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Favorite!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else { // Item is a book
                String image = ((Book) item).getImage();
                int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
                if (image.isEmpty() || image == null){
                    Glide.with(getContext())
                            .load(R.drawable.book_cover_placeholder)
                            .override(screenWidth/2 + 60, IMAGE_HEIGHT)
                            .centerCrop()
                            .into(ivImage);
                } else {
                    Glide.with(getContext())
                            .load(image)
                            .override(screenWidth/2 + 60, IMAGE_HEIGHT)
                            .centerCrop()
                            .into(ivImage);
                }

                tvTitle.setText(((Book) item).getTitle());
                if (((Book) item).getAuthors() != null){
                    tvAuthors.setText(setAuthorString(((Book) item).getAuthors()));
                } else {
                    tvAuthors.setVisibility(View.GONE);
                }
                tvUserUsername.setText(((Book) item).getUserName());
                btnCourse.setVisibility(View.GONE);
                if (((Book) item).getDescription().isEmpty()){
                    tvDescription.setVisibility(View.GONE);
                } else {
                    tvDescription.setText(((Book) item).getDescription());
                }
                ivUserProfileImage.setImageResource(R.drawable.google_books_logo);

                if (((Book) item).getPrice() != null){
                    tvPrice.setText("$" + String.valueOf(((Book) item).getPrice()));
                } else {
                    tvPrice.setVisibility(View.GONE);
                }

                btnLinkToGoogle.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);

                btnLinkToGoogle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((Book) item).getGoogleLink())));
                    }
                });
            }

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Favorite!", Toast.LENGTH_SHORT).show();
                }
            });

            progressBar.setVisibility(View.INVISIBLE);
        });
    }

    private void startConversation(Listing listing) {
        getConversation(listing);
    }

    // Check if conversation for this listing already exists, navigate to conversation
    private void getConversation(Listing listing){
        ParseMessageClient parseMessageClient = new ParseMessageClient(getContext()){
            @Override
            protected void onConversationFetched(Conversation conversation) {
                chatViewModel.select(conversation);
                chatViewModel.setMessages(new ArrayList<Message>());
                goToChat();
            }
        };

        parseMessageClient.getConversation(listing);
    }

    private String setAuthorString(List<String> authors){
        if (authors.size() > 0){
            String authorString = "By " + authors.get(0);
            for (int i = 1; i < authors.size(); i++){
                authorString = authorString + ", " + authors.get(i);
            }

            return authorString;
        }

        return "";
    }

    private void goToChat(){
        Fragment fragment = new ChatFragment();
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