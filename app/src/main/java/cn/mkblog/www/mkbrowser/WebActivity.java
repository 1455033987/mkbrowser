package cn.mkblog.www.mkbrowser;

import android.annotation.SuppressLint;

import java.util.Random;
import java.net.URL;
import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.chaquo.python.android.AndroidPlatform;
import com.chaquo.python.Python;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.king.toast.ToastUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.DownloadListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.graphics.Matrix;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.study.fileselectlibrary.AllFileActivity;
import com.study.fileselectlibrary.bean.FileItem;
import com.tencent.connect.common.Constants;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class WebActivity extends AppCompatActivity implements View.OnClickListener {
    static final String TAG = "PythonOnAndroid";
    @SuppressLint("SetJavaScriptEnabled")
    private static final String APP_ID = "101953013";
    public IUiListener loginListener;
    private String SCOPE = "all";
    private int mQQType = 0;
    Tencent mTencent;
    String PCuserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X -1_0_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";
    String phoneAgent;
    int isphone = 123;
    private IUiListener mIUiListener;
    private DialogService.MyBinder binder;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DialogService.MyBinder) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };
    private WebView webView;
    private ProgressBar progressBar;
    private EditText textUrl;
    private ImageView webIcon, goBack, goForward, navSet, goHome, btnStart;
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private View customView;
    private FrameLayout fullscreenContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;

    private long exitTime = 0;
    private String failuri = null;

    private Context mContext;
    private InputMethodManager manager;
    private ShareDialogFragment fragment;
    private static final int REQUEST_CODE_SCAN = 111;


    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final int PRESS_BACK_EXIT_GAP = 2000;
    private static final int REQUEST_CODE_ALBUM = 0x00000011;
    private static final int REQUEST_CODE_CAMERA_TRUE = 100;
    private static final int REQUEST_CODE_CAMERA = 2;
    private final int GET_PERMISSION_REQUEST = 100; //????????????????????????
    private ValueCallback<Uri[]> mFilePathCallback = null;
    private String content_type = null;
    private String filename = null;
    private String videourl = null;

    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        Bitmap newbm = null;
        if (bm != null) {
            // ?????????????????????
            int width = bm.getWidth();
            int height = bm.getHeight();
            // ??????????????????
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // ?????????????????????matrix??????
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // ??????????????????
            newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        }
        return newbm;
    }
    public class MyIUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            //??????????????????????????????
            Toast.makeText(WebActivity.this, "????????????", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError uiError) {
            //??????????????????????????????

            Toast.makeText(WebActivity.this, "????????????", Toast.LENGTH_SHORT).show();
            Log.e("LoginError:", uiError.toString());
        }

        @Override
        public void onCancel() {
            //??????????????????????????????
            Toast.makeText(WebActivity.this, "????????????", Toast.LENGTH_SHORT).show();
        }

