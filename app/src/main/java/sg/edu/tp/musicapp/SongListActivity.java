package sg.edu.tp.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import javax.annotation.Nullable;

/**
 * Created by Putra on 30/11/2020
 */

public class SongListActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    private RecyclerView recyclerView;
    SongAdapter adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database
    ListenerRegistration fStoreListener;
    private Button logOutBtn;
    private TextView greetingText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_listing);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialise the variables for our simple greeting
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        greetingText = findViewById(R.id.greeting_text);

        logOutBtn = findViewById(R.id.logOutBtn);

        /* --------------------------------------------------------
         * Implement Firebase RecyclerViewer Adapter
         * -------------------------------------------------------*/

        // Find RecyclerView ID
        recyclerView = findViewById(R.id.main_song_list);

        // To display the Recycler view in 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // This is the location of the data on Firebase
        mbase = FirebaseDatabase.getInstance().getReference();

        // This is provided by the FirebaseUI to query the database to fetch data
        FirebaseRecyclerOptions<Song> options
            = new FirebaseRecyclerOptions.Builder<Song>()
            .setQuery(mbase, Song.class)
            .build();

        // Create a new Adapter object for this activity
        adapter = new SongAdapter(options, SongListActivity.this);

        // Connecting Adapter class with the Recycler view
        recyclerView.setAdapter(adapter);

        /* --------------------------------------------------------
         * Greet the user by name retrieving name from Firestore
         * -------------------------------------------------------*/

        DocumentReference documentReference = fStore.collection("users").document(userId);
        fStoreListener = documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // Check if documentSnapshot is not null, otherwise app may crash when trying to log out
                if (documentSnapshot != null)
                {
                    greetingText.setText("Hi " + documentSnapshot.getString("name") + "!");
                }
            }
        });

    }

    // Function to tell the app to start getting data from database on starting of the activity
    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    // Function to tell the app to stop getting data from database on stopping of the activity
    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

    /* --------------------------------------------------------
     * Popup to Logout
     * -------------------------------------------------------*/

    public void showLogOut(View view)
    {
        if (logOutBtn.getVisibility() == view.GONE)
        {
            logOutBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            logOutBtn.setVisibility(View.GONE);
        }
    }

    // Log out of the app
    public void logOut(View view)
    {
        fStoreListener.remove();
        fAuth.signOut();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}