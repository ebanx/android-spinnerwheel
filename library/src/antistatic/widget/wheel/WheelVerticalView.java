/*
 * android-spinnerwheel
 * https://github.com/ai212983/android-spinnerwheel
 *
 * based on
 *
 * Android Wheel Control.
 * https://code.google.com/p/android-wheel/
 *
 * Copyright 2011 Yuri Kanivets
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package antistatic.widget.wheel;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Spinner wheel horizontal view.
 *
 * @author Yuri Kanivets
 * @author Dimitri Fedorov
 *
 */
public class WheelVerticalView extends AbstractWheelView {

    private static int itemID = -1;

    @SuppressWarnings("unused")
    private final String LOG_TAG = WheelVerticalView.class.getName() + " #" + (++itemID);

    
    // -------------- items above should be moved to AbstractWheel


    // Item height
    private int itemHeight = 0;


    public WheelVerticalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public WheelVerticalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor
     */
    public WheelVerticalView(Context context) {
        super(context);
    }
    
    protected WheelScroller createScroller(WheelScroller.ScrollingListener scrollingListener) {
        return new WheelVerticalScroller(getContext(), scrollingListener);
    }

    @Override
    protected void initData(Context context) {
        super.initData(context);

        int[] dividerColors = new int[] { 0xFF111111, 0xFF222222, 0xFF111111 };
        mSelectionDivider = new  GradientDrawable(Orientation.LEFT_RIGHT, dividerColors);
        /*
        mSelectionDivider = attributesArray.getDrawable(R.styleable.NumberPicker_selectionDivider);
        int defSelectionDividerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT,
                getResources().getDisplayMetrics());
        */
        mSelectionDividerWidth = 1;
        //mSelectionDividerWidth = attributesArray.getDimensionPixelSize(
        //        R.styleable.NumberPicker_selectionDividerHeight, defSelectionDividerHeight);

    }

    //
    // Methods related to scroll starting up/finishing
    //

    @Override
    protected void onScrollTouched() {
        mDimSelectorWheelAnimator.cancel();
        mDimSeparatorsAnimator.cancel();
        setSelectorPaintCoeff(1);
        setSeparatorsPaintAlpha(SEPARATORS_BRIGHT_ALPHA);
    }

    @Override
    protected void onScrollTouchedUp() {
        fadeSelectorWheel(750);
        lightSeparators(750);
    }

    @Override
    protected void onScrollFinished() {
        fadeSelectorWheel(500);
        lightSeparators(500);
    }


    @Override
    public void setSelectorPaintCoeff(float coeff) {

        int h = getMeasuredHeight();
        int ih = getItemDimension();

        float p1 = (1 - ih/(float) h)/2;
        float p2 = (1 + ih/(float) h)/2;
        float p3 = (1 - ih*3/(float) h)/2;
        float p4 = (1 + ih*3/(float) h)/2;

        float s = 255 * p3/p1;
        float z = SELECTOR_WHEEL_DIM_ALPHA * (1 - coeff);

        float c3f = s * coeff ; // here goes some optimized stuff
        float c2f = z + c3f;
        float c1f = z + 255 * coeff;

        int c1 = Math.round( c1f ) << 24;
        int c2 = Math.round( c2f ) << 24;
        int c3 = Math.round( c3f ) << 24;

        int[] colors =      {0, c3, c2, c1, 0xff000000, 0xff000000, c1, c2, c3, 0};
        float[] positions = {0, p3, p3, p1,     p1,         p2,     p2, p4, p4, 1};

        LinearGradient shader = new LinearGradient(0, 0, 0, h, colors, positions, Shader.TileMode.CLAMP);
        mSelectorWheelPaint.setShader(shader);

        invalidate();
    }

    /**
     * Sets the <code>alpha</code> of the {@link Paint} for drawing separators
     * widget.
     */
    @SuppressWarnings("unused")  // Called via reflection
    public void setSeparatorsPaintAlpha(int alpha) {
        mSeparatorsPaint.setAlpha(alpha);
        invalidate();
    }

    /**
     * Fade the selector widget via an animation.
     *
     * @param animationDuration The duration of the animation.
     */
    private void fadeSelectorWheel(long animationDuration) {
        mDimSelectorWheelAnimator.setDuration(animationDuration);
        mDimSelectorWheelAnimator.start();
    }

