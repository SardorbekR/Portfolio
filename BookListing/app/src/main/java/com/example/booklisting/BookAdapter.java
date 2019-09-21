package com.example.booklisting;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.aquery.AQuery;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {
    private static String LOG = "Aoklet";
    private AQuery mAQuery;
    public ImageLoader imageLoader = ImageLoader.getInstance();
    ;

    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list, parent, false);
        }

        Book currentBook = getItem(position);
        ImageView image = listItemView.findViewById(R.id.imageView);
        TextView title = listItemView.findViewById(R.id.name);
        TextView price = listItemView.findViewById(R.id.price);
        Log.d(LOG, "image: " + image + "\n" +
                "title: " + title +
                "\nprice:" + price);

        if (currentBook != null) {
            Log.d(LOG, "inside the current book");
            Log.d(LOG, "LIMAGE: " + currentBook.getImage());
//          Glide.with(getContext()).load(currentBook.getImage()).into(image);

            //Picasso.get().load(currentBook.getImage()).into(image);
            title.setText(currentBook.getTitle());
            price.setText(currentBook.getPrice() + "$");
            imageLoader.displayImage(currentBook.getImage(), image);

        }

        return listItemView;
    }
}
