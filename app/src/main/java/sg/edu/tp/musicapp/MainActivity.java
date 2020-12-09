package sg.edu.tp.musicapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Putra on 30/11/2020
 */

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fAuth = FirebaseAuth.getInstance();

        // Check if the user is already logged in, or already registered
        // If yes, skip this Activity
        if (fAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(), SongListActivity.class));
        }
    }

    public void goToLoginPage(View view)
    {
        Intent intent  = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToSignUpPage(View view)
    {
        Intent intent  = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    /* --------------------------------------------------------
     * Forgot Password Method
     * -------------------------------------------------------*/

    public void forgotPassword(View view)
    {
        // We will create a dialog to request for email
        // The content of the dialog will be as follows
        EditText resetMail = new EditText(view.getContext());
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext(), R.style.AlertDialogStyle);
        passwordResetDialog.setTitle("Reset Password? ");
        passwordResetDialog.setMessage("Enter your email to receive reset password link");
        passwordResetDialog.setView(resetMail);

        // Submits the form and display status message
        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = resetMail.getText().toString();

                // Validate locally before sending this field to Firebase Auth
                if (TextUtils.isEmpty(mail))
                {
                    Toast.makeText(MainActivity.this, "Email field cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Display reset password dialog
        passwordResetDialog.create().show();
    }
}

