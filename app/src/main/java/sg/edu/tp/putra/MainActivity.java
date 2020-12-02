package sg.edu.tp.putra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Putra on 30/11/2020
 * TODO: Google login integration with Firebase
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToLoginPage(View view)
    {
        Intent intent  = new Intent(this, SongListing.class);
        startActivity(intent);
    }
}
