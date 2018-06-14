package org.kinecosystem.kinit.view;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import org.kinecosystem.kinit.R;

import java.util.ArrayList;
import java.util.List;

public class BottomTabNavigation extends FrameLayout implements OnClickListener {


    private PageSelectionListener pageSelectionListener;
    private int currentTabSelectedIndex;
    private List<NavigationTab> tabs = new ArrayList<>();

    public BottomTabNavigation(@NonNull Context context) {
        super(context);
        init();
    }

    public BottomTabNavigation(@NonNull Context context,
                               @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomTabNavigation(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public BottomTabNavigation(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setPageSelectionListener(PageSelectionListener pageSelectionListener) {
        this.pageSelectionListener = pageSelectionListener;
    }

    public int getSelectedTabIndex() {
        return currentTabSelectedIndex;
    }

    public void setSelectedTabIndex(int index) {
        tabs.get(currentTabSelectedIndex).setSelected(false);
        currentTabSelectedIndex = index;
        tabs.get(currentTabSelectedIndex).setSelected(true);
    }

    @Override
    public void onClick(View tab) {
        final int tabSelectedIndex = tabs.indexOf(tab);
        final NavigationTab navTab = (NavigationTab) tab;
        final String tabSelectedTitle = navTab.title();
        if (currentTabSelectedIndex != tabSelectedIndex) {
            setSelectedTabIndex(tabSelectedIndex);
            if (pageSelectionListener != null) {
                // Tabs starts at index 1
                pageSelectionListener.onPageSelected(currentTabSelectedIndex + 1, tabSelectedTitle);
            }
        }
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.bottom_navigation_layout, this, true);
        initTabs(view);
    }

    private void initTabs(View view) {
        tabs.add(view.findViewById(R.id.tab_earn));
        tabs.add(view.findViewById(R.id.tab_spend));
        tabs.add(view.findViewById(R.id.tab_balance));
        tabs.add(view.findViewById(R.id.tab_info));
        for (final NavigationTab tab : tabs) {
            tab.setOnClickListener(this);
        }
    }

    public interface PageSelectionListener {

        void onPageSelected(int index, String tabTitle);
    }
}