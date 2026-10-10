package com.fanstatic.mc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int BLUE = Color.rgb(70,124,212);
    private LinearLayout page;

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        showHome();
    }

    private TextView label(String value, int size) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextColor(Color.WHITE);
        t.setTextSize(size);
        t.setPadding(12, 14, 12, 14);
        return t;
    }

    private Button makeButton(String value, Runnable action) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(BLUE));
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private void base(String title) {
        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.TOP);
        page.setPadding(24, 24, 24, 24);
        page.setBackgroundColor(Color.rgb(7,9,13));
        page.addView(label(title, 28));
        setContentView(page);
    }

    private void showHome() {
        base("FantasticMC");
        page.addView(label("v1.0  •  CLIENT READY", 14));
        page.addView(label("Android client companion prototype", 16));
        page.addView(makeButton("Enable Floating Logo", () -> {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())));
            } else {
                startService(new Intent(this, FloatingService.class));
            }
        }));
        page.addView(makeButton("Enable Minecraft Detection", () ->
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))));
        page.addView(makeButton("Start Floating Client", () ->
            startService(new Intent(this, FloatingService.class))));
        page.addView(makeButton("HUD Editor", this::showHudEditor));
        page.addView(label("Note: this starter build is a UI prototype. It does not inject into or modify Minecraft.", 13));
    }

    private void showHudEditor() {
        base("FantasticMC • HUD Editor");
        page.addView(label("Tap a row to toggle its preview state.", 14));
        String[] modules = {"FPS Counter", "CPS Counter", "Coordinates", "Keystrokes", "Armor HUD", "Potion HUD", "Clock"};
        for (String module : modules) {
            final Button b = makeButton("ON  •  " + module, () ->
                bTogglePlaceholder());
            b.setOnClickListener(v -> {
                String current = b.getText().toString();
                b.setText(current.startsWith("ON") ? "OFF • " + module : "ON  •  " + module);
            });
            page.addView(b);
        }
        page.addView(makeButton("Back", this::showHome));
    }

    private void bTogglePlaceholder() { }
}
