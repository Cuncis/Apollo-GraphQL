package com.gdc.graphqlexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.exception.ApolloException;
import com.gdc.graphql.FeedResultQuery;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "_MainActivity";

    private ApolloClient apolloClient;
    private static final String BASE_URL = "https://rickandmortyapi.com/graphql/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File file = new File(this.getFilesDir(), "Rick");
        long size = 1024 * 1024;

        DiskLruHttpCacheStore cacheStore = new DiskLruHttpCacheStore(file, size);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        apolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .httpCache(new ApolloHttpCache(cacheStore))
                .okHttpClient(okHttpClient)
                .build();

        apolloClient.query(FeedResultQuery.builder().build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .enqueue(new ApolloCall.Callback<FeedResultQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<FeedResultQuery.Data> response) {
                        for (int i = 0; i < response.data().characters().results().size(); i++) {
                            Log.d(TAG, "onResponse: " + response.data().characters().results().get(i).image());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });


    }
}
