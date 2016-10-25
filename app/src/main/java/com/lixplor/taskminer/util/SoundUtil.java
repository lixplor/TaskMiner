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

package com.lixplor.taskminer.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created :  2016-10-23
 * Author  :  Fantasymaker
 * Web     :  http://blog.fantasymaker.cn
 * Email   :  me@fantasymaker.cn
 */
public class SoundUtil {

    private static Context sContext;
    private static SoundPool sSoundPool;

    public static void init(Context context, int maxStreams){
        sContext = context.getApplicationContext();
        if(sSoundPool == null){
            sSoundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        }
    }

    public static int load(int resId){
        return sSoundPool.load(sContext, resId, 0);
    }

    public static void playOnce(int soundId){
        sSoundPool.play(soundId, 1, 1, 0, 0, 1);
    }

    public static void stop(int soundId){
        sSoundPool.stop(soundId);
    }

    public static void pause(int soundId){
        sSoundPool.pause(soundId);
    }

    public static void resume(int soundId){
        sSoundPool.resume(soundId);
    }

    public static void pauseAll(){
        sSoundPool.autoPause();
    }

    public static void resumeAll(){
        sSoundPool.autoResume();
    }

    public static void release(){
        sSoundPool.autoPause();
        sSoundPool.release();
        sSoundPool = null;
    }
}
