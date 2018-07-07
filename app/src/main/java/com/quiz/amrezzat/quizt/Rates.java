package com.quiz.amrezzat.quizt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quiz.amrezzat.quizt.Model.User;

public class Rates extends AppCompatActivity {
    RecyclerView mPositionList;
    FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter;
    DatabaseReference useres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);
        //recycle
        mPositionList = (RecyclerView) findViewById(R.id.recRate);
        mPositionList.setHasFixedSize(true);
        mPositionList.setLayoutManager(new LinearLayoutManager(this));
        useres = FirebaseDatabase.getInstance().getReference().child("Users");
        useres.keepSynced(true);
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

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUserName(String userName) {
            TextView User = (TextView) mView.findViewById(R.id.User);
            User.setText(User.getText().toString() + " " + userName);
        }

        public void setRate(String rate) {
            TextView Rate = (TextView) mView.findViewById(R.id.Rate);
            Rate.setText(Rate.getText().toString() + " " + rate);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
