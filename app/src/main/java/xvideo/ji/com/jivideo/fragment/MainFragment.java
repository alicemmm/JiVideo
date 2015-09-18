package xvideo.ji.com.jivideo.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.view.CustomItemFrameLayout;

/**
 * Created by Domon on 15-9-18.
 */
public class MainFragment extends Fragment {
    private CustomItemFrameLayout mBottomItem1Item;
    private CustomItemFrameLayout mBottomItem2Item;
    private CustomItemFrameLayout mBottomItem3Item;
    private CustomItemFrameLayout mTopItem1Item;
    private CustomItemFrameLayout mTopItem2Item;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        findViewById(view);

        //TODO bitmap
        Resources resources = this.getResources();

        mBottomItem1Item.setItemPic(R.mipmap.ic_launcher);
        mBottomItem1Item.setItemInfo("Bottom_Info1");
        mBottomItem2Item.setItemPic(R.mipmap.ic_launcher);
        mBottomItem2Item.setItemInfo("Bottom_Info2");
        mBottomItem3Item.setItemPic(R.mipmap.ic_launcher);
        mBottomItem3Item.setItemInfo("Bottom_Info3");

        mTopItem1Item.setItemPic(R.mipmap.ic_launcher);
        mTopItem1Item.setItemInfo("Top_Info1");
        mTopItem2Item.setItemPic(R.mipmap.ic_launcher);
        mTopItem2Item.setItemInfo("Top_info2");
        return view;
    }

    private void findViewById(View view) {
        mBottomItem1Item = (CustomItemFrameLayout) view.findViewById(R.id.bottom_item1_fl);
        mBottomItem2Item = (CustomItemFrameLayout) view.findViewById(R.id.bottom_item2_fl);
        mBottomItem3Item = (CustomItemFrameLayout) view.findViewById(R.id.bottom_item3_fl);
        mTopItem1Item = (CustomItemFrameLayout) view.findViewById(R.id.top_item1_fl);
        mTopItem2Item = (CustomItemFrameLayout) view.findViewById(R.id.top_item2_fl);
    }
}
