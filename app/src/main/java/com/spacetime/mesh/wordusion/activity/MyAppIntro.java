package com.spacetime.mesh.wordusion.activity;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.spacetime.mesh.wordusion.appIntro.Fifth;
import com.spacetime.mesh.wordusion.appIntro.First;
import com.spacetime.mesh.wordusion.appIntro.Fourth;
import com.spacetime.mesh.wordusion.appIntro.Second;
import com.spacetime.mesh.wordusion.appIntro.Seventh;
import com.spacetime.mesh.wordusion.appIntro.Sixth;
import com.spacetime.mesh.wordusion.appIntro.Third;

/**
 * Created by mehul on 2/28/16.
 */
public class MyAppIntro extends AppIntro {
    @Override
    public void init(Bundle savedInstanceState){
        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        //addSlide(AppIntroFragment.newInstance("Hello", "World", R.drawable.google_thumb, Color.parseColor("#000000")));
        //addSlide(AppIntroFragment.newInstance("My first", "Intro", R.drawable.google_thumb, Color.parseColor("#000000")));
        //First first = new First();
        //Second second = new Second();
        //Third third = new Third();
        //Fourth fourth = new Fourth();
        //addSlide(());
        addSlide(new First());
        addSlide(new Second());
        addSlide(new Third());
        addSlide(new Fourth());
        addSlide(new Fifth());
        addSlide(new Sixth());
        addSlide(new Seventh());
        // OPTIONAL METHODS
        // Override bar/separator color.

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed() {
        finish();
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed() {
        finish();
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }
}
