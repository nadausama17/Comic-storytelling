package com.app.cartoonme;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class WebViewPDF extends AppCompatActivity {
    WebView webview;
    Bitmap bitmap;
    RelativeLayout relativeLayout;
    TextToSpeech textToSpeech;
    String Word;
    Button button;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_p_d_f);
        webview=findViewById(R.id.WebView);
        relativeLayout=findViewById(R.id.rel);
        button=findViewById(R.id.ReadWebView);
        imageView=findViewById(R.id.tempImage);

        webview.getSettings().setJavaScriptEnabled(true);
        String storyurl = getIntent().getStringExtra("url");
        try {
            storyurl= URLEncoder.encode(storyurl,"UTF-8");
            webview.loadUrl("https://docs.google.com/gview?embedded=true&url="+storyurl);
        } catch (UnsupportedEncodingException e)
        { e.printStackTrace();
        }


    button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        bitmap= Bitmap.createBitmap(relativeLayout.getWidth(), relativeLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        relativeLayout.draw(canvas);
        imageView.setImageBitmap(bitmap);

        getTextFromImage(v,imageView);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.speak(Word, TextToSpeech.QUEUE_FLUSH, null);
                    textToSpeech.setLanguage(Locale.ENGLISH);

                } else {
                    Log.e("TTS", "Initialisation Failed!");
                }
            }

        });
    }
});


    }
    public void getTextFromImage(View v, ImageView iv){
        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        TextRecognizer textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational()){
        }else
        {
            Frame frame =new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder stringBuilder=new StringBuilder();

            for(int i = 0;i<items.size();i++){
                TextBlock myItem = items.valueAt(i);
                stringBuilder.append(myItem.getValue());
                stringBuilder.append("\n");
            }
            Word=stringBuilder.toString();
        }
    }
}