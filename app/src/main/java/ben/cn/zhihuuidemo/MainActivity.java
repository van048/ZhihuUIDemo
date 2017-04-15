package ben.cn.zhihuuidemo;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final long ANIM_DURATION = 300;
    private int mHeaderViewHeight;
    private View mHeaderView;
    private ValueAnimator mHeaderViewAnimator;
    private boolean mLastAnimDirectionIn; // record whether the last animation's direction is in
    private int mNewTop; // record the changing top during animation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();

        mHeaderView = findViewById(R.id.main_head);
        mHeaderViewHeight = (int) getResources().getDimension(R.dimen.main_header_height);

        setupAnimator();
        // the first anim must be out, so last must be in
        mLastAnimDirectionIn = true;
    }

    private void setupAnimator() {
        mNewTop = 0;
        mHeaderViewAnimator = ObjectAnimator.ofInt(mNewTop, -mHeaderViewHeight);
        mHeaderViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mNewTop = (int) animation.getAnimatedValue();
                mHeaderView.layout(
                        mHeaderView.getLeft(),
                        mNewTop,
                        mHeaderView.getRight(),
                        mNewTop + mHeaderViewHeight);
            }
        });
        mHeaderViewAnimator.setDuration(ANIM_DURATION);
    }

    private void setupRecyclerView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MyAdapter());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            static final int ANIM_OUT_THRESHOLD = 50;
            static final int ANIM_IN_THRESHOLD = -ANIM_OUT_THRESHOLD / 2;
            int mCirculatingDy;
            boolean mCirculatingDown;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // must keep moving in the same direction to circulate
                // update the circulating dy
                if (dy > 0) {
                    // scrolling down
                    if (mCirculatingDown) {
                        mCirculatingDy += dy;
                    } else {
                        // up changed to down, restart circulating
                        mCirculatingDy = dy;
                    }
                    mCirculatingDown = true;
                } else if (dy < 0) {
                    if (mCirculatingDown) {
                        mCirculatingDy = dy;
                    } else {
                        mCirculatingDy += dy;
                    }
                    mCirculatingDown = false;
                }

                if (mCirculatingDy >= ANIM_OUT_THRESHOLD) {
                    tryStartAnimOut();
                    mCirculatingDy = 0; // reset
                } else if (mCirculatingDy < ANIM_IN_THRESHOLD) {
                    tryStartAnimIn();
                    mCirculatingDy = 0;
                }
            }
        });
    }

    private void tryStartAnimIn() {
        // avoid repeat
        if (mLastAnimDirectionIn) return;
        mLastAnimDirectionIn = true;
        if (mHeaderViewAnimator != null) {
            mHeaderViewAnimator.cancel();
            mHeaderViewAnimator.setIntValues(mNewTop, 0);
            mHeaderViewAnimator.start();
        }
    }

    private void tryStartAnimOut() {
        // avoid repeat
        if (!mLastAnimDirectionIn) return;
        mLastAnimDirectionIn = false;
        if (mHeaderViewAnimator != null) {
            mHeaderViewAnimator.cancel();
            mHeaderViewAnimator.setIntValues(mNewTop, -mHeaderViewHeight);
            mHeaderViewAnimator.start();
        }
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
