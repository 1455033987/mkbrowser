package cn.mkblog.www.mkbrowser;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.cjt2325.cameralibrary.util.ScreenUtils;


public class ShareDialogFragment extends DialogFragment implements View.OnClickListener {

    TextView tvShareFacebook;
    TextView tvShareWechat;
    TextView tvShareMoments;
    TextView tvShareMoments1;
    TextView tvShareMoments2;
    TextView tvCancelShare;
    String getstring;
    private static ShareDialogFragment shareDialogFragment;
    private ScreenUtils UIUtils;

    public static ShareDialogFragment newInstance(String title, String message) {
        ShareDialogFragment shareDialogFragment = new ShareDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("message", message);
        shareDialogFragment.setArguments(bundle);
        return shareDialogFragment;
    }

    public static ShareDialogFragment newInstance() {
        if (shareDialogFragment == null) {
            ShareDialogFragment shareDialogFragment = new ShareDialogFragment();
        }
        return shareDialogFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.share_dialog, container, false);
        tvShareFacebook = (TextView) view.findViewById(R.id.tv_share_facebook);
        tvShareWechat = (TextView) view.findViewById(R.id.tv_share_wechat);
        tvShareMoments = (TextView) view.findViewById(R.id.tv_share_moments);
        tvShareMoments1 = (TextView) view.findViewById(R.id.tv_share_moments1);
        tvShareMoments2 = (TextView) view.findViewById(R.id.tv_share_moments2);
        tvCancelShare = (TextView) view.findViewById(R.id.tv_cancel_share);

        tvCancelShare.setOnClickListener(this);
        tvShareMoments.setOnClickListener(this);
        tvShareMoments1.setOnClickListener(this);
        tvShareMoments2.setOnClickListener(this);
        tvShareWechat.setOnClickListener(this);
        tvShareFacebook.setOnClickListener(this);

        Bundle bundle = getArguments();
        getstring = bundle.getString("useragent");
        tvShareFacebook.setText(getstring);
        if(getstring == "访问电脑版"){
            Drawable drawable= getResources().getDrawable(R.drawable.pc);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvShareFacebook.setCompoundDrawables(null,drawable,null,null);
        }
        else if(getstring == "恢复手机版"){
            Drawable drawable= getResources().getDrawable(R.drawable.phone);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvShareFacebook.setCompoundDrawables(null,drawable,null,null);
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // 不带style的构建的dialog边框距离屏幕会有一点缝隙，也许是机型的原因？
         Dialog dialog = new Dialog(getActivity());
//        Dialog dialog = new Dialog(getActivity(), R.style.CustomDatePickerDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_dialog);
        dialog.setCanceledOnTouchOutside(true);

        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_style);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        return dialog;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_share_facebook:
                shareClickListener.shareToFacebook();
                break;
            case R.id.tv_share_wechat:
                shareClickListener.shareToWechat();
                break;
            case R.id.tv_share_moments:
                shareClickListener.shareToComments();
                break;
            case R.id.tv_share_moments1:
                shareClickListener.shareToSharing();
                break;
            case R.id.tv_share_moments2:
                shareClickListener.shareToComments();
                break;
            case R.id.tv_cancel_share:
                dismiss();
                break;
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        Window window = getDialog().getWindow();

//        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }
    private OnShareClickListener shareClickListener;

    public interface OnShareClickListener {
        void shareToFacebook();

        void shareToWechat();

        void shareToComments();

        void shareToSharing();
    }

    public void setOnShareClickListener(OnShareClickListener shareClickListener) {
        this.shareClickListener = shareClickListener;
    }
}
