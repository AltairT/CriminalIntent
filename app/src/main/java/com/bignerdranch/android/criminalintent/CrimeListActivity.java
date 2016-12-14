package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.widget.Toast;

/**
 * Created by Ting on 2016/6/27.
 */
public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks,CrimeFragment.Callbacks {
    private static Context context;

    @Override
    protected Fragment createFragment() {
        context = getApplicationContext();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        Toast.makeText(context, "DPI = " + metric.densityDpi, Toast.LENGTH_SHORT).show();
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
//        return R.layout.activity_twopane;
        return R.layout.activity_masterdetail;
    }


    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }

    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
