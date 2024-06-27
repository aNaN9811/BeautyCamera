package com.example.beautycamera.filter;

import android.content.Context;

import com.example.beautycamera.R;

public class ScreenFilter extends BaseFilter {

    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_fragment);
    }
}