//        @Override
//        public void onWarning(int code) {
//            if (code == Constants.ERROR_NO_AUTHORITY) {
//                Toast.makeText(WebActivity.this, "onWarning: ????????????Q????????????????????????????????????!", Toast.LENGTH_SHORT).show();
//            }
//        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginListener = new MyIUiListener();
        Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        if (requestCode == Constants.REQUEST_API) {
           if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
              Tencent.handleResultData(data, loginListener);
           }
        }
        Uri[] results = null;
        Uri[] uris = null;
        Uri[] fileuris = null;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ALBUM:
                    if (data != null) {
                        ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                        uris = new Uri[images.size()];
                        for (int i = 0; i < images.size(); i++) {
                            String path = images.get(i);
                            System.out.println(path);
                            File file = new File(path);
                            uris[i] = Uri.fromFile(file);
                        }
                    }
                    break;
                case Constants.REQUEST_LOGIN:
                    if (resultCode == -1) {
                        Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
                        Tencent.handleResultData(data, loginListener);
                    }
                case REQUEST_CODE_CAMERA:
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                        Log.d("CustomChooserActivity", mCameraPhotoPath);
                    }
                    break;
                case REQUEST_CODE_SCAN:
                    if(resultCode == RESULT_OK){
                        if (data != null) {
                            String content = data.getStringExtra(Constant.CODED_CONTENT);
                            if (!isHttpUrl(content)) {
                                // ???????????????????????????????????????
                                if(content != ""){
                                    try {
                                        // URL ??????
                                        content = URLEncoder.encode(content, "utf-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    content = "https://www.baidu.com/s?wd=" + content + "&ie=UTF-8";
                                }
                                else {
                                    content = "about:blank";
                                }
                            }
                            try {
                                OkHttpClient httpClient = new OkHttpClient();

                                Request.Builder builder = new Request.Builder()
                                        .url(content.trim())
                                        .addHeader("abc", "zhe-shi-wo-tian-jia-de");
                                Request request = builder.build();

                                final Response response = httpClient.newCall(request).execute();

                                String name = getHeaderFileName(response);
                                if(name == null){
                                    throw new Exception("unknown url");
                                }
                                filename = name;
                                String conentType = response.header("Content-Type", response.body().contentType().type());
                                content_type = conentType;
                                System.out.println("content_type:"+conentType);
                                videourl = content;

                            }catch (Exception e){
                                System.out.println(e);
                                content_type = "error";
                            }
                            if(content_type.contains("video")){
                                final CommonDialog dialog = new CommonDialog(WebActivity.this);
                                dialog.setMessage("???????????????????????????")
                                        .setImageResId(zoomImg(webView.getFavicon(),150,150))
                                        .setPositive("????????????")
                                        .setNegtive("??????")
//                .setTitle("????????????")
                                        .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                                    @Override
                                    public void onPositiveClick() {
                                        dialog.dismiss();
                                        if(isWifi(WebActivity.this)==false){
                                            final CommonDialog dialog2 = new CommonDialog(WebActivity.this);
                                            dialog2.setMessage("????????????wifi?????????????????????")
                                                    .setImageResId(zoomImg(webView.getFavicon(),150,150))
                                                    .setPositive("??????")
                                                    .setNegtive("??????")
//                .setTitle("????????????")
                                                    .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                                                @Override
                                                public void onPositiveClick() {
                                                    dialog2.dismiss();
                                                    ArrayList<String> info = new ArrayList<String>();
                                                    info.add(videourl);
                                                    info.add(filename);
                                                    Intent intent = new Intent(WebActivity.this, FullScreen.class);
                                                    intent.putStringArrayListExtra("videoinfo", info);
                                                    startActivity(intent);

                                                }
                                                @Override
                                                public void onNegtiveClick() {
                                                    dialog2.dismiss();

                                                }
                                            }).show();
                                        }
                                        else{
                                            ArrayList<String> info = new ArrayList<String>();
                                            info.add(videourl);
                                            info.add(filename);
                                            Intent intent = new Intent(WebActivity.this, FullScreen.class);
                                            intent.putStringArrayListExtra("videoinfo", info);
                                            startActivity(intent);
                                        }

                                    }
                                    @Override
                                    public void onNegtiveClick() {
                                        dialog.dismiss();

                                    }
                                }).show();
                            }
                            else if(content_type.contains("audio")){
                                final CommonDialog dialog = new CommonDialog(WebActivity.this);
                                dialog.setMessage("???????????????????????????")
                                        .setImageResId(zoomImg(webView.getFavicon(),150,150))
                                        .setPositive("????????????")
                                        .setNegtive("??????")
//                .setTitle("????????????")
                                        .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                                    @Override
                                    public void onPositiveClick() {
                                        dialog.dismiss();
                                        if(isWifi(WebActivity.this)==false){
                                            final CommonDialog dialog2 = new CommonDialog(WebActivity.this);
                                            dialog2.setMessage("????????????wifi?????????????????????")
                                                    .setImageResId(zoomImg(webView.getFavicon(),150,150))
                                                    .setPositive("??????")
                                                    .setNegtive("??????")
//                .setTitle("????????????")
                                                    .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                                                @Override
                                                public void onPositiveClick() {
                                                    dialog2.dismiss();
                                                    ArrayList<String> info = new ArrayList<String>();
                                                    info.add(videourl);
                                                    info.add("");
                                                    Intent intent = new Intent(WebActivity.this, MusicPlay.class);
                                                    intent.putStringArrayListExtra("audioinfo", info);
                                                    startActivity(intent);

                                                }
                                                @Override
                                                public void onNegtiveClick() {
                                                    dialog2.dismiss();

                                                }
                                            }).show();
                                        }
                                        else{
                                            ArrayList<String> info = new ArrayList<String>();
                                            info.add(videourl);
                                            info.add("http://81.70.201.139/album/f4eef22b76e2895234733b2a6e281fb2bdb18dd0");
                                            Intent intent = new Intent(WebActivity.this, MusicPlay.class);
                                            intent.putStringArrayListExtra("audioinfo", info);
                                            startActivity(intent);
                                        }

                                    }
                                    @Override
                                    public void onNegtiveClick() {
                                        dialog.dismiss();

                                    }
                                }).show();
                            }

                            else{
                                webView.loadUrl(content);
                            }
                        }
                    }

            }
        }
        else if (resultCode == 101) {
            String path = data.getStringExtra("path");
            System.out.println(path);
            results = new Uri[]{Uri.parse("file://"+path)};
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
            }
            Log.i("CJT", "picture"+path);
        }
        else if (resultCode == 102) {
            String path = data.getStringExtra("path");
            results = new Uri[]{Uri.parse("file://"+path)};
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
            }
            Log.i("CJT", "video");
        }
        else if (resultCode == 103) {
            Toast.makeText(this, "?????????????????????~", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == 200){
            if (resultCode == 200) {
                ArrayList<FileItem> resultFileList = data.getParcelableArrayListExtra("file");
                if (resultFileList != null && resultFileList.size() > 0) {
                    fileuris = new Uri[resultFileList.size()];
                    for (int i = 0; i < resultFileList.size(); i++) {
                        String path = resultFileList.get(i).getPath();
                        System.out.println(path);
                        File file = new File(path);
                        fileuris[i] = Uri.fromFile(file);
                    }
//                    tvResult.setText(resultFileList.get(2).getPath().toString());
                }
            }
        }
        if (mFilePathCallback != null) {
            switch (requestCode){
                case REQUEST_CODE_CAMERA:
                    mFilePathCallback.onReceiveValue(results);
                    mFilePathCallback = null;
                    break;
                case REQUEST_CODE_ALBUM:
                    mFilePathCallback.onReceiveValue(uris);
                    mFilePathCallback = null;
                    break;
                    case REQUEST_CODE_CAMERA_TRUE:
                        mFilePathCallback.onReceiveValue(null);
                        mFilePathCallback = null;
                        break;
                case 200:
                    mFilePathCallback.onReceiveValue(fileuris);
                    mFilePathCallback = null;
                    break;
            }

        }

        }
    private void getDataFromBrowser(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            try {
                webView.loadUrl(data.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    void initPython(){
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }
    // ??????python??????
    String gethtmldes(String url){
        Python py = Python.getInstance();
        PyObject obj4 = py.getModule("hello").callAttr("get_java_bean");
        JavaBean data = obj4.toJava(JavaBean.class);

//        py.getBuiltins().get("help").call();

        PyObject obj1 = py.getModule("hello").callAttr("gethtmldes", url);
        String des = obj1.toJava(String.class);
        return des;

        // ??????python????????????????????????????????? sub(10,b=1,c=3)
//        PyObject obj2 = py.getModule("hello").callAttr("sub", 10,new Kwarg("b", 1), new Kwarg("c", 3));
//        Integer result = obj2.toJava(Integer.class);
//        Log.d(TAG,"sub = "+result.toString());

//        PyObject obj3 = py.getModule("hello").callAttr("get_list", 10,"xx",5.6,'c');
//        List<PyObject> pyList = obj3.asList();
//        Log.d(TAG,"get_list = "+pyList.toString());

        // ???Java???ArrayList????????????Python?????????
//        List<PyObject> params = new ArrayList<PyObject>();
//        params.add(PyObject.fromJava("alex"));
//        params.add(PyObject.fromJava("bruce"));
//        py.getModule("hello").callAttr("print_list", params);

        // Python?????????Java???

//        data.print();
    }
    //?????????QQ??????????????????????????????
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPython();
        mTencent = Tencent.createInstance(APP_ID, WebActivity.this);
        try {
            Intent bindService = new Intent(this,DialogService.class);
            //??????????????????????????????????????? ?????????????????????
            bindService(bindService,serviceConnection,BIND_AUTO_CREATE);
            String androidSDK = Build.VERSION.SDK;
            if(Integer.parseInt(androidSDK)>=23&&!Settings.canDrawOverlays(WebActivity.this)){
                Intent intent2 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                System.out.println(Uri.parse("package:" + getPackageName()));
                intent2.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent2,1);
            }
        }catch (Exception e){

        }

        // ????????????????????????
//        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
//        }
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_web);
        OpenJurisdiction openJurisdiction = new OpenJurisdiction();
        openJurisdiction.open(WebActivity.this);
        mContext = WebActivity.this;
        manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        File docufile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/chatting");
        if(!docufile.exists()){
            docufile.mkdirs();
        }
        // ????????????
        initView();

        // ????????? WebView
        initWeb();
    }

