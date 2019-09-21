package com.example.booklisting;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class Utils {

    public static List<Book> fetchQueryData(String request) throws JSONException {
        if (request.isEmpty()) {
            return null;
        }
        String LOG = "Aoklet";
        URL url = null;
        String jsonResponse = "";
        try {
            url = new URL(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            if (url != null) {
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    Log.d(LOG, "HTTP_OK");
                    inputStream = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line = bufferedReader.readLine();
                    StringBuilder output = new StringBuilder();
                    while (line != null) {
                        output.append(line);
                        line = bufferedReader.readLine();
                    }
                    jsonResponse = output.toString();
                    // Log.d(LOG, "json Response: "+jsonResponse);
                } else {
                    Log.d(LOG, "Error Response Code");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        List<Book> books = new ArrayList<>();

        JSONObject baseJson = new JSONObject(jsonResponse);
        JSONArray jsonArray = baseJson.getJSONArray("items");
        Log.d("LAGA", "i am outside for loop");
        for (int i = 0; i < jsonArray.length(); i++) {
            Log.d("LAGA", "i am inside for loop");

            JSONObject currentBook = jsonArray.getJSONObject(i);
            JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");
            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
            JSONObject saleInfo = currentBook.getJSONObject("saleInfo");
            JSONObject listPrice = saleInfo.getJSONObject("listPrice");
            String title = volumeInfo.getString("title");
            String imageUrl = imageLinks.getString("thumbnail");
            double price = listPrice.getDouble("amount");
            StringBuilder stringBuilder = new StringBuilder();
            String buyBook = (String) saleInfo.get("buyLink");
         /*   Pattern p = Pattern.compile("id=(.*?)&");
            Matcher m = p.matcher(imageUrl);
            if (m.matches()) {
                String id = m.group(1);
                imageUrl = String.valueOf(stringBuilder.append("https://books.google.com/books/content/images/frontcover/").append(id).append("?fife=w300"));
            } else {
                Log.i(LOG, "Issue with cover");

             }*/

            Log.d("LAGA", "image: " + imageUrl + "\ntitle: " + title + "" +
                    "\nprice:" + price);


            Book book = new Book(title, price, imageUrl, buyBook);
            books.add(book);


        }
        Log.d("LAGA", "LEFT the loop");


        return books;
    }
}
