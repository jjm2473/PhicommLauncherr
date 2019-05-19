package com.phicomm.ottbox;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.phicomm.ottbox.launcher.LauncherActivity;

import java.util.ArrayList;
import java.util.List;

public class LauncherSelector extends AppCompatActivity implements ListAdapter, AdapterView.OnItemClickListener {
    private List<ActivityItem> infos;
    private static class ActivityItem {
        private CharSequence name;
        private Drawable icon;
        private ComponentName component;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_selector);

        PackageManager pm = this.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(App.createLauncher(), 0);
        this.infos = new ArrayList<>(resolveInfos.size());
        for (ResolveInfo info:resolveInfos) {
            CharSequence label = info.activityInfo.loadLabel(pm);
            Drawable icon = info.activityInfo.loadIcon(pm);
            ActivityItem item = new ActivityItem();
            item.name = label;
            item.icon = icon;
            item.component = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
            this.infos.add(item);
        }
        ListView listView = this.findViewById(R.id.activity_list);
        listView.setAdapter(this);
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = LayoutInflater.from(this).inflate(R.layout.widget_activity_item, null);
            convertView = v;
        } else {
            v = convertView;
        }
        ((TextView)v.findViewById(R.id.name)).setText(infos.get(position).name);
        ((ImageView)v.findViewById(R.id.icon)).setImageDrawable(infos.get(position).icon);
        //v.setTag(infos.get(position));
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return infos.isEmpty();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        App.TARGET_COMPONENT = infos.get(position).component;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setClass(this, LauncherActivity.class);
        this.startActivity(intent);
        App.save(this.getApplicationContext(), App.TARGET_COMPONENT);
    }
}
