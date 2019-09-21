package com.example.booklisting;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;
import org.json.JSONException;
import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    private String url;

    public BookLoader(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.d("Aoklet", "onStartLoading started");
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        List<Book> books = null;
        Log.d("Aoklet", "loadInBackground started");
        try {
            books = Utils.fetchQueryData(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }

}
