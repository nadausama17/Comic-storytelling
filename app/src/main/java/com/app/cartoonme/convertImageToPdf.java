package com.app.cartoonme;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class convertImageToPdf extends AppCompatActivity {
    private static int PICK_IMAGE_REQUEST=1;
    Button upload;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Button pickImage;
    private Button convert;
    List<Bitmap> bitmaps=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_image_to_pdf);

        mStorageRef=  FirebaseStorage.getInstance().getReference();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference();

        pickImage=findViewById(R.id.PickImages);
        upload=findViewById(R.id.button11);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePDF();
            }
        });
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(convertImageToPdf.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ){

                    ActivityCompat.requestPermissions(convertImageToPdf.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            100);
                    return;
                }
                openFileChooser();
            }
        });


    }

    private void savePDF() {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1212);

    }

    private void openFileChooser()
    {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK)
         {
             ImageView imageView=findViewById(R.id.imageView);
             ClipData clipData=data.getClipData();
             if(clipData!=null)
             {
                 for(int i=0;i<clipData.getItemCount();i++)
                 {
                     Uri imageUri=clipData.getItemAt(i).getUri();
                     try{
                         InputStream is= getContentResolver().openInputStream(imageUri);
                         Bitmap bitmap = BitmapFactory.decodeStream(is);
                         bitmaps.add(bitmap);
                     }catch (FileNotFoundException e){
                         e.printStackTrace();
                     }
                 }
                 try {
                     File file = getOutputFile();

                     FileOutputStream fileOutputStream = new FileOutputStream(file);
                     PdfDocument pdfDocument = new PdfDocument();

                     for (int i = 0; i < clipData.getItemCount(); i++){
                         Uri imageUri=clipData.getItemAt(i).getUri();
                         InputStream is= getContentResolver().openInputStream(imageUri);
                         Bitmap bitmap = BitmapFactory.decodeStream(is);
                         PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), (i + 1)).create();
                         PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                         Canvas canvas = page.getCanvas();
                         Paint paint = new Paint();
                         paint.setColor(Color.BLUE);
                         canvas.drawPaint(paint);
                         canvas.drawBitmap(bitmap, 0f, 0f, null);
                         pdfDocument.finishPage(page);
                         bitmap.recycle();
                     }
                     pdfDocument.writeTo(fileOutputStream);
                     pdfDocument.close();
                     String path=getOutputFile().getPath();
                     Toast.makeText(convertImageToPdf.this, path +"/////", Toast.LENGTH_SHORT).show();


                     Uri Filepath=Uri.parse(path);
                     Toast.makeText(convertImageToPdf.this,Filepath.toString(),Toast.LENGTH_SHORT).show();
                     //UploadFile(Filepath);

                 } catch (IOException e) {
                     e.printStackTrace();
                 }

             }
             else
                 {
                    Uri imageUri=data.getData();

                     try{
                         InputStream is= getContentResolver().openInputStream(imageUri);
                         Bitmap bitmap = BitmapFactory.decodeStream(is);
                         bitmaps.add(bitmap);
                     }catch (FileNotFoundException e){
                         e.printStackTrace();
                     }
                 }
             new Thread(new Runnable() {
                 @Override
                 public void run() {
                     for(Bitmap b : bitmaps)
                     {
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                               imageView.setImageBitmap(b);
                             }
                         });
                         try
                         {
                             Thread.sleep(3000);
                         }catch (InterruptedException e)
                         {
                             e.printStackTrace();
                         }
                     }
                 }
             }).start();

         }
         if(requestCode == 1212 && resultCode==RESULT_OK && data.getData()!=null )
        {
            Toast.makeText(convertImageToPdf.this, data.getData().toString(), Toast.LENGTH_SHORT).show();
            UploadFile(data.getData());
        }

    }

    private void UploadFile(Uri filepath) {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("File is loading...");
        progressDialog.show();

        if(filepath!= null)
        {
            StorageReference fileReference=mStorageRef.child(UserNameSave.UserName+"File").child(System.currentTimeMillis()+".pdf");
            fileReference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(convertImageToPdf.this,"upload successful",Toast.LENGTH_LONG).show();

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadurl=uri;
                            String uploadId=UserNameSave.pdfName;
                            Upload_File upload =new Upload_File(UserNameSave.pdfName,downloadurl.toString());
                            mDatabaseRef.child(UserNameSave.UserName).child("File").child(uploadId).setValue(upload);
                            Toast.makeText(convertImageToPdf.this,"file uploaded",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss()
                            ;
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(convertImageToPdf.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    // mProgressBar.setProgress((int)progress);*/
                    progressDialog.setMessage("file Uploaded.."+(int)progress+"%");
                }
            });
        }
        else
        {
            Toast.makeText(this,"no file selected",Toast.LENGTH_SHORT).show();
        }
    }

    private File getOutputFile() {
        File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));

        boolean isFolderCreated = true;

        if (!root.exists()) {
            isFolderCreated = root.mkdir();
        }

        if (isFolderCreated) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "PDF_" + timeStamp;

            return new File(root, imageFileName + ".pdf");
        } else {
            Toast.makeText(this, "Folder is not created", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
