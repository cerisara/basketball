package fr.xtof.basketball;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.TextView;
import java.util.Arrays;
import android.os.Bundle;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.view.View;
import android.content.Context;
import java.lang.Exception;
import java.net.URLEncoder;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.content.Intent;

public class BasketTracker extends FragmentActivity {
    public static BasketTracker main;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    private Surface canvas=null;
    private String[][] cinq = {{"1","2","3","4","5"},{"1","2","3","4","5"}};
    private ArrayList<String> stats = new ArrayList<String>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        main=this;
        setContentView(R.layout.main);

        if (canvas==null) canvas = (Surface)findViewById(R.id.surfaces);
        //gestureDetector = new GestureDetector(this, new MyGestureListener());
        gestureListener = new View.OnTouchListener() {
          int x0,y0,x1,y1;
          public boolean onTouch(View v, MotionEvent event) {
            int a = event.getActionMasked();
            if (a==0) {
              // DOWN : debut du mouvement
              x0=(int)event.getX(0);
              y0=(int)event.getY(0);
              canvas.invertSelected(x0,y0);
              canvas.invalidate();
            } else if (a==1) {
              // UP: fin du mouvement
              x1=(int)event.getX(event.getPointerCount()-1);
              y1=(int)event.getY(event.getPointerCount()-1);
              x1-=x0; y1-=y0;
              canvas.fling(x0,y0,x1,y1);
            }
            return true;
          }
        };
        canvas.setOnTouchListener(gestureListener);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean isscrolling = false;
        private int dx0, dy0;
        private long lastT=0;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            System.out.println("DETUP");
            return true;
        }
        @Override
        public boolean onDown(MotionEvent event) {
            System.out.println("DETDOWN");
            canvas.invertSelected((int)event.getX(0),(int)event.getY(0));
            canvas.invalidate();
            return true;
        }
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            System.out.println("DETFLING "+velocityX+" "+velocityY);
            // canvas.fling((int)event1.getX(0),(int)event1.getY(0),velocityX,velocityY);
            return true;
        }
        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float dx, float dy) {
            System.out.println("DETSCROLL "+dx+" "+dy);
            if (!isscrolling) {
              isscrolling=true;
              lastT = System.currentTimeMillis();
              // dx0=dx; dy0=dy;
            } else {
            }
            return true;
        }
    }

    public void setText(final String s) {
      TextView t = (TextView)findViewById(R.id.textline);
      t.setText(s);
      t.invalidate();
    }

    public static void msg(final String s) {
        main.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(main, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void todowindow(final int taskid) {
      class LoginDialogFragment extends DialogFragment {
          @Override
          public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View custv = inflater.inflate(R.layout.dialog_edit, null);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(custv)
              // Add action buttons
              .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int id) {
                    TextView txt = (TextView) LoginDialogFragment.this.getDialog().findViewById(R.id.taskdef);
                    // TODO: initialise la fenetre avec le texte precedent de la task ? (sauf pour newtask)
                    String s = txt.getText().toString();
                  }
              })
              .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  LoginDialogFragment.this.getDialog().cancel();
                }
              });
            return builder.create();
          }
      }
      LoginDialogFragment dialog = new LoginDialogFragment();
      dialog.show(getSupportFragmentManager(),"edit task");
    }

    public String[] getCinq(int team) {
      return cinq[team];
    }

    public void addStat(int team, String player, int action) {
      stats.add(""+team+" "+player+" "+action);
    }
    public String getStats() {
      int[] pts={0,0};
      for (String x: stats) {
        String[] xx = x.split(" ");
        int action=Integer.parseInt(xx[2]);
        int team=Integer.parseInt(xx[0]);
        if (action==0) pts[team]+=2;
        else if (action==1) pts[team]+=3;
        else if (action==2) pts[team]+=1;
      }
      String s="TEAM A: "+pts[0]+"\n";
      s+="TEAM B: "+pts[1]+"\n";

      s+="\nDetails:\n";
      for (String x: stats) {
        String[] xx = x.split(" ");
        int action=Integer.parseInt(xx[2]);
        int team=Integer.parseInt(xx[0]);
        String tt="A";
        if (team==1) tt="B";
        if (action==0) s+="+2 Team "+tt+" Joueur "+xx[1]+"\n";
        else if (action==1) s+="+3 Team "+tt+" Joueur "+xx[1]+"\n";
        else if (action==2) s+="+1 Team "+tt+" Joueur "+xx[1]+"\n";
      }
      return s;
    }
  
    public void setJoueur(int team, int playerbox, int num) {
      cinq[team][playerbox]=""+num;
    }

    public void menu(View view) {
      canvas.viewmode=6;
      canvas.invalidate();
    }
    public void chrono(View view) {
    }
    public void bancLeft(View view) {
      canvas.viewmode=3;
      canvas.invalidate();
    }
    public void bancRight(View view) {
      canvas.viewmode=4;
      canvas.invalidate();
    }
    public void reset() {
    }
    public void email() {
      Intent emailIntent = new Intent(Intent.ACTION_SEND); 
      emailIntent.setType("message/rfc822");  //set the email recipient
      //emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL  , recipient);
      emailIntent.putExtra(Intent.EXTRA_SUBJECT, "game statistics");
      emailIntent.putExtra(Intent.EXTRA_TEXT, getStats());
      //let the user choose what email client to use
      try {
        startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
      } catch (android.content.ActivityNotFoundException e) {
        msg("there are no email clients installed");
      }
    }
}
