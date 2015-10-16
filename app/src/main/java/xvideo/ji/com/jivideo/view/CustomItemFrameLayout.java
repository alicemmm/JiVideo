package xvideo.ji.com.jivideo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import xvideo.ji.com.jivideo.R;

public class CustomItemFrameLayout extends FrameLayout {
    private ImageView mPicIv;
    private TextView mInfoTv;
    private ImageView mDpTabIv;

    public CustomItemFrameLayout(Context context) {
        super(context);
    }

    public CustomItemFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.view_main_customitem, this);
        mPicIv = (ImageView) findViewById(R.id.item_pic_iv);
        mInfoTv = (TextView) findViewById(R.id.item_info_tv);
        mDpTabIv = (ImageView) findViewById(R.id.item_dp_iv);
        mDpTabIv.setImageResource(R.mipmap.dp1080p_02);
    }

    /**
     * 设置自定义控件图片
     *
     * @param bitmap
     */
    public void setItemPic(int bitmap) {
//        mPicIv.setImageBitmap(bitmap);
        mPicIv.setImageResource(bitmap);
    }

    public ImageView getItemImageView() {
        return mPicIv;
    }

    /**
     * 设置自定义控件介绍
     *
     * @param str
     */
    public void setItemInfo(String str) {
        mInfoTv.setText(str);
    }

}
