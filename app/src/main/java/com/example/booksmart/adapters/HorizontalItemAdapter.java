package com.example.booksmart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.booksmart.R;
import com.example.booksmart.helpers.DeviceDimensionsHelper;
import com.example.booksmart.models.Book;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Listing;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HorizontalItemAdapter extends RecyclerView.Adapter {

    public static final String TAG = "HorizontalItemAdapter";
    public static final int CORNER_RADIUS = 40;

    List<Item> items;
    Context context;

    public HorizontalItemAdapter(Context context, List<Item> items){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listing_horizontal, parent, false);
        if (viewType == Item.TYPE_BOOK){
            return new BookViewHolder(view);
        } else {
            return new ListingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == Item.TYPE_BOOK){
            ((BookViewHolder) holder).bind(position);
        } else {
            ((ListingViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<? extends Item> newItems) {
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public class ListingViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        ImageView ivUserProfileImage;
        TextView tvTitle;
        TextView tvPrice;
        TextView tvUserUsername;
        RelativeLayout rlContainer;

        public ListingViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivListingImage);
            ivUserProfileImage = itemView.findViewById(R.id.ivListingUser);
            tvTitle = itemView.findViewById(R.id.tvListingTitle);
            tvPrice = itemView.findViewById(R.id.tvListingPrice);
            tvUserUsername = itemView.findViewById(R.id.tvListingUser);
            rlContainer = itemView.findViewById(R.id.rlItemListing);
        }

        public void bind(int position) {
            Listing listing = (Listing) items.get(position);

            ParseUser user = listing.getUser();
            try {
                user = user.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvTitle.setText(listing.getTitle());
            tvPrice.setText("$" + String.valueOf(listing.getPrice()));
            tvUserUsername.setText(user.getUsername());

            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(context);

            ParseFile image = listing.getImage();
            if (image != null){
                Glide.with(context)
                        .load(image.getUrl())
                        .override(screenWidth/2, screenWidth/2)
                        .transform(new MultiTransformation(new CenterCrop(), new GranularRoundedCorners(CORNER_RADIUS, CORNER_RADIUS, 0, 0)))
                        .into(ivImage);
            }

            if (listing.isSold()){
                ivImage.setColorFilter(R.color.black);
            }

            ParseFile profileImage = user.getParseFile(Listing.KEY_IMAGE);
            if (profileImage != null){
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .circleCrop()
                        .into(ivUserProfileImage);
            }

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) rlContainer.getLayoutParams();
            layoutParams.width = screenWidth/2;
            rlContainer.setLayoutParams(layoutParams);
        }
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        ImageView ivUserProfileImage;
        TextView tvTitle;
        TextView tvPrice;
        TextView tvUserUsername;
        RelativeLayout rlContainer;

        public BookViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivListingImage);
            ivUserProfileImage = itemView.findViewById(R.id.ivListingUser);
            tvTitle = itemView.findViewById(R.id.tvListingTitle);
            tvPrice = itemView.findViewById(R.id.tvListingPrice);
            tvUserUsername = itemView.findViewById(R.id.tvListingUser);
            rlContainer = itemView.findViewById(R.id.rlItemListing);
        }

        public void bind(int position) {

            Book book = (Book) items.get(position);

            tvTitle.setText(book.getTitle());
            if (book.getPrice() != null){
                tvPrice.setText(book.getPrice());
            } else {
                tvPrice.setVisibility(View.GONE);
            }

            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(context);

            String imageUrl = book.getImage();
            if (!imageUrl.isEmpty()){
                Glide.with(context)
                        .load(imageUrl)
                        .override(screenWidth/2,screenWidth/2)
                        .transform(new MultiTransformation(new CenterCrop(), new GranularRoundedCorners(CORNER_RADIUS, CORNER_RADIUS, 0, 0)))
                        .into(ivImage);
            } else {
                Glide.with(context)
                        .load(R.drawable.book_cover_placeholder_orange)
                        .override(screenWidth/2,(screenWidth/2))
                        .transform(new MultiTransformation(new CenterCrop(), new GranularRoundedCorners(CORNER_RADIUS, CORNER_RADIUS, 0, 0)))
                        .into(ivImage);
            }

            tvUserUsername.setText(book.getUserName());
            ivUserProfileImage.setImageResource(R.drawable.google_books_logo);

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) rlContainer.getLayoutParams();
            layoutParams.width = screenWidth/2;
            rlContainer.setLayoutParams(layoutParams);
        }
    }
}
