package com.example.chapter3.homework;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyfriendFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        return inflater.inflate(R.layout.fragment_myfriend, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().postDelayed(new Runnable() {

            final View showView = getView().findViewById(R.id.lvItemsm);
            final View hideView = getView().findViewById(R.id.animation_viewm);

            @Override
            public void run() {
                // 这里会在 5s 后执行
                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
                //淡入
                showView.setAlpha(0);
                showView.setVisibility(View.VISIBLE);

                showView.animate()
                        .alpha(1)
                        .setDuration(2000);
                //淡出
                hideView.animate()
                        .alpha(0)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                hideView.setVisibility(View.GONE);
                            }
                        });
            }
        }, 3000);
    }

}
