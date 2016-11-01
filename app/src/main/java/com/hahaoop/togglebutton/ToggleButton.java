package com.hahaoop.togglebutton;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hahaoop
 */
public class ToggleButton extends View{private static final int DEFAULT_WIDTH = 70;
    private static final int DEFAULT_HEIGHT = 40;
    private static final int CLOSED = 1;

    private Paint circlePaint;
    private Paint backgroundPaint;
    private RectF rectF;

    private OnToogleChangeListener listener;

    private int closeBackgroundColor;
    private int openBackgroundColor;
    private int circleColor;

    private float arcRadius;
    private float circleRadius;

    private int mWidth;
    private int mHeight;

    private float density;
    private Point mPoint;

    private boolean isComplete = true; //is Animate completed
    private boolean isClosed;

    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ToggleButton,0,0);
        closeBackgroundColor = array.getColor(R.styleable.ToggleButton_toggleCloseBackgroundColor, Color.LTGRAY);
        openBackgroundColor = array.getColor(R.styleable.ToggleButton_toggleOpenBackgroundColor,Color.GREEN);
        circleColor = array.getColor(R.styleable.ToggleButton_toggleCircleColor,Color.WHITE);
        int state = array.getInt(R.styleable.ToggleButton_toggleState,CLOSED);
        array.recycle();
        isClosed = state==CLOSED;
        initPaint();
        initListener();
    }

    private void initPaint(){
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        if(isClosed){
            backgroundPaint.setColor(closeBackgroundColor);
        } else {
            backgroundPaint.setColor(openBackgroundColor);
        }
    }

    private void initListener(){
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(isComplete){
                        startAnimate();
                        isComplete = !isComplete;
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        switch (widthMode){
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                width = (int) (DEFAULT_WIDTH*density);
                break;
        }
        switch (heightMode){
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                height = (int) (DEFAULT_HEIGHT*density);
                break;
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getHeight();
        mWidth = getWidth();
        arcRadius = mHeight/2;
        //prevent the circle cover border, circle radius -1dp
        circleRadius = arcRadius-density;
        rectF = new RectF(0,0,mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mPoint==null){
            if(isClosed){
                mPoint = new Point(arcRadius,mHeight/2);
            } else {
                mPoint = new Point(mWidth-arcRadius,mHeight/2);
            }
        }
        canvas.drawRoundRect(rectF,arcRadius,arcRadius,backgroundPaint);
        canvas.drawCircle(mPoint.getX(),mHeight/2,circleRadius,circlePaint);
    }

    public void setOnToogleChangeListener(OnToogleChangeListener listener){
        if(listener!=null){
            this.listener = listener;
        }
    }

    public void setBackgroundPaintColor(int color){
        backgroundPaint.setColor(color);
        invalidate();
    }

    public boolean isOpen(){
        return !isClosed;
    }

    private void startAnimate(){
        Point startPoint = mPoint;
        Point endPoint;
        if(isClosed){
            endPoint = new Point(mWidth-arcRadius,mHeight/2);
            setBackgroundPaintColor(openBackgroundColor);//when the animation beginning,set background.
        } else{
            endPoint = new Point(arcRadius,mHeight/2);
            setBackgroundPaintColor(closeBackgroundColor);//when the animation beginning,set background.
        }
        ValueAnimator animator = ObjectAnimator.ofObject(new PointEvaluator(),startPoint,endPoint);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                mPoint = (Point) animation.getAnimatedValue();
                if(fraction==1){
                    isComplete = !isComplete;
                    isClosed = !isClosed;
                    if(listener!=null){
                        listener.onChange(ToggleButton.this);
                    }
                }
                invalidate();
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    public interface OnToogleChangeListener{
        void onChange(ToggleButton view);
    }

    private class PointEvaluator implements TypeEvaluator<Point> {

        @Override
        public Point evaluate(float fraction, Point startPoint, Point endPoint) {
            float x = startPoint.getX()+fraction*(endPoint.getX()-startPoint.getX());
            return new Point(x,mHeight/2);
        }
    }

    private class Point{
        private float x;
        private float y;

        public Point(float x,float y){
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        boolean[] status = new boolean[1];
        status[0]=isClosed;
        return new SavedState(superState,status);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        isClosed = ss.status[0];
        if(isClosed){
            backgroundPaint.setColor(closeBackgroundColor);
        } else {
            backgroundPaint.setColor(openBackgroundColor);
        }
        invalidate();
    }

    static class SavedState extends BaseSavedState{

        private boolean[] status;

        public SavedState(Parcel source) {
            super(source);
        }

        public SavedState(Parcelable source,boolean[] status){
            super(source);
            this.status = status;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBooleanArray(status);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
