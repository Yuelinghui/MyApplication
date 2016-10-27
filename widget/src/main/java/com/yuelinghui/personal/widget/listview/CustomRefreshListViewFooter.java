package com.yuelinghui.personal.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuelinghui.personal.widget.R;

/**
 * Created by yuelinghui on 16/8/19.
 */
public class CustomRefreshListViewFooter extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;
    public final static int STATE_EMPTY = 3;

    ViewGroup mContentView;
    View mProgressBar;
    TextView mHintTxt;

    private Context mContext;

    public CustomRefreshListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomRefreshListViewFooter(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_refresh_listview_footer,null);
        mContentView = (ViewGroup) moreView.findViewById(R.id.view_footer_content);
        mProgressBar = moreView.findViewById(R.id.progressbar_footer);
        mHintTxt = (TextView) moreView.findViewById(R.id.txt_footer_hint);
        addView(moreView);
        moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
    }

    public void setState(int state) {
        mHintTxt.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mHintTxt.setVisibility(View.INVISIBLE);
        if (state ==STATE_EMPTY){
            mHintTxt.setVisibility(View.VISIBLE);
            mHintTxt.setText(R.string.refresh_listview_header_hint_empty);
        }else if (state == STATE_READY) {
            mHintTxt.setVisibility(View.VISIBLE);
            mHintTxt.setText(R.string.refresh_listview_header_hint_ready);
        } else if (state == STATE_LOADING) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mHintTxt.setVisibility(View.VISIBLE);
            mHintTxt.setText(R.string.refresh_listview_header_hint_normal);
        }
    }

    public void setBottomMargin(int height) {
        if (height < 0)
            return;
        LayoutParams lp = (LayoutParams) mContentView
                .getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LayoutParams lp = (LayoutParams) mContentView
                .getLayoutParams();
        return lp.bottomMargin;
    }

    /**
     * normal status
     */
    public void normal() {
        mHintTxt.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * loading status
     */
    public void loading() {
        mHintTxt.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * hide footer when disable pull load more
     */
    public void hide() {
        LayoutParams lp = (LayoutParams) mContentView
                .getLayoutParams();
        lp.height = 0;
        mContentView.setLayoutParams(lp);
    }

    /**
     * show footer
     */
    public void show() {
        LayoutParams lp = (LayoutParams) mContentView
                .getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mContentView.setLayoutParams(lp);
    }
}
