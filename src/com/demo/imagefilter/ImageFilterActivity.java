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
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.imagefilter.IImageFilter;
import com.imagefilter.Image;
import com.imagefilter.demo.R;
import com.imagefilter.effect.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ImageFilterActivity extends BaseActivity {

    private ViewPager mViewPager;

    private ImageView mOriginImage;

    private Bitmap mOriginBt;

    private Handler mHandler = new Handler(Looper.myLooper());

    private int mRestID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_compose1);

        mRestID = getIntent().getIntExtra(Config.KEY_RES_ID, 0);

        enableHomeButton("滤镜");

        initUI();
    }

    private void initUI() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mOriginImage = (ImageView) findViewById(R.id.original_iv);

        PagerTitleStrip titleStrip = (PagerTitleStrip) findViewById(R.id.title);
        mViewPager.setAdapter(new FacePageAdapter(getApplicationContext()));

        try {
            mOriginBt = ((BitmapDrawable) getResources().getDrawable(mRestID)).getBitmap();
            mOriginImage.setImageBitmap(mOriginBt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<IImageFilter> getFilterList() {
        ArrayList<IImageFilter> ret = new ArrayList<IImageFilter>();
        ret.add(new ExposureFilter(3));
        ret.add(new ThresholdFilter());
        ret.add(new AutoAdjustFilter());
        ret.add(new AutoLevelFilter());
        ret.add(new BigBrotherFilter());
        ret.add(new BilinearDistort());
        ret.add(new BlackWhiteFilter());
        ret.add(new BrickFilter());
        ret.add(new BrightContrastFilter(0.15f, 0.0f));
        ret.add(new BulgeFilter(-97));
        ret.add(new CleanGlassFilter());
        ret.add(new ColorQuantizeFilter());
        ret.add(new ColorToneFilter(0x00FF00, 192));
        ret.add(new ConvolutionFilter());
        ret.add(new EdgeFilter());
        ret.add(new FeatherFilter());
        ret.add(new GradientFilter());
        ret.add(new GradientMapFilter());
        ret.add(new HistogramEqualFilter());
        ret.add(new NoiseFilter());
        ret.add(new RadialDistortionFilter());
        ret.add(new RainBowFilter());
        ret.add(new RaiseFrameFilter(20));
        ret.add(new RectMatrixFilter());
        ret.add(new ReflectionFilter(true));
        ret.add(new ReliefFilter());
        ret.add(new RippleFilter(38, 15, true));
        ret.add(new TwistFilter(27, 106));
        ret.add(new WaveFilter(25, 10));

        return ret;
    }

    private final class FacePageAdapter extends PagerAdapter {

        private Context mContext;

        private List<IImageFilter> mFilterList;

        private SparseArray<WeakReference<Bitmap>> mQRBitmaps = new SparseArray<WeakReference<Bitmap>>();

        public FacePageAdapter(Context context) {
            mContext = context;
            mFilterList = getFilterList();
        }

        public void clearCache() {
            mQRBitmaps.clear();
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
            return mFilterList.size();
        }

        @Override
        public String getPageTitle(int position) {
            return mFilterList.get(position).getClass().getSimpleName();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.face_image_item, null);
            final ImageView imageview = (ImageView) view.findViewById(R.id.preview_iv);

            Bitmap cacheBitmap = null;
            if (mQRBitmaps != null) {
                WeakReference<Bitmap> weakBitmap = mQRBitmaps.get(position);
                if (weakBitmap != null) {
                    cacheBitmap = weakBitmap.get();
                }
            }
            if (cacheBitmap != null && !cacheBitmap.isRecycled()) {
                imageview.setImageBitmap(cacheBitmap);
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        Bitmap composedBmp = makeFilter(mOriginBt, mFilterList.get(position));
                        mQRBitmaps.put(position, new WeakReference<Bitmap>(composedBmp));

                        final Bitmap show = composedBmp;

                        mHandler.removeCallbacksAndMessages(null);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageview.setImageBitmap(show);
                            }
                        });
                    }
                }.start();
            }

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
