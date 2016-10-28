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

package com.lixplor.taskminer.function.floatview;

import android.app.Notification;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lixplor.taskminer.R;
import com.lixplor.taskminer.base.BaseApp;
import com.lixplor.taskminer.bean.Task;
import com.lixplor.taskminer.repository.TaskDao;
import com.lixplor.taskminer.util.SoundUtil;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created :  2016-10-24
 * Author  :  Fantasymaker
 * Web     :  http://blog.fantasymaker.cn
 * Email   :  me@fantasymaker.cn
 */
public class FloatViewService extends Service {

    private static final int TIME_STEP = 5;

    @BindView(R.id.tv_task_content)
    TextView mTvTaskContent;
    @BindView(R.id.pb_progress)
    ProgressBar mPbProgress;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_bonus)
    TextView mTvBonus;
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;
    @BindView(R.id.ll_input_window)
    LinearLayout mLlInputWindow;
    @BindView(R.id.iv_coin)
    ImageView mIvCoin;
    @BindView(R.id.et_task_content)
    EditText mEtTaskContent;
    @BindView(R.id.btn_time_minus)
    Button mBtnTimeMinus;
    @BindView(R.id.tv_time_expect)
    TextView mTvTimeExpect;
    @BindView(R.id.btn_time_plus)
    Button mBtnTimePlus;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;
    @BindView(R.id.iv_symbol)
    ImageView mIvSymbol;
    @BindView(R.id.ll_main)
    LinearLayout mLlMain;

    private View mFloatView;
    private WindowManager mWindowManager;
    private ScaleAnimation mCoinScaleAnim;
    private TranslateAnimation mEtShakeAnim;
    private CountDownTimer mCountDownTimer;
    private RotateAnimation mSymbolRotateAnim;

    // Sounds
    private int mSoundClickId;
    private int mSoundStartId;
    private int mSoundVictoryId;
    private int mSoundFailId;
    private int mSoundBellId;
    private int mVoiceStartId;
    private int mVoiceVictoryId;
    private int mVoiceFailId;
    private int mVoiceUnexpectedId;
    private int mVoiceNotimeId;

    private int mTaskExpectTimeMin = 5;
    private boolean isDoingTask;
    private boolean hasWarned;
    private TaskDao mTaskDao;

    private String mTaskContent;
    private long mTaskStartMillis;
    private long mTaskEndMillis;
    private long mTaskExpectDuration;
    private long mTaskActualDuration;
    private int mTaskExpectBonus;
    private int mTaskActualBonus;
    private boolean mIsTaskSuccess;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createFloatView();
        mTvTaskContent.setSelected(true);
        mTvTimeExpect.setText("" + mTaskExpectTimeMin);
        createCoinAnim();
        createEtShakeAnim();
        createSymbolRotateAnim();
        initSoundEffect();
        startFgService();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCoinScaleAnim.cancel();
        mIvCoin.clearAnimation();
        mEtShakeAnim.cancel();
        mEtTaskContent.clearAnimation();
        mCountDownTimer.cancel();
    }

    private void startFgService() {
        Notification notification = new Notification.Builder(this)
                .setTicker("TaskMiner已启动")
                .setContentTitle("::TaskMiner::")
                .setContentText("::TaskMiner::可以方便地规划您的时间💰")
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.lylst_ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.vc_coin))
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(1919191, notification);
    }

    private void createCoinAnim() {
        mCoinScaleAnim = new ScaleAnimation(1f, -1f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mCoinScaleAnim.setDuration(200);
        mCoinScaleAnim.setRepeatCount(Animation.INFINITE);
        mCoinScaleAnim.setRepeatMode(Animation.REVERSE);
        mCoinScaleAnim.setStartOffset(800);
    }

    private void createEtShakeAnim() {
        mEtShakeAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -0.02f,
                Animation.RELATIVE_TO_SELF, 0.02f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        mEtShakeAnim.setDuration(100);
        mEtShakeAnim.setRepeatMode(Animation.RESTART);
        mEtShakeAnim.setRepeatCount(2);
        mEtShakeAnim.setInterpolator(new DecelerateInterpolator(1f));
    }

    private void createSymbolRotateAnim(){
        mSymbolRotateAnim = new RotateAnimation(
                0f, 359f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        mSymbolRotateAnim.setDuration(1000);
        mSymbolRotateAnim.setRepeatCount(Animation.INFINITE);
        mSymbolRotateAnim.setRepeatMode(Animation.RESTART);
        mSymbolRotateAnim.setInterpolator(new LinearInterpolator());
    }

    private void createFloatView() {
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
        WindowManager.LayoutParams param = new WindowManager.LayoutParams();
        param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        param.format = PixelFormat.TRANSLUCENT;
        param.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        param.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        param.alpha = 0.9f;
        param.gravity = Gravity.LEFT | Gravity.TOP;
        param.width = WindowManager.LayoutParams.WRAP_CONTENT;
        param.height = WindowManager.LayoutParams.WRAP_CONTENT;
        param.x = (screenWidth - param.width) / 2;
        param.y = (screenHeight - param.height) / 2;

        mFloatView = View.inflate(this, R.layout.view_float_window, null);
        ButterKnife.bind(this, mFloatView);
        setTouchEvent(mLlTitle);
        setTouchEvent(mIvSymbol);
        mWindowManager.addView(mFloatView, param);
    }

    private void setTouchEvent(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            private float lastX = 0;
            private float lastY = 0;
            boolean isMoved = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMoved = false;
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isMoved = true;
                        float moveX = event.getRawX();
                        float moveY = event.getRawY();
                        WindowManager.LayoutParams param = (WindowManager.LayoutParams) mFloatView.getLayoutParams();
                        param.x += (int) (moveX - lastX);
                        param.y += (int) (moveY - lastY);
                        param = restrictLimit(param);
                        mWindowManager.updateViewLayout(mFloatView, param);
                        lastX = moveX;
                        lastY = moveY;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isMoved && View.VISIBLE != mLlInputWindow.getVisibility()) {
                            transformLayout();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isMoved = true;
                        break;
                }
                return isMoved;
            }
        });
    }

    private void transformLayout() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager.LayoutParams upParam = (WindowManager.LayoutParams) mFloatView.getLayoutParams();
        int floatWindowRight = upParam.x + mFloatView.getMeasuredWidth();

        if (floatWindowRight >= metrics.widthPixels || upParam.x <= 0) {
            // left or right edge, transform from bar to symbol
            if (View.VISIBLE == mLlMain.getVisibility()) {
                mLlMain.setVisibility(View.GONE);
                mIvSymbol.setVisibility(View.VISIBLE);
                if(isDoingTask){
                    mIvSymbol.startAnimation(mSymbolRotateAnim);
                }else{
                    mIvSymbol.clearAnimation();
                }
            }
            // when goes to right side, let symbol shrink to right side
            if (floatWindowRight >= metrics.widthPixels && View.VISIBLE == mIvSymbol.getVisibility()) {
                upParam.x = metrics.widthPixels - mIvSymbol.getMeasuredWidth();
                mWindowManager.updateViewLayout(mFloatView, upParam);
            }
        } else if (View.VISIBLE != mLlMain.getVisibility()) {
            // not edge, transform from symbol to bar
            mLlMain.setVisibility(View.VISIBLE);
            mIvSymbol.setVisibility(View.GONE);
            mIvSymbol.clearAnimation();
        }
    }

    private WindowManager.LayoutParams restrictLimit(WindowManager.LayoutParams param) {
        if (param.x < 0) {
            param.x = 0;
        }
        if (param.y < 0) {
            param.y = 0;
        }
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int rightLimit = displayMetrics.widthPixels - mFloatView.getMeasuredWidth();
        if (param.x > rightLimit) {
            param.x = rightLimit;
        }
        int bottomLimit = displayMetrics.heightPixels - mFloatView.getMeasuredHeight();
        if (param.y > bottomLimit) {
            param.y = bottomLimit;
        }
        return param;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void toggleInputWindow() {
        if (View.GONE == mLlInputWindow.getVisibility()) {
            mLlInputWindow.setVisibility(View.VISIBLE);
            if (isDoingTask) {
                mBtnCancel.setText("放弃");
                mBtnConfirm.setText("完成");
            } else {
                mBtnCancel.setText("关闭");
                mBtnConfirm.setText("开始");
            }
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) mFloatView.getLayoutParams();
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            params.dimAmount = 0.9f;
            mWindowManager.updateViewLayout(mFloatView, params);
        } else {
            mLlInputWindow.setVisibility(View.GONE);
            mTvTaskContent.setMarqueeRepeatLimit(-1);
            mTvTaskContent.setSingleLine();
            mTvTaskContent.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mTvTaskContent.setFocusableInTouchMode(true);
            mTvTaskContent.setFocusable(true);
            mTvTaskContent.setSelected(true);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) mFloatView.getLayoutParams();
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            mWindowManager.updateViewLayout(mFloatView, params);
        }
    }

    private void onTaskCreate() {
        Log.d("aa", "[TASK CREATE] content=" + mTaskContent + ", time=" + mTaskExpectDuration);
        playTaskStartSoundEffect();
        mTaskStartMillis = System.currentTimeMillis();
        isDoingTask = true;
        hasWarned = false;
        mIsTaskSuccess = false;
        mPbProgress.setProgress(100);
        mPbProgress.setSecondaryProgress(0);
        mTvTaskContent.setText(mTaskContent);
        mTvTime.setText("剩余" + mTaskExpectTimeMin + "分钟");
        mTvBonus.setText("" + mTaskExpectBonus);
        mIvCoin.startAnimation(mCoinScaleAnim);

        // 开始倒计时
        mCountDownTimer = new CountDownTimer(mTaskExpectDuration * 2, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long normalLeftMillis = millisUntilFinished - mTaskExpectDuration;
                if (normalLeftMillis >= 0) {
                    // normal time
                    doNormalTime(normalLeftMillis);
                } else {
                    // over time
                    long timeOverMillis = 0 - normalLeftMillis;
                    doOverTime(timeOverMillis);
                }
            }

            @Override
            public void onFinish() {
                onTaskFail();
            }
        };
        mCountDownTimer.start();
    }

    private void doOverTime(long timeOverMillis) {
        float percent = 1f * timeOverMillis / mTaskExpectDuration;
        // progressbar
        mPbProgress.setProgress(0);
        mPbProgress.setSecondaryProgress((int) (100 * percent));
        // time
        double minsOver = timeOverMillis / 1000.0 / 60;
        BigDecimal bigDecimal = new BigDecimal(minsOver);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_CEILING);
        mTvTime.setText("超时" + bigDecimal.toString() + "分钟");
        // bonus
        mTaskActualBonus = (int) ((1 - percent) * mTaskExpectBonus);
        mTvBonus.setText("" + mTaskActualBonus);
    }

    private void doNormalTime(long timeLeftMillis) {
        float percent = 1f * timeLeftMillis / mTaskExpectDuration;
        // warn
        if (!hasWarned && percent < 0.2f) {
            // less than 1/5 of total time, warn
            playTaskOvertimeSoundEffect();
            hasWarned = true;
        }
        // progressbar
        mPbProgress.setProgress((int) (100 * percent));
        // time
        double minsLeft = timeLeftMillis / 1000.0 / 60.0;
        BigDecimal bigDecimal = new BigDecimal(minsLeft);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_CEILING);
        mTvTime.setText("剩余" + bigDecimal.toString() + "分钟");
        // bonus
        mTaskActualBonus = mTaskExpectBonus;
    }

    private void onTaskFail() {
        Log.d("aa", "[TASK FAIL]");
        playTaskFailSoundEffect();
        mTvTime.setText("任务失败");
        mTaskActualBonus = 0;
        mTvBonus.setText("" + mTaskActualBonus);
        mPbProgress.setProgress(0);
        mPbProgress.setSecondaryProgress(100);
        mIsTaskSuccess = false;
        onTaskFinish();
    }

    private void onTaskAbandon() {
        Log.d("aa", "[TASK ABANDON]");
        playTaskAbandonSoundEffect();
        mTvTime.setText("任务取消");
        mTaskActualBonus = 0;
        mTvBonus.setText("" + mTaskActualBonus);
        mPbProgress.setProgress(0);
        mPbProgress.setSecondaryProgress(100);
        mIsTaskSuccess = false;
        onTaskFinish();
    }

    private void onTaskSuccess() {
        Log.d("aa", "[TASK SUCCESS]");
        playTaskSuccessSoundEffect();
        mTvTime.setText("任务完成");
        mIsTaskSuccess = true;
        onTaskFinish();
    }

    private void onTaskFinish() {
        mTaskEndMillis = System.currentTimeMillis();
        mCountDownTimer.cancel();
        isDoingTask = false;
        mCoinScaleAnim.cancel();
        mCoinScaleAnim.reset();
        if(View.VISIBLE == mIvSymbol.getVisibility()){
            mIvSymbol.clearAnimation();
        }
        Task task = new Task();
        task.setId(null);
        task.setContent(mTaskContent);
        task.setStartTime(mTaskStartMillis);
        task.setEndTime(mTaskEndMillis);
        task.setExpectDuration(mTaskExpectDuration);
        task.setExpectBonus(mTaskExpectBonus);
        task.setActualBonus(mTaskActualBonus);
        task.setIsSuccess(mIsTaskSuccess);
        Log.d("aa", "[TASK FINISH]" + task.toString());
        if (mTaskDao == null) {
            mTaskDao = BaseApp.getDaoSession().getTaskDao();
        }
        mTaskDao.insert(task);
        updateWidget();
    }

    private void updateWidget() {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(intent);
    }

    @OnClick({R.id.ll_title, R.id.btn_time_minus, R.id.btn_time_plus, R.id.btn_cancel, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_title:
                toggleInputWindow();
                break;
            case R.id.btn_time_minus:
                mTaskExpectTimeMin -= TIME_STEP;
                if (mTaskExpectTimeMin <= 0) {
                    mTaskExpectTimeMin = 60;
                }
                mTvTimeExpect.setText("" + mTaskExpectTimeMin);
                break;
            case R.id.btn_time_plus:
                mTaskExpectTimeMin += TIME_STEP;
                if (mTaskExpectTimeMin > 60) {
                    mTaskExpectTimeMin = 5;
                }
                mTvTimeExpect.setText("" + mTaskExpectTimeMin);
                break;
            case R.id.btn_cancel:
                if (isDoingTask) {
                    onTaskAbandon();
                } else {
                    // 关闭窗口即可
                }
                toggleInputWindow();
                break;
            case R.id.btn_confirm:
                if (isDoingTask) {
                    onTaskSuccess();
                } else {
                    mTaskContent = mEtTaskContent.getText().toString();
                    if (TextUtils.isEmpty(mTaskContent)) {
                        mEtTaskContent.startAnimation(mEtShakeAnim);
                        return;
                    }
                    String expectTimeMinStr = mTvTimeExpect.getText().toString();
                    mTaskExpectDuration = Long.valueOf(expectTimeMinStr) * 60 * 1000;
                    mTaskExpectTimeMin = Integer.valueOf(expectTimeMinStr);
                    mTaskExpectBonus = mTaskExpectTimeMin;
                    onTaskCreate();
                }
                toggleInputWindow();
                break;
        }
    }

    private void initSoundEffect() {
        SoundUtil.init(this, 2);
        mVoiceStartId = SoundUtil.load(R.raw.voice_start);
        mVoiceNotimeId = SoundUtil.load(R.raw.voice_no_time);
        mVoiceVictoryId = SoundUtil.load(R.raw.voice_keep_win);
        mVoiceFailId = SoundUtil.load(R.raw.voice_timesup);
        mVoiceUnexpectedId = SoundUtil.load(R.raw.voice_unexpected);
        mSoundStartId = SoundUtil.load(R.raw.sound_start);
        mSoundBellId = SoundUtil.load(R.raw.sound_bell);
        mSoundVictoryId = SoundUtil.load(R.raw.sound_victory);
        mSoundFailId = SoundUtil.load(R.raw.sound_fail);
        mSoundClickId = SoundUtil.load(R.raw.sound_click);
    }

    private void playClickSoundEffect() {
        SoundUtil.playOnce(mSoundClickId);
        SoundUtil.playOnce(mSoundClickId);
    }

    private void playTaskStartSoundEffect() {
        SoundUtil.playOnce(mVoiceStartId);
        SoundUtil.playOnce(mSoundStartId);
    }

    private void playTaskOvertimeSoundEffect() {
        SoundUtil.playOnce(mVoiceNotimeId);
        SoundUtil.playOnce(mSoundBellId);
    }

    private void playTaskSuccessSoundEffect() {
        SoundUtil.playOnce(mSoundVictoryId);
        SoundUtil.playOnce(mVoiceVictoryId);
    }

    private void playTaskFailSoundEffect() {
        SoundUtil.playOnce(mVoiceFailId);
        SoundUtil.playOnce(mSoundFailId);
    }

    private void playTaskAbandonSoundEffect() {
        SoundUtil.playOnce(mSoundFailId);
        SoundUtil.playOnce(mVoiceUnexpectedId);
    }

}
