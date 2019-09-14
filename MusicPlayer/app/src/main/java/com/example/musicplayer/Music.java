package com.example.musicplayer;



public class Music {
    String title, artist, duration, path;

    public Music(String title, String artist, String path, String duration) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }


    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

}
