package ben.cn.zhihuuidemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public class ScrollingHeaderBehavior extends CoordinatorLayout.Behavior {
    private static final long ANIM_DURATION = 200;
    private static final int ANIM_OUT_THRESHOLD = 50;
    private static final int ANIM_IN_THRESHOLD = -ANIM_OUT_THRESHOLD / 2;
    private final int mHeaderViewHeight;
    private int mCirculatingDy;
    private boolean mCirculatingDown;
    private ValueAnimator mHeaderViewAnimator;
    private boolean mLastScrollDirectionUp; // record whether the last scroll direction is up
    private int mNewTop; // record the changing top during animation
    private View mHeaderView;

    public ScrollingHeaderBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        mCirculatingDy = 0;
        mCirculatingDown = true;
        mHeaderViewHeight = (int) context.getResources().getDimension(R.dimen.main_header_height);

        setupAnimator();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        mHeaderView = child;
        // must keep moving in the same direction to circulate
        // update the circulating dy
        if (dyConsumed > 0) {
            // scrolling down
            if (mCirculatingDown) {
                mCirculatingDy += dyConsumed;
            } else {
                // up changed to down, restart circulating
                mCirculatingDy = dyConsumed;
            }
            mCirculatingDown = true;
        } else if (dyConsumed < 0) {
            if (mCirculatingDown) {
                mCirculatingDy = dyConsumed;
            } else {
                mCirculatingDy += dyConsumed;
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

    private void tryStartAnimIn() {
        // avoid repeat
        if (mLastScrollDirectionUp) return;
        mLastScrollDirectionUp = true;
        if (mHeaderViewAnimator != null) {
            mHeaderViewAnimator.cancel();
            mHeaderViewAnimator.setIntValues(mNewTop, 0);
            mHeaderViewAnimator.start();
        }
    }

    private void tryStartAnimOut() {
        // avoid repeat
        if (!mLastScrollDirectionUp) return;
        mLastScrollDirectionUp = false;
        if (mHeaderViewAnimator != null) {
            mHeaderViewAnimator.cancel();
            mHeaderViewAnimator.setIntValues(mNewTop, -mHeaderViewHeight);
            mHeaderViewAnimator.start();
        }
    }

    private void setupAnimator() {
        mNewTop = 0;
        // the first anim must be out, so last must be in
        mLastScrollDirectionUp = true;
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
