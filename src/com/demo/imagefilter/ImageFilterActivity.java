package com.demo.imagefilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.demo.imagefilter.utils.utils;
import com.imagefilter.IImageFilter;
import com.imagefilter.Image;
import com.imagefilter.demo.R;
import com.imagefilter.effect.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageFilterActivity extends BaseActivity {

    private ViewPager mViewPager;

    private ImageView mOriginImage;

    private Bitmap mOriginBt;

    private Handler mHandler = new Handler(Looper.myLooper());

    private View mLoading;

    private int mRestID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_compose1);

        mRestID = getIntent().getIntExtra(Config.KEY_RES_ID, 0);

        enableHomeButton("filter");

        initUI();
    }

    private void initUI() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mOriginImage = (ImageView) findViewById(R.id.original_iv);
        mLoading = findViewById(R.id.loading_spinner);
        mLoading.setVisibility(View.GONE);

        PagerTitleStrip titleStrip = (PagerTitleStrip) findViewById(R.id.title);
        mViewPager.setAdapter(new FacePageAdapter(getApplicationContext()));

        try {
            mOriginBt = ((BitmapDrawable) getResources().getDrawable(mRestID)).getBitmap();
            mOriginImage.setImageBitmap(mOriginBt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mOriginImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bitmap save = ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap();
                String filterName = (String) v.getTag();
                utils.saveBitmapImage(getApplicationContext(), save, filterName);
                Toast.makeText(getApplicationContext(), "保存成功" + filterName, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public static HashMap<Integer, IImageFilter> getFilterMap() {
        HashMap<Integer, IImageFilter> ret = new HashMap<Integer, IImageFilter>();
        ret.put(R.drawable.exposurefilter, new ExposureFilter(3));
        ret.put(R.drawable.thresholdfilter, new ThresholdFilter());
        ret.put(R.drawable.autoadjustfilter, new AutoAdjustFilter());
        ret.put(R.drawable.bigbrotherfilter, new BigBrotherFilter());
        ret.put(R.drawable.blackwhitefilter, new BlackWhiteFilter());
        ret.put(R.drawable.brickfilter, new BrickFilter());
        ret.put(R.drawable.brightcontrastfilter, new BrightContrastFilter(0.15f, 0.0f));
        ret.put(R.drawable.bulgefilter, new BulgeFilter(-97));
        ret.put(R.drawable.cleanglassfilter, new CleanGlassFilter());
        ret.put(R.drawable.colorquantizefilter, new ColorQuantizeFilter());
        ret.put(R.drawable.colortonefilter, new ColorToneFilter(0x00FF00, 192));
        ret.put(R.drawable.convolutionfilter, new ConvolutionFilter());
        ret.put(R.drawable.edgefilter, new EdgeFilter());
        ret.put(R.drawable.featherfilter, new FeatherFilter());
        ret.put(R.drawable.gradientmapfilter, new GradientMapFilter());
        ret.put(R.drawable.histogramequalfilter, new HistogramEqualFilter());
        ret.put(R.drawable.noisefilter, new NoiseFilter());
        ret.put(R.drawable.radialdistortionfilter, new RadialDistortionFilter());
        ret.put(R.drawable.rainbowfilter, new RainBowFilter());
        ret.put(R.drawable.raiseframefilter, new RaiseFrameFilter(20));
        ret.put(R.drawable.rectmatrixfilter, new RectMatrixFilter());
        ret.put(R.drawable.reflectionfilter, new ReflectionFilter(true));
        ret.put(R.drawable.relieffilter, new ReliefFilter());
        ret.put(R.drawable.ripplefilter, new RippleFilter(38, 15, true));
        ret.put(R.drawable.twistfilter, new TwistFilter(27, 106));
        ret.put(R.drawable.wavefilter, new WaveFilter(25, 10));

        return ret;
    }

    private final class FacePageAdapter extends PagerAdapter {

        private Context mContext;

        private HashMap<Integer, IImageFilter> mFilterMap;

        private List<Integer> mFilerItemList = new ArrayList<Integer>();

        public FacePageAdapter(Context context) {
            mContext = context;
            mFilterMap = getFilterMap();
            mFilerItemList.addAll(mFilterMap.keySet());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView imageview = (ImageView) ((View) object).findViewById(R.id.preview_iv);
            imageview.setImageBitmap(null);

            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFilerItemList.size();
        }

        @Override
        public String getPageTitle(int position) {
            return mFilterMap.get(mFilerItemList.get(position)).getClass().getSimpleName();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.face_image_item, null);
            ImageView imageview = (ImageView) view.findViewById(R.id.preview_iv);

            imageview.setImageResource(mFilerItemList.get(position));
            imageview.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mLoading.setVisibility(View.VISIBLE);
                    new Thread() {
                        @Override
                        public void run() {
                            final Bitmap composedBmp = makeFilter(mOriginBt, mFilterMap.get(mFilerItemList.get(position)));
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mOriginImage.setImageBitmap(composedBmp);
                                    mOriginImage.setTag(mFilterMap.get(mFilerItemList.get(position)).getClass().getSimpleName().toLowerCase());
                                    mLoading.setVisibility(View.GONE);
                                }
                            });
                        }
                    }.start();
                }
            });

//            Bitmap cacheBitmap = null;
//            if (mQRBitmaps != null) {
//                WeakReference<Bitmap> weakBitmap = mQRBitmaps.get(position);
//                if (weakBitmap != null) {
//                    cacheBitmap = weakBitmap.get();
//                }
//            }
//
//            imageview.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    Bitmap save = ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap();
//                    String filterName = (String) imageview.getTag();
//                    utils.saveBitmapImage(getApplicationContext(), save, filterName);
//                    Toast.makeText(getApplicationContext(), "保存成功" + filterName, Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//            });
//
//            imageview.setTag(mFilterMap.get(mFilerItemList.get(position)).getClass().getSimpleName());
//            if (cacheBitmap != null && !cacheBitmap.isRecycled()) {
//                imageview.setImageBitmap(cacheBitmap);
//            } else {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        Bitmap composedBmp = makeFilter(mOriginBt, mFilterMap.get(mFilerItemList.get(position)));
//                        mQRBitmaps.put(position, new WeakReference<Bitmap>(composedBmp));
//
//                        final Bitmap show = composedBmp;
//
//                        mHandler.removeCallbacksAndMessages(null);
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                imageview.setImageBitmap(show);
//                            }
//                        });
//                    }
//                }.start();
//            }

            container.addView(view);
            return view;
        }
    }

    public static Bitmap makeFilter(Bitmap bt, IImageFilter filter) {
        try {
            Image img = new Image(bt);
            if (filter != null) {
                img = filter.process(img);
                img.copyPixelsFromBuffer();
            }
            return img.getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