//    public void qqShare(View view) {
//        final Bundle params = new Bundle();
//        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);//???????????????
//        params.putString(QQShare.SHARE_TO_QQ_TITLE, "????????????CSDN??????");//????????????
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,"????????????????????????,??????????????????????????????????????????");//????????????????????????
//        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,"http://blog.csdn.net/sandyran");//????????????
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://avatar.csdn.net/B/3/F/1_sandyran.jpg");//???????????????URL
//        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "??????");//????????????
//        mTencent.shareToQQ(WebActivity.this, params, loginListener);
//    }

//    public void qqQzoneShare(View v) {
//        int QzoneType = QzoneShare.SHARE_TO_QZONE_TYPE_NO_TYPE;
//        Bundle params = new Bundle();
//        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneType);
//        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "????????????CSDN??????");//????????????
//        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "????????????????????????,??????????????????????????????????????????");//?????????????????????
//        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://blog.csdn.net/sandyran/article/details/53204529");//???????????????
//        //???????????????, ???ArrayList<String>?????????????????????????????????????????????????????????????????????9??????????????????????????????????????????
//        ArrayList<String> imageUrls = new ArrayList<String>();
//        imageUrls.add("http://avatar.csdn.net/B/3/F/1_sandyran.jpg");//????????????????????????
//        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);//???????????????URL
//        mTencent.shareToQzone(WebActivity.this, params, loginListener);
//    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.getPackageName(), "???appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "????????????"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "????????????"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
    /**
     * ????????????
     */
    private void initView() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        textUrl = findViewById(R.id.textUrl);
        webIcon = findViewById(R.id.webIcon);
        btnStart = findViewById(R.id.btnStart);
        goBack = findViewById(R.id.goBack);
        goForward = findViewById(R.id.goForward);
        navSet = findViewById(R.id.navSet);
        goHome = findViewById(R.id.goHome);

        // ????????????????????????
        btnStart.setOnClickListener(this);
        goBack.setOnClickListener(this);
        goForward.setOnClickListener(this);
        navSet.setOnClickListener(this);
        goHome.setOnClickListener(this);
        textUrl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = textUrl.getCompoundDrawables()[2];
                if (drawable == null) {
                    return false;
                }
                if (event.getX() > textUrl.getWidth() - textUrl.getCompoundDrawables()[2].getBounds().width()) {
                    textUrl.setText("");
                    textUrl.setCompoundDrawables(null,null,null,null);
                    return false;
                }
                return false;
            }
        });

        textUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                textUrl.setCompoundDrawables(null,null,null,null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s == ""){
                    textUrl.setText("");
                    textUrl.setCompoundDrawables(null,null,null,null);
                }
                else{
                    Drawable rightDrawable = getResources().getDrawable(R.drawable.delete);
                    rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                    textUrl.setCompoundDrawables(null, null, rightDrawable, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                textUrl.setCompoundDrawables(null,null,null,null);
            }
        });
        // ??????????????????????????????????????????
        textUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Drawable rightDrawable = getResources().getDrawable(R.drawable.delete);
                    rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                    textUrl.setCompoundDrawables(null, null, rightDrawable, null);
                    Uri geturi = Uri.parse(webView.getUrl());
                    String urischeme = geturi.getScheme();
                    switch (urischeme){
                        case "file":
                            if(failuri != null){
                                textUrl.setText(failuri);
                            }
                            else{
                                textUrl.setText("");
                                textUrl.setCompoundDrawables(null,null,null,null);
                            }
                            break;
                        default:
                            textUrl.setText(webView.getUrl());
                            break;
                    }
                    // ???????????????????????? TODO:???????????????????????????

                    // ??????????????????
                    textUrl.setSelection(textUrl.getText().length());
                    // ?????????????????????
                    webIcon.setImageResource(R.drawable.internet);
                    // ??????????????????
                    btnStart.setImageResource(R.drawable.go);
                } else {
                    textUrl.setCompoundDrawables(null,null,null,null);
                    // ???????????????
                    textUrl.setText(webView.getTitle());
                    // ??????????????????
                    webIcon.setImageBitmap(webView.getFavicon());
                    // ??????????????????
                    btnStart.setImageResource(R.drawable.refresh);
                }
            }
        });

        // ????????????????????????
        textUrl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    // ????????????
                    btnStart.callOnClick();
                    textUrl.clearFocus();
                }
                return false;
            }
        });
    }
    /**
     * ?????????????????????Bitmap
     *
     * @Author: ChengBin
     * @Time: 16/4/5 ??????2:41
     */
    public static Bitmap webData2bitmap(String src) {
        try {
            Log.d("FileUtil", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

//            //??????????????????
//            //???????????????
//            float newWidth = 200f;
//            float newHeigth = 200f;
//
//            //????????????
//            int width = myBitmap.getWidth();
//            int height = myBitmap.getHeight();
//
//            //????????????
//            float scaleWidth = newWidth / width;
//            float scaleHeigth = newHeigth / height;
//            Matrix matrix = new Matrix();
//            matrix.postScale(scaleWidth, scaleHeigth);
//
//            Bitmap bitmap = Bitmap.createBitmap(myBitmap, 0, 0, width, height, matrix, true);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

//    public Bitmap webData2bitmap(String data) {
//        byte[] imageBytes = Base64.decode(data.split(",")[1], Base64.DEFAULT);
//        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//    }
    //?????????????????????

    private void save2Album(Bitmap bitmap, String fileName) {
        String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/chatting";
        System.out.println(filepath);
        File file = new File(filepath, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            runOnUiThread(() -> {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
//                Toast.makeText(WebActivity.this, "????????????", Toast.LENGTH_SHORT);
            });
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(WebActivity.this, "????????????", Toast.LENGTH_SHORT));
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception ignored) {
            }
        }
    }
    public void saveImage(String data) {
        try {
            Bitmap bitmap = webData2bitmap(data);
            if (bitmap != null) {
                save2Album(bitmap, new SimpleDateFormat("SXS_yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".jpg");
                runOnUiThread(()-> ToastUtil.show(this, "????????????", ToastUtil.TYPE_SUCCESS));
            } else {
                runOnUiThread(() -> Toast.makeText(WebActivity.this, "????????????", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(WebActivity.this, "????????????", Toast.LENGTH_SHORT).show());
            e.printStackTrace();
        }
    }
    /**
     * ????????? web
     */

    private void initWeb() {
        // ?????? WebViewClient
        webView.setWebViewClient(new MkWebViewClient());
        // ?????? WebChromeClient
        webView.setWebChromeClient(new MkWebChromeClient());
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url,
                                        String userAgent,
                                        String contentDisposition,
                                        String mimetype,
                                        long contentLength) {
                Toast.makeText(WebActivity.this, "url=" + url + " mimetype=" + mimetype, Toast.LENGTH_LONG).show();
            }
        });

        webView.setOnLongClickListener(v -> {
            final WebView.HitTestResult hitTestResult = webView.getHitTestResult();
            // ?????????????????????????????????????????????????????????
            Log.d("changan",String.valueOf(hitTestResult.getType()));
            System.out.println(hitTestResult.getType());
            if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                    hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                // ??????????????????????????????
                new AlertDialog.Builder(WebActivity.this)
                        .setItems(new String[]{"?????????????????????", "????????????"}, (dialog, which) -> {
                            String pic = hitTestResult.getExtra();//????????????
                            switch (which) {
                                case 0:
                                    //?????????????????????
                                    new Thread(() -> saveImage(pic)).start();
                                    break;
                                case 1:
                                    // ????????????????????????RxJava????????????
                                    break;
                            }
                        })
                        .show();
                return true;
            }
            return false;//??????????????????????????????
        });
        WebSettings settings = webView.getSettings();
        // ?????? js ??????
        settings.setJavaScriptEnabled(true);
        // ??????????????? UserAgent
        String userAgent = settings.getUserAgentString().replace("Chrome","Firefox") + " mkBrowser/" + getVerName(mContext);
        phoneAgent = userAgent;
        settings.setUserAgentString(userAgent);
        settings.getUserAgentString();
        // ???????????????????????? WebView ?????????
        settings.setUseWideViewPort(true);
        // ????????????????????????
        settings.setLoadWithOverviewMode(true);

        // ????????????????????????true??????????????????????????????
        settings.setSupportZoom(true);
        // ????????????????????????????????????false????????? WebView ????????????
        settings.setBuiltInZoomControls(true);
        // ???????????????????????????
        settings.setDisplayZoomControls(false);

        // ??????
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // ????????????????????????
        settings.setAllowFileAccess(true);
        // ????????????JS???????????????
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // ????????????????????????
        settings.setLoadsImagesAutomatically(true);
        // ????????????????????????
        settings.setDefaultTextEncodingName("utf-8");
        // ????????????
        settings.setDomStorageEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setSupportMultipleWindows(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        // ??????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // ????????????
        webView.loadUrl(getResources().getString(R.string.home_url));
    }
    public static String getValueByName(String url, String name) {
        String result = "";
        int index = url.indexOf("?");
        String temp = url.substring(index + 1);
        String[] keyValue = temp.split("&");
        for (String str : keyValue) {
            if (str.contains(name)) {
                result = str.replace(name + "=", "");
                break;
            }
        }
        return result;
    }


    /**
     * ?????? WebViewClient
     */
    private class MkWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // ?????????webView?????????????????????????????????????????????,?????????????????????????????????
            final String url2 = url;
            if (url == null) {
                view.loadUrl("about:blank");
                // ??????true?????????????????????false?????????
                return true;
            }

            // ????????????????????????
            if (url.startsWith(HTTP) || url.startsWith(HTTPS)) {
                try {
                    OkHttpClient httpClient = new OkHttpClient();

                    Request.Builder builder = new Request.Builder()
                            .url(url.trim())
                            .addHeader("abc", "zhe-shi-wo-tian-jia-de");
                    Request request = builder.build();

                    final Response response = httpClient.newCall(request).execute();

                    String name = getHeaderFileName(response);
                    if(name == null){
                        throw new Exception("unknown url");
                    }
                    filename = name;
                    String conentType = response.header("Content-Type", response.body().contentType().type());
                    content_type = conentType;
                    System.out.println("content_type:"+conentType);
                    videourl = url;

                }catch (Exception e){
                    System.out.println(e);
                    content_type = "error";
                }
                if(content_type.contains("video")){
                    view.stopLoading();
                    final CommonDialog dialog = new CommonDialog(WebActivity.this);
                    dialog.setMessage("???????????????????????????")
                            .setImageResId(zoomImg(webView.getFavicon(),150,150))
                            .setPositive("????????????")
                            .setNegtive("??????")
//                .setTitle("????????????")
                            .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                        @Override
                        public void onPositiveClick() {
                            dialog.dismiss();
                            if(isWifi(WebActivity.this)==false){
                                final CommonDialog dialog2 = new CommonDialog(WebActivity.this);
                                dialog2.setMessage("????????????wifi?????????????????????")
                                        .setImageResId(zoomImg(webView.getFavicon(),150,150))
                                        .setPositive("??????")
                                        .setNegtive("??????")
//                .setTitle("????????????")
                                        .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                                    @Override
                                    public void onPositiveClick() {
                                        dialog2.dismiss();
                                        ArrayList<String> info = new ArrayList<String>();
                                        info.add(videourl);
                                        info.add(filename);
                                        Intent intent = new Intent(WebActivity.this, FullScreen.class);
                                        intent.putStringArrayListExtra("videoinfo", info);
                                        startActivity(intent);

                                    }
                                    @Override
                                    public void onNegtiveClick() {
                                        dialog2.dismiss();

                                    }
                                }).show();
                            }
                            else{
                                ArrayList<String> info = new ArrayList<String>();
                                info.add(videourl);
                                info.add(filename);
                                Intent intent = new Intent(WebActivity.this, FullScreen.class);
                                intent.putStringArrayListExtra("videoinfo", info);
                                startActivity(intent);
                            }

                        }
                        @Override
                        public void onNegtiveClick() {
                            dialog.dismiss();

                        }
                    }).show();
                }
                else if(content_type.contains("audio")){
                    view.stopLoading();
                    final CommonDialog dialog = new CommonDialog(WebActivity.this);
                    dialog.setMessage("???????????????????????????")
                            .setImageResId(zoomImg(webView.getFavicon(),150,150))
                            .setPositive("????????????")
                            .setNegtive("??????")
//                .setTitle("????????????")
                            .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                        @Override
                        public void onPositiveClick() {
                            dialog.dismiss();
                            if(isWifi(WebActivity.this)==false){
                                final CommonDialog dialog2 = new CommonDialog(WebActivity.this);
                                dialog2.setMessage("????????????wifi?????????????????????")
                                        .setImageResId(zoomImg(webView.getFavicon(),150,150))
                                        .setPositive("??????")
                                        .setNegtive("??????")
//                .setTitle("????????????")
                                        .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                                    @Override
                                    public void onPositiveClick() {
                                        dialog2.dismiss();
                                        ArrayList<String> info = new ArrayList<String>();
                                        info.add(videourl);
                                        info.add("");
                                        Intent intent = new Intent(WebActivity.this, MusicPlay.class);
                                        intent.putStringArrayListExtra("audioinfo", info);
                                        startActivity(intent);

                                    }
                                    @Override
                                    public void onNegtiveClick() {
                                        dialog2.dismiss();

                                    }
                                }).show();
                            }
                            else{
                                ArrayList<String> info = new ArrayList<String>();
                                info.add(videourl);
                                info.add("http://81.70.201.139/album/f4eef22b76e2895234733b2a6e281fb2bdb18dd0");
                                Intent intent = new Intent(WebActivity.this, MusicPlay.class);
                                intent.putStringArrayListExtra("audioinfo", info);
                                startActivity(intent);
                            }

                        }
                        @Override
                        public void onNegtiveClick() {
                            dialog.dismiss();

                        }
                    }).show();
                }
                else{
                    view.loadUrl(url);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            // ??????????????????????????????crash (???????????????????????????????????????scheme?????????url???APP, ?????????crash)
            try {
                new AlertDialog.Builder( WebActivity.this )
                        .setIcon(R.mipmap.ic_head_default_right)
                        .setTitle( "???????????????" )
                        .setMessage( "?????????????????????" )
                        .setNegativeButton( "??????",null )
                        .setPositiveButton( "??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // finish();
                                try {
                                    if(url2.indexOf("tencent://")==0){
                                        String url3 = "mqqwpa://im/chat?chat_type=wpa&uin="+getValueByName(url2,"uin");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url3));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                    }
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
                                    startActivity(intent);
//                            socket.close();
                                }
//                        catch (IOException e){
//                            e.printStackTrace();
//                        }
                                catch (Exception e2){
                                  System.out.println("error");
                                }
//                                setResult(2);
//                                finish();
                            }
                        } )
                        .show();
                // TODO:???????????????????????????????????????

                return true;
            } catch (Exception e) {
                return true;
            }
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // ????????????????????????????????????
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);

            // ??????????????????
            textUrl.setText("?????????...");

            // ????????????????????????
            webIcon.setImageResource(R.drawable.internet);
        }
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            //???javascript?????????????????????404????????????
            failuri = failingUrl;
           webView.loadUrl("file:///android_asset/error.html");
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // ????????????????????????????????????
            progressBar.setVisibility(View.INVISIBLE);

            // ????????????
            setTitle(webView.getTitle());
            // ??????????????????
            textUrl.setText(webView.getTitle());
        }
    }
    private Dialog dialog;
    private boolean resetCallback = true;
    private void showChooserDialog() {
        if (dialog == null) {
            dialog = new Dialog(this);
            dialog.setTitle(R.string.file_chooser);
            dialog.setContentView(R.layout.dialog_chooser_layout);

            //??????android5.0.1
            // android5.0???dialog???????????????wrap_content
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (resetCallback && mFilePathCallback != null) {
                        mFilePathCallback.onReceiveValue(null);
                        mFilePathCallback = null;
                    }
                    resetCallback = true;
                }
            });

            dialog.findViewById(R.id.text_album).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetCallback = false;
                    dialog.dismiss();
                    ImageSelector.builder()
                            .useCamera(true) // ????????????????????????
                            .setSingle(false)  //??????????????????
                            .canPreview(true) //??????????????????????????????,????????????true
                            .setMaxSelectCount(0) // ??????????????????????????????????????????0?????????????????????
                            .start(WebActivity.this, REQUEST_CODE_ALBUM); // ????????????
