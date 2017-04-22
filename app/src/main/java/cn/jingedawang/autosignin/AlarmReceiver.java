package cn.jingedawang.autosignin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import utils.Utils;

/**
 * Created by wjg on 2017/4/21.
 */
public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.wakeUpAndUnlock(context);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Utils.openCLD("com.baidu.tieba", context);
    }
}
