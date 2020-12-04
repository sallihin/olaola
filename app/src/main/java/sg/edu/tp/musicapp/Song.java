package sg.edu.tp.musicapp;

/**
 * Created by Putra on 30/11/2020
 */

public class Song
{
    // Initialize empty variables to use with getters below

    private int songId;
    private String title;
    private String artist;
    private String fileLink;
    private double songLength;
    private String coverArt;

    // Mandatory empty constructor for use of FirebaseUI
    public Song() {}

    // Only contains getters as the DB is read only
    // Getters are predefined by Firebase get<Key>

    public int getSongId() { return songId; }

    public String getTitle() { return title; }

    public String getArtist() { return artist; }

    public String getFileLink() { return fileLink; }

    public double getSongLength() { return songLength; }

    public String getCoverArt() { return coverArt; }

}
