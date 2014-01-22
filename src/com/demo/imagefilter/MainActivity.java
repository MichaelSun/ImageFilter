package com.demo.imagefilter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import com.imagefilter.demo.R;

/**
 * Created by michael on 14-1-17.
 */
public class MainActivity extends BaseActivity {


    private ViewPager mViewPager;
    private ImageSelectPagerAdapter mImageSelectPagerAdapter;

    private int[] mResList = { R.drawable.test_1, R.drawable.test_2, R.drawable.test_3, R.drawable.test_4
                                 , R.drawable.test_5, R.drawable.test_6, R.drawable.test_7 };

    private View mFilterBt;

    private int mCurrentResID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.qrcode_main);

        enableHomeButton(getString(R.string.app_name));
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setHomeButtonEnabled(false);

        PagerTitleStrip titleStrip = (PagerTitleStrip) findViewById(R.id.title);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mImageSelectPagerAdapter = new ImageSelectPagerAdapter(getApplicationContext(), mResList);
        mViewPager.setAdapter(mImageSelectPagerAdapter);

        mFilterBt = findViewById(R.id.filter);

        mFilterBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentResID == 0) {
                    Toast.makeText(getApplicationContext(), "没有选择资源", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent i = new Intent();
                i.setClass(getApplicationContext(), ImageFilterActivity.class);
                i.putExtra(Config.KEY_RES_ID, mCurrentResID);
                startActivity(i);
            }
        });
    }


    private class ImageSelectPagerAdapter extends PagerAdapter {

        private Context mContext;

        private int[] mResList;

        public ImageSelectPagerAdapter(Context context, int[] resList) {
            mContext = context;
            mResList = resList;
        }

        @Override
        public String getPageTitle(int position) {
            return getResources().getResourceEntryName(mResList[position]);
        }

        @Override
        public int getCount() {
            if (mResList != null) {
                return mResList.length;
            }

            return 0;
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
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.gradient_one, null);
            ImageView imageview = (ImageView) view.findViewById(R.id.image);
            final View cover = view.findViewById(R.id.cover);
            final CheckBox cb = (CheckBox) view.findViewById(R.id.choose_cb);
            cb.setVisibility(View.VISIBLE);
            if (mResList[position] == mCurrentResID) {
                cb.setChecked(true);
                cover.setVisibility(View.VISIBLE);
            } else {
                cb.setChecked(false);
                cover.setVisibility(View.GONE);
            }

            imageview.setImageResource(mResList[position]);
            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurrentResID != mResList[position]) {
                        mCurrentResID = mResList[position];
                        cb.setChecked(true);
                        cover.setVisibility(View.VISIBLE);
                        notifyDataSetChanged();
                    } else {
                        mCurrentResID = 0;
                        cb.setChecked(false);
                        cover.setVisibility(View.GONE);
                        notifyDataSetChanged();
                    }
                }
            });

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

}