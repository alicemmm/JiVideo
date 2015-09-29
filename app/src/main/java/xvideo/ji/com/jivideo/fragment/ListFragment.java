package xvideo.ji.com.jivideo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xvideo.ji.com.jivideo.R;

/**
 * Created by Domon on 15-9-18.
 */
public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soft, container, false);
        return view;
    }

}
