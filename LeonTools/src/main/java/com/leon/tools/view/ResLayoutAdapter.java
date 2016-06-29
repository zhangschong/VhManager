package com.leon.tools.view;

/**
 * 更为方便的 {@link CommonAdapter},如果 layoutIds > 1 则其类型将从0开始到 layoutIds 的总数,且
 * {@link #getViewTypeCount()}反回类型必须与其顺序相同
 * 
 * @author zhanghong
 * 
 * @param <ItemData>
 */
public abstract class ResLayoutAdapter<ItemData> extends CommonAdapter<ItemData> {

    private int[] mItemLayoutIds;

    public ResLayoutAdapter(int layoutId) {
        this(new int[] { layoutId });
    }

    /**
     * 需要传入,必须>0
     * 
     * @param layoutIds
     *            R.layout.id
     */
    public ResLayoutAdapter(int[] layoutIds) {
        mItemLayoutIds = layoutIds;
    }

    @Override
    public final int getViewTypeCount() {
        return mItemLayoutIds.length;
    }

    /**
     * 当需要设置数据值时,回调
     * 
     * @param ctr
     * @param data
     * @param position
     * @param viewType
     */
    protected abstract void convertItemData(DataCoverUiController<ItemData> ctr, ItemData data, int position,
            int viewType);

    @Override
    protected final CommonHolder<ItemData> createNewHolder(int viewType, int position) {
        return new CommonHolder<ItemData>(mItemLayoutIds[viewType]) {
            @Override
            protected void convertHolder(DataCoverUiController<ItemData> ctr, ItemData data) {
                convertItemData(ctr, data, getViewPosition(), getViewType());
            }
        };
    }

}
