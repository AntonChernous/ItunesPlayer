package com.intech_global.itunesplayer.Song;

/**
 * Created by Anton on 28.07.2016.
 */
public class Song {
    public int ID;
    public String TrackName;
    public String ImgUrl;
    public String ArtistName;
    public String PreviewUrl;


    public Song(int ID, String trackName, String imgUrl, String artistName, String previewUrl) {
        this.ID = ID;
        TrackName = trackName;
        ImgUrl = imgUrl;
        ArtistName = artistName;
        PreviewUrl = previewUrl;
    }


}
