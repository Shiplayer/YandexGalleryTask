package com.developer.java.yandex.yandexgallerytask.adapters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.developer.java.yandex.yandexgallerytask.R;
import com.developer.java.yandex.yandexgallerytask.entity.PhotoResponse;
import com.developer.java.yandex.yandexgallerytask.net.ImageLoader;
import com.developer.java.yandex.yandexgallerytask.util.ExecutorUtil;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anton on 25.04.2018.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private static final String TAG = GalleryAdapter.class.getSimpleName();
    List<PhotoResponse> urls = new ArrayList<>();
    private String auth;

    public GalleryAdapter(String auth){
        this.auth = auth;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_content, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.w(TAG, "loading image from " + urls.get(position).preview);
        ExecutorUtil.THREAD_POOL_EXECUTOR.execute(new ImageLoader(urls.get(position).path, new OnStateListener() {
            @Override
            public void shutdown() {
                Log.w(TAG, "process was interrupt");
            }

            @Override
            public void downloading() {
                Log.w(TAG, "downloading");
            }

            @Override
            public void downloaded() {
                Log.w(TAG, "downloaded");
            }

            @Override
            public void setResultIntoView(Bitmap bitmap) {
                holder.imageView.setImageBitmap(bitmap);
                Log.w(TAG, "inserting!");
            }
        }, auth));
        //Picasso.get().load(urls.get(position).preview).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }

    public void setData(List<PhotoResponse> data){
        Log.w(TAG, "setData");
        //Log.w(TAG, String.valueOf(data));
        urls = data;
        notifyDataSetChanged();
    }

    public void addData(List<PhotoResponse> data){
        urls.addAll(data);
        notifyItemRangeChanged(urls.size() - data.size() - 1, data.size());
    }

    public interface OnStateListener{
        public void shutdown();
        public void downloading();
        public void downloaded();
        public void setResultIntoView(Bitmap bitmap);
    }
}
