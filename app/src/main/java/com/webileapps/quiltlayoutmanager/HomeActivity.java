package com.webileapps.quiltlayoutmanager;

/**
 * Created by sairam on 17/2/17.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.webileapps.quiltlayoutmanager.adapter.SampleAdapter;
import com.webileapps.quiltlayoutmanager.layout.QuiltLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity{

    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SampleAdapter adapter = new SampleAdapter(this);
        QuiltLayoutManager quiltLayoutManager = new QuiltLayoutManager();
        recyclerView = (RecyclerView)findViewById(R.id.results_recyclerview);
        recyclerView.setLayoutManager(quiltLayoutManager);
        recyclerView.setAdapter(adapter);
        List<String> items = new ArrayList<>();
        List<Integer> priotities = new ArrayList<>();
        for(int i=1;i<=60;i++) {
            items.add("Item " + i);
        }
        priotities.add(9);
        priotities.add(9);
        priotities.add(10);
        priotities.add(2);
        priotities.add(3);
        priotities.add(5);
        priotities.add(7);
        priotities.add(8);
        priotities.add(2);
        priotities.add(6);
        priotities.add(7);
        priotities.add(3);
        priotities.add(9);
        priotities.add(6);
        priotities.add(2);
        priotities.add(2);
        priotities.add(3);
        priotities.add(5);
        priotities.add(7);
        priotities.add(8);
        priotities.add(2);
        priotities.add(6);
        priotities.add(5);
        priotities.add(7);
        priotities.add(1);
        priotities.add(9);
        priotities.add(3);
        priotities.add(9);
        priotities.add(1);
        priotities.add(1);
        priotities.add(9);
        priotities.add(6);
        priotities.add(2);
        priotities.add(2);
        priotities.add(3);
        priotities.add(5);
        priotities.add(7);
        priotities.add(8);
        priotities.add(2);
        priotities.add(6);
        priotities.add(1);
        priotities.add(1);
        priotities.add(9);
        priotities.add(6);
        priotities.add(2);
        priotities.add(2);
        priotities.add(3);
        priotities.add(5);
        priotities.add(7);
        priotities.add(8);
        priotities.add(2);
        priotities.add(6);
        priotities.add(7);
        priotities.add(3);
        priotities.add(5);
        priotities.add(7);
        priotities.add(1);
        priotities.add(9);
        priotities.add(3);
        priotities.add(9);
        priotities.add(1);
        priotities.add(1);
        priotities.add(1);
        priotities.add(1);
        adapter.setData(items,priotities);
    }
}
