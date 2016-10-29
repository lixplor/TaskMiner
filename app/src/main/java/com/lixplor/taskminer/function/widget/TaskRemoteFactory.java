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

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.lixplor.taskminer.R;
import com.lixplor.taskminer.base.BaseApp;
import com.lixplor.taskminer.bean.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created :  2016-10-24
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class TaskRemoteFactory implements RemoteViewsService.RemoteViewsFactory {

    private List<Task> mTasks = new ArrayList<>();
    private Context mContext;
    private int mTotalBonus;
    private boolean mShowDelBtn = true;

    public TaskRemoteFactory(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        mTasks.clear();
        mTasks.addAll(BaseApp.getDaoSession().getTaskDao().loadAll());
        Collections.reverse(mTasks);
    }

    @Override
    public void onDataSetChanged() {
        mTasks.clear();
        mTasks.addAll(BaseApp.getDaoSession().getTaskDao().loadAll());
        Collections.reverse(mTasks);
        Log.d("aa", mTasks.toString());
        Intent intent = new Intent(TaskWidgetProvider.ACTION_BONUS_CHANGED);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        mTasks.clear();
        mTasks = null;
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mTasks.size() <= position) {
            return null;
        }
        Task task = mTasks.get(position);
        RemoteViews item = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_task);
        item.setTextViewText(R.id.tv_id, "#" + task.getId());
        boolean isSuccess = task.getIsSuccess();
        item.setTextViewText(R.id.tv_status, isSuccess ? "完成" : "失败");
        item.setImageViewResource(R.id.iv_status_bg, isSuccess ? R.color.green : R.color.red);
        item.setTextViewText(R.id.tv_content, task.getContent());
        long actualDuration = task.getEndTime() - task.getStartTime();
        long expectDuration = task.getExpectDuration();
        int usedMin = (int) (actualDuration / 1000 / 60);
        int expectMin = (int) (expectDuration / 1000 / 60);
        item.setTextViewText(R.id.tv_duration, usedMin + "/" + expectMin + "分钟");
        item.setTextViewText(R.id.tv_bonus, task.getActualBonus() + "/" + task.getExpectBonus());
        Intent delItemIntent = new Intent(TaskWidgetProvider.ACTION_DEL_ITEM);
        delItemIntent.putExtra("task", task);
        item.setOnClickFillInIntent(R.id.btn_del, delItemIntent);
        if (mShowDelBtn) {
            item.setViewVisibility(R.id.btn_del, View.VISIBLE);
        } else {
            item.setViewVisibility(R.id.btn_del, View.GONE);
        }
        return item;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
