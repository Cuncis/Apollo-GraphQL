package com.gdc.graphqlexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.gdc.graphqlexample.adapter.CharacterAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "_MainActivity";

    private ApolloClient apolloClient;
    private static final String BASE_URL = "https://rickandmortyapi.com/graphql/";

    private RecyclerView recyclerView;

    private CharacterAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);

        adapter = new CharacterAdapter(this);

        initRecyclerView();
        getData();

    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
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
                        FeedResultQuery.Characters data = response.data().characters();

                        adapter.setResult(data.results());

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });

    }
}
