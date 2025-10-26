package com.example.prm392_finalproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // 🧩 Thêm import này
import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.model.Book;
import com.example.prm392_finalproject.ui.BookDetailActivity;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.VH> {
    private Context ctx;
    private List<Book> list;

    public BookAdapter(Context ctx, List<Book> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_book, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Book b = list.get(position);
        holder.tvTitle.setText(b.getTitle());
        holder.tvAuthor.setText(b.getAuthor() == null || b.getAuthor().isEmpty() ? "-" : b.getAuthor());
        holder.ivFav.setImageResource(b.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        // 🖼️ Load ảnh bằng Glide
        if (b.getImageUri() != null && !b.getImageUri().isEmpty()) {
            Glide.with(ctx)
                    .load(b.getImageUri())
                    .placeholder(R.drawable.sample_image) // hiển thị khi đang load
                    .error(R.drawable.sample_image) // hiển thị nếu lỗi
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.sample_image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, BookDetailActivity.class);
            i.putExtra("book_id", b.getId());
            ctx.startActivity(i);
        });
    }

    @Override
    public int getItemCount() { return list == null ? 0 : list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor;
        ImageView ivFav, ivImage;

        VH(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            ivFav = itemView.findViewById(R.id.ivFav);
            ivImage = itemView.findViewById(R.id.ivImage); // 🧩 lấy từ XML
        }
    }
}
