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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.booksmart.DeviceDimensionsHelper;
import com.example.booksmart.R;
import com.example.booksmart.models.Listing;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    public static final String TAG = "ListingAdapter";

    List<Listing> listings;
    Context context;

    public ListingAdapter(Context context, List<Listing> listings){
        this.context = context;
        this.listings = listings;
    }

    @NonNull
    @NotNull
    @Override
    public ListingAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ListingAdapter.ViewHolder holder, int position) {
        Listing listing = listings.get(position);
        holder.bind(listing);
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public void clear() {
        listings.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        ImageView ivUserProfileImage;
        TextView tvTitle;
        TextView tvPrice;
        TextView tvUserUsername;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivListingImage);
            ivUserProfileImage = itemView.findViewById(R.id.ivListingUser);
            tvTitle = itemView.findViewById(R.id.tvListingTitle);
            tvPrice = itemView.findViewById(R.id.tvListingPrice);
            tvUserUsername = itemView.findViewById(R.id.tvListingUser);
        }

        public void bind(Listing listing) {

            tvTitle.setText(listing.getTitle());
            tvPrice.setText("$" + String.valueOf(listing.getPrice()));
            tvUserUsername.setText(listing.getUser().getUsername());

            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(context);

            Log.d(TAG, String.valueOf((screenWidth/2) - 200) + String.valueOf(screenWidth/2));

            ParseFile image = listing.getImage();
            if (image != null){
                Glide.with(context)
                        .load(image.getUrl())
                        .override(screenWidth/2,(screenWidth/2) - 200)
                        .transform(new MultiTransformation(new CenterCrop(), new GranularRoundedCorners(45, 45, 0, 0)))
                        .into(ivImage);
            }

            ParseFile profileImage = listing.getUser().getParseFile(Listing.KEY_IMAGE);
            if (profileImage != null){
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .circleCrop()
                        .into(ivUserProfileImage);
            }
        }
    }
}
