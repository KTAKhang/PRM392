package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.Book;

public class AddBookActivity extends AppCompatActivity {

    EditText etTitle, etAuthor, etYear, etNotes;
    Switch swFav;
    Button btnSave, btnPickImage;
    ImageView imgPreview;
    DatabaseHelper db;
    int userId;
    Uri imageUri;

    ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_add_book);

        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("MyLibrary", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etYear = findViewById(R.id.etYear);
        etNotes = findViewById(R.id.etNotes);
        swFav = findViewById(R.id.swFav);
        btnSave = findViewById(R.id.btnSave);
        imgPreview = findViewById(R.id.imgPreview);
        btnPickImage = findViewById(R.id.btnPickImage);

        // chọn ảnh
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        imgPreview.setImageURI(uri);
                    }
                });

        btnPickImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Nhập tên sách", Toast.LENGTH_SHORT).show();
                return;
            }

            Book b = new Book();
            b.setTitle(title);
            b.setAuthor(etAuthor.getText().toString().trim());
            b.setYear(etYear.getText().toString().isEmpty() ? 0 :
                    Integer.parseInt(etYear.getText().toString()));
            b.setNotes(etNotes.getText().toString());
            b.setFavorite(swFav.isChecked());
            b.setUserId(userId);
            b.setImageUri(imageUri != null ? imageUri.toString() : null);

            long id = db.insertBook(b);
            if (id != -1) {
                Toast.makeText(this, "Thêm sách thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi thêm sách", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
