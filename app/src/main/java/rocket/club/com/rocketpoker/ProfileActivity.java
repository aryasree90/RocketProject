package rocket.club.com.rocketpoker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends ActionBarActivity {

    Context ctx = null;
    Button gotoHome;

    View.OnClickListener clickListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeWidgets();
        setClickListener();

//        ContactAsync contactAsync = new ContactAsync(this);
//        contactAsync.execute();

    }

    private void initializeWidgets() {
        ctx = getApplicationContext();
        gotoHome = (Button) findViewById(R.id.btn_toHome);
    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btn_toHome:
                        gotoHomeActivity();
                        break;
                }
            }
        };

        gotoHome.setOnClickListener(clickListener);
    }

    private void gotoHomeActivity() {
        Intent profileIntent = new Intent(ctx, LandingActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(profileIntent);
        this.finish();
    }
}