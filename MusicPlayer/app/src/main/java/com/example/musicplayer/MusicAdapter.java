package com.example.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class MusicAdapter extends ArrayAdapter {


    public MusicAdapter(@NonNull Context context, List<Music> musics) {
        super(context, 0, musics);
    }

    MediaMetadataRetriever retriever;
    Music currentMusic;
    TextView title, artist, duration;
    ImageView coverImage;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.music, parent, false);
        }

        currentMusic= (Music) getItem(position);
        title= convertView.findViewById(R.id.title);
        artist= convertView.findViewById(R.id.singer);
        duration= convertView.findViewById(R.id.duration);
        coverImage= convertView.findViewById(R.id.cover);



        if(currentMusic!=null){
            title.setText(currentMusic.getTitle());
            artist.setText(currentMusic.getArtist());
            duration.setText(currentMusic.getDuration());

            new LoadImageTask(coverImage).execute(currentMusic.getPath());
        }

        return convertView;
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView artImage;

        public LoadImageTask(ImageView image){
            artImage = image;
        }

        @Override
        protected void onPreExecute() {
            retriever = new MediaMetadataRetriever();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            retriever.setDataSource(params[0]);
            byte[] art = retriever.getEmbeddedPicture();
            Bitmap bitmap = null;
            if( art != null ){
                bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            if( bm != null ){
                artImage.setImageBitmap(bm);
            }

        }

    }

}
