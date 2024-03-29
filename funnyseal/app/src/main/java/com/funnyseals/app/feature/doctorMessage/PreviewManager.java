package com.funnyseals.app.feature.doctorMessage;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hyphenate.chat.EMClient;

import java.io.IOException;
import java.util.List;

/**
 * <pre>
 *     author : marin
 *     time   : 2018/12/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class PreviewManager implements PreviewCallback {

    private Camera          camera;
    private PreviewCallback previewCallback;
    private SurfaceView     surfaceView;

    private int videoWidth  = 320;
    private int videoHeight = 240;

    public PreviewManager (SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        init();
    }

    private void init () {
        previewCallback = this;
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated (SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
                // SurfaceView 构建完毕并且获取有效宽高时候打开摄像头
                if (camera != null) {
                    return;
                }
                camera = Camera.open();
                Camera.Parameters parms = camera.getParameters();
                List<Camera.Size> listsize = parms.getSupportedPictureSizes();
                Camera.Size sizeOut = null;
                for (Camera.Size size : listsize) {
                    if (size.width >= width) {
                        break;
                    }
                    sizeOut = size;
                }

                parms.setPreviewSize(videoWidth, videoHeight);
                parms.setPictureSize(videoWidth, videoHeight);
                parms.setPreviewFormat(ImageFormat.NV21);
                camera.setParameters(parms);
                camera.setPreviewCallback(previewCallback);
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();//开始预览
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceDestroyed (SurfaceHolder holder) {
                // 结束预览时关闭摄像头
                if (camera == null) {
                    return;
                }
                camera.setPreviewCallback(null);
                camera.stopPreview();// 停止预览
                camera.release();
                camera = null;
            }
        });

    }

    @Override
    public void onPreviewFrame (byte[] data, Camera camera) {
        EMClient.getInstance().callManager()
                .inputExternalVideoData(data, videoWidth, videoHeight, 0);
    }
}
