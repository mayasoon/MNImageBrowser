package com.maning.imagebrowserlibrary;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.maning.imagebrowserlibrary.listeners.OnClickListener;
import com.maning.imagebrowserlibrary.listeners.OnLongClickListener;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;
import com.maning.imagebrowserlibrary.transforms.DefaultTransformer;
import com.maning.imagebrowserlibrary.transforms.DepthPageTransformer;
import com.maning.imagebrowserlibrary.transforms.RotateDownTransformer;
import com.maning.imagebrowserlibrary.transforms.RotateUpTransformer;
import com.maning.imagebrowserlibrary.transforms.ZoomInTransformer;
import com.maning.imagebrowserlibrary.transforms.ZoomOutSlideTransformer;
import com.maning.imagebrowserlibrary.transforms.ZoomOutTransformer;
import com.maning.imagebrowserlibrary.view.MNGestureView;
import com.maning.imagebrowserlibrary.view.MNViewPager;

import java.util.ArrayList;


/**
 * 图片浏览的页面
 */
public class MNImageBrowserActivity extends AppCompatActivity {

//    public final static int ViewPagerTransform_Default = 0;
//    public final static int ViewPagerTransform_DepthPage = 1;
//    public final static int ViewPagerTransform_RotateDown = 2;
//    public final static int ViewPagerTransform_RotateUp = 3;
//    public final static int ViewPagerTransform_ZoomIn = 4;
//    public final static int ViewPagerTransform_ZoomOutSlide = 5;
//    public final static int ViewPagerTransform_ZoomOut = 6;

    private Context context;

    private MNGestureView mnGestureView;
    private MNViewPager viewPagerBrowser;
    private TextView tvNumShow;
    private RelativeLayout rl_black_bg;

    //图片地址
    private ArrayList<String> imageUrlList;
    //当前位置
    private int currentPosition;
    //当前切换的动画
    private ImageBrowserConfig.TransformType transformType;
    //图片加载引擎
    public ImageEngine imageEngine;
    //监听
    public OnLongClickListener onLongClickListener;
    public OnClickListener onClickListener;
    //相关配置信息
    public static ImageBrowserConfig imageBrowserConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFullScreen();
        setContentView(R.layout.activity_mnimage_browser);
        context = this;

        initViews();

        initData();

        initViewPager();

    }

    private void setWindowFullScreen() {
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 19) {
            // 虚拟导航栏透明
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void initViews() {
        viewPagerBrowser = (MNViewPager) findViewById(R.id.viewPagerBrowser);
        mnGestureView = (MNGestureView) findViewById(R.id.mnGestureView);
        tvNumShow = (TextView) findViewById(R.id.tvNumShow);
        rl_black_bg = (RelativeLayout) findViewById(R.id.rl_black_bg);

    }

    private void initData() {
        imageUrlList = imageBrowserConfig.getImageList();
        currentPosition = imageBrowserConfig.getPosition();
        transformType = imageBrowserConfig.getTransformType();
        imageEngine = imageBrowserConfig.getImageEngine();
        onClickListener = imageBrowserConfig.getOnClickListener();
        onLongClickListener = imageBrowserConfig.getOnLongClickListener();

        if(imageUrlList.size() == 1){
            tvNumShow.setVisibility(View.GONE);
        }else{
            tvNumShow.setVisibility(View.VISIBLE);
            tvNumShow.setText(String.valueOf((currentPosition + 1) + "/" + imageUrlList.size()));
        }
    }

    private void initViewPager() {
        viewPagerBrowser.setAdapter(new MyAdapter());
        viewPagerBrowser.setCurrentItem(currentPosition);
        setViewPagerTransforms();
        viewPagerBrowser.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvNumShow.setText(String.valueOf((position + 1) + "/" + imageUrlList.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mnGestureView.setOnSwipeListener(new MNGestureView.OnSwipeListener() {
            @Override
            public void downSwipe() {
                finishBrowser();
            }

            @Override
            public void onSwiping(float deltaY) {
                tvNumShow.setVisibility(View.GONE);

                float mAlpha = 1 - deltaY / 500;
                if (mAlpha < 0.3) {
                    mAlpha = 0.3f;
                }
                if (mAlpha > 1) {
                    mAlpha = 1;
                }
                rl_black_bg.setAlpha(mAlpha);
            }

            @Override
            public void overSwipe() {
                tvNumShow.setVisibility(View.VISIBLE);
                rl_black_bg.setAlpha(1);
            }
        });
    }

    private void setViewPagerTransforms() {
        if (transformType == ImageBrowserConfig.TransformType.Transform_Default) {
            viewPagerBrowser.setPageTransformer(true, new DefaultTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_DepthPage) {
            viewPagerBrowser.setPageTransformer(true, new DepthPageTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_RotateDown) {
            viewPagerBrowser.setPageTransformer(true, new RotateDownTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_RotateUp) {
            viewPagerBrowser.setPageTransformer(true, new RotateUpTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_ZoomIn) {
            viewPagerBrowser.setPageTransformer(true, new ZoomInTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_ZoomOutSlide) {
            viewPagerBrowser.setPageTransformer(true, new ZoomOutSlideTransformer());
        } else if (transformType == ImageBrowserConfig.TransformType.Transform_ZoomOut) {
            viewPagerBrowser.setPageTransformer(true, new ZoomOutTransformer());
        } else {
            viewPagerBrowser.setPageTransformer(true, new DefaultTransformer());
        }
    }

    private void finishBrowser() {
        tvNumShow.setVisibility(View.GONE);
        rl_black_bg.setAlpha(0);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        finish();
        this.overridePendingTransition(0, R.anim.browser_exit_anim);
    }

    @Override
    public void onBackPressed() {
        finishBrowser();
    }


    private class MyAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyAdapter() {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return imageUrlList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View inflate = layoutInflater.inflate(R.layout.mn_image_browser_item_show_image, container, false);
            final PhotoView imageView = (PhotoView) inflate.findViewById(R.id.imageView);
            final RelativeLayout rl_browser_root = (RelativeLayout) inflate.findViewById(R.id.rl_browser_root);
            final String url = imageUrlList.get(position);
            //图片加载
            imageEngine.loadImage(context,url,imageView);

            rl_browser_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishBrowser();
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //单击事件
                    if(onClickListener != null){
                        onClickListener.onClick(MNImageBrowserActivity.this,imageView,position,url);
                    }
                    finishBrowser();
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(onLongClickListener != null){
                        onLongClickListener.onLongClick(MNImageBrowserActivity.this,imageView,position,url);
                    }
                    return false;
                }
            });

            container.addView(inflate);
            return inflate;
        }
    }

}
