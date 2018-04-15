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
  private int[][] limits;
  private boolean wasFling=false;
  public int pts0=0, pts1=0;
  public int viewmode=0;
  private String[] cinq;
  // actions to include in stats:
  // 0 = +2 pts
  // 1 = +3 pts
  // 2 = LF +1 pt
  // 3 = Fault
  // 4 = shoot 2pts rate
  // 5 = shoot 3pts rate
  private int action=-1;

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
    if (viewmode>0) return;
    if (x>=limits[0][0]&&y>=limits[0][1]&&x<limits[0][2]&&y<limits[0][3]) {npts=2; action=0;}
    else if (x>=limits[1][0]&&y>=limits[1][1]&&x<limits[1][2]&&y<limits[1][3]) {npts=3; action=1;}
    if (deltax>10) {pts1+=npts; viewmode=2;}
    else if (deltax<-10) {pts0+=npts; viewmode=1;}
    // ajoute un flag pour empecher de prendre en compte un select s il y a un fling
    wasFling=true;
    new CountDownTimer(500,500) {
      public void onTick(long t) {}
      public void onFinish() {wasFling=false;}
    }.start();
    this.invalidate();
  }
  private void buttonSelected(int x, int y) {
    int selbutton=-1;
    for (int i=0;i<limits.length;i++) {
      if (x>=limits[i][0]&&y>=limits[i][1]&&x<limits[i][2]&&y<limits[i][3]) {selbutton=i; break;}
    }

    // reagit aux boutons presses
    if (selbutton<0) return;
    if (viewmode==0) {
      // si on arrive ici, on sait qu'il n'y a pas eu de fling
      // donc on selectionne un "shoot" rate, et il faut choisir le player qui l'a rate
      // TODO
    } else if (viewmode==1||viewmode==2) {
      // il y a eu un fling, donc un panier marque
      BasketTracker.main.addStat(viewmode-1,cinq[selbutton],action);
      viewmode=0;
    }
  }
  // called when pressed on the surface
  public void invertSelected(final int x, final int y) {
    if (viewmode==0) {
      // attends d'abord un peu pour verifier s'il y a eu un fling
      if (wasFling) {
        wasFling=false; return;
      } else {
        new CountDownTimer(100,100) {
          public void onTick(long t) {}
          public void onFinish() {
            if (wasFling) wasFling=false;
            else buttonSelected(x,y);
          }
        }.start();
      }
    } else buttonSelected(x,y);
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

    if (viewmode==0) showPoints(canvas);
    else if (viewmode==1||viewmode==2) choosePlayer(canvas,viewmode-1);
  }

  protected void choosePlayer(Canvas canvas, int team) {
    cinq = BasketTracker.main.getCinq(team);
    if (team==0) canvas.drawColor(Color.RED);
    else if (team==1) canvas.drawColor(Color.BLUE);
    Paint bPaint=new Paint();
    bPaint.setColor(Color.YELLOW);
    Paint tPaint = new Paint();
    tPaint.setColor(Color.BLACK);
    tPaint.setTextSize(40);
    limits = new int[cinq.length][4];
    for (int i=0;i<cinq.length;i++) {
      canvas.drawRect(100,10+i*60,170,60+i*60,bPaint);
      canvas.drawText(cinq[i],110,50+i*60,tPaint);
      limits[i][0]=100;
      limits[i][1]=10+i*60;
      limits[i][2]=170;
      limits[i][3]=60+i*60;
    }
  }
  protected void showPoints(Canvas canvas) {
    int hmid = canvas.getWidth()/2;
    int haut = realh;
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
    b2Paint.setColor(Color.YELLOW);
    int y=(int)((float)haut*0.9);
    canvas.drawRect(hmid-len,y-len,hmid+len,y+len,b2Paint);
    canvas.drawText("+2",hmid-len+6,y-len+40,tPaint);

    Paint b3Paint = new Paint();
    b3Paint.setColor(Color.YELLOW);
    int y2=(int)((float)haut*0.7);
    canvas.drawRect(hmid-len,y2-len,hmid+len,y2+len,b3Paint);
    canvas.drawText("+3",hmid-len+6,y2-len+40,tPaint);

    limits = new int[2][4];
    limits[0][0]=hmid-len;
    limits[0][1]=y-len;
    limits[0][2]=hmid+len;
    limits[0][3]=y+len;
    limits[1][0]=hmid-len;
    limits[1][1]=y2-len;
    limits[1][2]=hmid+len;
    limits[1][3]=y2+len;
  }
}

