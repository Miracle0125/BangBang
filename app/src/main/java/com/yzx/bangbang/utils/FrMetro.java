package com.yzx.bangbang.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.util.Stack;

//用于快速跳转管理FRAGMENT的工具类
public class FrMetro {
    private FragmentManager fm;
    private Fragment current;
    private Stack<Fragment> old;
    private int container;
    private IFrMetro iFrMetro;

    public FrMetro(FragmentManager fm, int container) {
        this.fm = fm;
        this.container = container;
        old = new Stack<>();
    }

    public void goToFragment(Class<?> cls) {
        this.goToFragment(cls, 0);
    }

    public void goToFragment(Class<?> cls, int res) {
        this.goToFragment(cls, res, null);
    }

    public void goToFragment(Class<?> cls, String tag) {
        this.goToFragment(cls, 0, tag);
    }

    public void goToFragment(Class<?> cls, int Res, String tag) {
        if (cls == null)
            return;
        if (Res == 0)
            Res = container;
        FragmentTransaction ft = fm.beginTransaction();
        try {

            String Tag = tag == null ? cls.toString() : tag;
            Fragment fragment = fm.findFragmentByTag(Tag);
            if (current != null) {
                ft.hide(current);
                old.add(current);
            }
            if (fragment == null) {
                fragment = (Fragment) cls.newInstance();
                if (iFrMetro != null) {
                    iFrMetro.onCreate(fragment);
                    iFrMetro = null;
                }
                ft.add(Res, fragment, Tag);
            } else if (fragment.isAdded())
                ft.show(fragment);
            ft.commit();
            current = fragment;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeCurrent() {
        FragmentTransaction ft = fm.beginTransaction();
        if (current != null)
            ft.remove(current);
        if (!old.isEmpty()) {
            current = old.pop();
            ft.show(current).commit();
            return;
        }
        ft.commit();
        current = null;
    }

    public Fragment findFragmentByClass(Class<?> cls) {
        return fm.findFragmentByTag(cls.toString());
    }

    public Fragment getCurrent() {
        return current;
    }

    public void setCallBack(IFrMetro iFrMetro) {
        //map.put(fragment, iFrMetro);
        this.iFrMetro = iFrMetro;
    }

    public interface IFrMetro {
        void onCreate(Fragment fragment);
    }
}
