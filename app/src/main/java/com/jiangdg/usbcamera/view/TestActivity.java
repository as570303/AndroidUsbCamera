package com.jiangdg.usbcamera.view;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jiangdg.usbcamera.R;

import androidx.annotation.Nullable;

import java.io.IOException;

/**
 * @author as752497576@gmail.com
 * @description
 * @time 2023/2/14
 */
public class TestActivity extends Activity {

    private Camera mCamera;
    private boolean isPreview = false;
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);
        surfaceView = findViewById(R.id.surface_view);
        //判断是前置还是后置
        surfaceView.getLayoutParams().width = 1920;
        surfaceView.getLayoutParams().height = 1080;

        SurfaceView mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        // 获得 SurfaceHolder 对象
        SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();

        // 设置 Surface 格式
        // 参数： PixelFormat中定义的 int 值 ,详细参见 PixelFormat.java
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        // 如果需要，保持屏幕常亮
        // mSurfaceHolder.setKeepScreenOn(true);

        // 设置 Surface 的分辨率
         mSurfaceHolder.setFixedSize(1920,1080);

        // 添加 Surface 的 callback 接口
        mSurfaceHolder.addCallback(mSurfaceCallback);
    }

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {

        /**
         *  在 Surface 首次创建时被立即调用：活得叫焦点时。一般在这里开启画图的线程
         * @param surfaceHolder 持有当前 Surface 的 SurfaceHolder 对象
         */
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                //打开硬件摄像头 这两句默认是后摄像头，如果指定摄像头用 ： Camera.open(CameraId) CameraId  0 （后置）  1 （前置）
                // Camera.open() 默认返回的后置摄像头信息 //导包得时候一定要注意是android.hardware.Camera
                // setCameraDisplayOrientation(MainActivity2.this,0,camera);

                //设置角度，此处 CameraId  0 （后置）  1 （前置）
//                if (TestActivity.getMode() == 0) {//后置
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开硬件摄像头，这里导包得时候一定要注意是android.hardware.Camera
                    setCameraDisplayOrientation(TestActivity.this, 0, mCamera);
//                } else {//前置
//                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//打开硬件摄像头，这里导包得时候一定要注意是android.hardware.Camera
//                    setCameraDisplayOrientation(TestActivity.this, 1, mCamera);
//                }

                //此处也可以设置摄像头参数
                /**
                 WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//得到窗口管理器
                 Display display  = wm.getDefaultDisplay();//得到当前屏幕
                 Camera.Parameters parameters = camera.getParameters();//得到摄像头的参数
                 parameters.setPictureFormat(PixelFormat.RGB_888);//设置照片的格式
                 parameters.setJpegQuality(85);//设置照片的质量
                 parameters.setPictureSize(display.getHeight(), display.getWidth());//设置照片的大小，默认是和     屏幕一样大
                 camera.setParameters(parameters);//设置需要预览的尺寸
                 **/
                mCamera.setPreviewDisplay(surfaceHolder);//通过SurfaceView显示取景画面
                mCamera.startPreview();//开始预览
                isPreview = true;//设置是否预览参数为真
            } catch (IOException e) {
                Log.e("TAG", e.toString());
            }
        }

        /**
         *  在 Surface 格式 和 大小发生变化时会立即调用，可以在这个方法中更新 Surface
         * @param surfaceHolder   持有当前 Surface 的 SurfaceHolder 对象
         * @param format          surface 的新格式
         * @param width           surface 的新宽度
         * @param height          surface 的新高度
         */
        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

        }

        /**
         *  在 Surface 被销毁时立即调用：失去焦点时。一般在这里将画图的线程停止销毁
         * @param surfaceHolder 持有当前 Surface 的 SurfaceHolder 对象
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (mCamera != null) {
                if (isPreview) {//正在预览
                    mCamera.stopPreview();
                    mCamera.release();
                }
            }
        }
    };

    /**
     * 设置 摄像头的角度
     *
     * @param activity 上下文
     * @param cameraId 摄像头ID（假如手机有N个摄像头，cameraId 的值 就是 0 ~ N-1）
     * @param camera   摄像头对象
     */
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {

        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        //获取摄像头信息
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        //获取摄像头当前的角度
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            //前置摄像头
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else {
            // back-facing  后置摄像头
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


}
