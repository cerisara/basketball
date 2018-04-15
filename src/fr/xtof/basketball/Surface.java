package fr.xtof.basketball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.os.CountDownTimer;

public class Surface extends View {
  public int realw,realh;
  private boolean selected=false;
  public boolean touchable=true;

  public Surface(Context c) {
    super(c);
  }
  public Surface(Context c, AttributeSet attribs) {
    super(c,attribs);
  }
  public Surface(Context c, AttributeSet attribs, int defStyle) {
    super(c,attribs,defStyle);
  }

  public void invertSelected() {
    touchable=false;
    selected = !selected;
    new CountDownTimer(500,500) {
      public void onTick(long t) {}
      public void onFinish() {
        touchable=true;
      }
    }.start();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

      int desiredWidth = 100;
      int desiredHeight = 100;

      int widthMode = MeasureSpec.getMode(widthMeasureSpec);
      int widthSize = MeasureSpec.getSize(widthMeasureSpec);
      int heightMode = MeasureSpec.getMode(heightMeasureSpec);
      int heightSize = MeasureSpec.getSize(heightMeasureSpec);

      int width;
      int height;

      //Measure Width
      if (widthMode == MeasureSpec.EXACTLY) {
          //Must be this size
          width = widthSize;
      } else if (widthMode == MeasureSpec.AT_MOST) {
          //Can't be bigger than...
          width = Math.min(desiredWidth, widthSize);
      } else {
          //Be whatever you want
          width = desiredWidth;
      }

      //Measure Height
      if (heightMode == MeasureSpec.EXACTLY) {
          //Must be this size
          height = heightSize;
      } else if (heightMode == MeasureSpec.AT_MOST) {
          //Can't be bigger than...
          height = Math.min(desiredHeight, heightSize);
      } else {
          //Be whatever you want
          height = desiredHeight;
      }
      realw=width;
      realh=height;
      //MUST CALL THIS
      setMeasuredDimension(width, height);
  }

  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int hmid = canvas.getWidth()/2;
    int haut = canvas.getHeight();
    haut = realh;

    // colors
    canvas.save();
    canvas.clipRect(0,0,hmid,haut);
    canvas.drawColor(Color.RED);
    canvas.restore();
    canvas.clipRect(hmid,0,canvas.getWidth(),haut);
    canvas.drawColor(Color.BLUE);
    canvas.restore();

    // buttons
    Paint tPaint = new Paint();
    tPaint.setColor(Color.BLACK);
    tPaint.setTextSize(40);
    Paint bPaint = new Paint();
    if (selected) bPaint.setColor(Color.WHITE);
    else bPaint.setColor(Color.YELLOW);
    int len=30;

    int y=(int)((float)haut*0.9);
    canvas.drawRect(hmid-len,y-len,hmid+len,y+len,bPaint);
    canvas.drawText("+2",hmid-len+6,y-len+40,tPaint);
  }
}

