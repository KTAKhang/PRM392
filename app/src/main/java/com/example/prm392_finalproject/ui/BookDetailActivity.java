package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.Book;

public class BookDetailActivity extends AppCompatActivity {
    TextView tvTitle, tvAuthor, tvYear, tvNotes;
    Button btnEdit;
    DatabaseHelper db;
    int bookId;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_book_detail);
        db = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvYear = findViewById(R.id.tvYear);
        tvNotes = findViewById(R.id.tvNotes);
        btnEdit = findViewById(R.id.btnEdit);

        bookId = getIntent().getIntExtra("book_id", -1);
        if (bookId == -1) finish();

        load();

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, EditBookActivity.class);
            i.putExtra("book_id", bookId);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        Book b = db.getBookById(bookId);
        if (b == null) return;
        tvTitle.setText(b.getTitle());
        tvAuthor.setText(b.getAuthor());
        tvYear.setText(String.valueOf(b.getYear()));
        tvNotes.setText(b.getNotes());
    }
}
