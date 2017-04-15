package ben.cn.zhihuuidemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

public class ScrollingBottomBehavior extends BaseScrollingBehavior {
    private View mBottomView;
    private final int mBottomViewHeight;
    private ValueAnimator mBottomViewAnimator;
    private int mNewTop; // record the changing top during animation
    private int mOriginalTop;

    public ScrollingBottomBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupAnimator();
        mBottomViewHeight = (int) context.getResources().getDimension(R.dimen.main_bottom_height);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (mBottomView == null) {
            mBottomView = child;
            mNewTop = mOriginalTop = mBottomView.getTop();
        }
    }

    @Override
    public void startAnimUp() {
        if (mBottomViewAnimator != null) {
            mBottomViewAnimator.cancel();
            mBottomViewAnimator.setIntValues(mNewTop, mOriginalTop);
            mBottomViewAnimator.start();
        }
    }

    @Override
    public void startAnimDown() {
        if (mBottomViewAnimator != null) {
            mBottomViewAnimator.cancel();
            mBottomViewAnimator.setIntValues(mNewTop, mOriginalTop + mBottomViewHeight);
            mBottomViewAnimator.start();
        }
    }

    @Override
    void setupAnimator() {
        mBottomViewAnimator = ValueAnimator.ofInt(0);
        mBottomViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mNewTop = (int) animation.getAnimatedValue();
                if (mBottomView != null)
                    mBottomView.layout(
                            mBottomView.getLeft(),
                            mNewTop,
                            mBottomView.getRight(),
                            mNewTop + mBottomViewHeight);
            }
        });
        mBottomViewAnimator.setDuration(ANIM_DURATION);
    }
}
