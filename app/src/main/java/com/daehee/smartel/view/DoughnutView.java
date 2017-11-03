package com.daehee.smartel.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.daehee.smartel.util.LayoutUtils;
import com.daehee.smartel.R;

/**
 * Created by daehee on 2017. 9. 10..
 */

public class DoughnutView extends View {

    private RectF _oval;
    private Paint paintOuter;
    private Paint paintInner;
    private Paint paintText;

    public float sweepAngle = 0.0f; // start at 0 degrees

    private Context context;

    private Handler handler = new Handler();

    private int DP_1;
    private int DP_5;
    private int DP_15;
    private int DP_20;

    private boolean isFree = false;

    public DoughnutView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DoughnutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public DoughnutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        DP_1 = (int) LayoutUtils.pxFromDp(context, 1);
        DP_5 = (int) LayoutUtils.pxFromDp(context, 5);
        DP_15 = (int) LayoutUtils.pxFromDp(context, 15);
        DP_20 = (int) LayoutUtils.pxFromDp(context, 20);

        paintOuter = new Paint();
        paintOuter.setStrokeWidth(DP_5);
        paintOuter.setAntiAlias(true);
        paintOuter.setColor(Color.parseColor("#dedede"));
        paintOuter.setStyle(Paint.Style.STROKE);
        paintOuter.setStrokeCap(Paint.Cap.ROUND);
        paintOuter.setStrokeMiter(1.8f);

        paintInner = new Paint();
        paintInner.setStrokeWidth(DP_5);
        paintInner.setAntiAlias(true);
        paintInner.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        paintInner.setStyle(Paint.Style.STROKE);
        paintInner.setStrokeCap(Paint.Cap.ROUND);

        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setTextSize(DP_20);
        paintText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paintText.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        paintText.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        Log.d("MyLog", "onDraw");

        canvas.drawArc(_oval, 0.0f, 360.0f, false, paintOuter);
        canvas.drawArc(_oval, -90.0f, sweepAngle, false, paintInner);


        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paintText.descent() + paintText.ascent()) / 2)) ;
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.

        if (!isFree) {
            canvas.drawText( (int)(sweepAngle * 100 / 360.f) + "%", xPos, yPos, paintText);
        } else {
            canvas.drawText("무제한", xPos, yPos, paintText);
        }
    }

    public void animateRing() {

        handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });

    } // end of animateRing

    public float getSweepAngle()
    {
        return sweepAngle;
    }

    public void setSweepAngle(float angle)
    {
        sweepAngle = angle;
    }

    // ---------------------------------------------------
    // animation method to be called outside this class
    // ---------------------------------------------------
    public void animateArc(float fromAngle, float toAngle, long duration)
    {
        sweepAngle = fromAngle;

        invalidate();

        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "sweepAngle", fromAngle, toAngle);
        anim.setDuration(duration);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                // calling invalidate(); will trigger onDraw() to execute
                invalidate();
            }
        });
        anim.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        final int minDimen = Math.min(w, h);
        setMeasuredDimension(minDimen, minDimen);

        _oval = new RectF();
        float left = DP_15 / 2;
        float top = DP_15 / 2;
        float right = minDimen - DP_15/ 2;
        float bottom = minDimen - DP_15 / 2;

        _oval.set(left, top, right, bottom);

//        _oval.set(0 + DP_15, 0 + DP_15, getWidth() - DP_15, getHeight() - DP_15);
//        Log.d("MyLog", "w: " + w + ", h: " + h + ", oldw: " + oldw + ", oldh: " + oldh);
    }

    public boolean isFree() { return isFree; }
    public void setFree(boolean free) {
        if (free) {
            paintText.setTextSize(DP_15);
        } else {
            paintText.setTextSize(DP_20);
        }
        isFree = free;
    }

} // end of class