//                    Intent albumIntent = new Intent(Intent.ACTION_PICK);
//                    albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                    startActivityForResult(albumIntent, REQUEST_CODE_ALBUM);
                }
            });
            dialog.findViewById(R.id.text_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetCallback = false;
                    dialog.dismiss();

                    startActivityForResult(new Intent(WebActivity.this, CameraActivity.class), 100);
                }
            });
            dialog.findViewById(R.id.text_files).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetCallback = false;
                    dialog.dismiss();
                    Intent intent = new Intent(WebActivity.this, AllFileActivity.class);
                    startActivityForResult(intent, 200);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });
        }
        dialog.show();
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }


    private String mCameraPhotoPath;

    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        this.getWindow().getDecorView();

        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }

    /**
     * ??????????????????
     */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }
        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        webView.setVisibility(View.VISIBLE);
    }

    /**
     * ??????????????????
     */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

    }
    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    /**
     * ?????? WebChromeClient
     */
    private class MkWebChromeClient extends WebChromeClient {
        private final static int WEB_PROGRESS_MAX = 100;

        @Override
        public View getVideoLoadingProgressView() {
            FrameLayout frameLayout = new FrameLayout(WebActivity.this);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            return frameLayout;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            showCustomView(view, callback);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//???????????????????????????????????????????????????????????????????????????
        }
        @Override
        public void onHideCustomView () {
            hideCustomView();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//??????????????????
        }
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;

            showChooserDialog();
            return true;
        }
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            final CommonDialog dialog = new CommonDialog(WebActivity.this);
            dialog.setMessage(message)
                    .setImageResId(zoomImg(webView.getFavicon(),150,150))
