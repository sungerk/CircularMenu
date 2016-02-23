package org.sunger.net.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import sunger.net.org.circularmenu.R;

/**
 * Created by sunger on 16/2/16.
 */
public class CircularMenu extends ViewGroup {
    private static final int INDEX_CUSTOM = 0;
    private static final int INDEX_CENTER = 1;
    private static final int NONE_ITEM = -1;
    private static final int CENTER_ITEM = Integer.MAX_VALUE;

    private int selectedItem = NONE_ITEM;
    //圆心坐标
    private int centerX;
    private int centerY;

    private int itemCount;

    //大圆半径
    private float radius;

    private float innerRadius;
    //线条宽度
    private float lineWidth;

    //内圆线宽度
    private float radiusLineWidth;

    private int backgroundColor;


    private int pressedColor;
    private int itemColor;

    //起始角度，默认为0度
    private int startAngle = 0;

    private OnItemClickListener onItemClickListener;


    public CircularMenu(Context context) {
        this(context, null);
    }

    public CircularMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PanView);
        if (typedArray == null) {
            addView(new CustomView(context), INDEX_CUSTOM);
            return;
        }
        itemCount = typedArray.getInteger(R.styleable.PanView_itemCount, 0);
        innerRadius = typedArray.getDimension(R.styleable.PanView_innerRadius, 0);
        radius = typedArray.getDimension(R.styleable.PanView_radius, 0);
        backgroundColor = typedArray.getColor(R.styleable.PanView_backgroundColor, 0);
        itemColor = typedArray.getColor(R.styleable.PanView_itemColor, 0);
        pressedColor = typedArray.getColor(R.styleable.PanView_pressedColor, 0);
        lineWidth = typedArray.getDimension(R.styleable.PanView_lineWidth, 20);
        radiusLineWidth = typedArray.getDimension(R.styleable.PanView_radiusLineWidth, 20);
        startAngle = typedArray.getInteger(R.styleable.PanView_startAngle, 0);
        addView(new CustomView(context), INDEX_CUSTOM);
        int centerViewLayoutId = typedArray.getResourceId(R.styleable.PanView_centerViewLayout, 0);
        if (centerViewLayoutId != 0) {
            setCenterView(centerViewLayoutId);
        }
    }


    private void onCustomLayout(int r, int b) {
        getChildAt(INDEX_CUSTOM).layout(0, 0, r * 2, b * 2);
    }


    private void refreshView() {
        requestLayout();
        getChildAt(INDEX_CUSTOM).invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int halfWidth = (r - l) / 2;
        int halfHeight = (b - t) / 2;
        onCustomLayout(halfWidth, halfHeight);
        int childWidth = getChildAt(INDEX_CENTER).getMeasuredWidth();
        int childHeight = getChildAt(1).getMeasuredHeight();
        getChildAt(INDEX_CENTER).layout(halfWidth - childWidth / 2, halfHeight - childHeight / 2, halfWidth + childWidth / 2, halfHeight + childHeight / 2);
        double startAnl = startAngle * Math.PI / 180;
        double avgAnl = 2 * Math.PI / itemCount;
        if (getChildCount() < 2)
            return;
        for (int i = 0; i < getChildCount() - 2; i++) {
            View childView = getChildAt(i + 2);
            int width = childView.getMeasuredWidth();
            int height = childView.getMeasuredHeight();
            double childRadius = radius - (radius - innerRadius - radiusLineWidth) / 2;
            int x = (int) (halfWidth + childRadius * Math.cos(avgAnl * (itemCount - i) - startAnl - avgAnl / 2));
            int y = (int) (halfHeight + childRadius * Math.sin(avgAnl * (itemCount - i) - startAnl - avgAnl / 2));
            childView.layout(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        }
    }


    private float getRectangularCoordinatesX(MotionEvent event) {
        return event.getX() - centerX;
    }

    private float getRectangularCoordinatesY(MotionEvent event) {
        float y = event.getY();
        if (y > centerY) {
            y = -y + centerY;
        } else {
            y = Math.abs(y - centerY);
        }
        return y;
    }


    private void calculateEventPosition(float x, float y) {
        double startAnl = startAngle * Math.PI / 180;
        double r = Math.sqrt(x * x + y * y);
        if (r <= innerRadius) {
            selectedItem = CENTER_ITEM;
        } else if (r <= radius && r >= innerRadius + radiusLineWidth) {
            double currentAngle = Math.atan2(y, x);
            if (y < 0) {
                currentAngle += 2 * Math.PI;
            }
            double avgAngle = 2 * Math.PI / itemCount;
            if (currentAngle > startAnl) {
                currentAngle -= startAnl;
            } else {
                currentAngle += 2 * Math.PI - startAnl;
            }
            //根据象限计算
            selectedItem = (int) (currentAngle / avgAngle);
        } else {
            selectedItem = NONE_ITEM;
        }
    }

    private boolean isItem() {
        return selectedItem >= 0 && selectedItem < itemCount;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = getRectangularCoordinatesX(event);
        float y = getRectangularCoordinatesY(event);
        calculateEventPosition(x, y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (selectedItem == CENTER_ITEM) {
                    getChildAt(1).setPressed(true);
                } else if (isItem()) {
                    getChildAt(selectedItem + 2).setPressed(true);
                }
                refreshView();
                break;
            case MotionEvent.ACTION_UP:
                if (selectedItem == CENTER_ITEM) {
                    getChildAt(1).setPressed(false);
                    if (onItemClickListener != null) {
                        onItemClickListener.onCenterClick();
                    }
                } else if (isItem()) {
                    getChildAt(selectedItem + 2).setPressed(false);
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(selectedItem);
                    }
                }
                selectedItem = NONE_ITEM;
                refreshView();
                break;
            case MotionEvent.ACTION_SCROLL:
            case MotionEvent.ACTION_CANCEL:
                selectedItem = NONE_ITEM;
                refreshView();
                break;
        }
        return true;
    }

    private int measureLength(int measureSpec) {
        int specModel = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if (specModel == MeasureSpec.EXACTLY || specModel == MeasureSpec.AT_MOST) {
            result = specSize;
        }
        return result;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measureLength(widthMeasureSpec);
        int measuredHeight = measureLength(heightMeasureSpec);
        centerY = measuredHeight / 2;
        centerX = measuredWidth / 2;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }


    public int getPressedColor() {
        return pressedColor;

    }

    public void setPressedColor(int pressedColor) {
        this.pressedColor = pressedColor;
        refreshView();
    }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
        refreshView();
    }

    public int getItemColor() {
        return itemColor;
    }

    public void setItemColor(int itemColor) {
        this.itemColor = itemColor;
        refreshView();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        refreshView();
    }

    public float getRadiusLineWidth() {
        return radiusLineWidth;
    }

    public void setRadiusLineWidth(float radiusLineWidth) {
        this.radiusLineWidth = radiusLineWidth;
        refreshView();
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        refreshView();
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(float innerRadius) {
        this.innerRadius = innerRadius;
        refreshView();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        refreshView();
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
        refreshView();
    }


    public void setCenterView(View view) {
        if (getChildAt(INDEX_CENTER) != null)
            removeViewAt(INDEX_CENTER);
        addView(view, INDEX_CENTER);
    }

    public void setCenterView(int centerViewId) {
        setCenterView(LayoutInflater.from(getContext()).inflate(centerViewId, this, false));
    }

    public void setAdapter(BaseAdapter adapter) {
        for (int i = 0; i < adapter.getCount(); i++) {
            if (getChildAt(i + 2) != null)
                removeView(getChildAt(i + 2));
            View view = adapter.getView(i, null, this);
            addView(view, i + 2);
        }
    }


    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onCenterClick();
    }

    private class CustomView extends View {

        public CustomView(Context context) {
            super(context);
        }

        private void drawBackground(Canvas canvas) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(itemColor);
            canvas.drawColor(backgroundColor);
            canvas.drawCircle(centerX, centerY, radius, paint);
        }

        private void drawArc(Canvas canvas, double avgAnl) {
            if (selectedItem >= 0 && selectedItem < itemCount) {
                Paint paint = new Paint();
                paint.setColor(pressedColor);
                paint.setStyle(Paint.Style.STROKE);
                float anlWidth = radius - innerRadius - radiusLineWidth;
                paint.setStrokeWidth(anlWidth + 1);
                paint.setAntiAlias(true);
                float anl = Float.parseFloat(avgAnl * 180 / Math.PI + "");
                float anl2 = Float.parseFloat((itemCount - selectedItem - 1) * avgAnl * 180 / Math.PI - startAngle + "");
                float tx = centerX - radius;
                float ty = centerY - radius;
                canvas.drawArc(new RectF(anlWidth / 2 + tx, anlWidth / 2 + ty, centerX + radius - anlWidth / 2, centerY + radius - anlWidth / 2), anl2, anl, false, paint);
            }
        }

        private void drawLine(Canvas canvas, double startAnl, double avgAnl) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(backgroundColor);
            paint.setStrokeWidth(lineWidth);
            for (int i = 0; i < itemCount; i++) {
                float stopX = (float) (centerX + (radius + 2) * Math.cos(avgAnl * i - startAnl));
                float stopY = (float) (centerY + (radius + 2) * Math.sin(avgAnl * i - startAnl));
                canvas.drawLine(centerX, centerY, stopX, stopY, paint);
            }
        }

        private void drawCenterCircle(Canvas canvas) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(1);
            paint.setColor(backgroundColor);
            canvas.drawCircle(centerX, centerY, innerRadius + radiusLineWidth, paint);
            if (selectedItem == CENTER_ITEM) {
                paint.setColor(pressedColor);
            } else {
                paint.setColor(itemColor);
            }
            canvas.drawCircle(centerX, centerY, innerRadius, paint);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            double startAnl = startAngle * Math.PI / 180;
            double avgAnl = 2 * Math.PI / itemCount;
            drawBackground(canvas);
            drawArc(canvas, avgAnl);
            drawLine(canvas, startAnl, avgAnl);
            drawCenterCircle(canvas);
        }

    }
}
