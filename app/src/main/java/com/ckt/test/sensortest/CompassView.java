package com.ckt.test.sensortest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by D22434 on 2017/8/25.
 */

public class CompassView extends View {


    private Paint mLinePaint;
    private Paint mAnglePaint;
    private Paint mPaint;

    public CompassView(Context context) {
        super(context, null);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);

        mAnglePaint = new Paint();
        mAnglePaint.setColor(Color.RED);
        mAnglePaint.setStyle(Paint.Style.FILL);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = getMeasuredWidth() / 2;
        int y = getMeasuredHeight() / 2;
        int radius = x - 20;
//        canvas.drawCircle(x, y, x - 10, mPaint);

        for (int i = 0; i < 120; i++) {

            if (i == 0) {
                mLinePaint.setColor(Color.BLACK);
                mLinePaint.setStrokeWidth(4);
                canvas.drawLine(x, y - radius, x, y - radius - 20, mLinePaint);
                canvas.drawText(i + "'", x, y - radius + 5, mAnglePaint);
            } else if (i % 10 == 0) {
                mLinePaint.setColor(Color.BLACK);
                mLinePaint.setStrokeWidth(3);
                canvas.drawLine(x, y - radius, x, y - radius - 12, mLinePaint);
                canvas.drawText(i * 3 + "'", x, y - radius + 5, mAnglePaint);
            } else {
                mLinePaint.setColor(Color.GRAY);
                mLinePaint.setStrokeWidth(1);
                canvas.drawLine(x, y - radius, x, y - radius - 12, mLinePaint);
            }
            canvas.rotate(3, x, y);
        }

    }
}
