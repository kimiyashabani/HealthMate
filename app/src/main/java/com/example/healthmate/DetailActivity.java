package com.example.healthmate;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
public class DetailActivity extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView articleImage = findViewById(R.id.articleImage);
        TextView articleTitle = findViewById(R.id.articleTitle);
        TextView articleText = findViewById(R.id.articleText);

        Intent intent = getIntent();
        int imageId = intent.getIntExtra("imageId", -1);
        String article = intent.getStringExtra("article");
        String title = intent.getStringExtra("title");

        articleImage.setImageResource(imageId);
        articleText.setText(article);
        articleTitle.setText(title);
    }
}
