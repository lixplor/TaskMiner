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

package com.lixplor.taskminer.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created :  2016-10-23
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
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
