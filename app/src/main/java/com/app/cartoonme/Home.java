package com.app.cartoonme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout CreateStory = findViewById(R.id.Createnewstorybtn);
        CreateStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, ImageAct.class);
                startActivity(i);
            }
        });

        LinearLayout pdf = findViewById(R.id.SaveStoryAsPDFbtn);
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, createstory.class);
                startActivity(i);
            }
        });

        LinearLayout MyStories = findViewById(R.id.MyStoriesbtn);
        MyStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, pdfAdapter.class);
                startActivity(i);
            }
        });

        LinearLayout ReadMyStories = findViewById(R.id.ReadMyStories);
        ReadMyStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, ReadMyStories.class);
                startActivity(i);
            }
        });
        LinearLayout LogoutButton = findViewById(R.id.LogOutbtn);
        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(Home.this, SignIn.class);
               startActivity(i);
            }
        });
    }
}