package sg.edu.tp.putra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class PlaySongActivity extends AppCompatActivity {

    // This is the constant that contains the website URL
    // where we will stream the music.
    private static final String BASE_URL = "https://p.scdn.co/mp3-preview/";

    // These variables are the song information that we will be using in the code
    private int songId = 0;
    private String title = "";
    private String artist = "";
    private String fileLink = "";
    private String coverArt = "";
    private String url = "";

    // This is the built-in MediaPlayer object that we will use to play the music
    private MediaPlayer player = null;

    // This is the position of the song in playback.
    // We set it to 0 here so that it starts at the beginning.
    private int musicPosition = 0;

    // his Button variable is created to link to the Play button at the playback screen. We need to do this
    // because it will act both as a Play and Pause button
    private Button btnPlayPause = null;

    // We need to create an instance of the SongCollection object so that we can get the next and previous song
    // private SongCollection songCollection = new SongCollection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        btnPlayPause = findViewById(R.id.btnPlayPause);
        retrieveData();
        displaySong(title, artist, coverArt);
        preparePlayer();
    }
    private void retrieveData()
    {
        Bundle songData = this.getIntent().getExtras();

        songId = songData.getInt("songId");
        title = songData.getString("title");
        artist = songData.getString("artist");
        fileLink = songData.getString("fileLink");
        coverArt = songData.getString("coverArt");

        url = BASE_URL + fileLink;
        Log.d("RECEIVING THIS", "" + songId);
    }

    private void displaySong(String title, String artist, String coverArt)
    {
        // This is to retrieve the song title TextView from the UI screen
        TextView txtTitle = findViewById(R.id.txtSongTitle);
        txtTitle.setText(title);

        // This is to retrieve the artiste TextView from the UI screen
        TextView txtArtiste = findViewById(R.id.txtArtist);
        txtArtiste.setText(artist);

        // This is to set the selected cover art image to the ImageView in the screen
        ImageView coverArtPlaceholder = findViewById(R.id.imgCoverArt);
        Picasso.get().load(coverArt).into(coverArtPlaceholder);
    }

    private void preparePlayer()
    {
        // 1. Create a new MediaPlayer
        player = new MediaPlayer();

        // The try and catch codes are required by the prepare() method
        // It is to catch any error that may occur and to handle the error.
        try
        {
            // 2. This sets the Audio Stream Type to music streaming
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // 3. Set the source of the music
            player.setDataSource(url);

            // 4. Prepare the player for playback (WHY?)
            player.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void playOrPauseMusic(View view)
    {
        // 1. Prepare the player
        if (player == null) {
            preparePlayer();
        }

        // If song is not playing
        if (!player.isPlaying())
        {
            // If position of music is greater than 0
            if (musicPosition > 0)
            {
                // Get player to go to music position
                player.seekTo(musicPosition);
            }

            // 2. Start the player
            player.start();

            //3. Set the text of the play button to "PAUSE"
            btnPlayPause.setText("PAUSE");

            // 4. Set the heading title of the app to the music that is currently playing
            setTitle("Now Playing " + title + " = " + artist);

            gracefullyStopWhenMusicEnds();
        }
        else
        {
            pauseMusic();
        }
    }

    private void pauseMusic()
    {
        // 1. Pause the player
        player.pause();

        // 2. Get the current position of the music that is playing
        musicPosition = player.getCurrentPosition();

        // 3. Set the text on the button back to "PLAY"
        btnPlayPause.setText("PLAY");
    }

    private void gracefullyStopWhenMusicEnds()
    {
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {
                stopActivities();
            }
        });
    }

    private void stopActivities()
    {
        if (player != null)
        {
            btnPlayPause.setText("PLAY");
            musicPosition = 0;
            player.stop();
            player.release();
            player = null;
        }
    }

    public void playNext(View view)
    {
        // Firebase function to reference the database location
        // Ref is empty due to how the JSON database is structure
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Query to find item with similar value. I created a songId key to compare.
        Query nextSongPosition =  databaseReference.orderByChild("songId").equalTo(songId+1);

        // Firebase function to receive events
        nextSongPosition.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot songSnapshot : dataSnapshot.getChildren())
                    {
                        // Assign all song data to variables
                        songId = songId + 1; // Update songId to next track
                        title = songSnapshot.child("title").getValue(String.class);
                        artist = songSnapshot.child("artist").getValue(String.class);
                        fileLink = songSnapshot.child("fileLink").getValue(String.class);
                        coverArt = songSnapshot.child("coverArt").getValue(String.class);

                        // Form the full url
                        url = BASE_URL + fileLink;

                        //Display song info on screen
                        displaySong(title, artist, coverArt);

                        // Stop current song
                        stopActivities();

                        // Call playOrPauseMusic to play the song
                        playOrPauseMusic(view);
                    }
                }
                else
                {
                    Toast.makeText(PlaySongActivity.this, "End of playlist", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

//    public void playPrevious(View view)
//    {
//        Song prevSong = songCollection.getPreviousSong(songId);
//
//        // If next song is not null
//        if (prevSong != null)
//        {
//            // Assign all song data to variables
//            songId = prevSong.getId();
//            title = prevSong.getTitle();
//            artist = prevSong.getArtist();
//            fileLink = prevSong.getFileLink();
//            coverArt = prevSong.getCoverArt();
//
//            // Form the full url
//            url = BASE_URL + fileLink;
//
//            // Stop current song
//            stopActivities(player);
//
//            //Display song info on screen
//            displaySong(title, artist, coverArt);
//
//            // Call playOrPauseMusic to play the song
//            playOrPauseMusic(view);
//        }
//    }
}