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
  private boolean selected2=false;
  private boolean selected3=false;
  private int[] limits2 = {0,0,0,0};
  private int[] limits3 = {0,0,0,0};
  public boolean touchable=true;
  public int pts0=0, pts1=0;

  public Surface(Context c) {
    super(c);
  }
  public Surface(Context c, AttributeSet attribs) {
    super(c,attribs);
  }
  public Surface(Context c, AttributeSet attribs, int defStyle) {
    super(c,attribs,defStyle);
  }

  public void fling(int x, int y, float deltax) {
    int npts=0;
    if (x>=limits2[0]&&y>=limits2[1]&&x<limits2[2]&&y<limits2[3]) npts=2;
    else if (x>=limits3[0]&&y>=limits3[1]&&x<limits3[2]&&y<limits3[3]) npts=3;
    if (deltax>10) pts1+=npts;
    else if (deltax<-10) pts0+=npts;
    selected2=selected3=false;
    this.invalidate();
  }
  public void invertSelected(int x, int y) {
    touchable=false;
    if (x>=limits2[0]&&y>=limits2[1]&&x<limits2[2]&&y<limits2[3]) selected2=!selected2;
    else if (x>=limits3[0]&&y>=limits3[1]&&x<limits3[2]&&y<limits3[3]) selected3=!selected3;

    new CountDownTimer(300,300) {
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

    // points
    Paint pPaint = new Paint();
    pPaint.setColor(Color.BLACK);
    pPaint.setTextSize(100);
    canvas.drawText(""+pts0,hmid/3,haut/2,pPaint);
    canvas.drawText(""+pts1,hmid+hmid/3,haut/2,pPaint);

    // buttons
    Paint tPaint = new Paint();
    tPaint.setColor(Color.BLACK);
    tPaint.setTextSize(40);
    int len=30;

    Paint b2Paint = new Paint();
    if (selected2) b2Paint.setColor(Color.WHITE);
    else b2Paint.setColor(Color.YELLOW);
    int y=(int)((float)haut*0.9);
    limits2[0]=hmid-len;
    limits2[1]=y-len;
    limits2[2]=hmid+len;
    limits2[3]=y+len;
    canvas.drawRect(hmid-len,y-len,hmid+len,y+len,b2Paint);
    canvas.drawText("+2",hmid-len+6,y-len+40,tPaint);

    Paint b3Paint = new Paint();
    if (selected3) b3Paint.setColor(Color.WHITE);
    else b3Paint.setColor(Color.YELLOW);
    y=(int)((float)haut*0.7);
    limits3[0]=hmid-len;
    limits3[1]=y-len;
    limits3[2]=hmid+len;
    limits3[3]=y+len;
    canvas.drawRect(hmid-len,y-len,hmid+len,y+len,b3Paint);
    canvas.drawText("+3",hmid-len+6,y-len+40,tPaint);
  }
}

