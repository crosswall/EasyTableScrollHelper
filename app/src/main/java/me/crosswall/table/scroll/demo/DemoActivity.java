package me.crosswall.table.scroll.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recycelerview.widget.TableScrollHelper;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_tab_scroll_list);

        RecyclerView recycler_view = findViewById(R.id.recycler_view);

        recycler_view.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ViewGroup header = findViewById(R.id.view_header_scroll_container);

        DemoAdapter adapter = new DemoAdapter();

        adapter.setData(createDemoDataList());

        new TableScrollHelper().attachToRecyclerView(recycler_view, adapter, header);
    }

    private List<DemoData> createDemoDataList() {
        List<DemoData> dataList = new ArrayList();

        for (int i = 0; i < 160; i++) {
            DemoData data = new DemoData();
            data.name = "神秘代码" + i;
            data.expanded = i % 3 == 0;
            dataList.add(data);
        }
        return dataList;
    }


    static class DemoData {
        public boolean expanded;
        public String name;

    }


    static class DemoAdapter extends RecyclerView.Adapter {


        private List<DemoData> mData = new ArrayList();


        public void setData(List<DemoData> data) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DemoVH(View.inflate(parent.getContext(), R.layout.demo_item_view_tab_scroll, null));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            DemoData data = mData.get(position);
            ((TextView)holder.itemView.findViewById(R.id.tv_name)).setText(data.name);

            View expendView = holder.itemView.findViewById(R.id.view_other);

            if (data.expanded){
                expendView.setVisibility(View.VISIBLE);
            }else{
                expendView.setVisibility(View.GONE);
            }

            expendView.setOnClickListener(v -> {
                mData.get(position).expanded = false;
                expendView.setVisibility(View.GONE);
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    static class DemoVH extends RecyclerView.ViewHolder {

        public DemoVH(@NonNull View itemView) {
            super(itemView);
        }
    }

}