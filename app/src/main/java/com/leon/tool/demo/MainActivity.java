package com.leon.tool.demo;

import com.leon.tools.fgmanager.VhActivity;


public class MainActivity extends VhActivity {

    @Override
    protected void onActivityCreated() {
        startViewHelper(BaseViewHelper.DefaultVh.class);//启动DefaultViewHelper
    }
}
