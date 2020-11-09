package com.thefatherinc.mydomrf.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.solver.GoalRow;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thefatherinc.mydomrf.GerritAPI;
import com.thefatherinc.mydomrf.LoginActivity;
import com.thefatherinc.mydomrf.MainActivity;
import com.thefatherinc.mydomrf.NewsFullActivity;
import com.thefatherinc.mydomrf.R;
import com.thefatherinc.mydomrf.RequestGetNews;
import com.thefatherinc.mydomrf.RequestLogin;
import com.thefatherinc.mydomrf.RequestLoginBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    static final int GALLERY_REQUEST = 1;
    private Call<RequestGetNews> call_get_news;
    public GerritAPI gerritAPI;
    public String[] news;
    private Button btn_refresh_home;
    ImageView imageView;
    public File file;
    private Call<RequestLogin> call_test;

    public Button test_upload;
    public Button test_send;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    file = new File(selectedImage.getPath());
                }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final String BASE_URL = "http://192.168.100.108";
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gerritAPI = retrofit.create(GerritAPI.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final ListView listNews = root.findViewById(R.id.list_news);
        btn_refresh_home = (Button) root.findViewById(R.id.btn_refresh_home);

        news = new String[]{"Обновите новости"};
        GetNews(new RequestLoginBody("123"));
        listNews.setAdapter(new ArrayAdapter<String>(getActivity(),  android.R.layout.simple_list_item_1, news));


        test_upload = (Button) root.findViewById(R.id.test_upload);

        test_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        test_send = (Button) root.findViewById(R.id.test_send);

        test_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TryUpload();
            }
        });


        btn_refresh_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNews(new RequestLoginBody("123"));
                listNews.setAdapter(new ArrayAdapter<String>(getActivity(),  android.R.layout.simple_list_item_1, news));
            }
        });

        listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NewsFullActivity.class);
                intent.putExtra("news", parent.getAdapter().getItem(position).toString());
                startActivity(intent);
            }
        });
        return root;
    }

    private void TryUpload() {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
        call_test = gerritAPI.upload(body);
        call_test.enqueue(new Callback<RequestLogin>() {
            @Override
            public void onResponse(Call<RequestLogin> call, Response<RequestLogin> response) {
            }

            @Override
            public void onFailure(Call<RequestLogin> call, Throwable t) {
                Log.d("test_file", t.toString());
            }
        });
    }

    private void GetNews(final RequestLoginBody message) {
        call_get_news = gerritAPI.getNews(message);
        call_get_news.enqueue(new Callback<RequestGetNews>() {
            @Override
            public void onResponse(Call<RequestGetNews> call, Response<RequestGetNews> response) {
                news = response.body().news;
            }

            @Override
            public void onFailure(Call<RequestGetNews> call, Throwable t) {
            }
        });
    }
}