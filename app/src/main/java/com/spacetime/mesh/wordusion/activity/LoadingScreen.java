package com.spacetime.mesh.wordusion.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.myapp.mehul.wordusion.R;
import com.spacetime.mesh.wordusion.MainActivity;
import com.spacetime.mesh.wordusion.app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by mehul on 2/6/16.
 */
public class LoadingScreen extends Activity {
    private static final String TAG = LoadingScreen.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    private Socket mSocket;
    private String you;
    private String opponentId;
    private String username;
    private String word;
    private String definition;
    private String opponentWord;
    private String opponentName;
    private String partOfSpeech;

    public void onCreate(Bundle icicle) {

        {
            try {
                mSocket = IO.socket(Constants.CHAT_SERVER_URL);
            } catch (URISyntaxException e) {

                throw new RuntimeException(e);
            }
        }
        super.onCreate(icicle);
        setContentView(R.layout.loadingcreen);
        ImageView gif = (ImageView) findViewById(R.id.imageView);
        //using Ion Library to diplay the gif!
        Ion.with(gif).load("android.resource://com.mesh.wordusion/" + R.drawable.ripple);

        mSocket.connect();
        //call add user
        mSocket.emit("add user");
        //start a listener for opponent
        mSocket.on("opponent", onOpponent);

        mSocket.on("opponent word", onOpponentWord);
        //initialise the username
        username = getIntent().getExtras().getString("username");
        //listener for game start
        mSocket.on("game start", gameStart);

    }


    public void onDestroy() {
        super.onDestroy();

        //mSocket.disconnect();
        mSocket.off("opponent", onOpponent);
        mSocket.off("game start", gameStart);
        mSocket.off("opponent word", onOpponentWord);

    }

    private Emitter.Listener onOpponent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LoadingScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    try {
                        you = data.getString("you");
                        opponentId = data.getString("opponent");
                        data.put("username", username);

                        //adding the name of the user and sending it
                        //to the server

                        Log.d(TAG, data.toString());

                        mSocket.emit("game start", data);

                    } catch (JSONException e) {
                        return;
                    }


                }
            });
        }
    };

    private Emitter.Listener gameStart = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LoadingScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        word = data.getString("word");
                        definition = data.getString("definition");
                        opponentName = data.getString("opponentUsername");
                        partOfSpeech = data.getString("partofspeech");

                        //mSocket.off("game start", gameStart);
                        final JSONObject params = new JSONObject();

                        try {
                            params.put("you", you);
                            params.put("opponent", opponentId);
                            params.put("word", word);
                            //params.put("word", word);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        mSocket.emit("opponent word", params);
                        if (opponentWord != null) {
                            Intent i = new Intent(LoadingScreen.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            i.putExtra("opponentId", opponentId);
                            i.putExtra("you", you);
                            i.putExtra("username", username);
                            i.putExtra("opponentName", opponentName);
                            i.putExtra("word", word);
                            i.putExtra("definition", definition);
                            i.putExtra("opponentWord", opponentWord);
                            i.putExtra("pos", partOfSpeech);
                            Log.d(TAG, "moving to word display");
                            startActivity(i);
                            finish();
                        }

                        Log.d(TAG, data.toString() + " word received");
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    private Emitter.Listener onOpponentWord = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LoadingScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    Log.d(TAG, data.toString() + " opponent word received");
                    try {
                        opponentWord = data.getString("opponentWord");
                    } catch (JSONException e) {
                        return;
                    }
                    if (word != null) {
                        Intent i = new Intent(LoadingScreen.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        i.putExtra("opponentId", opponentId);
                        i.putExtra("you", you);
                        i.putExtra("opponentName", opponentName);
                        i.putExtra("username", username);
                        i.putExtra("word", word);
                        i.putExtra("definition", definition);
                        i.putExtra("opponentWord", opponentWord);
                        i.putExtra("pos", partOfSpeech);
                        Log.d(TAG, "moving to word display");
                        startActivity(i);
                        finish();
                    }
                }
            });
        }

    };


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoadingScreen.this);
        builder.setMessage("Are you sure you want to quit?").setCancelable(
                false).setPositiveButton("QUIT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JSONObject discon = new JSONObject();
                        try {
                            discon.put("opponent", opponentId);
                            discon.put("you", you);
                            discon.put("where", "LoadingScreen onBackPressed");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        mSocket.emit("discon", discon);
                        mSocket.disconnect();
                        //finish the current activity.
                        Intent intent = new Intent(LoadingScreen.this, MainMenu.class);
                        startActivity(intent);
                        LoadingScreen.this.finish();

                    }
                }).setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
