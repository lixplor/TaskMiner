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
import com.lixplor.taskminer.repository.TaskDao;

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

    private static final int sLimit = 20;

    private static int sOffset = 0;
    private static int sCurrentPage = 1;

    private List<Task> mTasks = new ArrayList<>();
    private TaskDao mTaskDao;
    private Context mContext;
    private int mTotalBonus;
    private boolean mShowDelBtn = true;

    public TaskRemoteFactory(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        mTaskDao = BaseApp.getDaoSession().getTaskDao();
        mTasks.clear();
        List<Task> tasks = mTaskDao.queryBuilder().offset(sOffset).limit(sLimit).build().list();
        mTasks.addAll(tasks);
        Collections.reverse(mTasks);
    }

    @Override
    public void onDataSetChanged() {
        mTasks.clear();
        List<Task> tasks = mTaskDao.queryBuilder().offset(sOffset).limit(sLimit).build().list();
        mTasks.addAll(tasks);
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
        // task number
        item.setTextViewText(R.id.tv_id, "#" + task.getId());
        // task status
        boolean isSuccess = task.getIsSuccess();
        item.setTextViewText(R.id.tv_status, isSuccess ? "完成" : "失败");
        item.setImageViewResource(R.id.iv_status_bg, isSuccess ? R.color.green : R.color.red);
        // task content
        item.setTextViewText(R.id.tv_content, task.getContent());
        // duration
        long actualDuration = task.getEndTime() - task.getStartTime();
        long expectDuration = task.getExpectDuration();
        int usedMin = (int) (actualDuration / 1000 / 60);
        int expectMin = (int) (expectDuration / 1000 / 60);
        item.setTextViewText(R.id.tv_duration, usedMin + "/" + expectMin + "分钟");
        // bonus
        item.setTextViewText(R.id.tv_bonus, task.getActualBonus() + "/" + task.getExpectBonus());
        // delete record
        Intent delItemIntent = new Intent(TaskWidgetProvider.ACTION_DEL_ITEM);
        delItemIntent.putExtra("task", task);
        item.setOnClickFillInIntent(R.id.btn_del, delItemIntent);
        // del btn
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

    public static int prePage(){
        if(sOffset - sLimit >= 0){
            sOffset -= sLimit;
        }
        sCurrentPage = sOffset / sLimit + 1;
        return sCurrentPage;
    }

    public static int nextPage(){
        int taskSize = BaseApp.getDaoSession().getTaskDao().loadAll().size();
        if(sOffset + sLimit < taskSize){
            sOffset += sLimit;
        }
        sCurrentPage = sOffset / sLimit + 1;
        return sCurrentPage;
    }

    public static int getLimit(){
        return sLimit;
    }
}
