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
 * Author  :  Fantasymaker
 * Web     :  http://blog.fantasymaker.cn
 * Email   :  me@fantasymaker.cn
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
