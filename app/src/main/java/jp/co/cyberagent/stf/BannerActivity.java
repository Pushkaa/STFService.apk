package jp.co.cyberagent.stf;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by Clutch on 30/12/15.
 */
public class BannerActivity extends Activity {

    public static final String EXTRA_MSG = "MSG";

    private TextView mLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        LinearLayout layout = new LinearLayout(this);
        layout.setKeepScreenOn(true);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        String msg = intent.getStringExtra(EXTRA_MSG);

        layout.setBackgroundColor(Color.BLACK);
        layout.setPadding(16, 16, 16, 16);
        layout.setGravity(Gravity.CENTER);

        mLetter = createLabel(msg);
        layout.addView(mLetter);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ensureVisibility();
        setContentView(layout);
    }

    private TextView createLabel(String text) {
        TextView titleView = new TextView(this);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextColor(Color.parseColor("#c1272d"));
        titleView.setTextSize(250f);
        titleView.setText(text);
        return titleView;
    }

    private void ensureVisibility() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        unlock();

        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = 1.0f;
        window.setAttributes(params);
    }

    @SuppressWarnings("deprecation")
    private void unlock() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        keyguardManager.newKeyguardLock("InputService/Unlock").disableKeyguard();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(BannerReceiver.ACTION_BANNER_CHANGE);
        filter.addAction(BannerReceiver.ACTION_BANNER_CLOSE);
        registerReceiver(mBannerBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBannerBroadcastReceiver);
    }

    private BroadcastReceiver mBannerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BannerReceiver.ACTION_BANNER_CHANGE)) {
                String letter = intent.getStringExtra("IVON");
                mLetter.setText(letter);

            } else if (intent.getAction().equals(BannerReceiver.ACTION_BANNER_CLOSE)) {
                finish();
            }

        }
    };

    public static class BannerReceiver extends BroadcastReceiver {

        public static final String ACTION_BANNER_SHOW = "jp.co.cyberagent.stf.ACTION_BANNER_SHOW";
        public static final String ACTION_BANNER_CHANGE = "jp.co.cyberagent.stf.ACTION_BANNER_CHANGE";
        public static final String ACTION_BANNER_CLOSE = "jp.co.cyberagent.stf.ACTION_BANNER_CLOSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_BANNER_SHOW)) {
                Intent intent1 = new Intent(context, BannerActivity.class);
                intent1.putExtra(BannerActivity.EXTRA_MSG, intent.getStringExtra("IVON"));
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
        }
    }
}
