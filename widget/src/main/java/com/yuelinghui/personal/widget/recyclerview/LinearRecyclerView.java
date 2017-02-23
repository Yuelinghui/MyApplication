package com.yuelinghui.personal.widget.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuelinghui on 17/2/22.
 */

public class LinearRecyclerView <T extends Model.ItemData, VH extends Model.BaseViewHolder<T>> extends BaseRecyclerView implements IDataHandler<T>, View.OnClickListener, View.OnLongClickListener  {
    private static final int MIN_INTERVAL_CLICK_TIME = 100;

    private ResourceList<T> mDataResources = new ResourceList<>();

    private VHGenerator<T, VH> mGenerator;

    private OnItemClickListener<T> mOnItemClickListener;
    private OnItemLongClickListener<T> mOnItemLongClickListener;

    protected ArrayList<VH> mVisibleItem = new ArrayList<>();

    private long mLastClickTime;

    private int mLastBindPosition; //统计需要,看到的最后一个位置

    protected Adapter<VH> mAdapter = new Adapter<VH>() {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH viewHolder = mGenerator.buildViewHolder(parent, viewType);
            if (null != mOnItemClickListener) {
                viewHolder.itemView.setOnClickListener(LinearRecyclerView.this);
            }
            if (null != mOnItemLongClickListener) {
                viewHolder.itemView.setOnLongClickListener(LinearRecyclerView.this);
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            mVisibleItem.add(holder);
            holder.onViewBinded();
            holder.bindData(mDataResources.get(position));
            mLastBindPosition = Math.max(mLastBindPosition, position);
        }

        @Override
        public int getItemViewType(int position) {
            return mGenerator.getItemType(mDataResources.get(position));
        }

        @Override
        public int getItemCount() {
            return mDataResources.size();
        }

        @Override
        public void onViewAttachedToWindow(VH holder) {
            holder.onAttachedToWindow();
        }

        @Override
        public void onViewDetachedFromWindow(VH holder) {
            holder.onDetachedFromWindow();
        }

        @Override
        public void onViewRecycled(VH holder) {
            mVisibleItem.remove(holder);
            holder.onViewRecycled();
        }
    };


    public LinearRecyclerView(Context context) {
        this(context, null);
    }

