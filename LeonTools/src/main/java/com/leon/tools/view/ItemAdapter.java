package com.leon.tools.view;

/**
 * Created by leon.zhang on 2015/12/22.
 * 用于ListView item添加方案
 */
public class ItemAdapter extends CommonAdapter<ItemAdapter.AdapterItem> {
    private int[] mItemLayoutIds;

    public ItemAdapter(int layoutId) {
        this(new int[]{layoutId});
    }

    /**
     * 需要传入,必须>0
     *
     * @param layoutIds R.layout.id
     */
    public ItemAdapter(int[] layoutIds) {
        mItemLayoutIds = layoutIds;
    }


    @Override
    public final int getViewTypeCount() {
        return mItemLayoutIds.length;
    }


    @Override
    public final int getItemViewType(int position) {
        return mDatas.get(position).type;
    }

    /**
     * 清除Adapter相关数据
     */
    public final void clearAdapterItems() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 添加一个item
     *
     * @param item
     */
    public final void addAdapaterItem(AdapterItem item) {
        mDatas.add(item);
        notifyDataSetChanged();
    }

    @Override
    protected CommonHolder<AdapterItem> createNewHolder(int viewType, int position) {
        return new CommonHolder<AdapterItem>(mItemLayoutIds[viewType]) {
            @Override
            protected void convertHolder(DataCoverUiController<AdapterItem> ctr, AdapterItem adapterItem) {
                adapterItem.convertItem(ctr, getViewType(), getViewPosition());
            }

            @Override
            protected void onClick(DataCoverUiController<AdapterItem> ctr, AdapterItem adapterItem, int position) {
                adapterItem.onItemClick(ctr, adapterItem.type, position);
            }
        };
    }

    /**
     * AdapterItem基类
     */
    public static abstract class AdapterItem {
        private final int type;

        public AdapterItem(int type) {
            this.type = type;
        }

        protected abstract void convertItem(DataCoverUiController<AdapterItem> ctr, int type, int position);

        protected void onItemClick(DataCoverUiController<AdapterItem> ctr, int type, int position) {

        }
    }
}