    /**
     * Fade the selector widget via an animation.
     *
     * @param animationDuration The duration of the animation.
     */
    private void lightSeparators(long animationDuration) {
        mDimSeparatorsAnimator.setDuration(animationDuration);
        mDimSeparatorsAnimator.start();
    }

    /**
     * Calculates desired height for layout
     *
     * @param layout the source layout
     * @return the desired layout height
     */
    private int getDesiredHeight(LinearLayout layout) {
        if (layout != null && layout.getChildAt(0) != null) {
            itemHeight = layout.getChildAt(0).getMeasuredHeight();
        }

        int desired =  - itemHeight * ITEM_OFFSET_PERCENT / 100;
        return Math.max(itemHeight * visibleItems, getSuggestedMinimumHeight());
    }

    @Override
    protected int getBaseDimension() {
        return getHeight();
    }


    /**
     * Returns height of widget item
     * @return the item height
     */
    @Override
    protected int getItemDimension() {
        if (itemHeight != 0) {
            return itemHeight;
        }

        if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
            itemHeight = itemsLayout.getChildAt(0).getHeight();
            return itemHeight;
        }

        return getBaseDimension() / visibleItems;
    }

    /**
     * Calculates control width and creates text layouts
     * @param widthSize the input layout width
     * @param mode the layout mode
     * @return the calculated control width
     */
    private int calculateLayoutWidth(int widthSize, int mode) {
        // TODO: make it static
        itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        itemsLayout.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int width = itemsLayout.getMeasuredWidth();

        if (mode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width += 2 * PADDING;

            // Check against our minimum width
            width = Math.max(width, getSuggestedMinimumWidth());

            if (mode == MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize;
            }
        }

        itemsLayout.measure(
                MeasureSpec.makeMeasureSpec(width - 2 * PADDING, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        );

        return width;
    }

    @Override
    protected void measureLayout() {

        itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        itemsLayout.measure(
                MeasureSpec.makeMeasureSpec(getWidth() - 2 * PADDING, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        buildViewForMeasuring();

        int width = calculateLayoutWidth(widthSize, widthMode);

        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getDesiredHeight(itemsLayout);

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }


    @Override
    protected void doItemsLayout(int width, int height) {
        itemsLayout.layout(0, 0, width - 2 * PADDING, height);
    }


    @Override
    protected void drawItems(Canvas canvas) {
        canvas.save();
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int ih = getItemDimension();

        // resetting intermediate bitmap and canvas
        mSpinBitmap.eraseColor(0);
        Canvas c = new Canvas(mSpinBitmap);
        Canvas cSpin = new Canvas(mSpinBitmap);

        int top = (currentItem - firstItem) * ih + (ih - getHeight()) / 2;
        c.translate(PADDING, - top + scrollingOffset);
        itemsLayout.draw(c);

        // ----------------------------

        mSeparatorsBitmap.eraseColor(0);
        Canvas cSeparators = new Canvas(mSeparatorsBitmap);

        if (mSelectionDivider != null) {
            // draw the top divider
            int topOfTopDivider =
                    (getHeight() - mSelectorElementHeight - mSelectionDividerWidth) / 2;
            int bottomOfTopDivider = topOfTopDivider + mSelectionDividerWidth;
            mSelectionDivider.setBounds(0, topOfTopDivider, getRight(), bottomOfTopDivider);
            mSelectionDivider.draw(cSeparators);

            // draw the bottom divider
            int topOfBottomDivider =  topOfTopDivider + mSelectorElementHeight;
            int bottomOfBottomDivider = bottomOfTopDivider + mSelectorElementHeight;
            mSelectionDivider.setBounds(0, topOfBottomDivider, getRight(), bottomOfBottomDivider);
            mSelectionDivider.draw(cSeparators);
        }
        // ----------------------------

        cSpin.drawRect(0, 0, w, h, mSelectorWheelPaint);
        cSeparators.drawRect(0, 0, w, h, mSeparatorsPaint);

        canvas.drawBitmap(mSpinBitmap, 0, 0, null);
        canvas.drawBitmap(mSeparatorsBitmap, 0, 0, null);
        canvas.restore();
    }

    /**
     * Creates item layouts if necessary
     */
    @Override
    protected void createItemsLayout() {
        if (itemsLayout == null) {
            itemsLayout = new LinearLayout(getContext());
            itemsLayout.setOrientation(LinearLayout.VERTICAL);
        }
    }
    
    @Override
    protected float getMotionEventPosition(MotionEvent event) {
        return event.getY();
    }

}