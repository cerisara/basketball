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

import fr.xtof.basketball.Micro;

public class BasketTracker extends FragmentActivity {
    public static BasketTracker main;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    private Surface canvas=null;
    private ArrayList<String> stats = new ArrayList<String>();
    public Thread soundthread = null;

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

    public void addStat(int team, String player, int action) {
      stats.add(""+team+" "+player+" "+action);
    }
    public static int getPts(int action) {
        switch (action) {
          case 0: return 2;
          case 1: return 3;
          case 2: return 1;
        }
        return 0;
    }
    protected HashMap<String,Integer> getPlayers(int tteam) {
      HashMap<String,Integer> res = new HashMap<String,Integer>();
      for (String x: stats) {
        String[] xx = x.split(" ");
        int team=Integer.parseInt(xx[0]);
        if (team!=tteam) continue;
        String joueur=xx[1];
        if (joueur.charAt(0)=='X') continue;
        Integer jpts = res.get(joueur);
        if (jpts==null) jpts=0;
        int action=Integer.parseInt(xx[2]);
        switch (action) {
          case 0: jpts+=2; break;
          case 1: jpts+=3; break;
          case 2: jpts+=1; break;
        }
        res.put(joueur,jpts);
      }
      return res;
    }
    protected String getStats(int tteam, String tjoueur) {
      int[] st = {0,0,0,0,0,0};
      for (String x: stats) {
        String[] xx = x.split(" ");
        int team=Integer.parseInt(xx[0]);
        if (team!=tteam) continue;
        String joueur=xx[1];
        if (!joueur.equals(tjoueur)) continue;
        int action=Integer.parseInt(xx[2]);
        switch (action) {
          case 0: st[0]++; break;
          case 1: st[2]++; break;
          case 2: st[4]++; break;
          case 10: st[1]++; break;
          case 11: st[3]++; break;
          case 12: st[5]++; break;
        }
      }
      int nnp=0;
      int nnok=st[0]; int nntot=st[1]+st[0]; 
      if (nntot>0) nnp = (int)((float)nnok*100./(float)nntot);
      String s=nnp+"% ";
      nnp=0;
      nnok=st[2]; nntot=st[2]+st[3];
      if (nntot>0) nnp = (int)((float)nnok*100./(float)nntot);
      s+=nnp+"% ";
      nnp=0;
      nnok=st[4]; nntot=st[4]+st[5];
      if (nntot>0) nnp = (int)((float)nnok*100./(float)nntot);
      s+=nnp+"%";
      return s;
    }
    public String getStats() {
      int[] pts={0,0};
      HashMap<String,int[]> joueurA2stat = new HashMap<String,int[]>();
      HashMap<String,int[]> joueurB2stat = new HashMap<String,int[]>();
      for (String x: stats) {
        String[] xx = x.split(" ");
        int action=Integer.parseInt(xx[2]);
        int team=Integer.parseInt(xx[0]);
        if (action==0) pts[team]+=2;
        else if (action==1) pts[team]+=3;
        else if (action==2) pts[team]+=1;

        int[] st=null;
        if (team==0) st=joueurA2stat.get(xx[1]);
        else st=joueurB2stat.get(xx[1]);
        if (st==null) {
          // N2 ok; N2 ko; N3 ok; N3 ko; LF ok; LF ko
          st=new int[6];
          for (int i=0;i<st.length;i++) st[i]=0;
          if (team==0) joueurA2stat.put(xx[1],st);
          else joueurB2stat.put(xx[1],st);
        }
        switch (action) {
          case 0: st[0]++; break;
          case 1: st[2]++; break;
          case 2: st[4]++; break;
          case 10: st[1]++; break;
          case 11: st[3]++; break;
          case 12: st[5]++; break;
        }
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

      s+="\nJoueurs team A:\n";
      for (String p: joueurA2stat.keySet()) {
        int[] st = joueurA2stat.get(p);
        int nnok=st[0]; int nntot=st[1]+st[0]; int nnp = (int)((float)nnok*100./(float)nntot);
        s+=p+"\t 2pts: "+nnok+"/"+nntot+"="+nnp+"%";
        nnok=st[2]; nntot=st[2]+st[3]; nnp = (int)((float)nnok*100./(float)nntot);
        s+=" 3pts: "+nnok+"/"+nntot+"="+nnp+"%";
        nnok=st[4]; nntot=st[4]+st[5]; nnp = (int)((float)nnok*100./(float)nntot);
        s+=" LF: "+nnok+"/"+nntot+"="+nnp+"%";
        s+="\n";
      }
      s+="\nJoueurs team B:\n";
      for (String p: joueurB2stat.keySet()) {
        int[] st = joueurB2stat.get(p);
        int nnok=st[0]; int nntot=st[1]+st[0]; int nnp = (int)((float)nnok*100./(float)nntot);
        s+=p+"\t 2pts: "+nnok+"/"+nntot+"="+nnp+"%";
        nnok=st[2]; nntot=st[2]+st[3]; nnp = (int)((float)nnok*100./(float)nntot);
        s+=" 3pts: "+nnok+"/"+nntot+"="+nnp+"%";
        nnok=st[4]; nntot=st[4]+st[5]; nnp = (int)((float)nnok*100./(float)nntot);
        s+=" LF: "+nnok+"/"+nntot+"="+nnp+"%";
        s+="\n";
      }
      return s;
    }
  
    public void menu(View view) {
      canvas.viewmode=6;
      canvas.invalidate();
    }
    public void chrono(View view) {
	if (soundthread!=null) return;
	soundthread = new Thread(new Runnable() {
		public void run() {
			final ArrayList<byte[]> allbufs = new ArrayList<byte[]>();
			try {
				Micro mik = new Micro(new AudioConsumer() {
					public void consume(byte[] buf, double amplitude, double volume) {
						allbufs.add(buf);
					}
				});
				mik.start();
				Thread.sleep(5000);
				mik.end();
			} catch(Exception e) {
			} finally {
				int l=0;
				for (int i=0;i<allbufs.size();i++) l+=allbufs.get(i).length;
				byte[] aa = new byte[l];
				l=0;
				for (int i=0;i<allbufs.size();i++) {
					byte[] bb = allbufs.get(i);
					for (int j=0;j<bb.length;j++) aa[l+j]=bb[j];
					l+=bb.length;
				}
				FFT.properWAV(aa,0);
				BasketTracker.main.soundthread=null;
			}
		}
	});
	soundthread.start();
	
    }
    public void annule(View view) {
      if (canvas.viewmode==canvas.VUE_JOUEURA || canvas.viewmode==canvas.VUE_JOUEURB) {
        canvas.viewmode=canvas.VUE_SHOOTS;
        setText("Les points ne sont pas comptes");
      } else if (stats.size()>0) {
        String x=stats.get(stats.size()-1);
        stats.remove(stats.size()-1);
        String[] xx = x.split(" ");
        int action=Integer.parseInt(xx[2]);
        int team=Integer.parseInt(xx[0]);
        int delta=0;
        if (action==0) delta=2;
        else if (action==1) delta=3;
        else if (action==2) delta=1;
        if (team==0) canvas.pts0-=delta;
        else canvas.pts1-=delta;
        setText("derniere action annulee !");
      } 
      canvas.invalidate();
    }
    public void reset() {
      stats.clear();
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
