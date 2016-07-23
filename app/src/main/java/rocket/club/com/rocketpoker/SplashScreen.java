package rocket.club.com.rocketpoker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import rocket.club.com.rocketpoker.utils.AppGlobals;

public class SplashScreen extends Activity {

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    /** Called when the activity is first created. */

    Context ctx = null;
    Thread splashTread;
    AppGlobals appGlobals = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ctx = getApplicationContext();
        appGlobals = AppGlobals.getInstance();
        appGlobals.init(ctx);

        StartAnimations();
    }
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    startNewActivity();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreen.this.finish();
                }
            }
        };
        splashTread.start();
    }

    private void startNewActivity() {
        Intent loginActivity = null;

        if(appGlobals.sharedPref.getLogInStatus()) {
            loginActivity = new Intent(SplashScreen.this, LandingActivity.class);
        } else {
            loginActivity = new Intent(SplashScreen.this, LoginActivity.class);
            loginActivity.putExtra(LoginActivity.pageType, LoginActivity.loginPage);
        }

        startActivity(loginActivity);
        this.finish();
    }
}