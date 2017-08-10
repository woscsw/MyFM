package com.example.admin.myfm.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.myfm.R;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView LoadMore
 * @author Swing
 *         on 2016/8/10.
 */
public abstract class LoadMoreAdapter<B> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<B> datas;
    private int page = 1;
    //标记底部显示状态
    protected int hasMore;
    private LoadMoreListener loadMoreListener;
    private IdLoadMoreListener<B> idLoadMoreListener;
    protected Context context;
    //记录尾部的当前位置，防止onBindViewHolder()多次调用里面的操作
    private int pos = -1;
    public LoadMoreAdapter(Context context) {
        this.context = context;
    }
    enum ViewType {
        CONTENT, LOADMORE,
    }

    enum Load {
        EMPTY, ERROR, LOADING
    }

    /**
     * @return  请求的数据数
     */
    public abstract int getPageSize();

    /**
     * 设置是否可加载更多
     */
    protected abstract boolean getHasLoadMore();
    /**
     * 设置更多数据item变成加载出错
     */
    public void setErrorLoad() {
        pos = -1;
        hasMore = Load.ERROR.ordinal();
        notifyItemChanged(getItemCount() - 1);
    }
    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
    public void setIdLoadMoreListener(IdLoadMoreListener<B> idLoadMoreListener) {
        this.idLoadMoreListener = idLoadMoreListener;
    }


    /**
     * 设置RecyclerView
     * @param rv
     * @param spanSize  1行有几个item
     */
    public void bindRecyclerView(RecyclerView rv, final int spanSize){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,spanSize);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (getHasLoadMore()) {
                    return datas == null ? 0 : getItemCount() - 1 == position ? spanSize : 1;
                } else {
                    return datas == null ? 0 : 1;
                }
            }
        });
        rv.setLayoutManager(gridLayoutManager);
        rv.setHasFixedSize(true);
        rv.setAdapter(this);
    }
    /**
     * @param data null-->加载更多数据时,没有数据就传null,加载更多item就变成没有更多数据
     */
    public void addData(List<B> data) {
        if (data != null && data.size() >= getPageSize()) {
            hasMore = Load.LOADING.ordinal();
        } else {
            hasMore = Load.EMPTY.ordinal();
            if (data == null||data.size()==0) {
                pos = -1;//不然刷新后,加载更多item没改变
                notifyItemChanged(getItemCount() - 1);
                return;
            }
        }
        if (datas == null) {
            datas = new ArrayList<>();
        }
        datas.addAll(data);
        notifyDataSetChanged();
    }
    public List<B> getDatas() {
        return datas;
    }
    public void setData(List<B> data) {
        pos = -1;//不然刷新后,加载更多item没改变
        if (data != null && data.size() >= getPageSize()) {
            hasMore = Load.LOADING.ordinal();
        } else {
            hasMore = Load.EMPTY.ordinal();
        }
        if (datas != null)
            datas.clear();
        datas = data;
        page = 1;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ViewType.LOADMORE.ordinal()) {
            return new LoadMoreViewHolder(LayoutInflater.from(context).inflate(R.layout.loadmore_item, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(context).inflate(resId(), parent, false));
        }
    }

    public abstract int resId();

    public abstract void bindViewHolder(ViewHolder holder, B data, int position);

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadMoreViewHolder) {
            if (pos == position) {//防止listener多次回调
                return;
            } else {
                pos = position;
            }
            final View loading = ((LoadMoreViewHolder) holder).loading;
            View empty = ((LoadMoreViewHolder) holder).empty;
            final View error = ((LoadMoreViewHolder) holder).error;
            loading.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
            error.setVisibility(View.GONE);
            if (hasMore == Load.LOADING.ordinal()) {
                loading.setVisibility(View.VISIBLE);
                if (loadMoreListener!=null)
                    loadMoreListener.onLoadMore(++page);
                if (idLoadMoreListener!=null&&datas!=null)
                    idLoadMoreListener.onLoadMore(datas.get(position-1));
            } else if (hasMore == Load.EMPTY.ordinal()) {
                empty.setVisibility(View.VISIBLE);
            } else if (hasMore == Load.ERROR.ordinal()){
                error.setVisibility(View.VISIBLE);
                error.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        error.setVisibility(View.GONE);
                        loading.setVisibility(View.VISIBLE);
                        if (loadMoreListener!=null)
                            loadMoreListener.onLoadMore(page);
                        if (idLoadMoreListener!=null&&datas!=null)
                            idLoadMoreListener.onLoadMore(datas.get(holder.getAdapterPosition()-1));
                    }
                });
            }
        }
        if (holder instanceof ViewHolder)
            bindViewHolder((ViewHolder) holder, datas.get(position),position);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHasLoadMore()) {
            if (position == getItemCount() - 1) {
                return ViewType.LOADMORE.ordinal();
            }
            return ViewType.CONTENT.ordinal();
        } else {
            return ViewType.CONTENT.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        if (getHasLoadMore()) {
            return datas == null ? 0 : datas.size() + 1;//+1 加载更多item
        } else {
            return datas == null ? 0 : datas.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews;

        public ViewHolder(View itemView) {
            super(itemView);
            mViews = new SparseArray<>();
        }

        /**
         * 用于查找 View
         */
        public <V extends View> V getView(int id) {
            View view = mViews.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                mViews.append(id, view);
            }
            return (V) view;
        }
    }

    public interface LoadMoreListener {
        public void onLoadMore(int page);
    }

    public interface IdLoadMoreListener<B> {
        public void onLoadMore(B data);
    }

    public static class LoadMoreViewHolder extends RecyclerView.ViewHolder{
        public View loading;
        public View error;
        public View empty;
        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            loading = itemView.findViewById(R.id.view_loading);
            error = itemView.findViewById(R.id.view_error);
            empty = itemView.findViewById(R.id.view_empty);
        }
    }
}
