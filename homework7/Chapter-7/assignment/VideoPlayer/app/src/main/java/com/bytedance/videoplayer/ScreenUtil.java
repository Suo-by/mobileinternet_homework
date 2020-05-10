package com.bytedance.videoplayer;

import android.content.Context;
import android.view.WindowManager;

//屏幕高度
public class ScreenUtil {

    private static ScreenUtil instance;

    private ScreenUtil(){

    }

    public static ScreenUtil getInstance(){
        if (instance == null){
            synchronized (ScreenUtil.class){
                if (instance == null){
                    instance = new ScreenUtil();
                }
            }
        }
        return instance;
    }

    //测出屏幕高度
    @SuppressWarnings("deprecation")
    public int getHeight(Context context,int width,int height){
        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int scressWidth = windowManager.getDefaultDisplay().getWidth();
        if (width != 0) {
            return (int) ((height*scressWidth) / (float)width);
        }
        return 0;
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

}