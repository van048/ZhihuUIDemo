package ben.cn.zhihuuidemo;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

abstract class BaseScrollingBehavior extends CoordinatorLayout.Behavior {
    private static final int ANIM_OUT_THRESHOLD = 50;
    private static final int ANIM_IN_THRESHOLD = -ANIM_OUT_THRESHOLD;
    private int mCirculatingDy;
    private boolean mCirculatingDown;
    private boolean mLastScrollDirectionUp; // record whether the last scroll direction is up

    BaseScrollingBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        mCirculatingDy = 0;
        mCirculatingDown = true;
        // the first anim must be out, so last must be in
        mLastScrollDirectionUp = true;

        setupAnimator();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
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
            // avoid repeat
            if (mLastScrollDirectionUp) {
                mLastScrollDirectionUp = false;
                startAnimDown();
                mCirculatingDy = 0; // reset
            }
        } else if (mCirculatingDy < ANIM_IN_THRESHOLD) {
            // avoid repeat
            if (!mLastScrollDirectionUp) {
                mLastScrollDirectionUp = true;
                startAnimUp();
                mCirculatingDy = 0;
            }
        }
    }

    abstract void startAnimUp();

    abstract void startAnimDown();

    abstract void setupAnimator();
}
