package sg.edu.tp.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Putra on 30/11/2020
 */

public class SongListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    SongAdapter adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_listing);

        // Associate this variable with the RecyclerView that I designed
        recyclerView = findViewById(R.id.main_song_list);

        // To display the Recycler view in 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Create a instance of the database and get its reference
        mbase = FirebaseDatabase.getInstance().getReference();

        // This is provided by the FirebaseUI to query the database to fetch data
        FirebaseRecyclerOptions<Song> options
            = new FirebaseRecyclerOptions.Builder<Song>()
            .setQuery(mbase, Song.class)
            .build();

        // Connecting object of required Adapter class to the Adapter class itself
        adapter = new SongAdapter(options, SongListActivity.this);

        // Connecting Adapter class with the Recycler view
        recyclerView.setAdapter(adapter);
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
}