package cn.mkblog.www.mkbrowser;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.webkit.JsResult;

import cn.mkblog.www.mkbrowser.R;

/**
 * Created by Administrator on 2018/1/31.
 */

public class FPDialogUtils {

    private static AlertDialog dialog;
    private static String content = "";

    /**
     * @param activity                    Context
     * @param title                       提示标题
     * @param hint                        输入框提示文字
     * @param positiveText                确认
     * @param negativeText                取消
     * @param cancelableTouchOut          点击外部是否隐藏提示框
     * @param alertDialogBtnClickListener 点击监听
     */

    public static void showAlertDialog(Context activity, String title, String hint, String positiveText, String negativeText, boolean cancelableTouchOut, final JsPromptResult alertDialogBtnClickListener) {
        content = "";
        View view = LayoutInflater.from(activity).inflate(R.layout.fp_framework_edit_dialog,null);
//        ImageView frameCloseImage = (ImageView) view.findViewById(R.id.fp_framework_edit_colse);
        EditText frameEditText = (EditText) view.findViewById(R.id.fp_framework_edit_input);
        TextView frametitle = (TextView) view.findViewById(R.id.fp_framework_edit_title);
        TextView frameConfrim = (TextView) view.findViewById(R.id.fp_framework_edit_conform);
        TextView frameCancel = (TextView) view.findViewById(R.id.fp_framework_edit_cancel);
        frameEditText.setHint(hint);
        frametitle.setText(title);
        frameConfrim.setText(positiveText);
        frameCancel.setText(negativeText);

        frameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                content = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        frameConfrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBtnClickListener.confirm(content);
                dialog.dismiss();
            }
        });

        frameCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBtnClickListener.cancel();
                dialog.dismiss();
            }
        });

//        frameCloseImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//去掉圆角背景背后的棱角
        dialog.setCanceledOnTouchOutside(cancelableTouchOut);   //失去焦点dismiss
        dialog.show();

    }


}