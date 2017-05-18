package com.ebanx.wheel;

import android.util.Log;

@SuppressWarnings("PMD")
class WheelScrollingListener implements WheelScroller.ScrollingListener {

    private AbstractWheel abstractWheel;

    WheelScrollingListener(AbstractWheel abstractWheel) {
        this.abstractWheel = abstractWheel;
    }

    public void onStarted() {
        abstractWheel.mIsScrollingPerformed = true;
        abstractWheel.notifyScrollingListenersAboutStart();
        abstractWheel.onScrollStarted();
    }

    public void onTouch() {
        abstractWheel.onScrollTouched();
    }

    public void onTouchUp() {
        if (!abstractWheel.mIsScrollingPerformed) {
            abstractWheel.onScrollTouchedUp(); // if scrolling IS performed, whe should use onFinished instead
        }
    }

    @Override
    public void onFling(int direction) {
        Log.d("Fling", "Passed here! Direction: " + direction);
        abstractWheel.mLastTempDirection = direction;
    }

    @Override
    public void onScroll(int distance) {
        abstractWheel.doScroll(distance);

        int dimension = abstractWheel.getMaxOverScrollDimension();
        if (abstractWheel.mScrollingOffset > dimension) {
            abstractWheel.mScrollingOffset = dimension;
            abstractWheel.mScroller.stopScrolling();
        } else if (abstractWheel.mScrollingOffset < -dimension) {
            abstractWheel.mScrollingOffset = -dimension;
            abstractWheel.mScroller.stopScrolling();
        }
    }

    public void onFinished() {
        if (abstractWheel.mIsScrollingPerformed) {
            abstractWheel.notifyScrollingListenersAboutEnd();
            abstractWheel.mIsScrollingPerformed = false;
            abstractWheel.onScrollFinished();
        }

        abstractWheel.mScrollingOffset = 0;
        abstractWheel.invalidate();
    }

    public void onJustify() {
        if (Math.abs(abstractWheel.mScrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
            boolean handled = false;
            final int scrollOffsetDirection = abstractWheel.mScrollingOffset;

            // if justify direction is not fling direction, try make it be
            if (scrollOffsetDirection * abstractWheel.mLastTempDirection < 0) {
                if (abstractWheel.mLastTempDirection == WheelScroller.SCROLL_DIRECTION_UP) {
                    if (abstractWheel.isValidItemIndex(abstractWheel.mCurrentItemIdx + 1)) {
                        abstractWheel.mScroller.scroll(abstractWheel.mScrollingOffset + abstractWheel.getItemDimension(), 0);
                        handled = true;
                    }
                } else {
                    if (abstractWheel.isValidItemIndex(abstractWheel.mCurrentItemIdx - 1)) {
                        abstractWheel.mScroller.scroll(abstractWheel.mScrollingOffset - abstractWheel.getItemDimension(), 0);
                        handled = true;
                    }
                }
            }

            // default justify
            if (!handled) {
                abstractWheel.mScroller.scroll(abstractWheel.mScrollingOffset, 0);
            }
        }
    }
}
