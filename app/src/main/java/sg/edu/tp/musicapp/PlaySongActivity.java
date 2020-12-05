package sg.edu.tp.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
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
import java.util.Map;
import java.util.Random;

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
    private Double songLength;
    private String url = "";

    // This is the built-in MediaPlayer object that we will use to play the music
    private MediaPlayer player = null;
    private boolean onLoop = false;
    private boolean onShuffle = false;

    // This Button variable is created to link to the Play button at the playback screen. We need to do this
    // because it will act both as a Play and Pause button
    private ImageView btnImagePlayPause;
    private ImageView btnLoop;
    private ImageView btnShuffle;

    // This is the position of the song in playback.
    // We set it to 0 here so that it starts at the beginning.
    private int musicPosition = 0;

    // Variables for seekBar
    SeekBar seekBar;
    Handler handler = new Handler();

    // Firebase function to reference the database location
    // Ref is empty due to how the JSON database is structure
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // Variables for swipe left/right to change songs
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    // Random number variable
    int nextRandomSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        btnImagePlayPause = findViewById(R.id.playButton);
        btnLoop = findViewById(R.id.loop);
        btnShuffle = findViewById(R.id.shuffle);
        overridePendingTransition(R.anim.pull_from_bottom, 0);
        retrieveData();
        displaySong(title, artist, coverArt, songLength);
        preparePlayer();

        // To allow scrubbing on the seekBar
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player != null && player.isPlaying())
                {
                    player.seekTo(seekBar.getProgress());
                }

                // Continue music where they last scrubbed
                musicPosition = seekBar.getProgress();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {

            case MotionEvent.ACTION_DOWN: // Find out x position of where press gesture started
                x1 = event.getX();
                break;

            case MotionEvent.ACTION_UP: // Find out x position of where press gesture ended ended
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // If end bigger than start, then gesture is right to left or GO BACK
                    if (x2 > x1)
                    {
                        playPrevious(findViewById(R.id.imgCoverArt));
                    }

                    // Otherwise, go forward!
                    else
                    {
                        playNext(findViewById(R.id.imgCoverArt));
                    }
                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    Runnable forSeekBar = new Runnable() {
        @Override
        public void run() {
            if (player != null && player.isPlaying())
            {
                seekBar.setProgress(player.getCurrentPosition());
            }
            handler.postDelayed(this, 1000);
        }
    };

    private void retrieveData()
    {
        Bundle songData = this.getIntent().getExtras();
        songId = songData.getInt("songId");
        title = songData.getString("title");
        artist = songData.getString("artist");
        fileLink = songData.getString("fileLink");
        coverArt = songData.getString("coverArt");
        songLength = songData.getDouble("songLength");
        url = BASE_URL + fileLink;
    }

    private void displaySong(String title, String artist, String coverArt, Double songLength)
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

        // Display total length of song
        TextView txtTotalLength = findViewById(R.id.totalLength);
        txtTotalLength.setText(Double.toString(songLength));
    }

    private void preparePlayer()
    {
        // 1. Create a new MediaPlayer
        player = new MediaPlayer();

        // Catch any error that may occur and to handle the error.
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
        // Prepare the player
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

            // Start the player
            player.start();

            // Prepares seekbar and calls for runnable updates at 1 sec interval
            seekBar.setMax(player.getDuration());
            handler.removeCallbacks(forSeekBar);
            handler.postDelayed(forSeekBar, 1000);

            // Set the text of the play button to "PAUSE"
            btnImagePlayPause.setImageResource(R.drawable.btn_pause);

            // Set the heading title of the app to the music that is currently playing
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

        // 3. Set the button back to "PLAY"
        btnImagePlayPause.setImageResource(R.drawable.btn_play);
    }

    private void gracefullyStopWhenMusicEnds()
    {
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {
                // Restart if onLoop is true
                if (onLoop || onShuffle)
                {
                    Log.d("RESTART PROCESS", "stopactivities");
                    stopActivities();
                }
                else {
                    preparePlayer();
                    playNext(findViewById(android.R.id.content));
                    Log.d("RESTART PROCESS", "playnext");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (player != null)
        {
            handler.removeCallbacks(forSeekBar);
            player.stop();
            player.release();
            player = null;
            onLoop = false;
        }
    }

    private void stopActivities()
    {
        if (player != null)
        {
            musicPosition = 0;
            player.stop();
            player.release();
            seekBar.setProgress(0);
            if (onLoop)
            {
                preparePlayer();
                player.start();
                gracefullyStopWhenMusicEnds();
            }
            else if (onShuffle)
            {
                preparePlayer();
                playRandom(findViewById(android.R.id.content));
            }
            else
            {
                btnImagePlayPause.setImageResource(R.drawable.btn_play);
                player = null;
            }
        }
    }

    public void playNext(View view)
    {
        // Resets onLoop and onShuffle
        resetRepeatAndShuffle();
                
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
                        CardView viewCoverArtCard = findViewById(R.id.imageShadow);
                        Animation push_left_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_left_in);
                        Animation push_left_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_left_out);

                        // Assign all song data to variables
                        songId = songId + 1; // Update songId to next track
                        title = songSnapshot.child("title").getValue(String.class);
                        artist = songSnapshot.child("artist").getValue(String.class);
                        fileLink = songSnapshot.child("fileLink").getValue(String.class);
                        coverArt = songSnapshot.child("coverArt").getValue(String.class);
                        songLength = songSnapshot.child("songLength").getValue(Double.class);

                        // Form the full url
                        url = BASE_URL + fileLink;

                        // Animation: Remove old coverArt
                        viewCoverArtCard.startAnimation(push_left_out);

                        // Display song info on screen
                        displaySong(title, artist, coverArt, songLength);

                        // Animation: Slide in new coverArt
                        viewCoverArtCard.startAnimation(push_left_in);

                        // Stop current song
                        stopActivities();

                        // Call playOrPauseMusic to play the song
                        playOrPauseMusic(view);
                    }
                }
                else
                {
                    stopActivities();
                    Toast.makeText(PlaySongActivity.this, "End of playlist", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void playPrevious(View view)
    {
        // Resets onLoop and onShuffle
        resetRepeatAndShuffle();

        // Query to find item with similar value. I created a songId key to compare.
        Query previousSongPosition =  databaseReference.orderByChild("songId").equalTo(songId-1);

        // Firebase function to receive events
        previousSongPosition.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot songSnapshot : dataSnapshot.getChildren())
                    {
                        CardView viewCoverArtCard = findViewById(R.id.imageShadow);
                        Animation push_right_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_right_out);
                        Animation push_right_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_right_in);

                        // Assign all song data to variables
                        songId = songId - 1; // Update songId to next track
                        title = songSnapshot.child("title").getValue(String.class);
                        artist = songSnapshot.child("artist").getValue(String.class);
                        fileLink = songSnapshot.child("fileLink").getValue(String.class);
                        coverArt = songSnapshot.child("coverArt").getValue(String.class);
                        songLength = songSnapshot.child("songLength").getValue(Double.class);

                        // Form the full url
                        url = BASE_URL + fileLink;

                        // Animation: Remove old coverArt
                        viewCoverArtCard.startAnimation(push_right_out);

                        //Display song info on screen
                        displaySong(title, artist, coverArt, songLength);

                        // Animation: Slide in new coverArt
                        viewCoverArtCard.startAnimation(push_right_in);

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

    // I split this method into 2 parts so its easier to build and debug
    public void playRandom(View view)
    {
        // First we query the max number of songs in the database
        Query lastSongPosition = databaseReference.orderByChild("songId").limitToLast(1);
        lastSongPosition.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Initialize Random class & select random next song
                if(dataSnapshot.exists()) {

                    for (DataSnapshot lastSong : dataSnapshot.getChildren()) {

                        Integer lastSongIndex= lastSong.child("songId").getValue(Integer.class);
                        Log.d("CheckShuffle", "upperMax: " + lastSongIndex);

                        Random rand = new Random();
                        int lowerBound = 1;
                        int upperBound = lastSongIndex;
                        nextRandomSong = rand.nextInt(upperBound-lowerBound) + lowerBound;
                        Log.d("CheckShuffle", "nextRandomSong: " + nextRandomSong);

                        playSelectedRandomSong(view, nextRandomSong);
                    }
                }
                else {}
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void playSelectedRandomSong(View view, int nextRandomSong)
    {
        // From the above result, we query the database again using the nextRandomSong id
        Query randomSongPosition =  databaseReference.orderByChild("songId").equalTo(nextRandomSong);
        randomSongPosition.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot songSnapshot : dataSnapshot.getChildren())
                {
                    CardView viewCoverArtCard = findViewById(R.id.imageShadow);
                    Animation push_left_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_left_in);
                    Animation push_left_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_left_out);
                    Log.d("CheckShuffle", "Song Started");

                    // Assign all song data to variables
                    songId = songSnapshot.child("songId").getValue(Integer.class);; // Update songId to next track
                    title = songSnapshot.child("title").getValue(String.class);
                    artist = songSnapshot.child("artist").getValue(String.class);
                    fileLink = songSnapshot.child("fileLink").getValue(String.class);
                    coverArt = songSnapshot.child("coverArt").getValue(String.class);
                    songLength = songSnapshot.child("songLength").getValue(Double.class);

                    // Form the full url
                    url = BASE_URL + fileLink;

                    // Animation: Remove old coverArt
                    viewCoverArtCard.startAnimation(push_left_out);

                    //Display song info on screen
                    displaySong(title, artist, coverArt, songLength);

                    // Animation: Slide in new coverArt
                    viewCoverArtCard.startAnimation(push_left_in);

                    // Manually stop and release
                    player.stop();
                    player.release();

                    // Start the player
                    preparePlayer();
                    player.start();

                    // Prepares seekbar and calls for runnable updates at 1 sec interval
                    seekBar.setMax(player.getDuration());
                    handler.removeCallbacks(forSeekBar);
                    handler.postDelayed(forSeekBar, 1000);

                    gracefullyStopWhenMusicEnds();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // I built this method myself without using stackoverflow or any documentation! :)
    // 2nd part of the codes in the stopactivities method
    public void repeatSong(View view)
    {
        if (!onLoop)
        {
            btnLoop.setImageResource(R.drawable.btn_repeat_active);
            onLoop = true;

            if (onShuffle)
            {
                btnShuffle.setImageResource(R.drawable.btn_shuffle);
                onShuffle = false;
            }
        }
        else
        {
            btnLoop.setImageResource(R.drawable.btn_repeat);
            onLoop = false;
        }
    }

    public void randomSong(View view)
    {
        if (!onShuffle)
        {
            btnShuffle.setImageResource(R.drawable.btn_shuffle_active);
            onShuffle = true;

            if (onLoop)
            {
                // Disable onLoop if its true
                btnLoop.setImageResource(R.drawable.btn_repeat);
                onLoop = false;
            }
        }
        else
        {
            btnShuffle.setImageResource(R.drawable.btn_shuffle);
            onShuffle = false;
        }
    }
    
    public void resetRepeatAndShuffle()
    {
        onShuffle = false;
        onLoop = false;
        btnShuffle.setImageResource(R.drawable.btn_shuffle);
        btnLoop.setImageResource(R.drawable.btn_repeat);
    }

    public void goPrevActivity(View view) {
        Intent intent  = new Intent(this, SongListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.pull_from_top, R.anim.pull_from_top);
    }
}