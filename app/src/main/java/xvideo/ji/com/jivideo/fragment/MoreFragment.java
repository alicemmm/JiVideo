package xvideo.ji.com.jivideo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import xvideo.ji.com.jivideo.R;

/**
 * Created by Domon on 15-9-18.
 */
public class MoreFragment extends Fragment {
    InterstitialAd mInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soft, container, false);

        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        requestNewInterstitial();

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Toast.makeText(getActivity(), "closeAD", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_HASH")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(getActivity(), "Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }

}
