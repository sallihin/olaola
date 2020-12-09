package sg.edu.tp.musicapp;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Putra on 06/12/2020
 */

public class LoginActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mCreateBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.noAccount);

        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v)
            {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                /* --------------------------------------------------------
                 * Validate the form locally before sending data to Firebase
                 * -------------------------------------------------------*/

                if (TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password))
                {
                    mPassword.setError("Password is required. ");
                    return;
                }

                if (password.length() < 6)
                {
                    mPassword.setError("Password must be more than 6 characters");
                    return;
                }

                // Display progress bar in case login takes a long time
                progressBar.setVisibility(View.VISIBLE);

                // Authenticate User
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), SongListActivity.class));
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    public void goToSignUpPage(View view)
    {
        Intent intent  = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }
}