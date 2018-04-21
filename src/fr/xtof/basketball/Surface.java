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
  // type d'ecran a afficher
  // 0 = shoots + fautes
  // 1 = selection joueurs team 0
  // 2 = selection joueurs team 1
  // 3 = entree des joueurs team 0
  // 4 = entree des joueurs team 1
  // 6 = menu
  // 100+N = clavier numerique pour entrer numero du joueur N de la team 0
  // 200+N = clavier numerique pour entrer numero du joueur N de la team 1
  public static final int VUE_SHOOTS = 0;
  public static final int VUE_JOUEURA= 1;
  public static final int VUE_JOUEURB= 2;
  public int viewmode=0;

  private String[] cinq;
  // actions to include in stats:
  // 0 = +2 pts   10 = rate
  // 1 = +3 pts   11 = rate
  // 2 = LF +1 pt 12 = rate
  // 3 = Fault
  // 4 = shoot 2pts rate
  // 5 = shoot 3pts rate
  private int action=-1;
  // utilise quand on entre le numero d'un joueur
  private int joueurnum=0;

  public Surface(Context c) {
    super(c);
  }
  public Surface(Context c, AttributeSet attribs) {
    super(c,attribs);
  }
  public Surface(Context c, AttributeSet attribs, int defStyle) {
    super(c,attribs,defStyle);
  }

  public void fling(int x, int y, float deltax, float deltay) {
    int npts=0;
    if (viewmode!=VUE_SHOOTS) return;
    if (x>=limits[0][0]&&y>=limits[0][1]&&x<limits[0][2]&&y<limits[0][3]) {npts=2; action=0;}
    else if (x>=limits[1][0]&&y>=limits[1][1]&&x<limits[1][2]&&y<limits[1][3]) {npts=3; action=1;}
    else if (x>=limits[2][0]&&y>=limits[2][1]&&x<limits[2][2]&&y<limits[2][3]) {npts=1; action=2;}
    if (deltax>30) {
      if (deltay<0) {
        pts1+=npts;
        BasketTracker.main.setText("Joueur ayant réussi son +"+npts+" ?");
      } else {
        action+=10;
        BasketTracker.main.setText("Joueur ayant raté son +"+npts+" ?");
      }
      viewmode=VUE_JOUEURB;
      joueurnum=-1;
    } else if (deltax<-30) {
      if (deltay<0) {
        pts0+=npts;
        BasketTracker.main.setText("Joueur ayant réussi son +"+npts+" ?");
      } else {
        action+=10;
        BasketTracker.main.setText("Joueur ayant raté son +"+npts+" ?");
      }
      viewmode=VUE_JOUEURA;
      joueurnum=-1;
    }
    // ajoute un flag pour empecher de prendre en compte un select s il y a un fling
    // TODO: supprimer ce flag ? Pas sur qu'il soit utile
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
    if (viewmode==VUE_SHOOTS) {
      // si on arrive ici, on sait qu'il n'y a pas eu de fling
      // donc on selectionne un "shoot" rate, et il faut choisir le player qui l'a rate
      // viewmode=10+selbutton;
      // je tente une autre option: fling vers le bas pour rate, vers le haut pour reussi
    } else if (viewmode==VUE_JOUEURA) {
      // ici on selectionne le N° d'un joueur sur le pave numerique
      if (selbutton==limits.length-1) {
        // si on click sur "OK", le n° est fini
        viewmode=VUE_SHOOTS;
        if (joueurnum<0) BasketTracker.main.addStat(0,"X",action);
        else BasketTracker.main.addStat(0,""+joueurnum,action);
        BasketTracker.main.setText(BasketTracker.main.getStats(0,""+joueurnum));
        viewmode=VUE_SHOOTS;
      } else {
        // le N° n'est pas fini
        if (joueurnum>0) joueurnum*=10;
        else joueurnum=0;
        joueurnum+=selbutton;
        BasketTracker.main.setText(""+joueurnum);
      }
    } else if (viewmode==VUE_JOUEURB) {
      // ici on selectionne le N° d'un joueur sur le pave numerique
      if (selbutton==limits.length-1) {
        // si on click sur "OK", le n° est fini
        viewmode=VUE_SHOOTS;
        if (joueurnum<0) BasketTracker.main.addStat(1,"X",action);
        else BasketTracker.main.addStat(1,""+joueurnum,action);
        BasketTracker.main.setText(BasketTracker.main.getStats(1,""+joueurnum));
        viewmode=VUE_SHOOTS;
      } else {
        // le N° n'est pas fini
        if (joueurnum>0) joueurnum*=10;
        else joueurnum=0;
        joueurnum+=selbutton;
        BasketTracker.main.setText(""+joueurnum);
      }
    } else if (viewmode==6) {
      if (selbutton==0) {
        BasketTracker.main.reset();
        pts0=pts1=0;
      } else if (selbutton==1) {
        BasketTracker.main.email();
      }
      viewmode=VUE_SHOOTS;
    }
  }
  // called when pressed on the surface
  public void invertSelected(final int x, final int y) {
    if (viewmode==VUE_SHOOTS) {
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
    this.invalidate();
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

    if (viewmode==VUE_SHOOTS) showPoints(canvas);
    else if (viewmode==VUE_JOUEURA) claviernumerique(canvas,0);
    else if (viewmode==VUE_JOUEURB) claviernumerique(canvas,1);
    else if (viewmode==6) drawMenu(canvas);
  }

  private void drawMenu(Canvas canvas) {
    canvas.drawColor(Color.BLACK);
    Paint bPaint=new Paint();
    bPaint.setColor(Color.DKGRAY);
    Paint tPaint = new Paint();
    tPaint.setColor(Color.WHITE);
    tPaint.setTextSize(40);
    limits = new int[3][4];

    limits[0][0] = 50;
    limits[0][1] = 10;
    limits[0][2] = 250;
    limits[0][3] = 60;
    canvas.drawRect(limits[0][0],limits[0][1],limits[0][2],limits[0][3],bPaint);
    canvas.drawText("reset",10+limits[0][0],40+limits[0][1],tPaint);
    
    limits[1][0] = 50;
    limits[1][1] = 70;
    limits[1][2] = 250;
    limits[1][3] = 120;
    canvas.drawRect(limits[1][0],limits[1][1],limits[1][2],limits[1][3],bPaint);
    canvas.drawText("share stats",10+limits[1][0],40+limits[1][1],tPaint);
    
    limits[2][0] = 50;
    limits[2][1] = 130;
    limits[2][2] = 250;
    limits[2][3] = 180;
    canvas.drawRect(limits[2][0],limits[2][1],limits[2][2],limits[2][3],bPaint);
    canvas.drawText("cancel",10+limits[2][0],40+limits[2][1],tPaint);
  }

  private void claviernumerique(Canvas canvas, int team) {
    final int x0=50;
    if (team==0) canvas.drawColor(Color.RED);
    else if (team==1) canvas.drawColor(Color.BLUE);
    Paint bPaint=new Paint();
    bPaint.setColor(Color.YELLOW);
    Paint tPaint = new Paint();
    tPaint.setColor(Color.BLACK);
    tPaint.setTextSize(40);
    limits = new int[11][4];
    for (int i=1;i<=9;i++) {
      limits[i][0]=x0+((int)((i-1)%3))*80;
      limits[i][1]=10+((int)((i-1)/3))*60;
      limits[i][2]=x0+70+((int)((i-1)%3))*80;
      limits[i][3]=60+((int)((i-1)/3))*60;
      canvas.drawRect(limits[i][0],limits[i][1],limits[i][2],limits[i][3],bPaint);
      canvas.drawText(""+i,10+limits[i][0],40+limits[i][1],tPaint);
    }
    {
      limits[0][0]=x0+1*80;
      limits[0][1]=10+3*60;
      limits[0][2]=x0+70+1*80;
      limits[0][3]=60+3*60;
      canvas.drawRect(limits[0][0],limits[0][1],limits[0][2],limits[0][3],bPaint);
      canvas.drawText("0",10+limits[0][0],40+limits[0][1],tPaint);
    }
    {
      limits[10][0]=x0+2*80;
      limits[10][1]=10+3*60;
      limits[10][2]=x0+70+2*80;
      limits[10][3]=60+3*60;
      canvas.drawRect(limits[10][0],limits[10][1],limits[10][2],limits[10][3],bPaint);
      canvas.drawText("OK",10+limits[10][0],40+limits[10][1],tPaint);
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

    limits = new int[3][4];
    int y=(int)((float)haut*0.7);
    canvas.drawRect(hmid-len,y-len,hmid+len,y+len,b2Paint);
    canvas.drawText("+2",hmid-len+6,y-len+40,tPaint);
    limits[0][0]=hmid-len;
    limits[0][1]=y-len;
    limits[0][2]=hmid+len;
    limits[0][3]=y+len;

    int y2=(int)((float)haut*0.5);
    canvas.drawRect(hmid-len,y2-len,hmid+len,y2+len,b2Paint);
    canvas.drawText("+3",hmid-len+6,y2-len+40,tPaint);
    limits[1][0]=hmid-len;
    limits[1][1]=y2-len;
    limits[1][2]=hmid+len;
    limits[1][3]=y2+len;

    int y3=(int)((float)haut*0.3);
    canvas.drawRect(hmid-len,y3-len,hmid+len,y3+len,b2Paint);
    canvas.drawText("LF",hmid-len+6,y3-len+40,tPaint);
    limits[2][0]=hmid-len;
    limits[2][1]=y3-len;
    limits[2][2]=hmid+len;
    limits[2][3]=y3+len;

  }
}

