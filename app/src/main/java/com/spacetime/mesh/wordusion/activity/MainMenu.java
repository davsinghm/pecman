package com.spacetime.mesh.wordusion.activity;

/**
 * Created by mehul on 1/29/16.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.myapp.mehul.wordusion.R;
import com.spacetime.mesh.wordusion.MainActivity;
import com.spacetime.mesh.wordusion.app.AppConfig;
import com.spacetime.mesh.wordusion.app.AppController;
import com.spacetime.mesh.wordusion.helper.SQLiteHandler;
import com.spacetime.mesh.wordusion.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainMenu extends AppCompatActivity {
    private static final String TAG = MainMenu.class.getSimpleName();

    private TextView txtEmail;
    private TextView btnLogout;
    private TextView inst;
    private TextView btnChat;
    private TextView btnMulti;
    private String opponentId;
    private String you;
    private boolean stroke;
    private String username;
    private SQLiteHandler db;
    private SessionManager session;
    private TextView welcome;
    private TextView won;
    private TextView lost;
    private TextView wonNo;
    private TextView lostNo;
    private TextView header;
    private ProgressDialog pDialog;
    private int wonScore;
    private int lostScore;
    boolean doubleBackToExitPressedOnce = false;
    boolean connected = false;
    boolean reachable = false;

    //private Socket mSocket;

    /*{
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {

            throw new RuntimeException(e);
        }
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        //checkConnection();

        setContentView(R.layout.mainmenu);

        db = new SQLiteHandler(getApplicationContext());

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        HashMap<String, String> user = db.getUserDetails();

        username = user.get("username");
        wonScore = Integer.parseInt(user.get("won"));
        lostScore = Integer.parseInt(user.get("lost"));

        btnLogout = (TextView) findViewById(R.id.btnLogout);
        btnChat = (TextView) findViewById(R.id.btnchat);
        btnMulti = (TextView) findViewById(R.id.btnmulti);
        header = (TextView) findViewById(R.id.logo);
        inst = (TextView) findViewById(R.id.instructions);
        won = (TextView) findViewById(R.id.won);
        lost = (TextView) findViewById(R.id.lost);
        wonNo = (TextView) findViewById(R.id.wonNo);
        lostNo = (TextView) findViewById(R.id.lostNo);

        won.setText("WON");
        lost.setText("LOST");
        wonNo.setText(wonScore + "");
        lostNo.setText(lostScore + "");
        stroke = true;

        //changing the fonts
        Typeface myTypeFace = Typeface.createFromAsset(getAssets(), "fonts/thin.ttf");
        inst.setTypeface(myTypeFace);
        btnLogout.setTypeface(myTypeFace);
        btnChat.setTypeface(myTypeFace);
        btnMulti.setTypeface(myTypeFace);
        won.setTypeface(myTypeFace);
        lost.setTypeface(myTypeFace);
        header.setTypeface(myTypeFace);
        lostNo.setTypeface(myTypeFace);
        wonNo.setTypeface(myTypeFace);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
                builder.setMessage("Are you sure you want to log-out?").setCancelable(
                        false).setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                logoutUser();
                            }
                        }).setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stroke) {
                    stroke = false;
                    goToChat();
                }
            }
        });

        btnMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stroke) {
                    stroke = false;
                    goToChat2();
                }
            }
        });

        inst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenu.this, MyAppIntro.class);
                startActivity(i);
            }
        });

        JSONObject params = new JSONObject();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                AppConfig.URL_ROOT, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        Log.d(TAG, response.toString());

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No internet Connection!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error in response");
                Log.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }


    private void goToChat() {


        Log.d("go to chat", "was called");

        Intent intent = new Intent(MainMenu.this, MainActivity.class);
        //check if the activity is opening only once, prevent multiple instances of the same
        //activity
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    private void goToChat2() {

        Log.d("go to chat 2", "was called");

        Intent intent = new Intent(MainMenu.this, LoadingScreen.class);
        //check if the activity is opening only once, prevent multiple instances of the same
        //activity
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("username", username);
        intent.putExtra("isMultiplayer", true);
        startActivity(intent);
        finish();
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        pDialog.setMessage("Logging out...");
        pDialog.show();

        JSONObject params = new JSONObject();

        try {
            params.put("logout", true);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                AppConfig.URL_LOGOUT, params,
                new Response.Listener<JSONObject>() {
                    //at this point the request has been sent, waiting for the response.
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //if the response comes out to be true
                            //continue on the login screen.
                            if (response.getBoolean("logout")) {
                                pDialog.hide();
                                Intent intent = new Intent(
                                        MainMenu.this,
                                        LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Logout Failed!!!!!!!", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                        }

                        // Launch login activity

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Cannot log you out, check your internet connection.", Toast.LENGTH_SHORT).show();
                pDialog.hide();
                Log.d(TAG, "sdbc");
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    //check if user presses two times back button, if yes exit the app

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(this, "Please press back again to exit the app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void checkConnection() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            reachable = (returnVal == 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!reachable) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
            builder.setMessage("You are currently not connected to internet!").setCancelable(
                    false).setPositiveButton("Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            checkConnection();
                        }
                    }).setNegativeButton("Quit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

}
