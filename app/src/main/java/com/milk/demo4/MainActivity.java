package com.milk.demo4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.util.Log;
import android.widget.Toast;

import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;


public class MainActivity extends AppCompatActivity {
    private final int CONTEXT_CLIENT_VERSION = 3;
    private GLSurfaceView mGLSurfaceView;
    RendererJNI mRenderer;
    public static boolean isStarted = false;
    static {
        System.loadLibrary("native-lib");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isStarted) {
            return;
        } else if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);

        } else {
            isStarted = true;
        }
        startService(new Intent(getApplicationContext(), MilkService.class));
        /**
         mGLSurfaceView = new GLSurfaceView(this);
         if (detectOpenGLES30()) {
         // 设置OpenGl ES的版本
         mGLSurfaceView.setEGLContextClientVersion(CONTEXT_CLIENT_VERSION);
         // 设置与当前GLSurfaceView绑定的Renderer
         mGLSurfaceView.setRenderer(mRenderer);
         // 设置渲染的模式
         mGLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);
         } else {
         Log.e("opengles30", "OpenGL ES 3.0 not supported on device.  Exiting...");
         finish();
         }

         setContentView(mGLSurfaceView);*/


    }

    private native String stringFromJNI();


}