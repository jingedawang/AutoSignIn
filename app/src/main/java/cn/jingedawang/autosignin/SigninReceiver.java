package cn.jingedawang.autosignin;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import utils.Utils;

/**
 * Created by wjg on 2017/4/21.
 */

public class SigninReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isSigninSucceed = intent.getBooleanExtra("isSigninSucceed", false);
        String message = isSigninSucceed ? "签到成功" : "签到失败";
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification(R.mipmap.ic_launcher, message, System.currentTimeMillis());
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        builder.setContentTitle(message);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker(message);
        manager.notify(0, builder.build());
    }
}
