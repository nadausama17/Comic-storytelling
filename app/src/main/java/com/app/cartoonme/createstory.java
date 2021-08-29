package com.app.cartoonme;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class createstory extends AppCompatActivity {
    Button cont;
    EditText pdfn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createstory);

        pdfn=findViewById(R.id.BookName);
        cont = findViewById(R.id.Continue) ;

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserNameSave.pdfName=pdfn.getText().toString();
                Intent i = new Intent(createstory.this, convertImageToPdf.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }
}