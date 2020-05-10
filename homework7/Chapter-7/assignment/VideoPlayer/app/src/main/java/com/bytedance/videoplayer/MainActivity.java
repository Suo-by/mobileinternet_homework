package com.bytedance.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.view.Window.FEATURE_NO_TITLE;

public class MainActivity extends AppCompatActivity {
    private MyVideoView myVideoView;
    //总布局空间
    private RelativeLayout relacontroller;

    //视频暂停钮
    private ImageView playcontroller;
    //播放进度时间信息
    private TextView time_current_tv, time_total_tv;
    //播放进度条
    private SeekBar play_seek;
    //全屏控制
    private ImageView screen_img;
    public static final int UPDATEUI = 1;
    //横竖屏显示
    private boolean isFullScreen = false;

    public static final int PICK_VIDEO = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initUI();
        setPlayerEvent();
        if( getIntent().getData() == null ) {
            return;
        }
        video_Start(getIntent().getData());
    }

    private void initUI()
    {
        myVideoView = findViewById(R.id.videoview);
        relacontroller = findViewById(R.id.video_layout);
        playcontroller = findViewById(R.id.pause_img);
        screen_img = findViewById(R.id.screen_img);
        time_current_tv = findViewById(R.id.time_current_tv);
        time_total_tv = findViewById(R.id.time_total_tv);
        play_seek = findViewById(R.id.play_seek);
    }
    //播放视频
    private void video_Start(Uri uri) {
        MediaController mediaController = new MediaController(this);
        myVideoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(myVideoView);
        myVideoView.setVideoURI(uri);
        myVideoView.start();
        UIHandler.sendEmptyMessage(UPDATEUI);
    }
    //进度时间转换
    private void TimeFormat(TextView textView, int millsecond)
    {
        int second = millsecond/1000;
        int hh = second/3600;
        int mm = second%3600/60;
        int ss = second%60;

        String str = null;

        if(hh!=0)
            str = String.format("%02d:%02d:%02d", hh, mm, ss);
        else
            str = String.format("%02d:%02d", mm, ss);

        textView.setText(str);
    }
    //时间信息更新
    private Handler UIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == UPDATEUI){
                //获取视频当前的播放时间
                int currentPosition = myVideoView.getCurrentPosition();
                //获取视频播放的总时间
                int totalDuration = myVideoView.getDuration();

                //格式化视频播放时间
                TimeFormat(time_current_tv,currentPosition);
                TimeFormat(time_total_tv,totalDuration);
                //设置视频总时间
                play_seek.setMax(totalDuration);
                play_seek.setProgress(currentPosition);
                //时间界面的更新
                UIHandler.sendEmptyMessageDelayed(UPDATEUI,500);
            }
        }
    };
    @Override
    protected void onPause(){
        super.onPause();
        UIHandler.removeMessages(UPDATEUI);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    //视屏播放控制
    private void setPlayerEvent(){
        playcontroller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myVideoView.isPlaying()){
                    playcontroller.setImageResource(R.drawable.play_btn_style);
                    //暂停
                    myVideoView.pause();
                }
                else {
                    playcontroller.setImageResource(R.drawable.pause_btn_style);
                    //播放
                    myVideoView.start();
                    UIHandler.sendEmptyMessage(UPDATEUI);
                }
            }
        });
        //监听seekbar
        play_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TimeFormat(time_current_tv, i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                UIHandler.removeMessages(UPDATEUI);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                myVideoView.seekTo(progress);
                UIHandler.sendEmptyMessage(UPDATEUI);
            }
        });
        //手动横竖屏实现
        screen_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFullScreen)//竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
    }

    //监听横竖屏变换
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    ,dp2Px(this,240f));

            getSupportActionBar().show();
            relacontroller.setLayoutParams(params);
            myVideoView.setLayoutParams(params);
            isFullScreen = false;
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , LinearLayout.LayoutParams.MATCH_PARENT);

            getSupportActionBar().hide();
            relacontroller.setLayoutParams(params);
            myVideoView.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    ,LinearLayout.LayoutParams.MATCH_PARENT));
            isFullScreen = true;
        }
    }

    //dp->px
    public int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    //px->dp
    public int px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ADD_VIDEO://添加文件管理内的视频
                PickVideo();
                break;
            default://加载内置的示例视频
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.bytedance);
                video_Start(uri);
                break;
        }
        return true;
    }
    private void PickVideo(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent,MainActivity.PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_VIDEO && data != null){
            video_Start(data.getData());
        }
    }
}