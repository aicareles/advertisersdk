package cn.com.heaton.avertisetest.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liulei on 2016/8/3.
 * 封装baseAdapter
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    private List<T> mList;

    protected Context mContext;

    public BaseAdapter(Context context) {
        init(context, new ArrayList<T>());
    }

    public BaseAdapter(Context context, List<T> list) {
        init(context, list);
    }

    private void init(Context context, List<T> list) {
        this.mList = list;
        this.mContext = context;
    }

    public List<T> getList() {
        return mList;
    }

    public void setList(List<T> list) {
        this.mList = list;
    }

    public void clear() {
        this.mList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        if (list != null) {
            this.mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflate(getContentView());
        }
        onInitView(convertView, position);
        return convertView;
    }

    /** 加载布局 */
    private View inflate(int layoutResID) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResID, null);
        return view;
    }

    public abstract int getContentView();

    public abstract void onInitView(View view, int position);


    public static <T extends View> T getAdapterView(View convertView, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            convertView.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = convertView.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;

    }
}
