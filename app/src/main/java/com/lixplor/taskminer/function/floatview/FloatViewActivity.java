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

package com.lixplor.taskminer.function.floatview;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lixplor.taskminer.R;
import com.lixplor.taskminer.base.BaseActivity;

import java.util.List;

/**
 * Created :  2016-10-24
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class FloatViewActivity extends BaseActivity {
    @Override
    protected int setContentLayout() {
        return R.layout.act_floatview;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        if (!isServiceRunning(FloatViewService.class)) {
            startService(new Intent(this, FloatViewService.class));
        }
        finish();
    }

    public boolean isServiceRunning(Class<? extends Service> clazz) {
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : list) {
            ComponentName service = info.service;
            String className = service.getClassName();
            if (className.equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }
}
