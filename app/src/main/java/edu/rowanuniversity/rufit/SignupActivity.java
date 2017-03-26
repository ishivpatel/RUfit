package edu.rowanuniversity.rufit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by shiv on 3/3/2017.
 */

public class SignupActivity extends AppCompatActivity {
    private EditText inputName, inputEmail, inputPassword;
    private TextView signInText;
    private FirebaseAuth auth;
    private Button btnCreateAccount;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        inputName = (EditText) findViewById(R.id.user_signup_name);
        inputEmail = (EditText) findViewById(R.id.user_signup_email);
        inputPassword = (EditText) findViewById(R.id.user_signup_password);
        btnCreateAccount = (Button) findViewById(R.id.sign_up_button);
        signInText = (TextView) findViewById(R.id.sign_in_text);

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = inputName.getText().toString();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                if (task.isSuccessful()) {
                                   //TODO - prompt user to input initial settings and personal info
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(SignupActivity.this, "E-mail Already in Use", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}

