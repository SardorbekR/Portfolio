package com.example.booklisting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private static String LOG = "Aoklet";
    private static String JsonResponse = "";
    private BookAdapter adapter;
    private ProgressBar loading;
    private Button searchBtn;
    private SearchView searchField;
    private ImageView noNet;
    TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //final String[] searchKey = new String[1];
        searchBtn = findViewById(R.id.searchBtn);
        searchField = findViewById(R.id.search);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        noNet = findViewById(R.id.no_connection);
        List<Book> books = new ArrayList<>();
        loading = findViewById(R.id.progressBar);
        loading.setVisibility(GONE);
        adapter = new BookAdapter(this, books);

        noNet.setVisibility(GONE);
        empty = findViewById(R.id.empty_view);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        updateQueryUrl("space x");
        restartLoader();
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchField.getQuery().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please, fill the search field", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(LOG, searchField.getQuery().toString());
                    updateQueryUrl(searchField.getQuery().toString());
                    Log.d(LOG, JsonResponse);
                    restartLoader();
                }
            }
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Book currentBook = adapter.getItem(i);
            Uri uri = null;
            if (currentBook != null) {
                uri = Uri.parse(currentBook.getBuyBook());
            }
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        });
    }

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {
        return new BookLoader(this, JsonResponse);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> data) {
        loading.setVisibility(GONE);
        adapter.clear();

        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
            noNet.setVisibility(GONE);
        } else {
            empty.setText("Couldn't load data!");
            noNet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        adapter.clear();
    }

    private void updateQueryUrl(String searchValue) {

        if (searchValue.contains(" ")) {
            searchValue = searchValue.replace(" ", "+");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("https://www.googleapis.com/books/v1/volumes?q=").append(searchValue).append("&filter=paid-ebooks&maxResults=40");
        JsonResponse = sb.toString();
        //return JsonResponse;
    }

    public void restartLoader() {
        empty.setVisibility(GONE);
        loading.setVisibility(View.VISIBLE);
        LoaderManager.getInstance(this).restartLoader(1, null, MainActivity.this);
    }

}
