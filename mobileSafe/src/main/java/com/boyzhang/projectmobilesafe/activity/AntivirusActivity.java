package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.bean.AntivirusInfo;
import com.boyzhang.projectmobilesafe.config.NetConfig;
import com.boyzhang.projectmobilesafe.db.dao.AntivirusDao;
import com.boyzhang.projectmobilesafe.utils.EncodingUtils;
import com.boyzhang.projectmobilesafe.utils.UiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import java.util.List;

/**
 * 病毒查杀Activity
 */
public class AntivirusActivity extends Activity {

    private static final int ANTIVIRUS_STATUS_START = 1;
    private static final int ANTIVIRUS_STATUS_ONANTIVIRUS = 2;
    private static final int ANTIVIRUS_STATUS_STOP = 3;

    private ImageView ivAntivirusRadar;
    private TextView tvAntivirusProgressText;
    private ProgressBar tvAntivirusProgressBar;
    private LinearLayout llAntivirusInformations;

    public volatile boolean stopThread = false;//线程终止标示,volatile保证了线程的安全

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int what = msg.what;
            switch (what) {
                case ANTIVIRUS_STATUS_START://开始查杀
                    int appSize = msg.arg1;
                    tvAntivirusProgressBar.setMax(appSize);//进度条最大值
                    break;
                case ANTIVIRUS_STATUS_ONANTIVIRUS://查杀中
                    AntivirusInfos antivirusInfos = (AntivirusInfos) msg.obj;
                    tvAntivirusProgressBar.setProgress(antivirusInfos.currentTimeCount);//当前进度

                    String[] antivirusInfo = antivirusInfos.antivirusInfo;

                    //增加一个信息
                    TextView child = new TextView(AntivirusActivity.this);
                    child.setTextSize(12);

                    if (antivirusInfo != null) {
                        child.setTextColor(Color.RED);
                        child.setText(antivirusInfos.appName + ": " + antivirusInfo[1]);
                    } else {
                        child.setText(antivirusInfos.appName + ": 安全程序");
                    }

                    //llAntivirusInformations.addView(child,0);
                    llAntivirusInformations.addView(child);
                    /**
                     * ScrollView 自动滚动
                     */
                    svAntivirusInfo.post(new Runnable() {

                        @Override
                        public void run() {
                            //一直往下面进行滚动
                            svAntivirusInfo.fullScroll(svAntivirusInfo.FOCUS_DOWN);

                        }
                    });
                    break;
                case ANTIVIRUS_STATUS_STOP://查杀完成
                    Toast.makeText(AntivirusActivity.this, "查杀完成", Toast.LENGTH_SHORT).show();
                    //停止旋转动画
                    ivAntivirusRadar.clearAnimation();
                    break;
                default:
                    break;
            }
        }
    };
    private Thread t;
    private ScrollView svAntivirusInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);

        //获取控件
        ivAntivirusRadar = (ImageView) findViewById(R.id.iv_antivirus_radar);
        tvAntivirusProgressText = (TextView) findViewById(R.id.tv_antivirus_progress_text);
        tvAntivirusProgressBar = (ProgressBar) findViewById(R.id.tv_antivirus_progress_bar);
        llAntivirusInformations = (LinearLayout) findViewById(R.id.ll_antivirus_informations);
        svAntivirusInfo = (ScrollView) findViewById(R.id.sv_antivirus_info);


        initUI();
        startAntiVirus();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Activity Stop时就停止杀毒
        stopThread = true;
    }

    /**
     * 初始化UI
     */
    private void initUI() {

        startAnimation();//开启雷达旋转动画

    }

    /**
     * 初始化数据
     */
    private void startAntiVirus() {

        t = new Thread() {

            @Override
            public void run() {
                super.run();

                PackageManager packageManager = getPackageManager();//拿到包管理器
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);//拿到安装的包集合

                int appSize = installedPackages.size();
                //--------------------------------
                Message msg1 = handler.obtainMessage();
                msg1.arg1 = appSize;
                msg1.what = ANTIVIRUS_STATUS_START;
                handler.sendMessage(msg1);//发送一个开始的Message
                //--------------------------------

                int timeCount = 1;
                for (PackageInfo installedPackage : installedPackages) {

                    if (stopThread) {
                        return;//如果线程终止标示true就退出
                    }

                    String appName = installedPackage.applicationInfo.loadLabel(packageManager).toString();//应用名称
                    String sourceDir = installedPackage.applicationInfo.sourceDir;//资源Dir
                    String packageName = installedPackage.packageName;

                    String fileMd5;
                    fileMd5 = EncodingUtils.getFileMd5(sourceDir);
                    //System.out.println(fileMd5);


                        /*
                        System.out.println("appName:" + appName);
                        System.out.println("fileMd5:" + fileMd5);
                        System.out.println("-------------------------------------------------------------");
                        */

                    String[] antivirusInfo = AntivirusDao.checkFileAntivirus(fileMd5);//拿到病毒库的检测信息

                    AntivirusInfos antivirusInfos = new AntivirusInfos();
                    //发送一个查杀进度的消息
                    //--------------------------------
                    Message msg2 = handler.obtainMessage();

                    antivirusInfos.currentTimeCount = timeCount;
                    antivirusInfos.packageName = packageName;
                    antivirusInfos.appName = appName;
                    antivirusInfos.antivirusInfo = antivirusInfo;
                    msg2.what = ANTIVIRUS_STATUS_ONANTIVIRUS;
                    msg2.obj = antivirusInfos;

                    handler.sendMessage(msg2);
                    //--------------------------------

                    timeCount++;//计数器加
                }

                //发送一个查杀结束的消息
                //--------------------------------
                Message msg3 = handler.obtainMessage();
                msg3.arg1 = timeCount;
                msg3.what = ANTIVIRUS_STATUS_STOP;
                handler.sendMessage(msg3);
                //--------------------------------
            }
        };
        t.start();
    }

    /**
     * 更新病毒数据库
     *
     * @param v
     */
    public void updateAntivirusDB(View v) {

        //连接服务器获取病毒更新信息
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpMethod.POST, NetConfig.antivirusUpdateUrl, new RequestCallBack<Object>() {
            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                String responseText = (String) responseInfo.result;

                /**
                 * 手动解析Json
                 */
                /*
                JSONObject jsonObj;
                String md5 = null;
                String type = null;
                String name = null;
                String desc = null;
                try {
                    jsonObj = new JSONObject(responseText);
                    md5 = jsonObj.getString("md5");
                    type = jsonObj.getString("type");
                    name = jsonObj.getString("name");
                    desc = jsonObj.getString("desc");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //System.out.println("md5:" + md5 + "type:" + type + "name:" + name + "desc:" + desc);
                */

                /**
                 * 使用Google的Json解析Lib需要定义好JavaBean
                 */
                Gson gson = new Gson();//google的Json解析lib

                /**
                 *  这种的Json解析只能解析单条Json数据
                 *  like : {"md5":"5c7da93de3dff56be16e275176ddea69","type":"6","name":"Android.sss.Aggdtg.a","desc":"恶意后台扣费,病毒木马程序"}
                 */
                //AntivirusInfo antivirusInfo = gson.fromJson(responseText, AntivirusInfo.class);
                //System.out.println(antivirusInfo);

                /**
                 * 这种可以解析多条Json数据
                 * like :
                 *      [ {....},{....},{....},{....} ]
                 */
                List<AntivirusInfo> antivirusInfos = gson.fromJson(responseText, new TypeToken<List<AntivirusInfo>>() {
                }.getType());
                //System.out.println(AntivirusInfos);

                //迭代取出数据
                for (AntivirusInfo antivirusInfo : antivirusInfos) {

                    String md5 = antivirusInfo.md5;
                    String type = antivirusInfo.type;
                    String name = antivirusInfo.name;
                    String desc = antivirusInfo.desc;

                    boolean updateRes = AntivirusDao.addAntivirus(md5, type, name, desc);//增加病毒数据库

                    if (updateRes) {
                        UiUtils.showToast(AntivirusActivity.this, "病毒数据库成功!");
                    } else {
                        UiUtils.showToast(AntivirusActivity.this, "病毒数据库更新失败!");
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UiUtils.showToast(AntivirusActivity.this, "网络问题,病毒数据库更新失败!");
            }
        });
    }

    /**
     * 雷达旋转动画
     */
    public void startAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        rotateAnimation.setDuration(800);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());//让动画不卡顿
        ivAntivirusRadar.startAnimation(rotateAnimation);
    }

    /**
     * 存储查杀过程中的查杀结果
     */
    public class AntivirusInfos {

        public String appName;
        public String packageName;
        public int currentTimeCount;
        public String[] antivirusInfo;
    }
}
