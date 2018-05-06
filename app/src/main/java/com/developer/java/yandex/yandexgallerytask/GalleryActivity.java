package com.developer.java.yandex.yandexgallerytask;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.developer.java.yandex.yandexgallerytask.adapters.GalleryAdapter;
import com.developer.java.yandex.yandexgallerytask.entity.PhotoResponse;
import com.developer.java.yandex.yandexgallerytask.model.PhotoViewModel;
import com.developer.java.yandex.yandexgallerytask.model.ResponseModel;
import com.developer.java.yandex.yandexgallerytask.net.YandexCommunication;

import java.util.List;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.ACTION_VIEW;

public class GalleryActivity extends AppCompatActivity{
    private static final String TAG = GalleryActivity.class.getSimpleName();
    private static final String TOKEN_ACCOUNT = "YandexGalleryToken";
    private SharedPreferences sharedPreferences;
    private PhotoViewModel model;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        init();
    }

    public void init() {
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        model = ViewModelProviders.of(this).get(PhotoViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressBar = findViewById(R.id.pb_gallery);
        RecyclerView recyclerView = findViewById(R.id.rv_image);

        handleAction();

        String token = getToken();
        if(!token.isEmpty()) {
            YandexCommunication.setAuth(token);
            final GalleryAdapter galleryAdapter = new GalleryAdapter(this, token);
            recyclerView.setAdapter(galleryAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setHasFixedSize(true);
            Log.w(TAG, "token = " + token);
            model.getPhotoResponses().observe(this, new Observer<ResponseModel<List<PhotoResponse>>>() {
                @Override
                public void onChanged(@Nullable ResponseModel<List<PhotoResponse>> listResponseModel) {
                    if(listResponseModel != null &&listResponseModel.isSuccessful()){
                        galleryAdapter.setData(listResponseModel.getResponse());
                        mProgressBar.setVisibility(View.GONE);
                    }
                    else if(listResponseModel != null){
                        ErrorFragmentDialog dialog = new ErrorFragmentDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("message", listResponseModel.getError());
                        dialog.setArguments(bundle);
                        dialog.show(getSupportFragmentManager(), "error");
                    }
                }
            });
        }
    }

    public void handleAction(){
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action != null){
            switch (action) {
                case ACTION_VIEW:
                    Uri uri = intent.getData();
                    if (uri != null) {
                        String token = uri.getFragment().split("&")[0].split("=")[1];
                        sharedPreferences.edit().putString(TOKEN_ACCOUNT, token).apply();
                    }
                    break;
                case ACTION_MAIN:

                default:
                    break;
            }
        }
        Log.w(TAG, action);
    }

    public String getToken(){
        String token = sharedPreferences.getString(TOKEN_ACCOUNT, "");
        if(token.isEmpty()){
            String url = "https://oauth.yandex.ru/authorize?" +
                    "response_type=token" +
                    "&client_id=21c529ce43f3404f88ac68dfc8faa8f9";
            Intent intent = new Intent(ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
        return token;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            model.updatePhotoResponses();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
