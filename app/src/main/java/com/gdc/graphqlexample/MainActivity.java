package com.gdc.graphqlexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.exception.ApolloException;
import com.gdc.graphql.FeedResultQuery;
import com.gdc.graphqlexample.adapter.CharacterAdapter;
import com.gdc.graphqlexample.data.ApiClient;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import okhttp3.OkHttpClient;


public class MainActivity extends AppCompatActivity implements CharacterAdapter.ClickListener {
    private static final String TAG = "_MainActivity";


    private RecyclerView recyclerView;

    private CharacterAdapter adapter;
    FeedResultQuery.Characters data = null;


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
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        ApiClient.getClient().query(
                FeedResultQuery.builder()
                        .build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .enqueue(new ApolloCall.Callback<FeedResultQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<FeedResultQuery.Data> response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: " + response.toString());
                                data = response.data().characters();
                                adapter.setResult(data.results());
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });

    }

    @Override
    public void onClick(int position) {
        Toast.makeText(this, "Click " + data.results().get(position).status(), Toast.LENGTH_SHORT).show();
    }
}



















