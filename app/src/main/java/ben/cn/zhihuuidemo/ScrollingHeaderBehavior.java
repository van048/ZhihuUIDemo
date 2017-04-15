package ben.cn.zhihuuidemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

public class ScrollingHeaderBehavior extends BaseScrollingBehavior {
    private View mHeaderView;
    private final int mHeaderViewHeight;
    private static final long ANIM_DURATION = 200;
    private ValueAnimator mHeaderViewAnimator;
    private int mNewTop; // record the changing top during animation

    public ScrollingHeaderBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupAnimator();
        mHeaderViewHeight = (int) context.getResources().getDimension(R.dimen.main_header_height);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        mHeaderView = child;
    }

    @Override
    public void startAnimUp() {
        if (mHeaderViewAnimator != null) {
            mHeaderViewAnimator.cancel();
            mHeaderViewAnimator.setIntValues(mNewTop, 0);
            mHeaderViewAnimator.start();
        }
    }

    @Override
    public void startAnimDown() {
        if (mHeaderViewAnimator != null) {
            mHeaderViewAnimator.cancel();
            mHeaderViewAnimator.setIntValues(mNewTop, -mHeaderViewHeight);
            mHeaderViewAnimator.start();
        }
    }

    @Override
    void setupAnimator() {
        mNewTop = 0;
        mHeaderViewAnimator = ValueAnimator.ofInt(mNewTop, -mHeaderViewHeight);
        mHeaderViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mNewTop = (int) animation.getAnimatedValue();
                if (mHeaderView != null)
                    mHeaderView.layout(
                            mHeaderView.getLeft(),
                            mNewTop,
                            mHeaderView.getRight(),
                            mNewTop + mHeaderViewHeight);
            }
        });
        mHeaderViewAnimator.setDuration(ANIM_DURATION);
    }
}
