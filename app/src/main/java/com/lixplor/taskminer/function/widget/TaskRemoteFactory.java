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
 * Author  :  Fantasymaker
 * Web     :  http://blog.fantasymaker.cn
 * Email   :  me@fantasymaker.cn
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
