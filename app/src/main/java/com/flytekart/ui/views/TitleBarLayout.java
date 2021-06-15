package com.flytekart.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.flytekart.R;

public class TitleBarLayout extends RelativeLayout implements View.OnClickListener {
    private TextView tvTitle;
    private ImageView imgRight;
    private TitleBarIconClickListener iconClickListener;

    public TitleBarLayout(Context context) {
        super(context);
        init();
    }

    public TitleBarLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.TitleBarLayout,
                0, 0);
        init();

        try {
            this.setupView(
                    a.getResourceId(R.styleable.TitleBarLayout_titleText, -1),
                    a.getResourceId(R.styleable.TitleBarLayout_drawableRight, -1));
        } finally {
            a.recycle();
        }
    }

    private void setupView(int titleId, int rightResId) {
        if (titleId != -1)
            tvTitle.setText(titleId);
        if (rightResId != -1)
            imgRight.setImageResource(rightResId);
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.title_bar_layout, this, true);
        tvTitle = view.findViewById(R.id.txtTitle);
        imgRight = view.findViewById(R.id.imgRight);
        imgRight.setOnClickListener(this);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(4.0f);
        }
    }

    public void setOnIconClickListener(TitleBarIconClickListener aListener) {
        this.iconClickListener = aListener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgRight) {
            if (iconClickListener != null) {
                iconClickListener.onTitleBarRightIconClicked(v);
            }
        }
    }

    public void setTitleText(String title) {
        tvTitle.setText(title);
    }

    public void removeRightImg() {
        imgRight.setVisibility(GONE);
    }

    public interface TitleBarIconClickListener {
        void onTitleBarRightIconClicked(View view);
    }
}
