package com.fanstatic.mc;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

public class FloatingService extends Service {
    private WindowManager manager;
    private TextView bubble;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable watcher = new Runnable() {
        @Override public void run() {
            if (Settings.canDrawOverlays(FloatingService.this) && isMinecraftRecent()) showBubble();
            else hideBubble();
            handler.postDelayed(this, 1000);
        }
    };

    @Override public void onCreate() {
        super.onCreate();
        manager = (WindowManager)getSystemService(WINDOW_SERVICE);
        handler.post(watcher);
    }

    private boolean isMinecraftRecent() {
        try {
            UsageStatsManager usm = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
            long now = System.currentTimeMillis();
            List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 10000, now);
            UsageStats latest = null;
            if (stats != null) for (UsageStats s : stats)
                if (latest == null || s.getLastTimeUsed() > latest.getLastTimeUsed()) latest = s;
            return latest != null && "com.mojang.minecraftpe".equals(latest.getPackageName())
                && now - latest.getLastTimeUsed() < 10000;
        } catch (Exception e) { return false; }
    }

    private int overlayType() {
        return Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;
    }

    private void showBubble() {
        if (bubble != null) return;
        TextView t = new TextView(this);
        t.setText("F");
        t.setTextColor(Color.WHITE);
        t.setTextSize(23);
        t.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(70,124,212));
        bg.setShape(GradientDrawable.OVAL);
        t.setBackground(bg);
        t.setOnClickListener(v -> showPanel());
        WindowManager.LayoutParams p = new WindowManager.LayoutParams(58,58,overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        p.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        p.x = 14;
        try { manager.addView(t,p); bubble = t; } catch (Exception ignored) {}
    }

    private void hideBubble() {
        if (bubble != null) {
            try { manager.removeView(bubble); } catch (Exception ignored) {}
            bubble = null;
        }
    }

    private void showPanel() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(20,20,20,20);
        panel.setBackgroundColor(Color.rgb(13,18,26));
        TextView title = new TextView(this);
        title.setText("FantasticMC");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        panel.addView(title);
        String[] names = {"HUD Editor", "Name Prefix: [FMC]", "FPS Counter: ON", "Freelook: UI demo", "Zoom: UI demo"};
        for (String name : names) {
            Button b = new Button(this);
            b.setText(name);
            b.setTextColor(Color.WHITE);
            b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(70,124,212)));
            panel.addView(b);
        }
        Button close = new Button(this);
        close.setText("Close");
        close.setOnClickListener(v -> { try { manager.removeView(panel); } catch (Exception ignored) {} });
        panel.addView(close);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams(320,
            WindowManager.LayoutParams.WRAP_CONTENT, overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        p.gravity = Gravity.CENTER;
        try { manager.addView(panel,p); } catch (Exception ignored) {}
    }

    @Override public int onStartCommand(Intent i, int flags, int id) { return START_STICKY; }
    @Override public void onDestroy() { handler.removeCallbacks(watcher); hideBubble(); super.onDestroy(); }
    @Override public IBinder onBind(Intent i) { return null; }
}
