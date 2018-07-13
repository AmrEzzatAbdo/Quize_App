package com.quiz.amrezzat.quizt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quiz.amrezzat.quizt.Model.Quition;
import com.quiz.amrezzat.quizt.Model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class dashboard extends AppCompatActivity {
    static DatabaseReference mdatabase, mUserDatabase;
    RecyclerView mPositionList;
    FirebaseRecyclerAdapter<Quition, QuitionviewHolder> firebaseRecyclerAdapter;
    RadioGroup choices;
    static String currentUser;
    TextView TcurrentUser;
    static String CUserRate;
    static int newRate = 0;
    static User login;
    static ArrayList<String> GtrueChoice;
    static Date QuestionDate, currentDate;
    static Boolean Qtrue_falseBtn = true, Strue_falseBtn = true;
    static String CQuition;
    static Button submit;
    static View viewPos;
    static ArrayList<String> lastQuistion;
    static Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mPositionList = (RecyclerView) findViewById(R.id.DBposts);
        mPositionList.setHasFixedSize(true);
        mPositionList.setLayoutManager(new LinearLayoutManager(this));

        //test time
        currentDate = Calendar.getInstance().getTime();
        /*
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        Toast.makeText(this, currentTime.toString(), Toast.LENGTH_SHORT).show();
        */
        //********
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Questions");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mdatabase.keepSynced(true);
        //for check quition user answer
        //Previous versions of Firebase
        Firebase.setAndroidContext(this);

        //get Current user
        currentUser = readeFile();
        TcurrentUser = (TextView) findViewById(R.id.CurrentUser);
        if (!readeFile().isEmpty()) {
            TcurrentUser.setText(readeFile());
            ref = new Firebase("https://quizt-c68f7.firebaseio.com/Users/" + readeFile() + "/lastQuistionSubmited");
        }

        GtrueChoice = new ArrayList<String>();
        lastQuistion = new ArrayList<>();
        //SNACKBAR
        viewPos = findViewById(R.id.myCoordinatorLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Quition, QuitionviewHolder>(Quition.class, R.layout.dashboard_item, QuitionviewHolder.class, mdatabase) {
            @Override
            protected void populateViewHolder(QuitionviewHolder viewHolder, Quition model, int position) {
                viewHolder.setQuition(model.getQuition());
                viewHolder.setChoice1(model.getChoice1());
                viewHolder.setChoice2(model.getChoice2());
                viewHolder.setChoice3(model.getChoice3());
                viewHolder.setChoice4(model.getChoice4());
                viewHolder.setTrueChoice(model.getTrueChoice());
                viewHolder.setDate(model.getDate());
                viewHolder.setBooleanQuestion(model.isBooleanQuestion());
                // Toast.makeText(MainActivity.this, "Plz Sign In to push new Location Review..", Toast.LENGTH_SHORT).show();
            }
        };
        mPositionList.setAdapter(firebaseRecyclerAdapter);
    }

    public void uRate(View view) {
        startActivity(new Intent(dashboard.this, Rates.class));
    }

    public static class QuitionviewHolder extends RecyclerView.ViewHolder {
        View mView;

        public QuitionviewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //selection
            final RadioGroup choices = (RadioGroup) mView.findViewById(R.id.choices);
            submit = (Button) mView.findViewById(R.id.submit);
            submit.setEnabled(true);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get selected radio button from radioGroup
                    int selectedId = choices.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    final RadioButton radioButton = (RadioButton) mView.findViewById(selectedId);
                    //check current user rate
                    mUserDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(currentUser).exists()) {
                                if (!currentUser.isEmpty()) {
                                    login = dataSnapshot.child(currentUser).getValue(User.class);
                                    CUserRate = login.getRate();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //check current answer and add 1 if true
                    mdatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (GtrueChoice.get(0).toString().equals(radioButton.getText().toString())) {
                                try {
                                    newRate = Integer.valueOf(CUserRate) + Integer.valueOf("1");
                                    submit.setEnabled(false);
                                } catch (Exception e) {
                                    Snackbar.make(viewPos, "please logout and sign in again", Snackbar.LENGTH_LONG)
                                            .show();
                                }
                                //change boolean in quietion DB for selected quition and set it in view holder
                            } else {
                                try {
                                    // newRate = Integer.valueOf(CUserRate);
                                    submit.setEnabled(false);
                                } catch (Exception e) {
                                    Snackbar.make(viewPos, "please logout and sign in again", Snackbar.LENGTH_LONG)
                                            .show();
                                }

                                //change boolean in quietion DB for selected quition and set it in view holder
                            }

                            //insert quition id in true answer and check true anser if has quition id or not

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //Update User
                    mUserDatabase.child("users").child(currentUser)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                User uData;

                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    Map<String, Object> postValues = new HashMap<String, Object>();
                                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                        postValues.put(snapshot.getKey(), snapshot.getValue());
                                                                        uData = snapshot.getValue(User.class);
                                                                    }
                                                                    postValues.put("rate", String.valueOf(newRate));
                                                                    mUserDatabase.child(currentUser).updateChildren(postValues);
                                                                    //update this with quition id         //insert quition choice two
                                                                    ref.addValueEventListener(new com.firebase.client.ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                                                                            for (int i = 0; i < lastQuistion.size(); i++) {
                                                                                if (!dataSnapshot.child(lastQuistion.get(i)).exists()) {
                                                                                    mUserDatabase.child(currentUser).child("lastQuistionSubmited").child(lastQuistion.get(i)).setValue(lastQuistion.get(i)); //lquistion
                                                                                }
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(FirebaseError firebaseError) {

                                                                        }
                                                                    });

                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                    Log.e("DB ERROR", "onCancelled: ", databaseError.toException());
                                                                }
                                                            }
                            );
                }

            });

        }


        public void setQuition(final String quition) {
            CQuition = quition;
            TextView Qtext = (TextView) mView.findViewById(R.id.UDquition);
            Qtext.setText(quition);
            lastQuistion.add(quition);
            //check for contain user quition
            ref.addValueEventListener(new com.firebase.client.ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(CQuition).exists()) {
                        if (Qtrue_falseBtn == true) {
                            submit.setEnabled(false);
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }

        public void setChoice1(String choice1) {
            RadioButton Qchoice1 = (RadioButton) mView.findViewById(R.id.choice1);
            Qchoice1.setText(choice1);
        }

        public void setChoice2(String choice2) {
            RadioButton Qchoice2 = (RadioButton) mView.findViewById(R.id.choice2);
            Qchoice2.setText(choice2);
        }

        public void setChoice3(String choice3) {
            RadioButton Qchoice3 = (RadioButton) mView.findViewById(R.id.choice3);
            Qchoice3.setText(choice3);
        }

        public void setChoice4(String choice4) {
            RadioButton Qchoice4 = (RadioButton) mView.findViewById(R.id.choice4);
            Qchoice4.setText(choice4);
        }

        public void setTrueChoice(String trueChoice) {
            GtrueChoice.add(trueChoice);
        }

        public void setDate(Date date) {
            long diff = currentDate.getTime() - date.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            if (hours >= 24) { //in testing
                //Update it into DB
                mdatabase.child("Questions").child(CQuition)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                Map<String, Object> postValues = new HashMap<String, Object>();
                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    postValues.put(snapshot.getKey(), snapshot.getValue());
                                                                }
                                                                postValues.put("booleanQuestion", false);
                                                                mdatabase.child(CQuition).updateChildren(postValues);
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                Log.e("DB ERROR", "onCancelled: ", databaseError.toException());
                                                            }
                                                        }
                        );
            }
        }

        public void setBooleanQuestion(boolean booleanQuestion) {
            Qtrue_falseBtn = booleanQuestion;
        }

    }

    public String readeFile() {
        String nameT;
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String val = "";
        nameT = sharedPreferences.getString("name", val);
        return nameT;
    }

    //save in file to check on it
    public void saveInFile(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.commit();
    }

    public void logoutU(View view) {
        saveInFile("");
        startActivity(new Intent(dashboard.this, LoginActivity.class));
        finish();
    }
}
