package com.spacetime.mesh.wordusion.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.myapp.mehul.wordusion.R;
import com.spacetime.mesh.wordusion.app.AppController;
import com.spacetime.mesh.wordusion.helper.SQLiteHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mehul on 2/17/16.
 */
public class GameOver extends Activity{
    private static final String TAG = "GameOver.class";
    private TextView result;
    private TextView gameOver;
    private String finResult;
    private String username;
    private String opponentWord;
    private TextView oppoWord;
    private boolean won;
    private SQLiteHandler db;
    private int wonScore;
    private int lostScore;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gameover);

        result = (TextView)findViewById(R.id.result);
        gameOver = (TextView)findViewById(R.id.gameover);
        oppoWord = (TextView)findViewById(R.id.oppoWord);
        finResult = getIntent().getExtras().getString("result");
        opponentWord = getIntent().getExtras().getString("opponentWord");
        username = getIntent().getExtras().getString("username");
        result.setText(finResult);
        oppoWord.setText(opponentWord);
        if(finResult.equalsIgnoreCase("you won!"))
            won = true;
        else
            won = false;

        updateScore(won);

        //changing the font.
        Typeface myTypeFace = Typeface.createFromAsset(getAssets(), "fonts/thin.ttf");
        result.setTypeface(myTypeFace);
        gameOver.setTypeface(myTypeFace);
        oppoWord.setTypeface(myTypeFace);


    }

    private void updateScore(boolean result){

        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        wonScore = Integer.parseInt(user.get("won"));
        lostScore = Integer.parseInt(user.get("lost"));

        JSONObject params = new JSONObject();

        String url = "http://ninchat.herokuapp.com/user/update/score/" + username + "/" + result;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url , params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error in response");
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

        Log.d(TAG, "wonscore "+ wonScore + " lostscore " + lostScore + " " + result);
        if(result)
            db.updateUser(username, wonScore + 1, lostScore);
        else
            db.updateUser(username, wonScore, lostScore + 1);
    }


    public void onBackPressed(){
        Intent i = new Intent(GameOver.this, MainMenu.class);
        startActivity(i);
        finish();
    }
}
