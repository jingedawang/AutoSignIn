package cn.jingedawang.autosignin;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import utils.Constants;
import utils.Utils;


/**
 * Created by wjg on 2017/4/22.
 */

public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.TAG, "关闭屏幕");
//        String action = intent.getAction();
//        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Utils.wakeUpAndUnlock(context);
//
//        }
    }


}
