package com.example.musicplayer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class PlayList extends AppCompatActivity {

    ArrayList<Music> mArrayList;
    ListView mListView;
    MusicAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
        mListView = findViewById(R.id.playlistView);
        mArrayList = new ArrayList<>();
        getMusic();
        mAdapter = new MusicAdapter(this, mArrayList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //go back to mainActivity for playing chosen music
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("uri", mArrayList.get(i).getPath());
                intent.putExtra("title", mArrayList.get(i).title);
                intent.putExtra("artist", mArrayList.get(i).artist);
                intent.putExtra("duration", mArrayList.get(i).duration);
                intent.putExtra("position", i);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    public void getMusic() {
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.Media.ARTIST + " DESC";
        Cursor currentSong = getContentResolver().query(songUri, null, null, null, sortOrder);

        String currentTitle, currentArtist, currentPath, currentDuration;

        if (currentSong != null && currentSong.moveToFirst()) {
            int songTitle = currentSong.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = currentSong.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int path = currentSong.getColumnIndex(MediaStore.Audio.Media.DATA);
            int duration = currentSong.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                currentTitle = currentSong.getString(songTitle);
                currentArtist = currentSong.getString(songArtist);
                currentPath = currentSong.getString(path);
                currentDuration = convertDuration(Integer.parseInt(currentSong.getString(duration)));

                mArrayList.add(new Music(currentTitle, currentArtist, currentPath, currentDuration));
            } while (currentSong.moveToNext());
            currentSong.close();
        }
    }

    public String convertDuration(int time) {
        String timeLabel;
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

}
