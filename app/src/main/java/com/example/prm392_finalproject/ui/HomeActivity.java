package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.adapter.BookAdapter;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.Book;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvBooks;
    private BookAdapter adapter;
    private DatabaseHelper db;
    private int userId;
    private List<Book> allBooks = new ArrayList<>();
    private SearchView searchView;
    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("MyLibrary", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Ánh xạ view
        rvBooks = findViewById(R.id.rvBooks);
        topAppBar = findViewById(R.id.topAppBar);
        searchView = findViewById(R.id.searchView);
        bottomNav = findViewById(R.id.bottomNav);

        // Setup RecyclerView
        rvBooks.setLayoutManager(new GridLayoutManager(this, 2));

        // Sự kiện menu topbar
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.btnAdd) {
                startActivity(new Intent(this, AddBookActivity.class));
                return true;
            } else if (item.getItemId() == R.id.btnList) {
                Toast.makeText(this, "Xem danh sách sách", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });


        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_books) {
                return true;
            } else if (itemId == R.id.nav_search) {
                Toast.makeText(this, "Chức năng tìm kiếm", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(this, AddBookActivity.class));
                return true;
            } else if (itemId == R.id.nav_stats) {
                Toast.makeText(this, "Thống kê đang phát triển", Toast.LENGTH_SHORT).show();
                return true;
                //thêm profile
            } else if (itemId == R.id.nav_user) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }

            return false;
        });

        // Search
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override public boolean onQueryTextSubmit(String query) { return false; }
                @Override public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return true;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }

    private void loadBooks() {
        allBooks = db.getBooksByUser(userId);
        adapter = new BookAdapter(this, allBooks);
        rvBooks.setAdapter(adapter);
    }

    private void filter(String q) {
        if (allBooks == null) return;
        List<Book> filtered = new ArrayList<>();
        for (Book b : allBooks) {
            if ((b.getTitle() != null && b.getTitle().toLowerCase().contains(q.toLowerCase()))
                    || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(q.toLowerCase()))) {
                filtered.add(b);
            }
        }
        adapter = new BookAdapter(this, filtered);
        rvBooks.setAdapter(adapter);
    }
}
