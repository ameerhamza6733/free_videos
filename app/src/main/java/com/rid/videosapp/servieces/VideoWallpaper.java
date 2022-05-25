package com.rid.videosapp.servieces;


import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;


import com.rid.videosapp.MainActivity;
import com.rid.videosapp.sharedPref.PrefUtils;
import com.rid.videosapp.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by apple on 8/16/18.
 */

public class VideoWallpaper extends WallpaperService {
    public static final String VIDEO_PARAMS_CONTROL_ACTION = "com.dingmouren.videowallpager";
    public static final String ACTION = "action";
    public static final int ACTION_VOICE_SILENCE = 0x101;
    public static final int ACTION_SET_WALLAPER =6;
    public static final int ACTION_VOICE_NORMAL = 0x102;
    public static final String IS_SILENCE = "is_silence";
    private static final String TAG = VideoWallpaper.class.getName();
    private static boolean _sound = false;
    private static String wallPaperPath="";
    private FileInputStream inputStream;


    public static void setVoiceSilence(Context context) {
        Intent intent = new Intent(VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(ACTION, ACTION_VOICE_SILENCE);
        context.sendBroadcast(intent);
    }

    /**
     * 设置有声音
     *
     * @param context
     */
    public static void setVoiceNormal(Context context) {
        Intent intent = new Intent(VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(ACTION, ACTION_VOICE_NORMAL);
        context.sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"Oncrtae");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStart Command");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG,"onLowMemory");

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.e(TAG,"onTrimMemory");
    }


    @Override
    public Engine onCreateEngine() {
      wallPaperPath=   PrefUtils.INSTANCE.getString(this,"currentWallpaper");

        if (wallPaperPath == null || TextUtils.isEmpty(wallPaperPath.toString())) {
            Toast.makeText(VideoWallpaper.this, "Please select wallpaper first: ", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(VideoWallpaper.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            VideoWallpaper.this.stopSelf();

        }

        return new VideoWallpagerEngine();
    }

    class  VideoWallpagerEngine extends Engine {
        private MediaPlayer mMediaPlayer;
        private BroadcastReceiver mVideoVoiceControlReceiver;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG,"onWallaper engine crate");
            IntentFilter intentFilter = new IntentFilter(VIDEO_PARAMS_CONTROL_ACTION);
            mVideoVoiceControlReceiver = new VideoVoiceControlReceiver();
            registerReceiver(mVideoVoiceControlReceiver, intentFilter);
        }

        @Override
        public void onDestroy() {
            Log.d(TAG,"onWallaper engine onDestroy");
            unregisterReceiver(mVideoVoiceControlReceiver);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.d(TAG,"onVisibilityChanged "+visible);
            if (visible) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.start();
                    Log.d(TAG,"start media player  ");

                } else {

                }

            } else {
                if (mMediaPlayer != null) {
                    Log.d(TAG,"stop media player ");

                    mMediaPlayer.pause();
                } else {
                }

            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            if (wallPaperPath == null || TextUtils.isEmpty(wallPaperPath.toString())) {
                wallPaperPath=   PrefUtils.INSTANCE.getString(VideoWallpaper.this,"currentWallpaper");

            }
                Log.d(TAG, "" + wallPaperPath);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setSurface(holder.getSurface());


                try {

                    mMediaPlayer.setDataSource(VideoWallpaper.this,Uri.parse(wallPaperPath) );
                    mMediaPlayer.setLooping(true);
                    if (_sound) {
                        mMediaPlayer.setVolume(1.0f, 1.0f);
                    } else {
                        mMediaPlayer.setVolume(0f, 0f);
                    }
                    mMediaPlayer.prepare();

                    mMediaPlayer.start();
                    mMediaPlayer.setLooping(true);
                } catch (Exception e) {

                    Log.d(TAG,"error "+e.getMessage());
                    Toast.makeText(VideoWallpaper.this, "Unable to load video file trying alternative method ", Toast.LENGTH_LONG).show();



                    e.printStackTrace();
                }


        }



        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.d(TAG,"onSurfesDestry");
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

        class VideoVoiceControlReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent) {
                int action = intent.getIntExtra(ACTION, -1);
                switch (action) {
                    case ACTION_VOICE_NORMAL:
                        mMediaPlayer.setVolume(1.0f, 1.0f);
                        break;
                    case ACTION_VOICE_SILENCE:
                        mMediaPlayer.setVolume(0, 0);
                        break;

                    case  ACTION_SET_WALLAPER:

                        break;
                }
            }
        }
    }


}