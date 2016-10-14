package com.spacetime.mesh.wordusion.appIntro;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myapp.mehul.wordusion.R;

/**
 * Created by mehul on 4/3/16.
 */
public class Sixth extends Fragment {

    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sixth, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);
        Typeface myTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/thin.ttf");
        textView = (TextView)view.findViewById(R.id.inst);
        textView.setTypeface(myTypeFace);


        String instructions = "Put the word in between '*' while guessing the opponent's word.\n\nExample : *word*";

        textView.setText(instructions);
    }
}
