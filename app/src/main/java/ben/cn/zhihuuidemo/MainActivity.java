package ben.cn.zhihuuidemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTextView;

            ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