    public LinearRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        setAdapter(mAdapter);
        setItemAnimator(new NoAlphaItemAnimator());
    }

    public ResourceList<T> getDataResources() {
        return mDataResources;
    }

    public VHGenerator<T, VH> getGenerator() {
        return mGenerator;
    }

    public void setGenerator(VHGenerator<T, VH> mGenerator) {
        this.mGenerator = mGenerator;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
        this.mOnItemLongClickListener = listener;
    }

    public int getLastBindPosition() {
        return mLastBindPosition;
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    /********************** IDataHandler ****************/
    @Override
    public void add(@NonNull T model) {
        int curCount = mDataResources.size();
        mDataResources.add(model);
        mAdapter.notifyItemRangeInserted(curCount, 1);
    }

    @Override
    public void add(@NonNull T model, int index) {
        if (index < 0)
            return;

        int curCount = mDataResources.size();
        if (curCount >= index) {
            mDataResources.add(index, model);
            mAdapter.notifyItemRangeInserted(index, 1);
        } else {
            mDataResources.add(model);
            mAdapter.notifyItemRangeInserted(curCount, 1);
        }
    }

    @Override
    public void add(@NonNull List<T> models) {
        if (models.size() == 0)
            return;

        int curCount = mDataResources.size();
        mDataResources.addAll(models);
        mAdapter.notifyItemRangeInserted(curCount, models.size());
    }

    @Override
    public void add(@NonNull List<T> models, int index) {
        if (index < 0 || models.size() == 0)
            return;

        int curCount = mDataResources.size();
        if (curCount >= index) {
            mDataResources.addAll(index, models);
            mAdapter.notifyItemRangeInserted(index, models.size());
        } else {
            mDataResources.addAll(models);
            mAdapter.notifyItemRangeInserted(curCount, models.size());
        }
    }

    @Override
    public void remove(int index) {
        int curCount = mDataResources.size();
        if (curCount > index) {
            mDataResources.remove(index);
            mAdapter.notifyItemRemoved(index);
        }
    }

    @Override
    public void remove(int index, int count) {
        int curCount = mDataResources.size();
        if (curCount > index) {
            if (count + index > curCount) {
                count = curCount - index;
            }
            mDataResources.removeRange(index, count);
            mAdapter.notifyItemRangeRemoved(index, count);
        }
    }

    @Override
    public void remove(@NonNull T model) {
        int position = -1;
        for (int i = 0; i < mDataResources.size(); i++) {
            if (model == mDataResources.get(i)) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            mDataResources.remove(position);
            mAdapter.notifyItemRemoved(position);
        }
    }

    @Override
    public void update(int index) {
        mAdapter.notifyItemChanged(index);
    }

    @Override
    public void update(T model, int index) {
        int count = mDataResources.size();
        if (count > index) {
            mDataResources.remove(index);
            mDataResources.add(index, model);
            mAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void setDatas(List<T> models) {
        mDataResources.clear();
        mVisibleItem.clear();
        mDataResources.addAll(models);
        mAdapter.notifyDataSetChanged();
    }

    protected List<T> getDatas() {
        return mDataResources;
    }

    //-1表示没有
    @Override
    public int getPosition(T model) {
        if (mDataResources == null || mDataResources.size() == 0 || model == null)
            return -1;
        return mDataResources.indexOf(model);
    }

    @Override
    public void clear() {
        mDataResources.clear();
        mVisibleItem.clear();
        mAdapter.notifyDataSetChanged();
    }


    /********************** Custom Class ****************/
    public static class ResourceList<E> extends ArrayList<E> {
        @Override
        public void removeRange(int fromIndex, int toIndex) {
            super.removeRange(fromIndex, toIndex);
        }
    }

    @Override
    public void onClick(View v) {
        long curTime = System.currentTimeMillis();
        if (null != mOnItemClickListener && curTime - mLastClickTime > MIN_INTERVAL_CLICK_TIME) {
            int oriPosi = this.getChildAdapterPosition(v);
            if (oriPosi != NO_POSITION) {
                mLastClickTime = curTime;
                int posi = oriPosi - getHeaderViewsCount();
                mOnItemClickListener.onItemClick(this, v, posi, mDataResources.get(posi));
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        long curTime = System.currentTimeMillis();
        if (null != mOnItemClickListener && curTime - mLastClickTime > MIN_INTERVAL_CLICK_TIME) {
            int oriPosi = this.getChildAdapterPosition(v);
            if (oriPosi != NO_POSITION) {
                mLastClickTime = curTime;
                int posi = oriPosi - getHeaderViewsCount();
                return mOnItemLongClickListener.onItemLongClick(this, v, posi, mDataResources.get(posi));
            }
        }
        return false;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(LinearRecyclerView recyclerView, View view, int position, T model);
    }

    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(LinearRecyclerView recyclerView, View view, int position, T model);
    }

    public class NoAlphaItemAnimator extends RecyclerView.ItemAnimator {
        @Override
        public void runPendingAnimations() {

        }

        @Override
        public boolean animateAppearance(@NonNull ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
            return false;
        }

        @Override
        public boolean animateChange(@NonNull ViewHolder oldHolder, @NonNull ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
            return false;
        }

        @Override
        public boolean animateDisappearance(@NonNull ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
            return false;
        }

        @Override
        public boolean animatePersistence(@NonNull ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
            return false;
        }

        @Override
        public void endAnimation(ViewHolder item) {

        }

        @Override
        public void endAnimations() {

        }

        @Override
        public boolean isRunning() {
            return false;
        }
    }
}
