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

package com.lixplor.taskminer.bean;

import com.lixplor.taskminer.repository.DaoSession;
import com.lixplor.taskminer.repository.TaskDao;
import com.lixplor.taskminer.repository.UserDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

/**
 * Created :  2016-10-22
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
@Entity(nameInDb = "tb_task")
public class Task implements Serializable{

    public static final long serialVersionUID = 1;

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String content;

    @NotNull
    private long startTime;

    @NotNull
    private long endTime;

    @NotNull
    private long expectDuration;

    @NotNull
    private int expectBonus;

    @NotNull
    private int actualBonus;

    @NotNull
    private boolean isSuccess;

    @Generated(hash = 101291339)
    public Task(Long id, @NotNull String content, long startTime, long endTime,
            long expectDuration, int expectBonus, int actualBonus, boolean isSuccess) {
        this.id = id;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.expectDuration = expectDuration;
        this.expectBonus = expectBonus;
        this.actualBonus = actualBonus;
        this.isSuccess = isSuccess;
    }

    @Generated(hash = 733837707)
    public Task() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getExpectDuration() {
        return this.expectDuration;
    }

    public void setExpectDuration(long expectDuration) {
        this.expectDuration = expectDuration;
    }

    public int getExpectBonus() {
        return this.expectBonus;
    }

    public void setExpectBonus(int expectBonus) {
        this.expectBonus = expectBonus;
    }

    public int getActualBonus() {
        return this.actualBonus;
    }

    public void setActualBonus(int actualBonus) {
        this.actualBonus = actualBonus;
    }

    public boolean getIsSuccess() {
        return this.isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", expectDuration=" + expectDuration +
                ", expectBonus=" + expectBonus +
                ", actualBonus=" + actualBonus +
                ", isSuccess=" + isSuccess +
                '}';
    }
}
