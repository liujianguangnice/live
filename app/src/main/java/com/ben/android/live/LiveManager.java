package com.ben.android.live;

import android.content.Context;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

public class LiveManager implements LiveInterface{
    private static LiveManager mLiveManager;
    private WeakReference<Context> mContextWeakReference;
    private WeakReference<SurfaceView> mSurfaceViewWeakReference;
    private NativePush mNativePush;
    private AudioPusher mAudioPusher;
    private VideoPusher mVideoPusher;

    public LiveManager(Context context,SurfaceView surfaceView) {
        this.mContextWeakReference =  new WeakReference<>(context);
        this.mSurfaceViewWeakReference =  new WeakReference<>(surfaceView);
        this.mNativePush = new NativePush();
    }

    public static LiveManager getManager(Context context,SurfaceView surfaceView){
        if (mLiveManager == null) {
            mLiveManager = new LiveManager(context,surfaceView);
        }
        return mLiveManager;
    }

    @Override
    public void prepare() {
        mAudioPusher = new AudioPusher.Builder()
                .sampleRateInHz(44100)
                .channelConfig(AudioFormat.CHANNEL_IN_STEREO)
                .audioFormat(AudioFormat.ENCODING_PCM_16BIT)
                .build();
        //208 * 144
        //176 * 144
        mVideoPusher = new VideoPusher.Builder()
                .width(320)
                .height(240)
                .surfaceView(mSurfaceViewWeakReference.get().getHolder())
                .nativePush(mNativePush)
                .build();

        mAudioPusher.prepare();
        mVideoPusher.prepare();
    }

    @Override
    public void startPush() {
        mAudioPusher.startPush();
        mVideoPusher.startPush();

    }

    @Override
    public void pausePush() {
        mAudioPusher.pausePush();
        mVideoPusher.pausePush();


    }

    @Override
    public void stopPush() {
        mAudioPusher.stopPush();
        mVideoPusher.stopPush();

    }

    @Override
    public void free() {
        mAudioPusher.free();
        mVideoPusher.free();
    }


    public void switchCameraId() {
        if(mVideoPusher.getBuilder().getCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK){
            mVideoPusher.getBuilder().cameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }else{
            mVideoPusher.getBuilder().cameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        mVideoPusher.switchCamera();
    }
}
