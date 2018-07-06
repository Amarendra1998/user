package com.example.admin.saliteostask2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LginActivity extends AppCompatActivity {
    ProgressDialog mprogressDialog;
    private TextInputEditText email,pass;
    private Button logbtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference muserdatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lgin);
        email = (TextInputEditText)findViewById(R.id.edittextd);
        pass = (TextInputEditText)findViewById(R.id.edittextc);
        muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        logbtn = (Button)findViewById(R.id.button2);
        firebaseAuth = FirebaseAuth.getInstance();
        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(LginActivity.this,"please enter your email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass.getText().toString())) {
                    Toast.makeText(LginActivity.this,"please enter your password",Toast.LENGTH_SHORT).show();
                    return;
                }
                loginuser(email,pass);
            }
        });
    }

    private void loginuser(TextInputEditText email, TextInputEditText pass) {
        mprogressDialog = ProgressDialog.show(LginActivity.this, "Please wait...", "processing", true);
        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String current_user_id = firebaseAuth.getCurrentUser().getUid();
                    String devicetoken = FirebaseInstanceId.getInstance().getToken();
                    muserdatabase.child(current_user_id).child("device_token").setValue(devicetoken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent m=new Intent(LginActivity.this,Welcome.class);
                            Toast.makeText(LginActivity.this,"Successfully login",Toast.LENGTH_SHORT).show();
                            m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(m);
                            sendtowelcome();
                        }
                    });

                }else {
                    Toast.makeText(LginActivity.this,"Couldn't Login, Somthing went wrong",Toast.LENGTH_SHORT).show();
                    mprogressDialog.dismiss();
                }
            }
        });
    }

    private void sendtowelcome() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()){
            Intent m=new Intent(LginActivity.this,Welcome.class);
            startActivity(m);
        }else {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(LginActivity.this, "Verification email sent to :" + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(LginActivity.this, "Fail to send verification email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}
