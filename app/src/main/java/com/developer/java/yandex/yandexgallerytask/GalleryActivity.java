package com.developer.java.yandex.yandexgallerytask;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = GalleryActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = {Manifest.permission.GET_ACCOUNTS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        checkAndRequestPermission(PERMISSIONS);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if(intent != null) {
            String action = intent.getAction();
            Uri uri = intent.getData();

            Log.w(TAG, action + " " + uri);
        }

        final AccountManager manager = AccountManager.get(this);
        final Account[] accounts = manager.getAccountsByType("com.yandex.passport");
        final WebView webView = findViewById(R.id.wv_auth);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.w(TAG, request.getUrl().toString());

                return super.shouldOverrideUrlLoading(view, request);
            }
        });



        CharSequence[] listOfAccounts = new CharSequence[accounts.length];
        for(int i = 0; i < accounts.length; i++) {
            listOfAccounts[i] = accounts[i].name;
            Log.w(TAG, accounts[i].name + " " + accounts[i].type);
        }
        new AlertDialog.Builder(this).setTitle("Choose your account")
                .setItems(listOfAccounts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = "https://oauth.yandex.ru/authorize?" +
                                "response_type=token" +
                                "&client_id=21c529ce43f3404f88ac68dfc8faa8f9";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        //webView.setVisibility(View.VISIBLE);
                        //webView.loadUrl(url);

                        /*manager.getAuthToken(accounts[i], AccountManager.KEY_AUTHTOKEN, null, true, new AccountManagerCallback<Bundle>() {
                            @Override
                            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                                try {
                                    Bundle args = accountManagerFuture.getResult();
                                    for(String key : args.keySet())
                                    Log.w(TAG, "(key, value) = " + "(" + key + ", " + args.getString(key));
                                } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, null);*/
                    }
                }).create().show();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
}
