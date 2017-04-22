package services;

/**
 * Created by wjg on 2017/4/16.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import cn.jingedawang.autosignin.AlarmReceiver;
import cn.jingedawang.autosignin.SigninReceiver;
import utils.Constants;
import utils.Status;
import utils.TiebaProgress;

public class TiebaService extends AccessibilityService {

    public static TiebaService instance;
    private int index = 1;
    private Lock lock = new ReentrantLock();

    /**
     * 获取到短信通知
     *  0.唤醒屏幕
     *  1.打开钉钉
     *  2.确保当前页是主页界面
     *  3.找到“工作”tab并且点击
     *  4.确保到达签到页面
     *  5.找到签到按钮，并且点击
     *  6.判断签到是否成功
     *      1.成功，退出程序
     *      2.失败，返回到主页，重新从1开始签到
     */


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub
//       final int eventType = event.getEventType();

        if (Status.progress != TiebaProgress.Ready) {
            return;
        }

        if (!lock.tryLock()) {
            return;
        }

        ArrayList<String> texts = new ArrayList<String>();
        Log.i(Constants.TAG, "事件---->" + event.getEventType());
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {

        }


        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.i(Constants.TAG, "rootWindow为空");
            return;
        }
        System.out.println("nodeInfo" + nodeInfo);


        List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText("进吧");
        if (onclick(nodes)) {
            Log.i(Constants.TAG, "点击进吧");
        }
        else {
            lock.unlock();
            Status.progress = TiebaProgress.Failed;
            Toast.makeText(getApplicationContext(), "进吧失败", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {

        }
        Status.progress = TiebaProgress.Jinba;
        nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodes2 = nodeInfo.findAccessibilityNodeInfosByText(Constants.tiebaName);
        if (onclick(nodes2)) {
            Log.i(Constants.TAG, "点击围棋");
        }
        else {

            Status.progress = TiebaProgress.Failed;
            Toast.makeText(getApplicationContext(), "围棋失败", Toast.LENGTH_SHORT).show();
            lock.unlock();
            return;
        }

        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {

        }
        Status.progress = TiebaProgress.Wangfei;
        nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodes3 = nodeInfo.findAccessibilityNodeInfosByText("签到");

        if (onclick(nodes3)) {
            Log.i(Constants.TAG, "签到成功");
            Intent i = new Intent(getApplicationContext(), SigninReceiver.class);
            i.putExtra("isSigninSucceed", true);
            sendBroadcast(i);
            Status.progress = TiebaProgress.Finished;
            try {
                Thread.sleep(5000);
            }
            catch (Exception e) {

            }
            performGlobalAction(GLOBAL_ACTION_BACK);
            try {
                Thread.sleep(1000);
            }
            catch (Exception e) {

            }
            performGlobalAction(GLOBAL_ACTION_BACK);
            performGlobalAction(GLOBAL_ACTION_BACK);
        }
        else {
            Status.progress = TiebaProgress.Failed;
            Toast.makeText(getApplicationContext(), "签到失败", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), SigninReceiver.class);
            i.putExtra("isSigninSucceed", false);
            sendBroadcast(i);
        }
        lock.unlock();
    }

    private boolean onclick(List<AccessibilityNodeInfo> views){
        for (int i=0; i<views.size(); i++) {
            if(views.get(i).isClickable()) {
                views.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return true;
            }else{

//                AccessibilityNodeInfo parent = views.get(i).getParent();
//                if(parent==null){
//                    return false;
//                }
//                ArrayList<AccessibilityNodeInfo> parents = new ArrayList<AccessibilityNodeInfo>();
//                parents.add(parent);
//                return onclick(parents);
            }
        }
        for (int i=0; i<views.size(); i++) {
            AccessibilityNodeInfo parent = views.get(i).getParent();
            if(parent==null){
                continue;
            }
            if(parent.isClickable()) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return true;
            }
        }

        return false;
    }


    @Override
    public void onInterrupt() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onServiceConnected() {
        // TODO Auto-generated method stub
        super.onServiceConnected();
        Log.i(Constants.TAG, "service connected!");
        Toast.makeText(getApplicationContext(), "连接成功！", Toast.LENGTH_SHORT).show();
        instance = this;
    }

}
