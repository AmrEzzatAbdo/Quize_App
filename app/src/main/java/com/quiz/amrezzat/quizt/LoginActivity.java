package com.quiz.amrezzat.quizt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quiz.amrezzat.quizt.Model.User;
import com.quiz.amrezzat.quizt.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference useres;
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //chick current user
        if (!readeFile().isEmpty()) {
            if (readeFile().equals("adminXQ")) {
                startActivity(new Intent(LoginActivity.this, Admin.class));
                finish();
            } else {
                startActivity(new Intent(LoginActivity.this, dashboard.class));
                finish();
            }
        }
        //firebase
        database = FirebaseDatabase.getInstance();
        useres = database.getReference("Users");

        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailView = (EditText) findViewById(R.id.userNameLogin);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read from the database
                useres.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        if (dataSnapshot.child(mEmailView.getText().toString()).exists()) {
                            if (!mEmailView.getText().toString().isEmpty()) {
                                User login = dataSnapshot.child(mEmailView.getText().toString()).getValue(User.class);
                                if (login.getPassword().equals(mPasswordView.getText().toString())) {
                                    //login Ok
                                    saveInFile(mEmailView.getText().toString());
                                    if (mEmailView.getText().toString().equals("adminXQ")) {
                                        startActivity(new Intent(LoginActivity.this, Admin.class));
                                        finish();
                                    } else {
                                        startActivity(new Intent(LoginActivity.this, dashboard.class));
                                        finish();
                                    }
                                    Toast.makeText(LoginActivity.this, "Login succeeded", Toast.LENGTH_SHORT).show();
                                } else {
                                    //wrong password
                                    Toast.makeText(LoginActivity.this, "wrong password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //user not exist
                                Toast.makeText(LoginActivity.this, "User not exist", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("Login", "Failed to read value.", error.toException());
                    }
                });
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

}

