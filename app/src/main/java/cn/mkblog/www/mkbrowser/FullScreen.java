package cn.mkblog.www.mkbrowser;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

import cn.mkblog.www.mkbrowser.R;
import cn.mkblog.www.mkbrowser.BaseActivity;
//import xyz.doikki.dkplayer.util.DataUtil;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.component.CompleteView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videocontroller.component.GestureView;
import xyz.doikki.videocontroller.component.PrepareView;
import xyz.doikki.videocontroller.component.TitleView;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;

/**
 * 全屏播放
 * Created by Doikki on 2017/4/21.
 */

public class FullScreen extends BaseActivity<VideoView> {

    private StandardVideoController mController;
    private String width = null;
    private String height = null;

    @Override
    protected View getContentView() {
        mVideoView = new VideoView(this);
        adaptCutoutAboveAndroidP();
        return mVideoView;
    }

//    @Override
//    protected int getTitleResId() {
//        return R.string.str_fullscreen_directly;
//    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView.startFullScreen();
        ArrayList<String> infoList = new ArrayList<String>();
        infoList = getIntent().getStringArrayListExtra("videoinfo");
        mVideoView.setUrl(infoList.get(0));
        mController = new StandardVideoController(this);
        mController.addControlComponent(new CompleteView(this));
        mController.addControlComponent(new ErrorView(this));
        mController.addControlComponent(new PrepareView(this));

        TitleView titleView = new TitleView(this);
        // 我这里改变了返回按钮的逻辑，我不推荐这样做，我这样只是为了方便，
        // 如果你想对某个组件进行定制，直接将该组件的代码复制一份，改成你想要的样子
        titleView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleView.setTitle(infoList.get(1));
        mController.addControlComponent(titleView);
        VodControlView vodControlView = new VodControlView(this);
        // 我这里隐藏了全屏按钮并且调整了边距，我不推荐这样做，我这样只是为了方便，
        // 如果你想对某个组件进行定制，直接将该组件的代码复制一份，改成你想要的样子
        vodControlView.findViewById(R.id.fullscreen).setVisibility(View.GONE);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vodControlView.findViewById(R.id.total_time).getLayoutParams();
        lp.rightMargin = PlayerUtils.dp2px(this, 16);
        mController.addControlComponent(vodControlView);
        mController.addControlComponent(new GestureView(this));
        mVideoView.setVideoController(mController);
        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (infoList.get(0) != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                retriever.setDataSource(infoList.get(0), headers);
            } else {
                //retriever.setDataSource(mFD, mOffset, mLength);
            }

            width = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            height = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            retriever.release();
        }
        if(Integer.valueOf(width).intValue() < Integer.valueOf(height).intValue()){
            mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mVideoView.start();
    }

    private void adaptCutoutAboveAndroidP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }


    @Override
    public void onBackPressed() {
        if (!mController.isLocked()) {
            finish();
        }
    }
}

