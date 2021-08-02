package com.example.booksmart.ui.listings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.booksmart.R;
import com.example.booksmart.helpers.ParseClient;
import com.example.booksmart.helpers.ParseMessageClient;
import com.example.booksmart.models.Listing;
import com.example.booksmart.viewmodels.ConversationsViewModel;
import com.example.booksmart.viewmodels.ListingDetailViewModel;
import com.example.booksmart.viewmodels.ListingsViewModel;
import com.parse.DeleteCallback;
import com.parse.ParseException;

public class SoldDialogFragment extends DialogFragment {

    Listing listing;
    ListingDetailViewModel viewModel;
    ListingsViewModel listingsViewModel;
    ListingDetailFragment callback;
    ParseClient parseClient;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (ListingDetailFragment) getTargetFragment();
        parseClient = new ParseClient(getContext()){
            @Override
            public void onListingUpdated() {
                listingsViewModel.fetchItems("");
                callback.onSold();
            }
        };
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listingsViewModel = new ViewModelProvider(requireActivity()).get(ListingsViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(ListingDetailViewModel.class);
        listing = (Listing) viewModel.getSelected().getValue();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.mark_as_sold_confirm)
                .setPositiveButton(R.string.sold_listing, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        parseClient.markAsSold(listing);
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