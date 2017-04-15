package ben.cn.zhihuuidemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

public class ScrollingFabBehavior extends BaseScrollingBehavior {
    private View mFabView;
    private int mFabViewHeight;
    private ValueAnimator mFabViewAnimator;
    private int mNewTop; // record the changing top during animation
    private int mOriginalTop;
    private int mDisplayHeight;

    public ScrollingFabBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupAnimator();
        mDisplayHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (mFabView == null) {
            mFabView = child;
            mNewTop = mOriginalTop = mFabView.getTop();
            mFabViewHeight = mFabView.getMeasuredHeight();
        }
    }

    @Override
    public void startAnimUp() {
        if (mFabViewAnimator != null) {
            mFabViewAnimator.cancel();
            mFabViewAnimator.setIntValues(mNewTop, mOriginalTop);
            mFabViewAnimator.start();
        }
    }

    @Override
    public void startAnimDown() {
        if (mFabViewAnimator != null) {
            mFabViewAnimator.cancel();
            mFabViewAnimator.setIntValues(mNewTop, mDisplayHeight + mFabViewHeight);
            mFabViewAnimator.start();
        }
    }

    @Override
    void setupAnimator() {
        mFabViewAnimator = ValueAnimator.ofInt(0);
        mFabViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mNewTop = (int) animation.getAnimatedValue();
                if (mFabView != null)
                    mFabView.layout(
                            mFabView.getLeft(),
                            mNewTop,
                            mFabView.getRight(),
                            mNewTop + mFabViewHeight);
            }
        });
        mFabViewAnimator.setDuration(ANIM_DURATION);
    }
}
