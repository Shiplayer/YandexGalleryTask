package com.developer.java.yandex.yandexgallerytask;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.developer.java.yandex.yandexgallerytask.net.YandexCommunication;

import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = GalleryActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = {Manifest.permission.GET_ACCOUNTS};
    private static final String TOKEN_ACCOUNT = "YandexGalleryToken";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);


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
            Log.w(TAG, "token = " + token);
            YandexCommunication.getInstance().getImages(token);
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

    public void getImages() {

    }
}
