package com.example.booksmart.ui.listings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.booksmart.R;
import com.example.booksmart.adapters.ItemAdapter;
import com.example.booksmart.helpers.ParseClient;
import com.example.booksmart.helpers.ParseMessageClient;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Listing;
import com.example.booksmart.ui.MainActivity;
import com.example.booksmart.viewmodels.ConversationsViewModel;
import com.example.booksmart.viewmodels.ListingDetailViewModel;
import com.example.booksmart.viewmodels.ListingsViewModel;
import com.example.booksmart.viewmodels.ProfileViewModel;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class RemoveDialogFragment extends DialogFragment {

    private static final String SUCCESS = "Removed Listing!";
    private static final String FAILURE = "Oops, unable to remove listing! Please try again";

    Listing listing;
    ListingDetailViewModel viewModel;
    ListingsViewModel listingsViewModel;
    ProfileViewModel profileViewModel;
    ConversationsViewModel conversationsViewModel;
    ListingDetailFragment callback;
    ParseMessageClient parseMessageClient;
    ParseClient parseClient;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (ListingDetailFragment) getTargetFragment();
        parseMessageClient = new ParseMessageClient(getContext()){
            @Override
            protected void onConversationsRemoved(Listing listing) {
                conversationsViewModel.refreshConversations();
                parseClient.removeFavorites(listing);
            }
        };

        parseClient = new ParseClient(getContext()){
            @Override
            public void onFavoritesUpdated() {
                listing.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Toast.makeText(getContext(), FAILURE, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        profileViewModel.refreshListings();
                        listingsViewModel.fetchItems("");
                        callback.onRemove();
                    }
                });
            }
        };
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        conversationsViewModel = new ViewModelProvider(requireActivity()).get(ConversationsViewModel.class);
        listingsViewModel = new ViewModelProvider(requireActivity()).get(ListingsViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(ListingDetailViewModel.class);
        listing = (Listing) viewModel.getSelected().getValue();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.remove_listing_confirm)
                .setPositiveButton(R.string.remove_listing, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        parseMessageClient.removeConversations(listing);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}