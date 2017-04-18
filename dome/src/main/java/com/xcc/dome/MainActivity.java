package com.xcc.dome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xcc.horizontalrefresh.HorizontalRefreshView;

public class MainActivity extends AppCompatActivity implements HorizontalRefreshView.OnHorizontalRefresh {
    private RecyclerView list;
    private HorizontalRefreshView refreshView;
    private MyAdpter adpter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshView = (HorizontalRefreshView) findViewById(R.id.refreshView);
        refreshView.setOnHorizontalRefresh(this);

        list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this, 0, false));
        adpter = new MyAdpter();
        list.setAdapter(adpter);

    }

    private int len = 3;

    public void OnRightRefresh(HorizontalRefreshView view) {
        len++;
        adpter.notifyDataSetChanged();
        Toast.makeText(this, "加载成功", 0).show();
    }

    public void OnLeftRefresh(HorizontalRefreshView view) {
        len += 2;
        adpter.notifyDataSetChanged();
        Toast.makeText(this, "加载成功", 0).show();
    }

    class MyAdpter extends RecyclerView.Adapter {
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_list, parent, false);
            return new RecyclerView.ViewHolder(inflate) {
            };
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        }

        public int getItemCount() {
            return len;
        }
    }
}