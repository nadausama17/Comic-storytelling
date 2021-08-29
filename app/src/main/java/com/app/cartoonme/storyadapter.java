package com.app.cartoonme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class storyadapter extends ArrayAdapter<Upload_File> {
    private Context contextt;
    int res;
    List<Upload_File> up;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRef2;

    public storyadapter(Context context,int resource,List<Upload_File> uploadName){
        super(context,resource,uploadName);
        contextt=context;
        res=resource;
        up=uploadName;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

       String storyname= getItem(position).getmName().toString();
        LayoutInflater inflater=LayoutInflater.from(contextt);
        convertView=inflater.inflate(res,parent,false);


        //////open story

        TextView txt = convertView.findViewById(R.id.storyname);
        txt.setText(storyname);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upload_File upload=up.get(position);
                Intent intent =new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.setType("application/pdf");
                intent.setData(Uri.parse(upload.getmImageurl()));
                contextt.getApplicationContext().startActivity(intent);
            }
        });
        

        ////share story
        Button button = convertView.findViewById(R.id.sharebtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(),position+" this", Toast.LENGTH_SHORT).show();
                Upload_File upload_file = up.get(position);
                String[] url = new String[2];
                url[0] = upload_file.getmImageurl();


                Intent ShareIntent=new Intent(Intent.ACTION_SEND);
                ShareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                ShareIntent.setType("application/pdf");
                ShareIntent.putExtra(Intent.EXTRA_TEXT,url[0]);
                ShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent chooserIntent = Intent.createChooser(ShareIntent, "Share Story Via...");
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                contextt.startActivity(chooserIntent);
            }
        });


        ////delete story
        Button button2 = convertView.findViewById(R.id.deletebtn);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upload_File upload2=up.get(position);
                final String name_pdf=upload2.getmName();
                mDatabaseRef = FirebaseDatabase.getInstance().getReference(UserNameSave.UserName);
                mDatabaseRef2 = mDatabaseRef.child("File");

                mDatabaseRef2.child(name_pdf).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mDatabaseRef2.child(name_pdf).removeValue();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Intent i = new Intent(contextt, pdfAdapter.class);
                contextt.startActivity(i);
                Toast.makeText(contextt, "Pdf Deleted", Toast.LENGTH_SHORT).show();

            }
        });


        return convertView;
    }
}
