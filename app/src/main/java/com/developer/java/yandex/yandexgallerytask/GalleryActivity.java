package com.developer.java.yandex.yandexgallerytask;

import android.Manifest;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.developer.java.yandex.yandexgallerytask.adapters.GalleryAdapter;
import com.developer.java.yandex.yandexgallerytask.api.FlickrApi;

import com.developer.java.yandex.yandexgallerytask.entity.PhotoResponse;
import com.developer.java.yandex.yandexgallerytask.model.PhotoViewModel;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements HandleResponse {
    private static final String TAG = GalleryActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = {Manifest.permission.GET_ACCOUNTS};
    private static final String TOKEN_ACCOUNT = "YandexGalleryToken";
    private SharedPreferences sharedPreferences;
    private PhotoViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        model = ViewModelProviders.of(this).get(PhotoViewModel.class);

        checkAndRequestPermission(PERMISSIONS);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String token = sharedPreferences.getString(TOKEN_ACCOUNT, "");
        Intent intent = getIntent();
        String action = intent.getAction();
        Log.w(TAG, action);
        if(action.equals("android.intent.action.VIEW")){
            Uri uri = intent.getData();
            token = uri.getFragment().split("&")[0].split("=")[1];
            Log.w(TAG, "fragment = " + token);

            sharedPreferences.edit().putString(TOKEN_ACCOUNT, token).commit();
            Log.w(TAG, action + " " + uri);
        }
        if(token.length() == 0)
            getToken();
        else {
            final GalleryAdapter galleryAdapter = new GalleryAdapter(this, token);
            RecyclerView recyclerView = findViewById(R.id.rv_image);
            recyclerView.setAdapter(galleryAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setHasFixedSize(true);
            Log.w(TAG, "token = " + token);
            model.getPhotoResponses(token).observe(this, new Observer<List<PhotoResponse>>() {
                @Override
                public void onChanged(@Nullable List<PhotoResponse> photoResponses) {
                    galleryAdapter.setData(photoResponses);
                    if(photoResponses != null){
                        for(PhotoResponse elem : photoResponses){
                            Log.w(TAG, elem.toString());
                        }
                        setOnHandle();
                    }
                    else
                        Log.w(TAG, "onChanged get null object");
                }
            });
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void getToken(){
        String url = "https://oauth.yandex.ru/authorize?" +
                "response_type=token" +
                "&client_id=21c529ce43f3404f88ac68dfc8faa8f9";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void checkAndRequestPermission(String[] permission){
        for(String perm : permission) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, perm)){

                } else
                    ActivityCompat.requestPermissions(this, new String[]{perm}, 1001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.w(TAG, "Permission (" + permissions[0] + ") is granted");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG, "(requestCode, resultCode) = " + "(" + requestCode + ", " + resultCode + ")");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setOnHandle() {
        List<PhotoResponse> list = model.getPhotoResponses().getValue();
        if(list == null)
            return;
        List<String> link = new ArrayList<>();
        for(PhotoResponse photo : list){
            link.add(photo.path.split(":/")[1]);
        }
        final List<String> urlLinks = new ArrayList<>();
        final int sizeOfList = list.size();
        final MediatorLiveData<String> mediator = model.getLinkImage(link);
        mediator.observeForever(new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                urlLinks.add(s);
                if(urlLinks.size() == sizeOfList)
                    mediator.removeObserver(this);
            }
        });
        // уведомляем адаптор об изменении данных и используем пикасо, чтоб загрузить картинку просто используя полученный url

        for(String s : urlLinks){
            Log.w(TAG + "Mediator", s);
        }
    }
}
