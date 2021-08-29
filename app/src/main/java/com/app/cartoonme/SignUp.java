package com.app.cartoonme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    EditText mEmail_reg;
    EditText mPassword_reg;
    EditText mName_reg;
    Button mCreate;

    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmail_reg=findViewById(R.id.EmailSignUp);
        mPassword_reg=findViewById(R.id.PasswordSignUp);
        mName_reg=findViewById(R.id.UsernameSignUp);

        mCreate=findViewById(R.id.Signup_btn);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateUser();
            }
        });

        TextView SignInButton =findViewById(R.id.openSigninIntent);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this, SignIn.class);
                startActivity(i);
            }
        });
    }

    private void CreateUser() {
        String email=mEmail_reg.getText().toString();
        String password=mPassword_reg.getText().toString();
        String name=mName_reg.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"please write your email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"please write your password",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"please write your name",Toast.LENGTH_SHORT).show();
        }
        if(!TextUtils.isEmpty(name)) {
            int counter_alpha = 0;
            int counter_number = 0;
            for (int i = 0; i < name.length(); i++) {
               /* if (name.charAt(i) == '#' || name.charAt(i) == '@' || name.charAt(i) == '$' || name.charAt(i) == '&') {
                    counter_special++;
                }*/
                for (char alpha = 'A'; alpha <= 'Z'; alpha++) {
                    if (name.charAt(i) == alpha) {
                        counter_alpha++;
                    }
                }
                for (int j = 0; j < 10; j++) {
                    //  String g= String.valueOf(name.charAt(i));
                    if (Character.getNumericValue(name.charAt(i)) == j) {
                        counter_number++;
                    }
                }
            }
            if (counter_alpha == 0 || counter_number == 0) {
                Toast.makeText(this, "must be capital letter and number", Toast.LENGTH_SHORT).show();
            } else {
                ValdataFromFirebase(email, name, password);
            }
        }}
        private void ValdataFromFirebase(String email,String name, String password)
        {
        mDatabaseRef= FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(!(snapshot.child("Users").child(name).exists()))
                {
                    HashMap<String,Object> userdataMap=new HashMap<>();
                    userdataMap.put("Email",email);
                    userdataMap.put("Name",name);
                    userdataMap.put("Password",password);
                    mDatabaseRef.child("Users").child(name).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(SignUp.this,"Your Account Has Been Created",Toast.LENGTH_SHORT).show();
                                        Intent intent =new Intent(SignUp.this,SignIn.class);
                                        startActivity(intent);

                                    }
                                    else
                                    {
                                        Toast.makeText(SignUp.this,"Network Error: Please Try Again After Some Time",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(SignUp.this,"This email Already Exists",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}