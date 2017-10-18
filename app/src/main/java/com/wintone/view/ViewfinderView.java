package com.wintone.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public final class ViewfinderView extends View {
    private static final long ANIMATION_DELAY = 10;
    boolean boo = false;
    private int bottomLine = 0;
    private Rect frame;
    private final int frameColor;
    int h;
    private final int laserColor;
    private int leftLine = 0;
    private final int maskColor;
    private final Paint paint;
    private final Paint paintLine;
    private final int resultColor;
    private int rightLine = 0;
    private int scannerAlpha;
    private int topLine = 0;
    int w;

    public ViewfinderView(Context context, int w, int h) {
        super(context);
        this.w = w;
        this.h = h;
        this.paint = new Paint();
        this.paintLine = new Paint();
        Resources resources = getResources();
        this.maskColor = resources.getColor(getResources().getIdentifier("viewfinder_mask", "color", context.getPackageName()));
        this.resultColor = resources.getColor(getResources().getIdentifier("result_view", "color", context.getPackageName()));
        this.frameColor = resources.getColor(getResources().getIdentifier("viewfinder_frame", "color", context.getPackageName()));
        this.laserColor = resources.getColor(getResources().getIdentifier("viewfinder_laser", "color", context.getPackageName()));
        this.scannerAlpha = 0;
    }

    public ViewfinderView(Context context, int w, int h, boolean boo) {
        super(context);
        this.w = w;
        this.h = h;
        this.boo = boo;
        this.paint = new Paint();
        this.paintLine = new Paint();
        Resources resources = getResources();
        this.maskColor = resources.getColor(getResources().getIdentifier("viewfinder_mask", "color", context.getPackageName()));
        this.resultColor = resources.getColor(getResources().getIdentifier("result_view", "color", context.getPackageName()));
        this.frameColor = resources.getColor(getResources().getIdentifier("viewfinder_frame", "color", context.getPackageName()));
        this.laserColor = resources.getColor(getResources().getIdentifier("viewfinder_laser", "color", context.getPackageName()));
        this.scannerAlpha = 0;
    }

    public void setLeftLine(int leftLine) {
        this.leftLine = leftLine;
    }

    public void setTopLine(int topLine) {
        this.topLine = topLine;
    }

    public void setRightLine(int rightLine) {
        this.rightLine = rightLine;
    }

    public void setBottomLine(int bottomLine) {
        this.bottomLine = bottomLine;
    }

    public void onDraw(Canvas canvas) {
        int t;
        int b;
        int l;
        int r;
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        if (width <= height || this.boo) {
            t = this.h / 5;
            b = this.h - t;
            l = (this.w - ((int) (((double) (b - t)) * 1.585d))) / 2;
            r = this.w - l;
            l += 30;
            t += 19;
            r -= 30;
            b -= 19;
            this.frame = new Rect(l, t, r, b);
        } else {
            t = this.h / 10;
            b = this.h - t;
            l = (this.w - ((int) (((double) (b - t)) * 1.585d))) / 2;
            r = this.w - l;
            l += 30;
            t += 19;
            r -= 30;
            b -= 19;
            this.frame = new Rect(l, t, r, b);
        }
        this.paint.setColor(this.maskColor);
        canvas.drawRect(0.0f, 0.0f, (float) width, (float) this.frame.top, this.paint);
        canvas.drawRect(0.0f, (float) this.frame.top, (float) this.frame.left, (float) (this.frame.bottom + 1), this.paint);
        canvas.drawRect((float) (this.frame.right + 1), (float) this.frame.top, (float) width, (float) (this.frame.bottom + 1), this.paint);
        canvas.drawRect(0.0f, (float) (this.frame.bottom + 1), (float) width, (float) height, this.paint);
        if (width <= height || this.boo) {
            this.paintLine.setColor(this.frameColor);
            this.paintLine.setStrokeWidth(8.0f);
            this.paintLine.setAntiAlias(true);
            canvas.drawLine((float) l, (float) t, (float) (l + 100), (float) t, this.paintLine);
            canvas.drawLine((float) l, (float) t, (float) l, (float) (t + 100), this.paintLine);
            canvas.drawLine((float) r, (float) t, (float) (r - 100), (float) t, this.paintLine);
            canvas.drawLine((float) r, (float) t, (float) r, (float) (t + 100), this.paintLine);
            canvas.drawLine((float) l, (float) b, (float) (l + 100), (float) b, this.paintLine);
            canvas.drawLine((float) l, (float) b, (float) l, (float) (b - 100), this.paintLine);
            canvas.drawLine((float) r, (float) b, (float) (r - 100), (float) b, this.paintLine);
            canvas.drawLine((float) r, (float) b, (float) r, (float) (b - 100), this.paintLine);
        } else {
            this.paintLine.setColor(this.frameColor);
            this.paintLine.setStrokeWidth(8.0f);
            this.paintLine.setAntiAlias(true);
            int num = t - 40;
            canvas.drawLine((float) (l - 4), (float) t, (float) (l + num), (float) t, this.paintLine);
            canvas.drawLine((float) l, (float) t, (float) l, (float) (t + num), this.paintLine);
            canvas.drawLine((float) r, (float) t, (float) (r - num), (float) t, this.paintLine);
            canvas.drawLine((float) r, (float) (t - 4), (float) r, (float) (t + num), this.paintLine);
            canvas.drawLine((float) (l - 4), (float) b, (float) (l + num), (float) b, this.paintLine);
            canvas.drawLine((float) l, (float) b, (float) l, (float) (b - num), this.paintLine);
            canvas.drawLine((float) r, (float) b, (float) (r - num), (float) b, this.paintLine);
            canvas.drawLine((float) r, (float) (b + 4), (float) r, (float) (b - num), this.paintLine);
            if (this.leftLine == 1) {
                canvas.drawLine((float) l, (float) t, (float) l, (float) b, this.paintLine);
            }
            if (this.rightLine == 1) {
                canvas.drawLine((float) r, (float) t, (float) r, (float) b, this.paintLine);
            }
            if (this.topLine == 1) {
                canvas.drawLine((float) l, (float) t, (float) r, (float) t, this.paintLine);
            }
            if (this.bottomLine == 1) {
                canvas.drawLine((float) l, (float) b, (float) r, (float) b, this.paintLine);
            }
        }
        if (this.frame != null) {
            postInvalidateDelayed(ANIMATION_DELAY);
        }
    }
}
