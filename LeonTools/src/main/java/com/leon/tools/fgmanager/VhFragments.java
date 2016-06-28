package com.leon.tools.fgmanager;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by leon.zhang on 2016/6/24.
 */

public class VhFragments {

    public static class VhFragment extends Fragment implements IVhFragment {
        private OnFragmentCallback mCb;
        private int mIndex;
        private String mTagName;
        private ViewHelper mVh;//ViewHelper
        private int mCurrentState = VH_STATE_NULL;//当前状态

        void setOnFragmentCallback(OnFragmentCallback cb) {
            mCb = cb;
            if (null == mCb) {
                throw new RuntimeException("OnFragmentCallback is null");
            }
        }

        @Override
        public final void setIndex(int index) {
            mIndex = index;
            mTagName = getClass().getName() + mIndex;
        }

        @Override
        public final void setViewHelper(ViewHelper vh) {
            mVh = vh;
            setViewHelperState(null == mVh ? VH_STATE_NULL : VH_STATE_ATTACH);
        }

        @Override
        public int getIndex() {
            return mIndex;
        }

        @Override
        public ViewHelper getViewHelper() {
            return mVh;
        }

        /**
         * 获取tag Name,用于Fragment栈
         *
         * @return
         */
        @Override
        public String getTagName() {
            return mTagName;
        }

        @Override
        public int getVhState() {
            return mCurrentState;
        }

        @Override
        public boolean isLived() {
            return mCurrentState == VH_STATE_ATTACH;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mCb.onCreate(this, savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return mCb.onCreateView(this, inflater, container, savedInstanceState);
        }

        @Override
        public void onDestroy() {
            mCb.onDestroy(this);
            super.onDestroy();
        }

        @Override
        public void onAttach(Activity activity) {
            setOnFragmentCallback(VhManagerImpl.STATIC_FRAGMENT_CALL_BACKS.get(activity));
            super.onAttach(activity);
            mCb.onAttach(this, activity);
        }

        @Override
        public void onDetach() {
            mCb.onDetach(this);
            super.onDetach();
        }

        @Override
        public void onStart() {
            mCb.onStart(this);
            super.onStart();
        }

        @Override
        public void onStop() {
            mCb.onStop(this);
            super.onStop();
        }

        @Override
        public void onResume() {
            mCb.onResume(this);
            super.onResume();
        }

        @Override
        public void onPause() {
            mCb.onPause(this);
            super.onPause();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            mCb.onSaveInstanceState(this, outState);
            super.onSaveInstanceState(outState);
        }

        /*
        * 设置当前ViewHelper状态
        *
        * @param state
        */
        private void setViewHelperState(int state) {
            mCurrentState = state;
        }

    }

    public static class DialogVhFragment extends DialogFragment implements IVhFragment {
        private OnFragmentCallback mCb;
        private int mIndex;
        private String mTagName;
        private ViewHelper mVh;//ViewHelper
        private int mCurrentState = VH_STATE_NULL;//当前状态

        void setOnFragmentCallback(OnFragmentCallback cb) {
            mCb = cb;
            if (null == mCb) {
                throw new RuntimeException("OnFragmentCallback is null");
            }
        }

        @Override
        public final void setIndex(int index) {
            mIndex = index;
            mTagName = getClass().getName() + mIndex;
        }

        @Override
        public final void setViewHelper(ViewHelper vh) {
            mVh = vh;
            setViewHelperState(null == mVh ? VH_STATE_NULL : VH_STATE_ATTACH);
        }

        @Override
        public int getIndex() {
            return mIndex;
        }

        @Override
        public ViewHelper getViewHelper() {
            return mVh;
        }

        /**
         * 获取tag Name,用于Fragment栈
         *
         * @return
         */
        @Override
        public String getTagName() {
            return mTagName;
        }

        @Override
        public int getVhState() {
            return mCurrentState;
        }

        @Override
        public boolean isLived() {
            return mCurrentState == VH_STATE_ATTACH;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mCb.onCreate(this, savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return mCb.onCreateView(this, inflater, container, savedInstanceState);
        }

        @Override
        public void onDestroy() {
            mCb.onDestroy(this);
            super.onDestroy();
        }

        @Override
        public void onAttach(Activity activity) {
            setOnFragmentCallback(VhManagerImpl.STATIC_FRAGMENT_CALL_BACKS.get(activity));
            super.onAttach(activity);
            mCb.onAttach(this, activity);
        }

        @Override
        public void onDetach() {
            mCb.onDetach(this);
            super.onDetach();
        }

        @Override
        public void onStart() {
            mCb.onStart(this);
            super.onStart();
        }

        @Override
        public void onStop() {
            mCb.onStop(this);
            super.onStop();
        }

        @Override
        public void onResume() {
            mCb.onResume(this);
            super.onResume();
        }

        @Override
        public void onPause() {
            mCb.onPause(this);
            super.onPause();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            mCb.onSaveInstanceState(this, outState);
            super.onSaveInstanceState(outState);
        }

        /*
        * 设置当前ViewHelper状态
        *
        * @param state
        */
        private void setViewHelperState(int state) {
            mCurrentState = state;
        }

    }
}

interface OnFragmentCallback {

    void onCreate(IVhFragment fg, Bundle savedInstanceState);

    View onCreateView(IVhFragment fg, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void onAttach(IVhFragment fg, Activity activity);

    void onStart(IVhFragment fg);

    void onResume(IVhFragment fg);

    void onPause(IVhFragment fg);

    void onStop(IVhFragment fg);

    void onDetach(IVhFragment fg);

    void onDestroy(IVhFragment fg);

    void onSaveInstanceState(IVhFragment fg, Bundle outState);
}



