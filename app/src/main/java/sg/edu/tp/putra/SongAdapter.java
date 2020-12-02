package sg.edu.tp.putra;

import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Putra on 30/11/2020
 * This is one type of RecyclerView adapters offered by the FirebaseUI for Firestore.
 * Firebase x Recycler Code adapted from GeeksforGeeks
 * https://www.geeksforgeeks.org/how-to-populate-recyclerview-with-firebase-data-using-firebaseui-in-android-studio/
 */

public class SongAdapter extends FirebaseRecyclerAdapter<Song, SongAdapter.SongViewHolder> {

    private ArrayList<Song> nameList = new ArrayList<>();

    public SongAdapter(@NonNull FirebaseRecyclerOptions<Song> options)
    {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SongViewHolder holder, int position, @NonNull Song model)
    {
        // Pulls title from Song class
        holder.song_title.setText(model.getTitle());

        // Convert double to string and concatenate "mins"
        String songLength = Double.toString(model.getSongLength()) + " mins";
        holder.song_duration.setText(songLength);

        // Using Picasso library to dynamically download images and insert to the placeholder
        Picasso.get().load(model.getCoverArt()).into(holder.song_coverArt);

        // To pass the intent from Firebase to the PlaySongActivity
        holder.song_coverArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.song_coverArt.getContext(), PlaySongActivity.class);
                intent.putExtra("songId", model.getSongId());
                int nextPosition = model.getSongId();
                Log.d("SENDING", "Position " + position + " & ID " + model.getSongId()); // REMOVE AFTER DEBUG
                intent.putExtra("artist", model.getArtist());
                intent.putExtra("song_duration", model.getSongLength());
                intent.putExtra("title", model.getTitle());
                intent.putExtra("fileLink", model.getFileLink());
                intent.putExtra("coverArt", model.getCoverArt());

                holder.song_coverArt.getContext().startActivity(intent);
            }
        });
    }

    String artist, title, fileLink, coverArt;
    Double songLength;
    ArrayList<Song>  Songs = new ArrayList<>();

    // Takes data from SongAdapter, inflates into song_thumbnail
    @NonNull @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_thumbnail, parent, false);
        return new SongAdapter.SongViewHolder(view);
    }

    // These are the locations of the placeholders on song_thumbnail.xml
    class SongViewHolder extends RecyclerView.ViewHolder
    {
        TextView song_title, song_duration;
        ImageButton song_coverArt;
        public SongViewHolder(@NonNull View itemView)
        {
            super(itemView);
            song_title = itemView.findViewById(R.id.song_title);
            song_duration = itemView.findViewById(R.id.song_duration);
            song_coverArt = itemView.findViewById(R.id.song_coverArt);
        }
    }


}
