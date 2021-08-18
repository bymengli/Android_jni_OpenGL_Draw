package com.milk.demo4;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import java.util.Random;

import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;


public class MilkService extends Service {
    private static final String TAG = "MilkService：";
    private WindowManager Milk_WM;
    private WindowManager.LayoutParams MilkParams;
    private final int CONTEXT_CLIENT_VERSION = 3;
    private GLSurfaceView mGLSurfaceView;
    private RendererJNI mRenderer;
    private View view;
    public static boolean mIsDrawing;
    private int fps = 144;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        MilkFloatingWindow();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand：");
        return super.onStartCommand(intent, flags, startId);

    }

    private void MilkFloatingWindow() {
        view = View.inflate(getApplicationContext(), R.layout.milk_glsurfaceview, null);
        mGLSurfaceView = view.findViewById(R.id.milk_gl_surface);
        mGLSurfaceView = new GLSurfaceView(this);
        mRenderer=new RendererJNI(this);
        if (detectOpenGLES30()) {
            // 设置OpenGl ES的版本
            mGLSurfaceView.setEGLContextClientVersion(CONTEXT_CLIENT_VERSION);
            // 设置与当前GLSurfaceView绑定的Renderer
            mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            mGLSurfaceView.getHolder() .setFormat(PixelFormat.TRANSLUCENT);
            mGLSurfaceView. setZOrderOnTop(true);

            mGLSurfaceView.setRenderer(mRenderer);
            // 设置渲染的模式
            mGLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);

        } else {
            Log.e("opengles30", "OpenGL ES 3.0 not supported on device.  Exiting...");

        }

        Milk_WM = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        MilkParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            //Android 8.0及以后使用
            MilkParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            MilkParams.flags = AdaptationPermissions(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        } else {
            //Android 8.0以前使用
            MilkParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
            MilkParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        }
        MilkParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        MilkParams.gravity = Gravity.START | Gravity.TOP;
        MilkParams.format = PixelFormat.RGBA_8888;
        Milk_WM.addView(mGLSurfaceView, MilkParams);
    }


    private boolean detectOpenGLES30() {
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x0000);//原0x3000
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy：");
        if (view != null) {
            Milk_WM.removeView(view);
        }
    }

    /**
    class MilkDraw extends Thread {
        private SurfaceHolder mSurfaceHolder;
        private Canvas mCanvas = null;


        public MilkDraw(SurfaceHolder milk) {
            initView();
            mSurfaceHolder = milk;

        }


        private void initView() {


        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void Draw() {

            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            while (mIsDrawing) {
                try {
                    //mCanvas = mSurfaceHolder.lockCanvas();
                    //开启硬件加速
                    mCanvas = mSurfaceHolder.lockHardwareCanvas();
                    //绘制背景
                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Draw();
            }
        }

    }
    */

    private int AdaptationPermissions(int Permissions) {
        boolean Milk1 = false;
        boolean Milk2 = false;
        boolean Milk3 = true;
        boolean Milk4 = false;
        if (!Milk1) {
            Permissions |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        } else {
            Permissions &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        if (!Milk2) {
            Permissions |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        } else {
            Permissions &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        if (!Milk3) {
            Permissions |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        } else {
            Permissions &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        }
        if (Milk4) {
            Permissions |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        } else {
            Permissions &= ~WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        }

        return Permissions;
    }


}
