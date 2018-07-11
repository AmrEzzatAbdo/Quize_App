package com.quiz.amrezzat.quizt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.quiz.amrezzat.quizt.Model.Quition;
import com.quiz.amrezzat.quizt.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class Admin extends AppCompatActivity {
    RadioGroup adminChoice;
    RelativeLayout addPost, addUser;
    Button subUser, subPost;
    EditText userName, Email, password;
    EditText quition, choice1, choice2, trueChoice;
    TextView currentUser;
    //firebase
    FirebaseDatabase databaseUser, databaseQuition;
    DatabaseReference useres, Quitions;
    RecyclerView mPositionList;
    FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter;
    //date
    Date currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        adminChoice = (RadioGroup) findViewById(R.id.admChoice);
        addPost = (RelativeLayout) findViewById(R.id.admPost);
        addUser = (RelativeLayout) findViewById(R.id.admUser);
        subUser = (Button) findViewById(R.id.subUser);
        subPost = (Button) findViewById(R.id.subPost);
        userName = (EditText) findViewById(R.id.userName);
        Email = (EditText) findViewById(R.id.Email);
        password = (EditText) findViewById(R.id.password);
        quition = (EditText) findViewById(R.id.quition);
        choice1 = (EditText) findViewById(R.id.choice1);
        choice2 = (EditText) findViewById(R.id.choice2);
        trueChoice = (EditText) findViewById(R.id.trueChoice);
        currentUser= (TextView) findViewById(R.id.CurrentUser);
        //init Date
        currentDate = Calendar.getInstance().getTime();
        //recycle
        mPositionList = (RecyclerView) findViewById(R.id.adminRecycel);
        mPositionList.setHasFixedSize(true);
        mPositionList.setLayoutManager(new LinearLayoutManager(this));
        if(!readeFile().isEmpty()){
            currentUser.setText(readeFile());
        }
        //firebase
        Quitions= FirebaseDatabase.getInstance().getReference().child("Questions");
        useres = FirebaseDatabase.getInstance().getReference().child("Users");
        useres.keepSynced(true);
        //Gon by default
        addUser.setVisibility(View.GONE);
        addPost.setVisibility(View.GONE);
        adminChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.post:
                        addUser.setVisibility(View.GONE);
                        addPost.setVisibility(View.VISIBLE);
                        break;
                    case R.id.addUser:
                        addUser.setVisibility(View.VISIBLE);
                        addPost.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    public void subUser(View view) {
        final User user = new User(userName.getText().toString(), password.getText().toString(), Email.getText().toString(), "0","test");
        useres.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.getUserName()).exists()) {
                    Toast.makeText(Admin.this, "User already exist", Toast.LENGTH_SHORT).show();
                } else {
                    useres.child(user.getUserName()).setValue(user);
                    Toast.makeText(Admin.this, "User registration success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class, R.layout.rate_recycle_item, UserViewHolder.class, useres) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {
                viewHolder.setUserName(model.getUserName());
                viewHolder.setRate(model.getRate());
                // Toast.makeText(MainActivity.this, "Plz Sign In to push new Location Review..", Toast.LENGTH_SHORT).show();
            }
        };
        mPositionList.setAdapter(firebaseRecyclerAdapter);
    }

    public void subPost(View view) {
        final Quition quitions = new Quition(quition.getText().toString(), choice1.getText().toString(), choice2.getText().toString(), trueChoice.getText().toString(),currentDate,true);
        Quitions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(quitions.getQuition()).exists()) {
                    Toast.makeText(Admin.this, "Quition already exist", Toast.LENGTH_SHORT).show();
                } else {
                    Quitions.child(quitions.getQuition()).setValue(quitions);
                    Toast.makeText(Admin.this, "quition added successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //save in file to check on it
    public void saveInFile(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.commit();
    }
    public String readeFile() {
        String nameT;
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String val = "";
        nameT = sharedPreferences.getString("name", val);
        return nameT;
    }
    public void logout(View view) {
        saveInFile("");
        startActivity(new Intent(Admin.this,LoginActivity.class));
        finish();
    }




    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
                }
        public void setUserName(String userName) {
            TextView User= (TextView) mView.findViewById(R.id.User);
            User.setText(userName);
        }
        public void setRate(String rate) {
            TextView Rate= (TextView) mView.findViewById(R.id.Rate);
            Rate.setText(rate);
        }
        }

}