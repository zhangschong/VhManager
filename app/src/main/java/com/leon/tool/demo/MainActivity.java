package com.leon.tool.demo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leon.tools.fgmanager.VhActivity;


public class MainActivity extends VhActivity {

//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
////        getVhManager().startViewHelper(TestViewHelper.class);
//        setContentView(R.layout.activity_main);
//    }

    private int i = 0;

    @Override
    protected void onActivityCreated() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void addFgment(View view) {
        TestFgment tf = new TestFgment(Integer.toString(i++));
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.fl_body, tf, tf.getTfTag());
        ft.addToBackStack(tf.getTfTag());
        ft.commit();
    }

    public void backFgment(View view) {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 1) {
            manager.popBackStack();
        } else {
            finish();
        }
    }

    public void testFgment(View view) {
        FragmentManager manager = getFragmentManager();
        final int size = manager.getBackStackEntryCount() - 2;
        if (size > 0) {
            TestFgment fg = (TestFgment) manager.findFragmentByTag("fg-" + size);
            if (null != fg) {
                Log.e("Leon", " testFgment : " + fg.tag);
                FragmentTransaction ft = manager.beginTransaction();
                ft.remove(fg);
                ft.commit();
            }
        }
    }

    private class TestFgment extends Fragment {
        private TextView iTv;
        private String tag;

        String getTfTag() {
            return tag;
        }

        public TestFgment(String text) {
            tag = "fg-" + text;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.e("leon", tag + ": onCreate");
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.e("leon", tag + ": onPause");
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.e("leon", tag + ": onResume");
        }

        @Override
        public void onStart() {
            super.onStart();
            Log.e("leon", tag + ": onStart");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.e("leon", tag + ": onStop");
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            Log.e("leon", tag + ": onAttach");
        }

        @Override
        public void onDetach() {
            super.onDetach();
            Log.e("leon", tag + ": onDetach");
        }

        @Override
        public void onDestroy() {
            Log.e("leon", tag + ": onDestroy");
            super.onDestroy();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.e("leon", "onCreateView " + savedInstanceState);
            if (null == iTv) {
                iTv = new TextView(getActivity());
                iTv.setText(getTfTag());
                iTv.setBackgroundColor(0xffffffff);
            }
            return iTv;
        }

    }

}
