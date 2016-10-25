/*
 *     Copyright © 2016 Fantasymaker
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all
 *     copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *     SOFTWARE.
 */

package com.lixplor.taskminer.function.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.lixplor.taskminer.R;
import com.lixplor.taskminer.base.BaseApp;
import com.lixplor.taskminer.bean.Task;
import com.lixplor.taskminer.repository.TaskDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created :  2016-10-24
 * Author  :  Fantasymaker
 * Web     :  http://blog.fantasymaker.cn
 * Email   :  me@fantasymaker.cn
 */
public class TaskWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_DEL_ITEM = "com.lixplor.taskminer.ACTION_DEL_ITEM";
    public static final String ACTION_BONUS_CHANGED = "com.lixplor.taskminer.ACTION_BONUS_CHANGED";

    private TaskDao mTaskDao;
    private int mTotalBonus;


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.d("aa", "onReceive: Action=" + action);
        switch (action) {
            case AppWidgetManager.ACTION_APPWIDGET_UPDATE:
                updateData(context);
                break;
            case ACTION_DEL_ITEM:
                Task task = (Task) intent.getSerializableExtra("task");
                if (task != null) {
                    if(mTaskDao == null){
                        mTaskDao = BaseApp.getDaoSession().getTaskDao();
                    }
                    mTaskDao.delete(task);
                    updateData(context);
                }
                updateTotalCoin(context);
                break;
            case ACTION_BONUS_CHANGED:
                updateTotalCoin(context);
                break;
        }
    }

    private void updateTotalCoin(Context context){
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = mgr.getAppWidgetIds(new ComponentName(context, TaskWidgetProvider.class));
        onUpdate(context, mgr, appWidgetIds);
    }

    private void updateData(Context context) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = mgr.getAppWidgetIds(new ComponentName(context, TaskWidgetProvider.class));
        mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_tasks);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d("aa", "onUpdate(): appWidgetIds.length=" + appWidgetIds.length);

        List<RemoteViews> mRemoteViewses = new ArrayList<>();

        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent intent = new Intent(context, TaskRemoteService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_tasks);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.lv_tasks, intent);
            rv.setEmptyView(R.id.lv_tasks, R.id.lv_tasks);

            // total bonus
            if(mTaskDao == null){
                mTaskDao = BaseApp.getDaoSession().getTaskDao();
            }
            List<Task> tasks = mTaskDao.loadAll();
            for(Task task : tasks){
                mTotalBonus += task.getActualBonus();
            }
            rv.setTextViewText(R.id.tv_bonus_total, "" + mTotalBonus);

            // listview click template
            Intent delItemIntent = new Intent(TaskWidgetProvider.ACTION_DEL_ITEM);
            rv.setPendingIntentTemplate(R.id.lv_tasks, PendingIntent.getBroadcast(context, 0, delItemIntent, 0));

            mRemoteViewses.add(i, rv);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.d("aa", "onAppWidgetOptionsChanged");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d("aa", "onDeleted(): appWidgetIds.length=" + appWidgetIds.length);
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("aa", "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d("aa", "onDisabled");
    }


}
