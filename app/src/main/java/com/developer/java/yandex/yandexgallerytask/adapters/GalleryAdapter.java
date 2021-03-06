package com.developer.java.yandex.yandexgallerytask.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.developer.java.yandex.yandexgallerytask.ImageViewActivity;
import com.developer.java.yandex.yandexgallerytask.R;
import com.developer.java.yandex.yandexgallerytask.entity.PhotoResponse;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anton on 25.04.2018.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private static final String TAG = GalleryAdapter.class.getSimpleName();
    List<PhotoResponse> urls = new ArrayList<>();
    private OkHttpClient client;
    private Picasso picasso;
    private Context context;

    public GalleryAdapter(Context context, final String auth){
        this.context = context;
        client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("Authorization", "OAuth " + auth).build();
                return chain.proceed(newRequest);
            }
        }).build();
        picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(client)).build();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_content, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
        picasso.load(urls.get(position).preview).fit().centerInside().into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("URL", urls.get(position).file);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            progressBar = itemView.findViewById(R.id.pb_adaptor_element);

        }
    }

    public void setData(List<PhotoResponse> data){
        Log.w(TAG, "setData");
        urls = data;
        notifyDataSetChanged();
    }

    public void addData(List<PhotoResponse> data){
        urls.addAll(data);
        notifyItemRangeChanged(urls.size() - data.size() - 1, data.size());
    }
}
