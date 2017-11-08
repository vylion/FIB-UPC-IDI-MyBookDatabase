package com.example.pr_idi.mydatabaseexample;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by vylion on 1/4/17.
 */

public class MyFabBehavior extends FloatingActionButton.Behavior {

    private static final int scrollMargin = 2;

    public MyFabBehavior(Context c, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout cl, FloatingActionButton fab, View directTarget,
                                       View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(cl, fab, directTarget, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout cl, FloatingActionButton fab, View target,
                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(cl, fab, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed);

        if (dyConsumed > scrollMargin && fab.getVisibility() == View.VISIBLE) {
            fab.hide();
        } else if (dyConsumed < -scrollMargin && fab.getVisibility() != View.VISIBLE) {
            fab.show();
        }
    }
}
