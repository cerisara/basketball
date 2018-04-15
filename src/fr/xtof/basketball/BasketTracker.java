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

public class BasketTracker extends FragmentActivity {
    public static BasketTracker main;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        main=this;
        setContentView(R.layout.main);
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

    public void cancel(View view) {
    }
    public void menu(View view) {
    }
    public void chrono(View view) {
    }
    public void bancLeft(View view) {
    }
    public void bancRight(View view) {
    }
    public void j0(View view) {
    }
    public void j1(View view) {
    }
}