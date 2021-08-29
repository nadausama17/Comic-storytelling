package com.app.cartoonme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReadMyStories extends AppCompatActivity {

    private ListView mListView;
    storyadapter adapter;
    static String URL = null;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRef2;
    private List<Upload_File> mUploads;
    ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_my_stories);
        mListView = findViewById(R.id.List_view_pdfReadStories);
        mUploads = new ArrayList<>();

        EditText SearchBarFilter=findViewById(R.id.Searchbar1ReadStories);
        ListView listView=findViewById(R.id.List_view_pdfReadStories);




        listView.setTextFilterEnabled(true);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(UserNameSave.UserName);
        mDatabaseRef2 = mDatabaseRef.child("File");
        ClearALL();
        mDatabaseRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload_File upload = postSnapshot.getValue(Upload_File.class);
                    mUploads.add(upload);
                }
                String[] UploadName = new String[mUploads.size()];
                ArrayList<String> temp = new ArrayList<String>();
                for (int i = 0; i < UploadName.length; i++) {
                    UploadName[i] = mUploads.get(i).getmName();
                    URL=mUploads.get(i).getmImageurl();
                    temp.add(UploadName[i]);
                }
                arrayAdapter= new ArrayAdapter<String>(getApplicationContext(),
                       android.R.layout.simple_list_item_1, UploadName);

                mListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReadMyStories.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ReadMyStories.this, WebViewPDF.class);
                i.putExtra("Position",position);
                i.putExtra("url",URL);
                startActivity(i);
            }
        });


        SearchBarFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ReadMyStories.this.adapter.getFilter().filter(s);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void ClearALL() {


    }
}