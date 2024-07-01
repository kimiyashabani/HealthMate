package com.example.healthmate;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech tts;
    private TextView articleText;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView articleImage = findViewById(R.id.articleImage);
        TextView articleTitle = findViewById(R.id.articleTitle);
        articleText = findViewById(R.id.articleText);
        Button readText = findViewById(R.id.readText);

        Intent intent = getIntent();
        int imageId = intent.getIntExtra("imageId", -1);
        String article = intent.getStringExtra("article");
        String title = intent.getStringExtra("title");

        articleImage.setImageResource(imageId);
        articleText.setText(article);
        articleTitle.setText(title);

        //initializing text to speech :
        tts = new TextToSpeech(this, this);
        readText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speackOut();

            }
        });
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.getDefault());
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                System.out.println("Language not supported");
            }
        }else{
            System.out.println("Initialization failed");
        }

    }
    @Override
    protected void onDestroy() {
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speackOut(){
        String text = articleText.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
