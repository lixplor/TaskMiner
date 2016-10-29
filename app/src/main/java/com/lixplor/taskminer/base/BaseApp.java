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

package com.lixplor.taskminer.base;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lixplor.taskminer.repository.DaoMaster;
import com.lixplor.taskminer.repository.DaoSession;

/**
 * Created :  2016-10-22
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class BaseApp extends Application {

    private static Context sContext;
    private static DaoSession sDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        sDaoSession = initDb();
    }

    private DaoSession initDb() {
        // 创建表并获得Helper
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "task_db", null);
        // 获取数据库对象
        SQLiteDatabase db = helper.getWritableDatabase();
        // 创建DaoMaster
        DaoMaster daoMaster = new DaoMaster(db);
        // 创建新的session
        return daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }

    public static Context getContext() {
        return sContext;
    }
}
