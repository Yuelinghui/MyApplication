package com.yuelinghui.personal.myapplication.home.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.yuelinghui.personal.maframe.UIData;
import com.yuelinghui.personal.myapplication.R;
import com.yuelinghui.personal.myapplication.core.AppBroadcastAction;
import com.yuelinghui.personal.myapplication.core.AppRunningContext;
import com.yuelinghui.personal.widget.core.ui.BaseActivity;
import com.yuelinghui.personal.myapplication.home.ui.recyclerview.RecyclerFragment;
import com.yuelinghui.personal.myapplication.util.BroadcastUtil;
import com.yuelinghui.personal.widget.dialog.CustomDialog;
import com.yuelinghui.personal.widget.titlebar.CustomAction;
import com.yuelinghui.personal.widget.titlebar.CustomTitleBar;
import com.yuelinghui.personal.widget.toast.CustomExitToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuelinghui on 16/9/30.
 */

public class MainActivity extends BaseActivity {


    /**
     * 点击回退建，记录当前时间
     */
    private long exitTime = 0;

    private List<CustomAction> mActionList;

    private IntentFilter mNightModeFilter = new IntentFilter(
            AppBroadcastAction.NIGHT_MODE_CHANGE);
    private NightModeReceiver mNightModeReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentViewAndTitle(FRAGMENT_LAYOUT, getString(R.string.main_title));
        String rightText = getString(R.string.main_menu_night_mode);
        if (AppRunningContext.isNightMode()) {
            rightText = getString(R.string.main_menu_day_mode);
            setTitleBarColor(getResources().getColor(R.color.night_mode_background));
            setTheme(R.style.NightTheme);
        }
        mActionList = new ArrayList<>();
        // 日间或夜间
        CustomAction action = new CustomAction();
        action.menuTitle = rightText;
        // 声明
        CustomAction statement = new CustomAction();
        statement.menuTitle = getString(R.string.main_menu_statement);
        mActionList.add(action);
        mActionList.add(statement);
        setActions(mActionList);
        setActionClickListener(mRightClick);
        AppRunningContext.sAppData.sIsExitApp = false;

        if (savedInstanceState == null) {
            load();
        }

        // 注册夜间切换广播
        mNightModeReceiver = new NightModeReceiver();
        registerReceiver(mNightModeReceiver, mNightModeFilter);
    }

    @Override
    protected void load() {
        startFirstFragment(new RecyclerFragment());
    }

    @Override
    protected UIData initUIData() {
        return new MainData();
    }

    private CustomTitleBar.ActionClickListener mRightClick = new CustomTitleBar.ActionClickListener() {
        @Override
        public void onClick(CustomAction menu) {
            int index = mActionList.indexOf(menu);
            switch (index) {
                case 0:
                    boolean isNight = !AppRunningContext.isNightMode();
                    AppRunningContext.setNightMode(isNight);
                    BroadcastUtil.sendNightModeChange(MainActivity.this);
                    menu.menuTitle = isNight ? getString(R.string.main_menu_day_mode) : getString(R.string.main_menu_night_mode);
                    mActionList.remove(index);
                    mActionList.add(index, menu);
                    setActions(mActionList);
                    break;
                case 1:
                    final CustomDialog dialog = new CustomDialog(MainActivity.this);
                    dialog.setMsg(getString(R.string.statement_message));
                    dialog.setTitle(getString(R.string.main_menu_statement));
                    dialog.setOkButton(getString(R.string.statement_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {

        // 点击回退键操作
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            CustomExitToast.makeText(getString(R.string.quit_remind)).show();
            exitTime = System.currentTimeMillis();
        } else {
            AppRunningContext.exitAPP();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNightModeReceiver != null) {
            unregisterReceiver(mNightModeReceiver);
        }
    }

    class NightModeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppBroadcastAction.NIGHT_MODE_CHANGE)) {
                if (AppRunningContext.isNightMode()) {
                    setTitleBarColor(getResources().getColor(R.color.night_mode_item_background));
                    setTheme(R.style.NightTheme);
                } else {
                    setTitleBarColor(Color.WHITE);
                    setTheme(R.style.DayTheme);
                }
                startFirstFragment(new RecyclerFragment());
            }
        }
    }

}
