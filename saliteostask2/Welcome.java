package com.example.admin.saliteostask2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Welcome extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Button refresh;
    private RecyclerView mUserlist;
    private Toolbar mtoolbar;
    private DatabaseReference mdatabasereference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        firebaseAuth = FirebaseAuth.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mtoolbar = (Toolbar)findViewById(R.id.myappbar);
        mdatabasereference = FirebaseDatabase.getInstance().getReference().child("Users");
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,linearLayoutManager.getOrientation());
        mUserlist = (RecyclerView)findViewById(R.id.users_list);
        mUserlist.addItemDecoration(dividerItemDecoration);
        mUserlist.setHasFixedSize(true);
        mUserlist.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("User Information");
        info();
    }

    private void info() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()){
            mUserlist.setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(Welcome.this,"email is not verified,please verify your email to see your information and login again",Toast.LENGTH_SHORT).show();
            mUserlist.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, UsersViewHolder>(
                users.class,
                R.layout.information,
                UsersViewHolder.class,
                mdatabasereference
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, users model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setPassword(model.getPassword());
                viewHolder.setEmail(model.getEmail());
                final String user_id  = getRef(position).getKey();

            }
        };
        mUserlist.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==R.id.log){
            Intent m = new Intent(Welcome.this,Main2Activity.class);
            startActivity(m);
        }

        return super.onOptionsItemSelected(item);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public UsersViewHolder(View itemView){
            super(itemView);
            mview=itemView;
        }

        public void setName(String name) {
            TextView userview = (TextView)mview.findViewById(R.id.textView3);
            userview.setText("Your Name:"+name);
        }

        public void setStatus(String status) {
            TextView userstatus = (TextView)mview.findViewById(R.id.textView7);
            userstatus.setText("Your Status:"+status);
        }


        public void setPassword(String password) {
            TextView userpass = (TextView)mview.findViewById(R.id.textView6);
            userpass.setText("Your Password:"+password);
        }
        public void setEmail(String email) {
            TextView useremail = (TextView)mview.findViewById(R.id.textView4);
            useremail.setText("Your Email:"+email);
        }
    }


}
