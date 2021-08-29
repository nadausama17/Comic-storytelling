package com.app.cartoonme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class pdfAdapter extends AppCompatActivity {
    private ListView mListView;
    storyadapter adapter;
    private ProgressBar mProgressCircle;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRef2;

    private List<Upload_File> mUploads;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_adapter);
        mListView = findViewById(R.id.List_view_pdf);
        mUploads = new ArrayList<>();

        EditText SearchBarFilter=findViewById(R.id.Searchbar1);
        ListView listView=findViewById(R.id.List_view_pdf);


        listView.setTextFilterEnabled(true);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference(UserNameSave.UserName);
        mDatabaseRef2 = mDatabaseRef.child("File");
        //getDataFromFireBase();
        ClearALL();
        mDatabaseRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload_File upload = postSnapshot.getValue(Upload_File.class);
                    mUploads.add(upload);
                }
                String[] UploadName = new String[mUploads.size()];
                List<Upload_File> temp = new ArrayList<Upload_File>();
                for (int i = 0; i < UploadName.length; i++) {
                    UploadName[i] = mUploads.get(i).getmName();
                    temp.add(mUploads.get(i));
                }
                //arrayAdapter= new ArrayAdapter<String>(getApplicationContext(),
                 //       android.R.layout.simple_list_item_1, UploadName);

                adapter=new storyadapter(getApplicationContext(),R.layout.story,temp);
                mListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(pdfAdapter.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                // mProgressCircle.setVisibility(View.INVISIBLE);

            }
        });




        SearchBarFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pdfAdapter.this.adapter.getFilter().filter(s);
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