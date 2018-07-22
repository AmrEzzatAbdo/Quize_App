package com.quiz.amrezzat.quizt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
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
    static Boolean Qtrue_falseBtn = true;
    static String CQuition, CUserSubCheck;
    static Button submit;
    static Firebase ref;
    static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mPositionList = (RecyclerView) findViewById(R.id.DBposts);
        mPositionList.setHasFixedSize(true);
        mPositionList.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);
        //test time
        currentDate = Calendar.getInstance().getTime();
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
            ref = new Firebase("https://quizt-c68f7.firebaseio.com/Questions");
        }

        GtrueChoice = new ArrayList<String>();
/*
        //bottom sheet
        View bottomSheet=findViewById(R.id.d_bottom_sheet);
        final BottomSheetBehavior behavior=BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:

                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.show();
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
                //viewHolder.setcuser(model.getcuser());
                // Toast.makeText(MainActivity.this, "Plz Sign In to push new Location Review..", Toast.LENGTH_SHORT).show();
            }
        };
        //CHECK FALSE QUISTION
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
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Update quition user submition state
                    //Update boolean VALUE FOR QUITION
                    mdatabase.child("Questions").child(CQuition)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                Quition QData;

                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    Map<String, Object> postValues = new HashMap<String, Object>();
                                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                        postValues.put(snapshot.getKey(), snapshot.getValue());
                                                                        QData = snapshot.getValue(Quition.class);
                                                                    }
                                                                    postValues.put(currentUser, currentUser);
                                                                    mdatabase.child(CQuition).child("cuser").updateChildren(postValues);
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                    Log.e("DB ERROR", "onCancelled: ", databaseError.toException());
                                                                }
                                                            }
                            );
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
                                    //check current answer
                                    if (GtrueChoice.get(0).toString().equals(radioButton.getText().toString())) {
                                        try {
                                            newRate = Integer.valueOf(CUserRate) + Integer.valueOf("1");
                                            submit.setEnabled(false);
                                        } catch (Exception e) {

                                        }
                                        //change boolean in quietion DB for selected quition and set it in view holder
                                    } else {
                                        try {
                                            // newRate = Integer.valueOf(CUserRate);
                                            submit.setEnabled(false);
                                        } catch (Exception e) {

                                        }

                                        //change boolean in quietion DB for selected quition and set it in view holder
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //check current answer and add 1 if true
                    /*
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

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/
                    //Update User rate
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
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                    Log.e("DB ERROR", "onCancelled: ", databaseError.toException());
                                                                }
                                                            }
                            );

                }

            });
            progressDialog.dismiss();

        }


        public void setQuition(final String quition) {
            ref = new Firebase("https://quizt-c68f7.firebaseio.com/Questions/"+quition+"/cuser");
            chickingSubQ();
            CQuition = quition;
            TextView Qtext = (TextView) mView.findViewById(R.id.UDquition);
            Qtext.setText(quition);

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
                        //Update boolean VALUE FOR QUITION
                        mdatabase.child("Questions").child(CQuition)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    Quition uData;

                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        Map<String, Object> postValues = new HashMap<String, Object>();
                                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                            postValues.put(snapshot.getKey(), snapshot.getValue());
                                                                            uData = snapshot.getValue(Quition.class);
                                                                        }
                                                                        postValues.put("booleanQuestion", false);
                                                                        mUserDatabase.child(CQuition).updateChildren(postValues);
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
/*
        public void setcuser(String cuser) {
            CUserSubCheck = cuser;
            if (Qtrue_falseBtn == false || cuser.equals(currentUser)) {
                submit.setEnabled(false);
            } else if (Qtrue_falseBtn = false && (cuser.equals(currentUser) || !cuser.equals(currentUser))) {
                submit.setEnabled(false);
            } else if (Qtrue_falseBtn = true && cuser.equals(currentUser)) {
                submit.setEnabled(false);
            } else
                submit.setEnabled(true);
        }
        */
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

    public static void chickingSubQ(){
        ref.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUser).exists()){
                    submit.setEnabled(false);
                }
                else
                    submit.setEnabled(true);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
