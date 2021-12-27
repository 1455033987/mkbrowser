package cn.mkblog.www.mkbrowser;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.lzg.musicplayer.MyMusicPlayerView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class MusicPlay extends AppCompatActivity{
    private MyMusicPlayerView musicView;
    private List<Object[]> list = new ArrayList<Object[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplay);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("打印", "打印");
    }

    private void initView() {
        ArrayList<String> infoList = new ArrayList<String>();
        infoList = getIntent().getStringArrayListExtra("audioinfo");
        System.out.println(infoList);
        musicView = (MyMusicPlayerView) findViewById(R.id.my_music_view);
        try{
            musicView.setUp(infoList.get(0), infoList.get(1));
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override//点击返回键不关闭当前activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d("zdbb","===============onNewIntent========================");
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            System.out.println("有打开的act");
        }
    }
}
