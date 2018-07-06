package com.example.admin.saliteostask2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.widget.Toast.LENGTH_SHORT;

public class Main2Activity extends AppCompatActivity {
    ProgressDialog mprogressDialog;
    private TextInputEditText mname,email,confirmpass,pass,mstatus;
    private Button register;
    private DatabaseReference muserdatabase;
    private TextView textView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mcurrentuser;

    public Main2Activity() {
        mprogressDialog = null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mname = (TextInputEditText)findViewById(R.id.edittext);
        mstatus = (TextInputEditText)findViewById(R.id.edittextq);
        email = (TextInputEditText)findViewById(R.id.edittextd);
        pass = (TextInputEditText)findViewById(R.id.edittextc);
        confirmpass = (TextInputEditText)findViewById(R.id.edittexte);
        register = (Button)findViewById(R.id.button6);
        textView = (TextView)findViewById(R.id.textView2);
        firebaseAuth = FirebaseAuth.getInstance();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myne = new Intent(Main2Activity.this,LginActivity.class);
                startActivity(myne);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mname.getText().toString())) {
                    Toast.makeText(Main2Activity.this,"please enter your name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(Main2Activity.this,"please enter your email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass.getText().toString())) {
                    Toast.makeText(Main2Activity.this,"please enter your password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmpass.getText().toString())) {
                    Toast.makeText(Main2Activity.this,"please enter your confirmed password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mstatus.getText().toString())) {
                    Toast.makeText(Main2Activity.this,"please enter your status",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirmpass.getText().toString().matches(pass.getText().toString()))
                {
                    Toast.makeText(Main2Activity.this,"your password is confirmed",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Main2Activity.this,"password does not match",Toast.LENGTH_SHORT).show();
                }
                if (pass.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                register_user(mname,email,pass,confirmpass,mstatus);
            }
        });
    }

    private void register_user(final TextInputEditText mname, final TextInputEditText email, final TextInputEditText pass, TextInputEditText confirmpass, final TextInputEditText mstatus) {
        mprogressDialog = ProgressDialog.show(Main2Activity.this, "Please wait...", "processing", true);
        (firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mprogressDialog.dismiss();
                if (task.isSuccessful()) {
                    mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
                    assert mcurrentuser != null;
                    String current_uid = mcurrentuser.getUid();
                    muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
                    String device_token = FirebaseInstanceId.getInstance().getToken();
                    HashMap<String,String> usermap = new HashMap<>();
                    usermap.put("name", mname.getText().toString());
                    usermap.put("email", email.getText().toString());
                    usermap.put("status",mstatus.getText().toString());
                    usermap.put("password",pass.getText().toString());
                    usermap.put("device_token",device_token);
                    usermap.put("user_id",current_uid);
                    muserdatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Main2Activity.this,"you signup successfully",Toast.LENGTH_SHORT).show();
                            Intent m=new Intent(Main2Activity.this,LginActivity.class);
                            m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(m);
                        }
                    });

                } else {
                    Log.e("Error", task.getException().toString());
                    Toast.makeText(Main2Activity.this, task.getException().getMessage(), LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
        builder.setMessage("Do you really wanna exit");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
