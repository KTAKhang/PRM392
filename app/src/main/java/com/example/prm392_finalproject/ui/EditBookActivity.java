package com.example.prm392_finalproject.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.Book;

public class EditBookActivity extends AppCompatActivity {
    EditText etTitle, etAuthor, etYear, etNotes;
    Switch swFav;
    Button btnUpdate, btnDelete;
    DatabaseHelper db;
    int bookId;
    Book book;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_edit_book);
        db = new DatabaseHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etYear = findViewById(R.id.etYear);
        etNotes = findViewById(R.id.etNotes);
        swFav = findViewById(R.id.swFav);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        bookId = getIntent().getIntExtra("book_id", -1);
        if (bookId == -1) { finish(); return; }

        loadBook();

        btnUpdate.setOnClickListener(v -> {
            book.setTitle(etTitle.getText().toString().trim());
            book.setAuthor(etAuthor.getText().toString().trim());
            book.setYear(Integer.parseInt(etYear.getText().toString().isEmpty() ? "0" : etYear.getText().toString()));
            book.setNotes(etNotes.getText().toString());
            book.setFavorite(swFav.isChecked());
            boolean ok = db.updateBook(book);
            Toast.makeText(this, ok ? "Cập nhật thành công" : "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
            if (ok) finish();
        });

        btnDelete.setOnClickListener(v -> {
            boolean ok = db.deleteBook(bookId);
            Toast.makeText(this, ok ? "Đã xóa" : "Lỗi xóa", Toast.LENGTH_SHORT).show();
            if (ok) finish();
        });
    }

    private void loadBook() {
        book = db.getBookById(bookId);
        if (book == null) return;
        etTitle.setText(book.getTitle());
        etAuthor.setText(book.getAuthor());
        etYear.setText(String.valueOf(book.getYear()));
        etNotes.setText(book.getNotes());
        swFav.setChecked(book.isFavorite());
    }
}
