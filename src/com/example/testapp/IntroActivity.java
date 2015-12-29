package com.example.testapp;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.example.feeds.SampleSlide;
import com.github.paolorotolo.*;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;


public class IntroActivity extends AppIntro{
	@Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(first_fragment);
//        addSlide(second_fragment);
//        addSlide(third_fragment);
//        addSlide(fourth_fragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
//        addSlide(AppIntroFragment.newInstance("title", "description", R.drawable.ic_launcher, Color.parseColor("#2196F3")));
//
//        // OPTIONAL METHODS
//        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));
//
//        // Hide Skip/Done button.
//        showSkipButton(false);
//        setProgressButtonEnabled(false);
//
//        // Turn vibration on and set intensity.
//        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
//        setVibrate(true);
//        setVibrateIntensity(30);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
		addSlide(SampleSlide.newInstance(R.layout.intro));
        addSlide(SampleSlide.newInstance(R.layout.intro3));
        addSlide(SampleSlide.newInstance(R.layout.intro4));

        setZoomAnimation();
    }

    @Override
    public void onSkipPressed() {
    // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed() {
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
