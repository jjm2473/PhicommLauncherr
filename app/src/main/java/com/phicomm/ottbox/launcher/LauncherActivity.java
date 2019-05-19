package com.phicomm.ottbox.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.phicomm.ottbox.App;
import com.phicomm.ottbox.LauncherSelector;
import com.phicomm.ottbox.R;

public class LauncherActivity extends Activity {
    private static final long FAILSAFE_DELAY = 2000;
    private static final int FAILSAFE_CLICK = 5;
    private long ts;
    private int count;
    private long homeAfter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ts = System.currentTimeMillis() + FAILSAFE_DELAY;
        try {
            launch(App.getTargetComponent());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Whoops!?", Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_launcher);
        this.findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeAfter = Long.MAX_VALUE;
                Intent intent = App.createLauncher();
                intent.setPackage("com.android.settings");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                LauncherActivity.this.startActivity(intent);
            }
        });

        this.findViewById(R.id.launcher_selector).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch(null);
            }
        });
    }

    private boolean failsafe() {
        if (System.currentTimeMillis() > ts) {
            count = 1;
            ts = System.currentTimeMillis() + FAILSAFE_DELAY;
        } else {
            ++count;
            if (count >= FAILSAFE_CLICK) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        launch(failsafe()?null:App.getTargetComponent());
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        if (System.currentTimeMillis() > homeAfter) {
            launch(App.getTargetComponent());
        }
        super.onResume();
    }

    private void launch(ComponentName targetComponent) {
        Intent intent = App.createLauncher();
        if (targetComponent == null) {
            homeAfter = Long.MAX_VALUE;
            intent.setClass(this.getApplicationContext(), LauncherSelector.class);
        } else {
            homeAfter = System.currentTimeMillis()+100;
            intent.putExtra("android.intent.extra.FROM_HOME_KEY", true);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.setPackage(targetComponent.getPackageName());
            intent.setComponent(targetComponent);
        }
        try {
            this.startActivity(intent);
        } catch (RuntimeException e) {
            if (targetComponent == null) {
                throw e;
            } else {
                Toast.makeText(this, R.string.msg_launcher_fail, Toast.LENGTH_LONG).show();
                launch(null);
            }
        }
    }
}