//                .setTitle("????????????")
                    .setSingle(true).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                @Override
                public void onPositiveClick() {
                    dialog.dismiss();
                    result.confirm();

                }

                @Override
                public void onNegtiveClick() {
                    dialog.dismiss();
                    result.cancel();

                }
            }).show();
            return true;
            // return super.onJsAlert(view, url, message, result);
        }

        public boolean onJsBeforeUnload(WebView view, String url,
                                        String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }

        /**
         * ???????????????window.confirm?????????????????????title????????????????????????file:???
         */
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            final CommonDialog dialog = new CommonDialog(WebActivity.this);
            dialog.setMessage(message)
                    .setImageResId(zoomImg(webView.getFavicon(),150,150))
//                .setTitle("????????????")
                    .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                @Override
                public void onPositiveClick() {
                    dialog.dismiss();
                    result.confirm();

                }

                @Override
                public void onNegtiveClick() {
                    dialog.dismiss();
                    result.cancel();

                }
            }).show();
            return true;
            // return super.onJsConfirm(view, url, message, result);
        }

        /**
         * ???????????????window.prompt?????????????????????title????????????????????????file:???
         * window.prompt('???????????????????????????', '618119.com');
         */
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, final JsPromptResult result) {
            final FPDialogUtils dialog = new FPDialogUtils();
            dialog.showAlertDialog(WebActivity.this,message,"?????????","??????","??????",true,result);
            return true;
            // return super.onJsPrompt(view, url, message, defaultValue,
            // result);
        }
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            // ????????????????????????????????????
            progressBar.setProgress(newProgress);
            if (newProgress > 0) {
                if (newProgress == WEB_PROGRESS_MAX) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }



        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);

            // ????????????
            webIcon.setImageBitmap(icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

            // ????????????
            setTitle(title);
            // ??????????????????
            textUrl.setText(title);
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void onBackPressed() {
        // ??????????????????????????????
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if ((System.currentTimeMillis() - exitTime) > PRESS_BACK_EXIT_GAP) {
                // ????????????????????????
                Toast.makeText(mContext, "????????????????????????",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }

        }
    }
    private static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (!TextUtils.isEmpty(dispositionHeader)) {
            dispositionHeader.replace("attachment;filename=", "");
            dispositionHeader.replace("filename*=utf-8", "");
            String[] strings = dispositionHeader.split("; ");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
            return "";
        }
        return getRandomString(16);
    }
    public static String getRandomString(int length) { //length??????????????????????????????
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    private static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager =(ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo =connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null &&activeNetworkInfo.getType() == connectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ?????? ??? ??????
            case R.id.btnStart:
                if (textUrl.hasFocus()) {
                    // ???????????????
                    if (manager.isActive()) {
                        manager.hideSoftInputFromWindow(textUrl.getApplicationWindowToken(), 0);
                    }

                    // ??????????????????????????????
                    String input = textUrl.getText().toString();
                    if (!isHttpUrl(input)) {
                        // ???????????????????????????????????????
                        if(input != ""){
                            try {
                                // URL ??????
                                input = URLEncoder.encode(input, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            input = "https://www.baidu.com/s?wd=" + input + "&ie=UTF-8";
                        }
                        else {
                            input = "about:blank";
                        }
                        }
                    try {
                        OkHttpClient httpClient = new OkHttpClient();

                        Request.Builder builder = new Request.Builder()
                                .url(input.trim())
                                .addHeader("abc", "zhe-shi-wo-tian-jia-de");
                        Request request = builder.build();

                        final Response response = httpClient.newCall(request).execute();

                        String name = getHeaderFileName(response);
                        if(name == null){
                            throw new Exception("unknown url");
                        }
                        filename = name;
                        String conentType = response.header("Content-Type", response.body().contentType().type());
                        content_type = conentType;
                        System.out.println("content_type:"+conentType);
                        videourl = input;

                    }catch (Exception e){
                        System.out.println(e);
                        content_type = "error";
                    }
                    if(content_type.contains("video")){
                        final CommonDialog dialog = new CommonDialog(WebActivity.this);
                        dialog.setMessage("???????????????????????????")
                                .setImageResId(zoomImg(webView.getFavicon(),150,150))
                                .setPositive("????????????")
                                .setNegtive("??????")
//                .setTitle("????????????")
                                .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick() {
                                dialog.dismiss();
                                if(isWifi(WebActivity.this)==false){
                                    final CommonDialog dialog2 = new CommonDialog(WebActivity.this);
                                    dialog2.setMessage("????????????wifi?????????????????????")
                                            .setImageResId(zoomImg(webView.getFavicon(),150,150))
                                            .setPositive("??????")
                                            .setNegtive("??????")
//                .setTitle("????????????")
                                            .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            dialog2.dismiss();
                                            ArrayList<String> info = new ArrayList<String>();
                                            info.add(videourl);
                                            info.add(filename);
                                            Intent intent = new Intent(WebActivity.this, FullScreen.class);
                                            intent.putStringArrayListExtra("videoinfo", info);
                                            startActivity(intent);

                                        }
                                        @Override
                                        public void onNegtiveClick() {
                                            dialog2.dismiss();

                                        }
                                    }).show();
                                }
                                else{
                                    ArrayList<String> info = new ArrayList<String>();
                                    info.add(videourl);
                                    info.add(filename);
                                    Intent intent = new Intent(WebActivity.this, FullScreen.class);
                                    intent.putStringArrayListExtra("videoinfo", info);
                                    startActivity(intent);
                                }

                            }
                            @Override
                            public void onNegtiveClick() {
                                dialog.dismiss();

                            }
                        }).show();
                    }
                    else if(content_type.contains("audio")){
                        final CommonDialog dialog = new CommonDialog(WebActivity.this);
                        dialog.setMessage("???????????????????????????")
                                .setImageResId(zoomImg(webView.getFavicon(),150,150))
                                .setPositive("????????????")
                                .setNegtive("??????")
//                .setTitle("????????????")
                                .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick() {
                                dialog.dismiss();
                                if(isWifi(WebActivity.this)==false){
                                    final CommonDialog dialog2 = new CommonDialog(WebActivity.this);
                                    dialog2.setMessage("????????????wifi?????????????????????")
                                            .setImageResId(zoomImg(webView.getFavicon(),150,150))
                                            .setPositive("??????")
                                            .setNegtive("??????")
//                .setTitle("????????????")
                                            .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            dialog2.dismiss();
                                            ArrayList<String> info = new ArrayList<String>();
                                            info.add(videourl);
                                            info.add("");
                                            Intent intent = new Intent(WebActivity.this, MusicPlay.class);
                                            intent.putStringArrayListExtra("audioinfo", info);
                                                    startActivity(intent);

                                        }
                                        @Override
                                        public void onNegtiveClick() {
                                            dialog2.dismiss();

                                        }
                                    }).show();
                                }
                                else{
                                    ArrayList<String> info = new ArrayList<String>();
                                    info.add(videourl);
                                    info.add("http://81.70.201.139/album/f4eef22b76e2895234733b2a6e281fb2bdb18dd0");
                                    Intent intent = new Intent(WebActivity.this, MusicPlay.class);
                                    intent.putStringArrayListExtra("audioinfo", info);
                                    startActivity(intent);
                                }

                            }
                            @Override
                            public void onNegtiveClick() {
                                dialog.dismiss();

                            }
                        }).show();
                    }

                    else{
                        webView.loadUrl(input);
                    }

                    // ???????????????????????????
                    textUrl.clearFocus();

                } else {
                    // ??????????????????????????????
                    if(failuri!=null){
                        webView.loadUrl(failuri);
                        failuri = null;
                    }
                    else {
                        webView.reload();
                    }
                }
                break;

            // ??????
            case R.id.goBack:
                webView.goBack();
                break;

            // ??????
            case R.id.goForward:
                webView.goForward();
                break;

            // ??????
            case R.id.navSet:
                fragment = new ShareDialogFragment();
                if(isphone == 123){
                    Bundle bundle = new Bundle();
                    bundle.putString("useragent","???????????????");
                    fragment.setArguments(bundle);
                    fragment.show(getFragmentManager(), "");
                }
                else if(isphone == 456){
                    Bundle bundle = new Bundle();
                    bundle.putString("useragent","???????????????");
                    fragment.setArguments(bundle);
                    fragment.show(getFragmentManager(), "");
                }
                fragment.setOnShareClickListener(new ShareDialogFragment.OnShareClickListener() {
                    @Override
                    public void shareToFacebook() {
                        if(isphone == 123){
                            isphone = 456;
                            WebSettings settings = webView.getSettings();
                            settings.setUserAgentString(PCuserAgent);
                            webView.reload();
                            fragment.dismiss();
                        }
                        else if(isphone == 456){
                            isphone = 123;
                            WebSettings settings = webView.getSettings();
                            settings.setUserAgentString(phoneAgent);
                            webView.reload();
                            fragment.dismiss();
                        }
                    }

                    @Override
                    public void shareToWechat() {
                        String urlcopy = webView.getUrl();
                        copy(urlcopy);
                        ToastUtil.show(WebActivity.this, "????????????", ToastUtil.TYPE_SUCCESS);
                        fragment.dismiss();
                    }

                    @Override
                    public void shareToComments() {
                        AndPermission.with(WebActivity.this)
                                .runtime()
                                .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                                .onGranted(data -> {
                                    Intent intent = new Intent(WebActivity.this, CaptureActivity.class);
                                    ZxingConfig config = new ZxingConfig();
                                    config.setFullScreenScan(true);//??????????????????  ?????????true  ??????false??????????????????????????????
                                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                                })
                                .onDenied(data -> {
                                    Uri packageURI = Uri.parse("package:" + getPackageName());
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    startActivity(intent);

                                    Toast.makeText(WebActivity.this, "???????????????????????????", Toast.LENGTH_LONG).show();
                                })
                                .start();
                        fragment.dismiss();
                    }
                    @Override
                    public void shareToSharing(){
                        String url = webView.getUrl();
                        Bundle params = new Bundle();
                        mQQType = 1;
                        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, webView.getTitle());
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,gethtmldes(url));
                        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,url);
                        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"https://avatar.csdn.net/C/3/D/1_u013451048.jpg");
                        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "CSDN");
                        params.putString(QQShare.SHARE_TO_QQ_EXT_INT, "??????????????????");
