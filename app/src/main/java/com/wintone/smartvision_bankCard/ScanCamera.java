package com.wintone.smartvision_bankCard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.hardware.camera2.CameraManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.wintone.R;
import com.wintone.ShowResult;
import com.wintone.bankcard.BankCardAPI;
import com.wintone.utils.Utils;
import com.wintone.view.ViewfinderView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.security.AccessController.getContext;

public class ScanCamera extends Activity implements Callback, PreviewCallback {
    private static final String PATH = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString())).append("/DCIM/Camera/").toString();

    private static double NORMAL_CARD_SCALE = 1.58577d;
    private BankCardAPI api;
    private ImageButton back;
    private Bitmap bitmap;
    private Camera camera;
    private TextView copyright_label;
    private int counter = 0;
    private int counterCut = 0;
    private int counterFail = 0;
    private ImageButton flash;
    private int height;
    private ImageView help_word;
    private boolean isFatty = false;
    private boolean isROI = false;
    private boolean isShowBorder = false;
    List<Size> list;
    private Vibrator mVibrator;
    private ViewfinderView myView;

    private int preHeight = 0;
    private int preWidth = 0;
    private RelativeLayout re_c;
    public int srcHeight;
    public int srcWidth;
    public int surfaceHeight;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    public int surfaceWidth;
    private byte[] tackData;
    private Timer time;
    private TimerTask timer;
    private int width;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan_camera);
        setScreenSize(this);
        findView();
    }

    protected void onRestart() {
        if (this.bitmap != null) {
            this.bitmap.recycle();
            this.bitmap = null;
        }
        this.counterFail = 0;
        super.onRestart();
    }

    protected void onResume() {
        super.onResume();
        this.api = new BankCardAPI();
        this.api.WTInitCardKernal("", 0);
    }

    @SuppressLint({"NewApi"})
    private void setScreenSize(Context context) {
        int x;
        int y;
        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if (VERSION.SDK_INT >= 13) {
            Point screenSize = new Point();
            if (VERSION.SDK_INT >= 17) {
                display.getRealSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            } else {
                display.getSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            }
        } else {
            x = display.getWidth();
            y = display.getHeight();
        }
        this.srcWidth = x;
        this.srcHeight = y;
    }

    private void findView() {
        this.surfaceView = (SurfaceView) findViewById(R.id.surfaceViwe);
        this.re_c = (RelativeLayout) findViewById(R.id.re_c);
        this.help_word = (ImageView) findViewById(R.id.help_word);
        this.copyright_label = (TextView) findViewById(R.id.copyright_label);
        this.back = (ImageButton) findViewById(R.id.back_camera);
        this.flash = (ImageButton) findViewById(R.id.flash_camera);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        this.width = metric.widthPixels;
        this.height = metric.heightPixels;
        if (this.width * 3 == this.height * 4) {
            this.isFatty = true;
        }
        int back_w = (int) (((double) this.width) * 0.066796875d);
        int back_h = back_w;
        LayoutParams layoutParams = new LayoutParams(back_w, back_h);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        int Fheight = this.height;
        if (this.isFatty) {
            Fheight = (int) (((double) this.height) * 0.75d);
        }
        layoutParams.leftMargin = (int) ((((((double) this.width) - ((((double) Fheight) * 0.8d) * 1.585d)) / 2.0d) - ((double) back_h)) / 2.0d);
        layoutParams.bottomMargin = (int) (((double) this.height) * 0.10486111111111111d);
        this.back.setLayoutParams(layoutParams);
        int flash_w = (int) (((double) this.width) * 0.066796875d);
        layoutParams = new LayoutParams(flash_w, flash_w);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        if (this.isFatty) {
            Fheight = (int) (((double) this.height) * 0.75d);
        }
        layoutParams.leftMargin = (int) ((((((double) this.width) - ((((double) Fheight) * 0.8d) * 1.585d)) / 2.0d) - ((double) back_h)) / 2.0d);
        layoutParams.topMargin = (int) (((double) this.height) * 0.10486111111111111d);
        this.flash.setLayoutParams(layoutParams);
        int help_word_w = (int) (((double) this.width) * 0.474609375d);
        int help_word_h = (int) (((double) help_word_w) * 0.05185185185185185d);
        layoutParams = new LayoutParams(help_word_w, help_word_h);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        this.help_word.setLayoutParams(layoutParams);
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        if (this.isFatty) {
            layoutParams.bottomMargin = (this.height / 10) - (help_word_h / 2);
        } else {
            layoutParams.bottomMargin = (this.height / 20) - (help_word_h / 2);
        }
        this.copyright_label.setLayoutParams(layoutParams);
        this.surfaceHolder = this.surfaceView.getHolder();
        this.surfaceHolder.addCallback(this);
        this.surfaceHolder.setType(3);
        this.back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ScanCamera.this.finish();
            }
        });
        this.flash.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!ScanCamera.this.getPackageManager().hasSystemFeature("android.hardware.camera.flash")) {
                    Toast.makeText(ScanCamera.this, ScanCamera.this.getResources().getString(ScanCamera.this.getResources().getIdentifier("toast_flash", "string", ScanCamera.this.getApplication().getPackageName())), Toast.LENGTH_SHORT).show();
                } else if (ScanCamera.this.camera != null) {
                    Parameters parameters = ScanCamera.this.camera.getParameters();
                    if (parameters.getFlashMode().equals("torch")) {
                        parameters.setFlashMode("off");
                        parameters.setExposureCompensation(0);
                    } else {
                        parameters.setFlashMode("torch");
                        parameters.setExposureCompensation(-1);
                    }
                    try {
                        ScanCamera.this.camera.setParameters(parameters);
                    } catch (Exception e) {
                        Toast.makeText(ScanCamera.this, ScanCamera.this.getResources().getString(ScanCamera.this.getResources().getIdentifier("toast_flash", "string", ScanCamera.this.getApplication().getPackageName())), Toast.LENGTH_SHORT).show();
                    }
                    ScanCamera.this.camera.startPreview();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            camera = Camera.open();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (this.camera == null) {
            try {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScanCamera.this,
                            new String[]{Manifest.permission.CAMERA},
                            1);
                }else {
                    camera = Camera.open();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_camera), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        try {
            this.camera.setPreviewDisplay(holder);
            this.time = new Timer();
            if (this.timer == null) {
                this.timer = new TimerTask() {
                    public void run() {
                        if (ScanCamera.this.camera != null) {
                            try {
                                ScanCamera.this.camera.autoFocus(new AutoFocusCallback() {
                                    public void onAutoFocus(boolean success, Camera camera) {
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
            }
            this.time.schedule(this.timer, 500, 2500);
            initCamera(holder);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (this.camera != null) {
                this.camera.setPreviewCallback(null);
                this.camera.stopPreview();
                this.camera.release();
                this.camera = null;
            }
        } catch (Exception e) {
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                if (this.camera != null) {
                    this.camera.setPreviewCallback(null);
                    this.camera.stopPreview();
                    this.camera.release();
                    this.camera = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initCamera(SurfaceHolder holder) {
        Parameters parameters = this.camera.getParameters();
        getCameraPreParameters(this.camera);
        if (!this.isROI) {
            int t = this.height / 10;
            int b = this.height - t;
            int l = (this.width - ((int) (((double) (b - t)) * NORMAL_CARD_SCALE))) / 2;
            int r = this.width - l;
            l += 30;
            t += 19;
            r -= 30;
            b -= 19;
            if (this.isFatty) {
                t = this.height / 5;
                b = this.height - t;
                l = (this.width - ((int) (((double) (b - t)) * NORMAL_CARD_SCALE))) / 2;
                r = this.width - l;
            }
            double proportion = ((double) this.width) / ((double) this.preWidth);
            l = (int) (((double) l) / proportion);
            t = (int) (((double) t) / proportion);
            r = (int) (((double) r) / proportion);
            b = (int) (((double) b) / proportion);
            this.api.WTSetROI(new int[]{l, t, r, b}, this.preWidth, this.preHeight);
            this.isROI = true;
            if (this.isFatty) {
                this.myView = new ViewfinderView(this, this.width, this.height, this.isFatty);
            } else {
                this.myView = new ViewfinderView(this, this.width, this.height);
            }
            this.re_c.addView(this.myView);
        }
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPreviewSize(this.preWidth, this.preHeight);
        if (parameters.getSupportedFocusModes().contains("continuous-picture")) {
            if (this.time != null) {
                this.time.cancel();
                this.time = null;
            }
            if (this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }
            parameters.setFocusMode("continuous-picture");
        } else if (parameters.getSupportedFocusModes().contains("auto")) {
            System.out.println("聚焦else");
            parameters.setFocusMode("auto");
        }
        this.camera.setPreviewCallback(this);
        this.camera.setParameters(parameters);
        try {
            this.camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.camera.startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    public void onPreviewFrame(byte[] data, Camera camera) {
        this.tackData = data;
        Parameters parameters = camera.getParameters();
        int[] isBorders = new int[4];
        this.counter++;
        if (this.counter == 2) {
            this.counter = 0;
            char[] recogval = new char[30];
            int[] bRotated = new int[1];
            int[] pLineWarp = new int[32000];
            int result = this.api.RecognizeNV21(data, parameters.getPreviewSize().width, parameters.getPreviewSize().height, isBorders, recogval, 30, bRotated, pLineWarp);
            if (isBorders[0] == 1) {
                if (this.myView != null) {
                    this.myView.setLeftLine(1);
                }
            } else if (this.myView != null) {
                this.myView.setLeftLine(0);
            }
            if (isBorders[1] == 1) {
                if (this.myView != null) {
                    this.myView.setTopLine(1);
                }
            } else if (this.myView != null) {
                this.myView.setTopLine(0);
            }
            if (isBorders[2] == 1) {
                if (this.myView != null) {
                    this.myView.setRightLine(1);
                }
            } else if (this.myView != null) {
                this.myView.setRightLine(0);
            }
            if (isBorders[3] == 1) {
                if (this.myView != null) {
                    this.myView.setBottomLine(1);
                }
            } else if (this.myView != null) {
                this.myView.setBottomLine(0);
            }
            if (isBorders[0] != 1 || isBorders[1] != 1 || isBorders[2] != 1 || isBorders[3] != 1) {
                this.counterCut++;
                if (this.counterCut == 5) {
                    this.counterFail = 0;
                    this.counterCut = 0;
                }
            } else if (result == 0) {
                camera.stopPreview();
                this.api.WTUnInitCardKernal();
                this.mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
                this.mVibrator.vibrate(100);
                Intent intent = new Intent(this, ShowResult.class);
                intent.putExtra("PicR", pLineWarp);
                intent.putExtra("StringR", recogval);
                startActivity(intent);
                finish();
                camera.setPreviewCallback(null);
            }
        }
    }

    protected void onStop() {
        super.onStop();
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        if (this.bitmap != null) {
            this.bitmap.recycle();
            this.bitmap = null;
        }
        try {
            if (this.camera != null) {
                this.camera.setPreviewCallback(null);
                this.camera.stopPreview();
                this.camera.release();
                this.camera = null;
            }
        } catch (Exception e) {
        }
    }

    public void getCameraPreParameters(Camera camera) {
        this.isShowBorder = false;
        if ("PLK-TL01H".equals(Build.MODEL)) {
            this.preWidth = 1920;
            this.preHeight = 1080;
        } else if ("MI 3".equals(Build.MODEL)) {
            this.preWidth = 1024;
            this.preHeight = 576;
        } else {
            this.list = camera.getParameters().getSupportedPreviewSizes();
            float ratioScreen = ((float) this.srcWidth) / ((float) this.srcHeight);
            int i = 0;
            while (i < this.list.size()) {
                if (ratioScreen == ((float) (this.list.get(i)).width) / ((float) (this.list.get(i)).height) && ((this.list.get(i)).width >= 1280 || (this.list.get(i)).height >= 720)) {
                    if (this.preWidth == 0 && this.preHeight == 0) {
                        this.preWidth = (this.list.get(i)).width;
                        this.preHeight = (this.list.get(i)).height;
                    }
                    if ((this.list.get(0)).width > (this.list.get(this.list.size() - 1)).width) {
                        if (this.preWidth > (this.list.get(i)).width || this.preHeight > (this.list.get(i)).height) {
                            this.preWidth = (this.list.get(i)).width;
                            this.preHeight = (this.list.get(i)).height;
                        }
                    } else if ((this.preWidth < (this.list.get(i)).width || this.preHeight < (this.list.get(i)).height) && this.preWidth < 1280 && this.preHeight < 720) {
                        this.preWidth = (this.list.get(i)).width;
                        this.preHeight = (this.list.get(i)).height;
                    }
                }
                i++;
            }
            if (this.preWidth == 0 || this.preHeight == 0) {
                this.isShowBorder = true;
                this.preWidth = (this.list.get(0)).width;
                this.preHeight = (this.list.get(0)).height;
                i = 0;
                while (i < this.list.size()) {
                    if ((this.list.get(0)).width > (this.list.get(this.list.size() - 1)).width) {
                        if ((this.preWidth >= (this.list.get(i)).width || this.preHeight >= (this.list.get(i)).height) && (this.list.get(i)).width >= 1280) {
                            this.preWidth = (this.list.get(i)).width;
                            this.preHeight = (this.list.get(i)).height;
                        }
                    } else if ((this.preWidth <= (this.list.get(i)).width || this.preHeight <= (this.list.get(i)).height) && this.preWidth < 1280 && this.preHeight < 720 && (this.list.get(i)).width >= 1280) {
                        this.preWidth = (this.list.get(i)).width;
                        this.preHeight = (this.list.get(i)).height;
                    }
                    i++;
                }
            }
            if (this.preWidth == 0 || this.preHeight == 0) {
                this.isShowBorder = true;
                if ((this.list.get(0)).width > (this.list.get(this.list.size() - 1)).width) {
                    this.preWidth = (this.list.get(0)).width;
                    this.preHeight = (this.list.get(0)).height;
                } else {
                    this.preWidth = (this.list.get(this.list.size() - 1)).width;
                    this.preHeight = (this.list.get(this.list.size() - 1)).height;
                }
            }
            if (!this.isShowBorder) {
                this.surfaceWidth = this.srcWidth;
                this.surfaceHeight = this.srcHeight;
            } else if (ratioScreen > ((float) this.preWidth) / ((float) this.preHeight)) {
                this.surfaceWidth = (int) ((((float) this.preWidth) / ((float) this.preHeight)) * ((float) this.srcHeight));
                this.surfaceHeight = this.srcHeight;
            } else {
                this.surfaceWidth = this.srcWidth;
                this.surfaceHeight = (int) ((((float) this.preHeight) / ((float) this.preWidth)) * ((float) this.srcHeight));
            }
        }
    }

    @TargetApi(19)
    public void hiddenVirtualButtons(View mDecorView) {
        if (VERSION.SDK_INT >= 19) {
            mDecorView.setSystemUiVisibility(3334);
        }
    }
}