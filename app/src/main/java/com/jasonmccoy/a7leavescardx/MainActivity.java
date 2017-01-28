package com.jasonmccoy.a7leavescardx;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ImageView[] cups = new ImageView[10];
    private int scanned = 0;
    private int previous = 0;
    private int totalNumberOfStamps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        setup();

    }


    private void setup() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        cups[0] = (ImageView) findViewById(R.id.g1);
        cups[1] = (ImageView) findViewById(R.id.g2);
        cups[2] = (ImageView) findViewById(R.id.g3);
        cups[3] = (ImageView) findViewById(R.id.g4);
        cups[4] = (ImageView) findViewById(R.id.g5);
        cups[5] = (ImageView) findViewById(R.id.g6);
        cups[6] = (ImageView) findViewById(R.id.g7);
        cups[7] = (ImageView) findViewById(R.id.g8);
        cups[8] = (ImageView) findViewById(R.id.g9);
        cups[9] = (ImageView) findViewById(R.id.g10);

        Glide.with(this).load(R.drawable.background).into(ivBack);

        for (int i = 0; i < 10; i++) {
            Glide.with(getApplicationContext()).load(R.drawable.empty_glass).into(cups[i]);
        }

        Glide.with(this).load(R.drawable.background).into(ivBack);

        if (getIsFirstLaunch()) {
            setIsFirstLaunch(false);
            startAlert();
        }
        scanned = getScannedCupsCount();
        previous = scanned;
        totalNumberOfStamps = getTotalNumberOfStamps();
        clearCups();
        updateCups(0);
    }

    public void redeem(View view) {
        animateReddem(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 11 && resultCode == RESULT_OK) {
            int result = data.getIntExtra("numberOfItems", 1);
            int tmpResult = result;
            scanned = scanned + tmpResult;
            if (scanned > 10) {
                clearCups();
                scanned = scanned - result;
                if (scanned == 9) {
                    if (result == 1) {
                        tmpResult = 1;
                        scanned = scanned + tmpResult;
                    } else if (result == 2) {
                        tmpResult = 1;
                        scanned = tmpResult;
                    }
                } else {
                    tmpResult = result;
                    scanned = tmpResult;
                }
            } else {
                updateNumberOfStamps();
            }

            updateScannedCupsCount(scanned);
            updateCups(tmpResult);
        }
    }

    private void updateNumberOfStamps() {
        totalNumberOfStamps++;
        setTotalNumberOfStamps(totalNumberOfStamps);
    }

    private void updateCups(int result) {

        for (int i = 0; i < scanned - result; i++) {
            Glide.with(getApplicationContext()).load(R.drawable.selected).into(cups[i]);
        }

        if (result > 0) {
            String ringtoneFileName = "click.mp3";
            new AudioPlayer(ringtoneFileName, this);

            switch (result) {
                case 1:
                    Glide.with(getApplicationContext()).load(R.drawable.empty_glass).into
                            (cups[scanned - 1]);
                    animateView(cups[scanned - 1]);
                    break;
                case 2:
                    if (scanned == 1) {
                        Glide.with(getApplicationContext()).load(R.drawable.empty_glass).into
                                (cups[scanned - 1]);
                        animateView(cups[scanned - 1]);
                    } else {
                        Glide.with(getApplicationContext()).load(R.drawable.empty_glass).into
                                (cups[scanned - 2]);
                        Glide.with(getApplicationContext()).load(R.drawable.empty_glass).into
                                (cups[scanned - 1]);
                        animateTwoView(cups[scanned - 2], cups[scanned - 1]);
                    }
                    break;
            }
        }
    }

    private void clearCups() {
        for (int i = 0; i < 10; i++) {
            Glide.with(getApplicationContext()).load(R.drawable.empty_glass).into(cups[i]);
        }
    }

    private int getScannedCupsCount() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int cops = sharedPref.getInt("cups", 0);
        return cops;
    }

    private void updateScannedCupsCount(int count) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("cups", count);
        editor.apply();
    }

    private void setIsFirstLaunch(boolean flag) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isFirstLaunch", flag);
        editor.apply();
    }

    private boolean getIsFirstLaunch() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean flag = sharedPref.getBoolean("isFirstLaunch", true);
        return flag;
    }

    private int getTotalNumberOfStamps() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int cops = sharedPref.getInt("stamps", 0);
        return cops;
    }

    private void setTotalNumberOfStamps(int count) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("stamps", count);
        editor.apply();
    }

    private void animateView(final View view) {

        // Load the animation
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        double animationDuration = 1500;
        myAnim.setDuration((long) animationDuration);

        // Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.30, 20.0);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);

        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Glide.with(getApplicationContext()).load(R.drawable.selected).into((ImageView)
                        view);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void animateTwoView(final View view1, final View view2) {

        Glide.with(getApplicationContext()).load(R.drawable.empty_glass).into((ImageView)
                view2);

        // Load the animation
        Animation myAnim1 = AnimationUtils.loadAnimation(this, R.anim.bounce);
        double animationDuration = 1500;
        myAnim1.setDuration((long) animationDuration);

        // Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.30, 20.0);
        myAnim1.setInterpolator(interpolator);
        view1.startAnimation(myAnim1);

        final Animation myAnim2 = AnimationUtils.loadAnimation(this, R.anim.bounce);
        myAnim2.setDuration((long) animationDuration);

        // Use custom animation interpolator to achieve the bounce effect
        myAnim2.setInterpolator(interpolator);

        myAnim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Glide.with(getApplicationContext()).load(R.drawable.selected).into((ImageView)
                        view1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view2.startAnimation(myAnim2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        myAnim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Glide.with(getApplicationContext()).load(R.drawable.selected).into((ImageView)
                        view2);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void animateReddem(View view) {

        // Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_button);
        double animationDuration = 700;
        myAnim.setDuration((long) animationDuration);

        // Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.15, 20.0);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);

        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent scan = new Intent(MainActivity.this, ScanActivity.class);
                startActivityForResult(scan, 11);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void startAlert() {
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + TimeUnit.DAYS.toMillis(3650), pendingIntent);
    }
}
