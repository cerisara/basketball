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
    if (viewmode>0) return;
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
      viewmode=2;
    } else if (deltax<-30) {
      if (deltay<0) {
        pts0+=npts;
        BasketTracker.main.setText("Joueur ayant réussi son +"+npts+" ?");
      } else {
        action+=10;
        BasketTracker.main.setText("Joueur ayant raté son +"+npts+" ?");
      }
      viewmode=1;
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
    if (viewmode==0) {
      // si on arrive ici, on sait qu'il n'y a pas eu de fling
      // donc on selectionne un "shoot" rate, et il faut choisir le player qui l'a rate
      // viewmode=10+selbutton;
      // je tente une autre option: fling vers le bas pour rate, vers le haut pour reussi
    } else if (viewmode==1||viewmode==2) {
      // il y a eu un fling, donc un panier marque
      // si on ne donne pas de joueur, on compte les points pour l'equipe, mais pas de stats !
      if (selbutton>cinq.length-1) viewmode=0;
      else BasketTracker.main.addStat(viewmode-1,cinq[selbutton],action);
      BasketTracker.main.setText("Faites glisser les shoots");
      viewmode=0;
    } else if (viewmode==3) {
      // entree du joueur N de la team 0
      joueurnum=0;
      if (selbutton==limits.length-1) viewmode=0;
      else viewmode=100+selbutton;
    } else if (viewmode==4) {
      // entree du joueur N de la team 1
      joueurnum=0;
      if (selbutton==limits.length-1) viewmode=0;
      else viewmode=200+selbutton;
    } else if (viewmode>=10&&viewmode<50) {
      // choix d'un joueur pour un shoot rate
      if (selbutton<5) BasketTracker.main.addStat(0,cinq[selbutton],viewmode-10);
      else if (selbutton<10) BasketTracker.main.addStat(1,cinq[selbutton],viewmode-10);
      viewmode=0;
    } else if (viewmode>=100&&viewmode<200) {
      if (selbutton==10) {
        BasketTracker.main.setJoueur(0,viewmode-100,joueurnum);
        viewmode=3;
      } else {
        joueurnum*=10;
        joueurnum+=selbutton;
      }
    } else if (viewmode>=200&&viewmode<300) {
      if (selbutton==10) {
        BasketTracker.main.setJoueur(1,viewmode-200,joueurnum);
        viewmode=4;
      } else {
        joueurnum*=10;
        joueurnum+=selbutton;
      }
    } else if (viewmode==6) {
      if (selbutton==0) {
        BasketTracker.main.reset();
        pts0=pts1=0;
      } else if (selbutton==1) {
        BasketTracker.main.email();
      }
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
    } else if (viewmode>=1&&viewmode<5) {
        // on est dans un mode de selection de joueur
        buttonSelected(x,y);
    } else if (viewmode==6) {
        // on est dans le menu
        buttonSelected(x,y);
    } else if (viewmode>=100&&viewmode<300) {
        // on est dans un mode d'entree de joueur
        buttonSelected(x,y);
    }
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

    if (viewmode==0) showPoints(canvas);
    else if (viewmode==1||viewmode==2) choosePlayer(canvas,viewmode-1);
    else if (viewmode==3||viewmode==4) choosePlayer(canvas,viewmode-3);
    else if (viewmode==6) drawMenu(canvas);
    else if (viewmode>=10&&viewmode<50) chooseAllPlayers(canvas);
    else if (viewmode>=100) claviernumerique(canvas);
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

  private void claviernumerique(Canvas canvas) {
    int team=0;
    if (viewmode>=200) team=1;
    if (team==0) canvas.drawColor(Color.RED);
    else if (team==1) canvas.drawColor(Color.BLUE);
    Paint bPaint=new Paint();
    bPaint.setColor(Color.YELLOW);
    Paint tPaint = new Paint();
    tPaint.setColor(Color.BLACK);
    tPaint.setTextSize(40);
    limits = new int[11][4];
    for (int i=1;i<=9;i++) {
      limits[i][0]=100+((int)((i-1)%3))*80;
      limits[i][1]=10+((int)((i-1)/3))*60;
      limits[i][2]=170+((int)((i-1)%3))*80;
      limits[i][3]=60+((int)((i-1)/3))*60;
      canvas.drawRect(limits[i][0],limits[i][1],limits[i][2],limits[i][3],bPaint);
      canvas.drawText(""+i,10+limits[i][0],40+limits[i][1],tPaint);
    }
    {
      limits[0][0]=100+1*80;
      limits[0][1]=10+3*60;
      limits[0][2]=170+1*80;
      limits[0][3]=60+3*60;
      canvas.drawRect(limits[0][0],limits[0][1],limits[0][2],limits[0][3],bPaint);
      canvas.drawText("0",10+limits[0][0],40+limits[0][1],tPaint);
    }
    {
      limits[10][0]=100+2*80;
      limits[10][1]=10+3*60;
      limits[10][2]=170+2*80;
      limits[10][3]=60+3*60;
      canvas.drawRect(limits[10][0],limits[10][1],limits[10][2],limits[10][3],bPaint);
      canvas.drawText("OK",10+limits[10][0],40+limits[10][1],tPaint);
    }
  }

  protected void chooseAllPlayers(Canvas canvas) {
    String[] cinq0 = BasketTracker.main.getCinq(0);
    String[] cinq1 = BasketTracker.main.getCinq(1);
    cinq = new String[cinq0.length+cinq1.length];
    for (int i=0;i<cinq0.length;i++) cinq[i]=cinq0[i];
    for (int i=0;i<cinq1.length;i++) cinq[cinq0.length+i]=cinq1[i];
    Paint bPaint=new Paint();
    bPaint.setColor(Color.YELLOW);
    Paint tPaint = new Paint();
    tPaint.setColor(Color.BLACK);
    tPaint.setTextSize(40);

    int hmid = canvas.getWidth()/2;
    limits = new int[cinq.length+1][4];
    for (int i=0;i<cinq0.length;i++) {
      canvas.drawRect(50,10+i*60,120,60+i*60,bPaint);
      canvas.drawText(cinq0[i],60,50+i*60,tPaint);
      limits[i][0]=50;
      limits[i][1]=10+i*60;
      limits[i][2]=120;
      limits[i][3]=60+i*60;
    }
    for (int i=0;i<cinq1.length;i++) {
      canvas.drawRect(hmid+50,10+i*60,hmid+120,60+i*60,bPaint);
      canvas.drawText(cinq1[i],hmid+60,50+i*60,tPaint);
      limits[cinq0.length+i][0]=hmid+50;
      limits[cinq0.length+i][1]=10+i*60;
      limits[cinq0.length+i][2]=hmid+120;
      limits[cinq0.length+i][3]=60+i*60;
    }
 
    {
      int i=2;
      canvas.drawRect(hmid-30,10+i*60,hmid+30,60+i*60,bPaint);
      canvas.drawText("NO",hmid-20,50+i*60,tPaint);
      limits[cinq.length][0]=hmid-30;
      limits[cinq.length][1]=10+i*60;
      limits[cinq.length][2]=hmid+30;
      limits[cinq.length][3]=60+i*60;
    }
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
    limits = new int[cinq.length+1][4];
    for (int i=0;i<cinq.length;i++) {
      canvas.drawRect(100,10+i*60,170,60+i*60,bPaint);
      canvas.drawText(cinq[i],110,50+i*60,tPaint);
      limits[i][0]=100;
      limits[i][1]=10+i*60;
      limits[i][2]=170;
      limits[i][3]=60+i*60;
    }
    {
      int i=2;
      canvas.drawRect(190,10+i*60,260,60+i*60,bPaint);
      canvas.drawText("OK",200,50+i*60,tPaint);
      limits[cinq.length][0]=190;
      limits[cinq.length][1]=10+i*60;
      limits[cinq.length][2]=260;
      limits[cinq.length][3]=60+i*60;
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
    int y=(int)((float)haut*0.9);
    canvas.drawRect(hmid-len,y-len,hmid+len,y+len,b2Paint);
    canvas.drawText("+2",hmid-len+6,y-len+40,tPaint);
    limits[0][0]=hmid-len;
    limits[0][1]=y-len;
    limits[0][2]=hmid+len;
    limits[0][3]=y+len;

    int y2=(int)((float)haut*0.7);
    canvas.drawRect(hmid-len,y2-len,hmid+len,y2+len,b2Paint);
    canvas.drawText("+3",hmid-len+6,y2-len+40,tPaint);
    limits[1][0]=hmid-len;
    limits[1][1]=y2-len;
    limits[1][2]=hmid+len;
    limits[1][3]=y2+len;

    int y3=(int)((float)haut*0.5);
    canvas.drawRect(hmid-len,y3-len,hmid+len,y3+len,b2Paint);
    canvas.drawText("LF",hmid-len+6,y3-len+40,tPaint);
    limits[2][0]=hmid-len;
    limits[2][1]=y3-len;
    limits[2][2]=hmid+len;
    limits[2][3]=y3+len;

  }
}

