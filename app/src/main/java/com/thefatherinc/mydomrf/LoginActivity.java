package com.thefatherinc.mydomrf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login_vk;
    private String base;
    private SharedPreferences sPref;
    public GerritAPI gerritAPI;
    private Call<RequestLogin> call_login;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VK.onActivityResult(requestCode, resultCode, data, new VKAuthCallback() {
            @Override
            public void onLoginFailed(int i) {
            }

            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                base = vkAccessToken.getUserId().toString();
                TryLogin(new RequestLoginBody(base));
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final String BASE_URL = "http://192.168.100.108";
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gerritAPI = retrofit.create(GerritAPI.class);

        btn_login_vk = (Button) findViewById(R.id.btn_login_vk);
        sPref = this.getSharedPreferences("SavedState", MODE_PRIVATE);
        String stateUser = sPref.getString("SavedState", "NO");
        Log.d("stateUser", stateUser);

        if (stateUser.equals("YES")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        btn_login_vk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VK.login(LoginActivity.this);
            }
        });
    }

    private void saveStateUser() {
        sPref = this.getSharedPreferences("SavedState", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("SavedState", "YES");
        ed.apply();
    }

    private void TryLogin(final RequestLoginBody message) {
        call_login = gerritAPI.logIn(message);
        call_login.enqueue(new Callback<RequestLogin>() {
            @Override
            public void onResponse(Call<RequestLogin> call, Response<RequestLogin> response) {
                if (response.body().token != null) {
                    Log.d("token_new", response.body().token + "vk");
                }
            }

            @Override
            public void onFailure(Call<RequestLogin> call, Throwable t) {
            }
        });
    }
}