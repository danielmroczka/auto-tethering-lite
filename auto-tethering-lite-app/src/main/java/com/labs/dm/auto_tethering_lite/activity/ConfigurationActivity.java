package com.labs.dm.auto_tethering_lite.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.labs.dm.auto_tethering_lite.R;
import com.labs.dm.auto_tethering_lite.Utils;
import com.labs.dm.auto_tethering_lite.receiver.TetheringWidgetProvider;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

/**
 * Created by Daniel Mroczka on 2016-01-09.
 */
public class ConfigurationActivity extends Activity {

    private int mAppWidgetId;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        setResult(RESULT_CANCELED);
        init();
    }

    private void init() {
        editMode = getIntent().getExtras() != null && getIntent().getExtras().getBoolean("editMode", false);
        mAppWidgetId = Utils.getWidgetId(getIntent());
        if (mAppWidgetId == INVALID_APPWIDGET_ID) {
            Log.e("WidgetAdd", "Cannot continue. Widget ID incorrect");
        }

        CheckBox mobile = (CheckBox) findViewById(R.id.chkWidget3G);
        CheckBox tethering = (CheckBox) findViewById(R.id.chkWidgetWifi);
        CheckBox startService = (CheckBox) findViewById(R.id.chkStartService);
        CheckBox autoStart = (CheckBox) findViewById(R.id.chkAutoStart);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mobile.setChecked(prefs.getBoolean(key("mobile"), false));
        tethering.setChecked(prefs.getBoolean(key("tethering"), true));
        startService.setChecked(prefs.getBoolean(key("start.service"), false));
        autoStart.setChecked(prefs.getBoolean(key("autostart"), false));

        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setText(editMode ? "MODIFY WIDGET" : "ADD WIDGET");
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                handleOkButton();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mobile.setChecked(false);
            mobile.setEnabled(false);
        }
    }

    private void handleOkButton() {
        saveWidget();
    }

    private void saveWidget() {
        CheckBox mobile = (CheckBox) findViewById(R.id.chkWidget3G);
        CheckBox tethering = (CheckBox) findViewById(R.id.chkWidgetWifi);
        CheckBox startService = (CheckBox) findViewById(R.id.chkStartService);
        CheckBox autoStart = (CheckBox) findViewById(R.id.chkAutoStart);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.edit().putBoolean(key("mobile"), mobile.isChecked()).apply();
        prefs.edit().putBoolean(key("tethering"), tethering.isChecked()).apply();
        prefs.edit().putBoolean(key("start.service"), startService.isChecked()).apply();
        prefs.edit().putBoolean(key("auto.start"), autoStart.isChecked()).apply();

        if (!editMode) {
            Toast.makeText(this, "Double tap on widget to modify settings", Toast.LENGTH_LONG).show();
        }

        Intent serviceIntent = new Intent(ConfigurationActivity.this, TetheringWidgetProvider.class);
        serviceIntent.putExtra(EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, serviceIntent);
        startService(serviceIntent);
        finish();
    }

    private String key(String key) {
        return key;//"widget." + mAppWidgetId + "." + key;
    }
}