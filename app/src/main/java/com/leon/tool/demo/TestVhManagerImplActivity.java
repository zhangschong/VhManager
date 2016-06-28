package com.leon.tool.demo;

import android.content.Context;
import android.view.View;

import com.leon.tools.fgmanager.VhActivity;
import com.leon.tools.fgmanager.ViewHelper;


/**
 * Created by leon.zhang on 2016/6/24.
 */
public class TestVhManagerImplActivity extends VhActivity {

    public static class TestViewHelper extends ViewHelper {

        private static int counts = 0;

        private final int mIndex;

        public TestViewHelper() {
            mIndex = counts++;
        }

        /**
         * 获取createIndex
         *
         * @return
         */
        public int getCreateIndex() {
            return mIndex;
        }

        @Override
        protected View onCreateView(Context context) {
            return View.inflate(context, R.layout.activity_main, null);
        }

        @Override
        protected void onInit() {
            findViewById(R.id.tv_add).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startViewHelper(TestViewHelper.class);
                }
            });

            findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    @Override
    protected void onActivityCreated() {
        getVhManager().startViewHelper(TestViewHelper.class);
    }


}
