package cn.mkblog.www.mkbrowser;

import android.app.ActivityManager;
import android.app.Service;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.feiyu.floatingview.weight.FloatingView;

import java.util.List;

import cn.mkblog.www.mkbrowser.MyLifecycleHandler;

public class DialogService extends Service {

    private FloatingView floatingView;
    private int numberMask;
    @Override
    public void onCreate() {
        super.onCreate();
//        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
        if (null == floatingView) {
            floatingView = new FloatingView(this);
            floatingView.showFloat();
//            floatingView.dismissFloatView();
            floatingView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(DialogService.this, "你长按了哦！", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            floatingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    numberMask++;
                    if (numberMask == 3) {
//                        Toast.makeText(DialogService.this, "恭喜你发现了隐藏页面！", Toast.LENGTH_SHORT).show();
//                        numberMask = 0;
//                        Intent intent = new Intent(DialogService.this, EggActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
                    } else {
                        Toast.makeText(DialogService.this, "你点击了", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            Glide.with(getApplicationContext()).load(R.drawable.circle)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(floatingView.CircleImageView());
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return new MyBinder();
    }
    class MyBinder extends Binder {
        public DialogService getService(){
            return DialogService.this;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) {
            floatingView.dismissFloatView();
            floatingView = null;
        }
    }
}
