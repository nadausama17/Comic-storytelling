package com.app.cartoonme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cartoonme.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    private EditText mname;
    private EditText mpassword;

    private DatabaseReference mDatabaseRef;
    private String ParentDBName="Users";
    private CheckBox rememberme;
    SharedPreferences sharedPreferences;
    private static final String File_Name="myFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        mname=findViewById(R.id.UsernameSignin);
        mpassword=findViewById(R.id.PasswordSignin);
        rememberme=findViewById(R.id.checkBox);
        sharedPreferences=getSharedPreferences(File_Name,MODE_PRIVATE);
        String name=sharedPreferences.getString("username","");
        String password=sharedPreferences.getString("password","");
        mname.setText(name);
        mpassword.setText(password);



        TextView SignUpButton = findViewById(R.id.openSignUpIntent);
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this, SignUp.class);
                startActivity(i);
            }
        });
        Button SignInpButton = findViewById(R.id.Login_btn);
        SignInpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

    }

    private void LoginUser() {
        String name=mname.getText().toString();
        String password=mpassword.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"please write your email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"please write your password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            AllowAccessToAccount(name,password);
        }
    }

    private void AllowAccessToAccount(String name, String password) {

        mDatabaseRef= FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.child(ParentDBName).child(name).exists())
                {
                    Users usersData=snapshot.child(ParentDBName).child(name).getValue(Users.class);
                    if(usersData.getName().equals(name))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            if(rememberme.isChecked())
                            {
                                StoredDataUsingSharedPref(name,password);
                            }
                            else{sharedPreferences.edit().clear().apply();}

                            Toast.makeText(SignIn.this,"Login In Successfully",Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(SignIn.this,Home.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("name", name);
                            UserNameSave.UserName=name;
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                }
                else
                {
                    Toast.makeText(SignIn.this,"the name dows not exist",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void StoredDataUsingSharedPref(String name, String password)
    {
        SharedPreferences.Editor editor=getSharedPreferences(File_Name,MODE_PRIVATE).edit();
        editor.putString("username",name);
        editor.putString("password",password);
        editor.apply();
    }

}