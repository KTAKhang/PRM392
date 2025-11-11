package com.example.prm392_finalproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.model.Restaurant;
import com.example.prm392_finalproject.ui.RestaurantDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.VH> {

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Restaurant restaurant, int position);
    }

    private final Context ctx;
    private List<Restaurant> list;
    private final OnFavoriteClickListener favoriteClickListener;

    public RestaurantAdapter(Context ctx, List<Restaurant> list, OnFavoriteClickListener favoriteClickListener) {
        this.ctx = ctx;
        this.list = list != null ? list : new ArrayList<>();
        this.favoriteClickListener = favoriteClickListener;
    }

    public void updateData(List<Restaurant> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_restaurant, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Restaurant restaurant = list.get(position);
        holder.tvName.setText(restaurant.getName());
        holder.tvAddress.setText(
                restaurant.getAddress() == null || restaurant.getAddress().isEmpty() ? "-" : restaurant.getAddress());
        
        // Update favorite button state
        boolean isFavorite = restaurant.isFavorite();
        holder.ivFav.setSelected(isFavorite);
        holder.ivFav.setImageResource(
                isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        
        // Update background and ripple effect based on favorite state
        if (isFavorite) {
            holder.ivFav.setBackgroundResource(R.drawable.bg_fav_button_active_ripple);
            holder.ivFav.setColorFilter(ContextCompat.getColor(ctx, android.R.color.white));
            // Animate scale for visual feedback
            holder.ivFav.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(200)
                    .start();
        } else {
            holder.ivFav.setBackgroundResource(R.drawable.bg_fav_button_ripple);
            holder.ivFav.setColorFilter(ContextCompat.getColor(ctx, android.R.color.white));
            holder.ivFav.setScaleX(1.0f);
            holder.ivFav.setScaleY(1.0f);
        }

        if (restaurant.getImageUri() != null && !restaurant.getImageUri().isEmpty()) {
            String imageUri = restaurant.getImageUri();
            Uri parsed = Uri.parse(imageUri);
            Object source = parsed.getScheme() != null ? parsed : imageUri;
            Glide.with(ctx)
                    .load(source)
                    .placeholder(R.drawable.sample_image)
                    .error(R.drawable.sample_image)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.sample_image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, RestaurantDetailActivity.class);
            i.putExtra("restaurant_id", restaurant.getId());
            ctx.startActivity(i);
        });

        holder.ivFav.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            if (favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClick(list.get(adapterPosition), adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress;
        ImageView ivFav, ivImage;

        VH(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivFav = itemView.findViewById(R.id.ivFav);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}