//                        Handler handler = new Handler(Looper.getMainLooper());
                        mTencent.shareToQQ(WebActivity.this, params, mIUiListener);
                    }
                });
//                Toast.makeText(mContext, "???????????????", Toast.LENGTH_SHORT).show();
                break;

            // ??????
            case R.id.goHome:
                webView.loadUrl(getResources().getString(R.string.home_url));
                break;

            default:
        }
    }
    //??????
    private void copy(String data) {
        // ?????????????????????
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // ?????????????????????????????????????????????????????????????????????????????????????????????,???????????????
        // newHtmlText???
        // newIntent???
        // newUri???
        // newRawUri
        ClipData clipData = ClipData.newPlainText(null, data);

        // ??????????????????????????????????????????
        clipboard.setPrimaryClip(clipData);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            webView.getClass().getMethod("onPause").invoke(webView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromBrowser(getIntent());
        try {
            webView.getClass().getMethod("onResume").invoke(webView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getDataFromBrowser(intent);
    }

    /**
     * ????????????????????????URL???https://blog.csdn.net/bronna/article/details/77529145???
     *
     * @param urls ?????????????????????
     * @return true:???URL???false:??????URL
     */
    public static boolean isHttpUrl(String urls) {
        boolean isUrl;
        // ???????????????????????????????????????
        String regex = "^(http|https)://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";

        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(urls.trim());
        isUrl = mat.matches();
        return isUrl;
    }

    /**
     * ?????????????????????
     *
     * @param context ?????????
     * @return ??????????????????
     */
    private static String getVerName(Context context) {
        String verName = "unKnow";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

}
