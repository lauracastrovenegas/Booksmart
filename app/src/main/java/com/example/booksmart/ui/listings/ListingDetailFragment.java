package com.example.booksmart.ui.listings;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.booksmart.R;
import com.example.booksmart.helpers.DeviceDimensionsHelper;
import com.example.booksmart.helpers.ParseClient;
import com.example.booksmart.helpers.ParseMessageClient;
import com.example.booksmart.models.Book;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Favorite;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Message;
import com.example.booksmart.ui.MainActivity;
import com.example.booksmart.ui.chat.ChatFragment;
import com.example.booksmart.viewmodels.ChatViewModel;
import com.example.booksmart.viewmodels.ConversationsViewModel;
import com.example.booksmart.viewmodels.ListingDetailViewModel;
import com.example.booksmart.viewmodels.ProfileViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.example.booksmart.models.Listing;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class ListingDetailFragment extends Fragment {

    public static final String KEY = "detail_listing";
    public static final String FAIL_MSG = "Failed to retrieve listing";
    public static final String TAG = "ListingDetailFragment";
    public static final int IMAGE_HEIGHT = 800;

    ChatViewModel chatViewModel;
    ListingDetailViewModel listingDetailViewModel;
    ProfileViewModel profileViewModel;
    ParseClient parseClient;
    ParseMessageClient parseMessageClient;
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
    Button btnGetBook;
    Button btnRemove;
    Button btnSold;
    ProgressBar progressBar;
    Favorite existingFavorite;
    Favorite favorite;
    Boolean isFavorite;
    KonfettiView konfettiView;
    RelativeLayout content;
    ScrollView getBookLinks;
    RelativeLayout amazonButton;
    RelativeLayout barnesNoblesButton;
    RelativeLayout booksMillionButton;
    RelativeLayout googleBooksButton;

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
        btnGetBook = itemView.findViewById(R.id.btnGetBook);
        btnSave = itemView.findViewById(R.id.btnSaveItem);
        btnRemove = itemView.findViewById(R.id.btnRemove);
        btnSold = itemView.findViewById(R.id.btnSold);
        ivClose = itemView.findViewById(R.id.ivClose);
        progressBar = itemView.findViewById(R.id.pbListingDetail);
        konfettiView = itemView.findViewById(R.id.viewKonfetti);
        content = itemView.findViewById(R.id.rlContentDetail);
        getBookLinks = itemView.findViewById(R.id.get_book_links);
        amazonButton = itemView.findViewById(R.id.amazon_link_button);
        barnesNoblesButton = itemView.findViewById(R.id.barnes_nobles_link_button);
        booksMillionButton = itemView.findViewById(R.id.books_million_link_button);
        googleBooksButton = itemView.findViewById(R.id.google_books_link_button);
        progressBar.setVisibility(View.VISIBLE);
        isFavorite = false;

        setParseClient();

        return itemView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());
        chatViewModel = viewModelProvider.get(ChatViewModel.class);
        profileViewModel = viewModelProvider.get(ProfileViewModel.class);
        listingDetailViewModel = viewModelProvider.get(ListingDetailViewModel.class);

        listingDetailViewModel.getPreviousFragment().observe(getViewLifecycleOwner(), fragment -> {
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToFragment(fragment);
                }
            });
        });

        listingDetailViewModel.getSelected().observe(getViewLifecycleOwner(), item -> {
            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
            if (item.getType() == Item.TYPE_LISTING){
                ParseUser user = ((Listing) item).getUser();
                try {
                    user = user.fetchIfNeeded();
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }

                ivImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                ParseFile image = ((Listing) item).getParseFile(Listing.KEY_IMAGE);
                if (image != null){
                    Glide.with(getContext())
                            .load(image.getUrl())
                            .into(ivImage);
                }

                ParseFile profileImage = ((Listing) item).getParseUser(Listing.KEY_USER).getParseFile(Listing.KEY_IMAGE);
                if (profileImage != null){
                    Glide.with(getContext())
                            .load(profileImage.getUrl())
                            .circleCrop()
                            .into(ivUserProfileImage);
                }

                tvTitle.setText(((Listing) item).getTitle());
                tvPrice.setText("$" + String.valueOf(((Listing) item).getPrice()));
                tvUserUsername.setText(((Listing) item).getUser().getUsername());
                if (!((Listing) item).getCourse().equals("")){
                    btnCourse.setText(((Listing) item).getCourse());
                } else {
                    btnCourse.setVisibility(View.GONE);
                }

                tvDescription.setText(((Listing) item).getDescription());
                tvAuthors.setVisibility(View.GONE);

                // This listing belongs to the current user
                if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    checkIfSold((Listing) item);
                    btnRemove.setVisibility(View.VISIBLE);
                    btnSold.setVisibility(View.VISIBLE);

                    btnSold.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!((Listing) item).isSold()){
                                showSoldDialog();
                            }
                        }
                    });

                    btnRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showRemoveDialog();
                        }
                    });

                } else { // Listing does not belong to current user
                    checkIfFavorite(item);
                    btnSave.setVisibility(View.VISIBLE);
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleFavorite(item);
                        }
                    });

                    btnMessageSeller.setVisibility(View.VISIBLE);
                    btnMessageSeller.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startConversation((Listing) item);
                        }
                    });
                }
            } else { // Item is a book
                checkIfFavorite(item);
                ivImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                String image = ((Book) item).getImage();
                if (image.isEmpty() || image == null){
                    Glide.with(getContext())
                            .load(R.drawable.book_cover_placeholder)
                            .into(ivImage);
                } else {
                    Glide.with(getContext())
                            .load(image)
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
                    tvPrice.setText(String.valueOf(((Book) item).getPrice()));
                } else {
                    tvPrice.setVisibility(View.GONE);
                }

                btnGetBook.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleFavorite(item);
                    }
                });

                setGetBookLinks((Book) item, view);
            }

            progressBar.setVisibility(View.INVISIBLE);
        });
    }

    @Override
    public void onPause() {
        saveFavorite();
        super.onPause();
    }

    public void setParseClient() {
        parseMessageClient = new ParseMessageClient(getContext()){
            @Override
            protected void onConversationFetched(Conversation conversation) {
                chatViewModel.select(conversation);
                goToChat();
            }
        };

        parseClient = new ParseClient(getContext()){
            @Override
            public void onItemFavorite(Favorite favorite) {
                existingFavorite = favorite;
                if (existingFavorite != null){
                    isFavorite = true;
                    btnSave.setText("Saved");
                    btnSave.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_active, 0, 0, 0);
                }
            }

            @Override
            public void onFavoritesUpdated() {
                profileViewModel.refreshFavorites();
            }
        };
    }

    private void checkIfFavorite(Item item) {
        parseClient.checkItemFavorite(item);
    }

    private void toggleFavorite(Item item) {
        if (!isFavorite){ // Not a favorite
            favorite = new Favorite();
            favorite.setUser(ParseUser.getCurrentUser());
            favorite.setType(item.getType());
            if (item.getType() == Item.TYPE_BOOK){
                favorite.setBookId(((Book) item).getId());
            } else {
                favorite.setListing((Listing) item);
            }
            btnSave.setText("Saved");
            btnSave.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_active, 0, 0, 0);
        } else { // Already a favorite
            favorite = null;
            btnSave.setText("Save");
            btnSave.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart, 0, 0, 0);
        }
        isFavorite = !isFavorite;
    }

    private void saveFavorite(){
        if (existingFavorite == null && isFavorite){
            parseClient.saveFavorite(favorite);
        } else if (existingFavorite != null && isFavorite == false){
            parseClient.removeFavorite(existingFavorite);
        }
    }

    private void showRemoveDialog(){
        DialogFragment dialogFragment = new RemoveDialogFragment();
        dialogFragment.setTargetFragment(this, MainActivity.REMOVE_REQUEST);
        dialogFragment.show(getFragmentManager(), RemoveDialogFragment.class.getSimpleName());
    }

    public void onRemove() {
        profileViewModel.refreshListings();
        goToFragment(listingDetailViewModel.getPreviousFragment().getValue());
    }

    private void checkIfSold(Listing listing) {
        if (listing.isSold()){
            btnSold.setText("Sold");
            btnSold.setClickable(false);
            btnSold.setBackgroundColor(getActivity().getResources().getColor(R.color.gray));
            ivImage.setColorFilter(R.color.black);
        }
    }

    private void showSoldDialog(){
        DialogFragment dialogFragment = new SoldDialogFragment();
        dialogFragment.setTargetFragment(this, MainActivity.REMOVE_REQUEST);
        dialogFragment.show(getFragmentManager(), SoldDialogFragment.class.getSimpleName());
    }

    public void onSold() {
        showConfetti();
        profileViewModel.refreshListings();
        btnSold.setText("Sold");
        btnSold.setClickable(false);
        btnSold.setBackgroundColor(getActivity().getResources().getColor(R.color.gray));
        ivImage.setColorFilter(R.color.black);
    }

    private void setGetBookLinks(Book item, View view){
        btnGetBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getBookLinks.getVisibility() == View.GONE){
                    getBookLinks.setVisibility(View.VISIBLE);
                } else {
                    getBookLinks.setVisibility(View.GONE);
                }
            }
        });

        String amazonLink = ((Book) item).getAmazonLink();
        if (!amazonLink.equals("")){
            amazonButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(amazonLink)));
                    getBookLinks.setVisibility(View.GONE);
                }
            });
        } else {
            View divider = view.findViewById(R.id.divider2);
            divider.setVisibility(View.GONE);
            amazonButton.setVisibility(View.GONE);
        }

        String barnesNoblesLink = ((Book) item).getBarnesNoblesLink();
        if (!barnesNoblesLink.equals("")){
            barnesNoblesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(barnesNoblesLink)));
                    getBookLinks.setVisibility(View.GONE);
                }
            });
        } else {
            View divider = view.findViewById(R.id.divider3);
            divider.setVisibility(View.GONE);
            barnesNoblesButton.setVisibility(View.GONE);
        }

        String booksMillionLink = ((Book) item).getBooksMillionLink();
        if (!booksMillionLink.equals("")){
            booksMillionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(booksMillionLink)));
                    getBookLinks.setVisibility(View.GONE);
                }
            });
        } else {
            View divider = view.findViewById(R.id.divider4);
            divider.setVisibility(View.GONE);
            booksMillionButton.setVisibility(View.GONE);
        }

        googleBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((Book) item).getGoogleLink())));
                getBookLinks.setVisibility(View.GONE);
            }
        });

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBookLinks.setVisibility(View.GONE);
            }
        });
    }

    private void showConfetti() {
        konfettiView.build()
                .addColors(Color.rgb(255,115,71), Color.rgb(169,138,255), Color.rgb(55,0,179))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 3000L);
    }

    // Check if conversation for this listing already exists, navigate to conversation
    private void startConversation(Listing listing) {
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
        saveFavorite();
        Fragment fragment = new ChatFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out_left);
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void goToFragment(Fragment fragment){
        saveFavorite();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out);
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}