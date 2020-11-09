package com.thefatherinc.mydomrf;

import android.media.Image;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.thefatherinc.mydomrf.GerritAPI;
import com.thefatherinc.mydomrf.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFullActivity extends AppCompatActivity {

    private Call<RequestGetNewsFull> call_get_news_full;
    public ImageView image_of_news;
    public TextView full_news_label;
    public TextView full_news_description;

    public GerritAPI gerritAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_full);

        final Bundle arguments = getIntent().getExtras();

        final String BASE_URL = "http://192.168.100.108";
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gerritAPI = retrofit.create(GerritAPI.class);
        image_of_news = (ImageView) findViewById(R.id.image_of_news);
        full_news_label = (TextView) findViewById(R.id.full_news_label);
        full_news_description = (TextView) findViewById(R.id.full_news_description);
        full_news_description.setMovementMethod(new ScrollingMovementMethod());
        GetFullNews(new RequestGetNewsFullBody(arguments.get("news").toString()));
    }

    private void GetFullNews(final RequestGetNewsFullBody message) {
        call_get_news_full = gerritAPI.getNewsFull(message);
        call_get_news_full.enqueue(new Callback<RequestGetNewsFull>() {
            @Override
            public void onResponse(Call<RequestGetNewsFull> call, Response<RequestGetNewsFull> response) {
                Picasso.with(NewsFullActivity.this)
                        .load("http://192.168.100.108" + response.body().full_news[2])
                        .placeholder(R.drawable.ic_home_black_24dp)
                        .error(R.drawable.ic_dashboard_black_24dp)
                        .into(image_of_news);
                full_news_label.setText(response.body().full_news[0]);
                full_news_description.setText(response.body().full_news[1]);
            }

            @Override
            public void onFailure(Call<RequestGetNewsFull> call, Throwable t) {
            }
        });
    }
}