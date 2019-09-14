package com.example.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int MY_PERMISSION_REQUEST = 1;
    ArrayList<Music> mMusic;
    private int resumePosition;
    private int position = 0;
    String artist, title, currentPath, duration;
    MediaPlayer mediaPlayer;
    ImageView playBtn, pauseBtn, playListBtn, nextBtn, previousBtn;
    TextView singer, musicTitle, currentProgress, maxDuration;
    SeekBar durationBar;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }

        durationBar = findViewById(R.id.seekBar);
        playListBtn = findViewById(R.id.playlist);
        playBtn = findViewById(R.id.playBtn);
        pauseBtn = findViewById(R.id.pauseBtn);
        singer = findViewById(R.id.currentArtistName);
        musicTitle = findViewById(R.id.currentMusicTitle);
        currentProgress = findViewById(R.id.currentProgress);
        maxDuration = findViewById(R.id.maxDuration);
        nextBtn = findViewById(R.id.nextBtn);
        previousBtn = findViewById(R.id.previousBtn);


        playListBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);

        if (mediaPlayer == null) {
            String sortOrder = MediaStore.Audio.Media.ARTIST + " DESC";
            try (Cursor currentSong = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, sortOrder)) {

                if (currentSong != null && currentSong.moveToFirst()) {
                    mMusic = new ArrayList<>();

                    do {
                        currentPath = currentSong.getString(currentSong.getColumnIndex(MediaStore.Audio.Media.DATA));
                        artist = currentSong.getString(currentSong.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        title = currentSong.getString(currentSong.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        duration = currentSong.getString(currentSong.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        mMusic.add(new Music(title, artist, currentPath, duration));
                    } while (currentSong.moveToNext());

                    currentSong.moveToFirst();
                    currentSong.close();
                }
                playIt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        durationBar.setMax(Integer.parseInt(mMusic.get(position).duration));
        durationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                    durationBar.setProgress(i);
                    resumePosition = i;
                    currentProgress.setText(createTimeLabel(resumePosition));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    Message msg = new Message();
                    msg.what = mediaPlayer.getCurrentPosition();
                    handler.sendMessage(msg);
                    resumePosition++;
                }
            }
        }, 0, 1000);


        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                        Log.e("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        Log.e("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        Log.e("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                        break;
                }
                return true;
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            resumePosition = msg.what;
            String elapsedTime = createTimeLabel(resumePosition);
            durationBar.setProgress(resumePosition);
            currentProgress.setText(elapsedTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No Permission Granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.playBtn:
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(resumePosition);
                } else {
                    playIt();
                }
                mediaPlayer.start();
                visibility();
                break;
            case R.id.pauseBtn:
                resumePosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
                visibility();
                break;
            case R.id.playlist:

                Intent intent = new Intent(this, PlayList.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.nextBtn:
                if (position == mMusic.size() - 1) {
                    Toast.makeText(this, "End of the Playlist!", Toast.LENGTH_SHORT).show();
                    mediaPlayer.stop();
                    visibility();
                } else {
                    mediaPlayer.reset();
                    ++position;
                    resumePosition = 0;
                    playIt();
                    mediaPlayer.start();
                    visibility();
                }
                break;
            case R.id.previousBtn:
                if (resumePosition == 0 && position != 0) {
                    mediaPlayer.reset();
                    --position;
                    playIt();
                    mediaPlayer.start();
                    visibility();
                } else {
                    mediaPlayer.seekTo(0);
                    resumePosition = 0;
                    visibility();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent bundle) {
        if (bundle == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        durationBar.setProgress(0);
        uri = Uri.parse(bundle.getStringExtra("uri"));
        title = bundle.getStringExtra("title");
        artist = bundle.getStringExtra("artist");
        duration = bundle.getStringExtra("duration");
        position = bundle.getIntExtra("position", position);


        mediaPlayer = MediaPlayer.create(this, uri);
        singer.setText(artist);
        musicTitle.setText(title);

        duration = createTimeLabel(Integer.parseInt(mMusic.get(position).duration));
        maxDuration.setText(duration);
        durationBar.setMax(Integer.parseInt(mMusic.get(position).duration));

        mediaPlayer.start();
        visibility();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextBtn.performClick();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  handler.removeCallbacks();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }


    private void visibility() {
        if (mediaPlayer.isPlaying()) {
            playBtn.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.VISIBLE);
        } else {
            playBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.GONE);
        }
    }


    private void playIt() {
        durationBar.setProgress(0);

        uri = Uri.parse(mMusic.get(position).path);
        mediaPlayer = MediaPlayer.create(this, uri);

        singer.setText(mMusic.get(position).artist);
        musicTitle.setText(mMusic.get(position).title);
        duration = createTimeLabel(Integer.parseInt(mMusic.get(position).duration));
        maxDuration.setText(duration);
        durationBar.setMax(Integer.parseInt(mMusic.get(position).duration));

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                nextBtn.performClick();
            }
        });
    }
}
