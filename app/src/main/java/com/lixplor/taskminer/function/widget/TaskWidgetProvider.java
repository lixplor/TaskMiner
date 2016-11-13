/*
 *  Copyright 2016 Lixplor
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class TaskWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_DEL_ITEM = "com.lixplor.taskminer.ACTION_DEL_ITEM";
    public static final String ACTION_BONUS_CHANGED = "com.lixplor.taskminer.ACTION_BONUS_CHANGED";
    public static final String ACTION_PAGE_PRE = "com.lixplor.taskminer.ACTION_PAGE_PRE";
    public static final String ACTION_PAGE_NEXT = "com.lixplor.taskminer.ACTION_PAGE_NEXT";

    private static int sTotalPage;
    private static int sCurrentPage = 1;

    private TaskDao mTaskDao;
    private int mTotalBonus;


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.d("aa", "[WIDGET BRDCST RCV] action=" + action);
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
            case ACTION_PAGE_PRE:
                sCurrentPage = TaskRemoteFactory.prePage();
                updateData(context);
                break;
            case ACTION_PAGE_NEXT:
                sCurrentPage = TaskRemoteFactory.nextPage();
                updateData(context);
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

            // total page number
            sTotalPage = tasks.size() / TaskRemoteFactory.getLimit() + 1;
            rv.setTextViewText(R.id.tv_page, sCurrentPage + "/" + sTotalPage);

            // listview click template
            Intent delItemIntent = new Intent(TaskWidgetProvider.ACTION_DEL_ITEM);
            rv.setPendingIntentTemplate(R.id.lv_tasks, PendingIntent.getBroadcast(context, 0, delItemIntent, 0));

            // pre page
            Intent prePageIntent = new Intent(TaskWidgetProvider.ACTION_PAGE_PRE);
            rv.setOnClickPendingIntent(R.id.btn_pre, PendingIntent.getBroadcast(context, 0, prePageIntent, 0));
            // next page
            Intent nextPageIntent = new Intent(TaskWidgetProvider.ACTION_PAGE_NEXT);
            rv.setOnClickPendingIntent(R.id.btn_next, PendingIntent.getBroadcast(context, 0, nextPageIntent, 0));

            mRemoteViewses.add(i, rv);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }


}
