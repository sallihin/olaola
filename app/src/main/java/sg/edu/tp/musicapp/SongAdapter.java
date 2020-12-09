package sg.edu.tp.musicapp;

import android.content.Context;
import android.content.Intent;

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

/**
 * Created by Putra on 30/11/2020
 */

public class SongAdapter extends FirebaseRecyclerAdapter<Song, SongAdapter.SongViewHolder> {

    /* --------------------------------------------------------
     * This class populates the placeholder inside RecycleViewer
     * -------------------------------------------------------*/

    Context mContext;
    public SongAdapter(@NonNull FirebaseRecyclerOptions<Song> options, Context context)
    {
        super(options);
        this.mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull SongViewHolder holder, int position, @NonNull Song model)
    {
        // Pulls title from Song class
        holder.song_title.setText(model.getTitle());

        // Sets Artist name
        holder.song_artist.setText(model.getArtist());

        // Using Picasso library to dynamically download images and insert to the placeholder
        Picasso.get().load(model.getCoverArt()).into(holder.song_coverArt);

        // To pass the intent from Firebase to the PlaySongActivity
        holder.song_coverArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent startActivity from running many times due to multiple clicks
                holder.song_coverArt.setEnabled(false);

                // Create intent for the next Activity
                Intent intent = new Intent(holder.song_coverArt.getContext(), PlaySongActivity.class);
                intent.putExtra("songId", model.getSongId());
                intent.putExtra("artist", model.getArtist());
                intent.putExtra("songLength", model.getSongLength());
                intent.putExtra("title", model.getTitle());
                intent.putExtra("fileLink", model.getFileLink());
                intent.putExtra("coverArt", model.getCoverArt());

                holder.song_coverArt.getContext().startActivity(intent);
            }
        });
    }

    // These are the View ids of the placeholders on song_thumbnail.xml
    class SongViewHolder extends RecyclerView.ViewHolder
    {
        TextView song_title, song_artist;
        ImageButton song_coverArt;
        public SongViewHolder(@NonNull View itemView)
        {
            super(itemView);
            song_title = itemView.findViewById(R.id.song_title);
            song_artist = itemView.findViewById(R.id.song_artist);
            song_coverArt = itemView.findViewById(R.id.song_coverArt);
        }
    }

    // Takes data from SongAdapter, inflates into song_thumbnail
    @NonNull @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_thumbnail, parent, false);
        return new SongAdapter.SongViewHolder(view);
    }


}
