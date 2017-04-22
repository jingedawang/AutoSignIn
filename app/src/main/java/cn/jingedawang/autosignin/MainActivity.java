package cn.jingedawang.autosignin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import services.TiebaService;
import utils.Constants;
import utils.Status;
import utils.TiebaProgress;
import utils.Utils;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {

    private ScreenReceiver screenReceiver = new ScreenReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        Button btnRunWhenIdle = (Button) findViewById(R.id.btnRunWhenIdle);

        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.editText);
                String tieba = editText.getText().toString();
                Constants.tiebaName = tieba;

                Status.progress = TiebaProgress.Ready;

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int nowHour = calendar.get(Calendar.HOUR);
                int nowMinute = calendar.get(Calendar.MINUTE);
                if (timePicker.getHour() < nowHour || (nowHour == timePicker.getHour() && nowMinute >= timePicker.getMinute())) {
                    Utils.openCLD("com.baidu.tieba", MainActivity.this);
                }
                else {
                    AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                    calendar.set(Calendar.MINUTE, timePicker.getMinute());
                    manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                    Toast.makeText(getApplicationContext(), "定时任务已启动", Toast.LENGTH_SHORT).show();
                }



            }
        });

        btnRunWhenIdle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editText);
                String tieba = editText.getText().toString();
                Constants.tiebaName = tieba;
                Status.progress = TiebaProgress.Ready;
                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
                manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, pi);
                Toast.makeText(getApplicationContext(), "请锁屏，1分钟后执行", Toast.LENGTH_SHORT).show();
            }
        });

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        getApplicationContext().registerReceiver(screenReceiver, filter);

        if (!isAccessibilitySettingsOn(getApplicationContext())) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }

    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + TiebaService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }
}
