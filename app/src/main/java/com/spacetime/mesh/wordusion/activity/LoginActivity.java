package com.spacetime.mesh.wordusion.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.myapp.mehul.wordusion.R;
import com.spacetime.mesh.wordusion.app.AppConfig;
import com.spacetime.mesh.wordusion.app.AppController;
import com.spacetime.mesh.wordusion.helper.SQLiteHandler;
import com.spacetime.mesh.wordusion.helper.SessionManager;


/**
 * Created by mehul on 1/17/16.
 */
public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnLogin;
    private Button btnLinkToRegister;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private SessionManager session;
    private TextView header;

    private JSONObject jObj;
    @Override
    public void onCreate(Bundle savedInstanceState) {


        //  Initialize SharedPreferences
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        //  Create a new boolean and preference and set it to true
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
        //  If the activity has never started before..
        if (isFirstStart) {
            //  Launch app intro
            Intent i = new Intent(LoginActivity.this, MyAppIntro.class);
            startActivity(i);

            //  Make a new preferences editor
            SharedPreferences.Editor e = getPrefs.edit();

            //  Edit preference to make it false because we don't want this to run again
            e.putBoolean("firstStart", false);

            //  Apply changes
            e.apply();
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        header = (TextView)findViewById(R.id.textView);

        //changing the font
        Typeface myTypeFace = Typeface.createFromAsset(getAssets(), "fonts/thin.ttf");
        Typeface myTypeFace_2 = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        header.setTypeface(myTypeFace);
        btnLogin.setTypeface(myTypeFace);
        btnLinkToRegister.setTypeface(myTypeFace);
        inputEmail.setTypeface(myTypeFace_2);
        inputPassword.setTypeface(myTypeFace_2);


        //Progress dialog... this is used to show the progress
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        //SqlLite database Handler
        db = new SQLiteHandler(getApplicationContext());

        //create a new session
        session = new SessionManager(getApplicationContext());
        //check if the user is already logged in or not, if yes take him/her to the main Activity
        if (session.isLoggedIn()) {
            //just call the chat activity

            String usr = db.getUserDetails().get("username").toString();
            //mSocket.emit("add user", usr);

            Log.d(TAG, "already logged in");
            Intent intent = new Intent(LoginActivity.this, MainMenu.class);

            startActivity(intent);

            finish();
        }


        inputEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                inputEmail.requestFocus();
                return true;
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //check for empty data in the form

                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {

                    checkLogin(email, password);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter the credentials!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                finish();
            }
        });

        //try {
        //    checkConnection();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
    }


    //function to check if the credentials are right or not

    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        pDialog.show();
        JSONObject params = new JSONObject();

        try {
            params.put("username", email);
            params.put("password", password);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST,
                AppConfig.URL_LOGIN , params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        Log.d(TAG, response.toString());
                        try {
                            if(response.getBoolean("auth")){

                                    session.setLogin(true);
                                    String username = response.getString("username");
                                    String scoreWon = response.getString("score_won");
                                    String scoreLost = response.getString("score_lost");
                                    db.addUser(username, Integer.parseInt(scoreWon), Integer.parseInt(scoreLost));
                                    pDialog.hide();
                                    Intent i = new Intent(LoginActivity.this, MainMenu.class);
                                    startActivity(i);
                                    finish();

                                }
                            else{
                                pDialog.hide();
                                Toast.makeText(getApplicationContext(), "Incorrect username or password entered", Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception e){
                            pDialog.hide();
                            Toast.makeText(getApplicationContext(), "Could not log you in. Check your internet connection.", Toast.LENGTH_LONG).show();
                        }






                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.toString().equalsIgnoreCase("com.android.volley.AuthFailureError")){
                    Toast.makeText(getApplicationContext(), "Incorrect username or password.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Could not log you in. Check your internet connection.", Toast.LENGTH_LONG).show();
                }
                pDialog.hide();
                Log.d("shjdbc", "sdbc");
                Log.d(TAG, "Error: " + error.toString());
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

    private void checkConnection() throws IOException, InterruptedException {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Connecting to internet");
        progressDialog.show();
        if(!isConnected()){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("You are currently not connected to internet!").setCancelable(
                    false).setPositiveButton("Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                checkConnection();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
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
        else {
            progressDialog.hide();
        }
    }

    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }
}

