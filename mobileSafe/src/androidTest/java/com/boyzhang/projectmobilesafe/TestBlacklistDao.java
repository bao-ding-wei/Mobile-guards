package com.boyzhang.projectmobilesafe;

import android.content.Context;
import android.test.AndroidTestCase;

import com.boyzhang.projectmobilesafe.bean.BlackListNumber;
import com.boyzhang.projectmobilesafe.db.dao.BlacklistDao;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * ===========================================================
 * <p/>
 * 版权 : 张海锋 版权所有(c)2016
 * <p/>
 * 作者 : 张海锋
 * <p/>
 * 版本 : 1.0
 * <p/>
 * 创建时间 : 2016-2-28 下午4:36:13
 * <p/>
 * 描述 : AndroidAStudio中的测试框架
 * 实现步骤:
 * 1.在src目录下创建 androidTest 文件夹
 * 2.在 androidTest 文件夹下创建 java 文件夹
 * 3.在java文件夹下创建一个和项目包名相同的package
 * 4.在package下创建要测试的class文件
 * 5.class继承自AndroidTestCase
 * 6.重写setUp()方法获得Context对象
 * 7.测试对象方法
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class TestBlacklistDao extends AndroidTestCase {

    public Context context;

    @Override
    protected void setUp() throws Exception {

        //拿到Context对象
        this.context = getContext();

        super.setUp();
    }

    /**
     * 测试获取条目数
     */
    public void test_getCount(){
        BlacklistDao blacklistDao = new BlacklistDao(context);
        int count = blacklistDao.getCount();

        System.out.println(count);
    }

    /**
     * 测试添加的方法
     */
    public void test_insertItem() {

        BlacklistDao blacklistDao = new BlacklistDao(context);

        Random random = new Random();

        boolean b = false;
        for (int i = 100; i < 299; i++) {
            b = blacklistDao.insertItem("13000576" + i, String.valueOf(random.nextInt(3) + 1));
        }

        assertEquals(true, b);

        //System.out.println(b);
        Log.v("b", b + "");
    }

    /**
     * 测试删除方法
     */
    public void test_deleteItem() {

        BlacklistDao blacklistDao = new BlacklistDao(context);
        boolean b = blacklistDao.deleteItem("18656466577");

        //通过断言的方式判断是否成功,如果b是true那么测试结果解释绿色
        assertEquals(true, b);

        //Log.v("b", b + "");
    }

    /**
     * 测试更新
     */
    public void test_updateMode() {

        BlacklistDao blacklistDao = new BlacklistDao(context);
        boolean b = blacklistDao.updateMode("13000576117", BlackListNumber.MODE_PHONE);

        assertEquals(true, b);

        //Log.v("b", b + "");
    }

    /**
     * 测试根据号码选择mode
     */
    public void test_selectMode() {

        BlacklistDao blacklistDao = new BlacklistDao(context);
        String s = blacklistDao.selectMode("13000576117");

        Log.v("s", s + "");
    }

    /**
     * 测试查找所有的黑名单
     */
    public void test_selectAll() {

        BlacklistDao blacklistDao = new BlacklistDao(context);
        ArrayList<BlackListNumber> blackListNumbers = blacklistDao.selectAll();

        for (BlackListNumber blackListNumber : blackListNumbers) {
            System.out.println(blackListNumber);
        }
    }
}
