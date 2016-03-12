package com.boyzhang.projectmobilesafe.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.config.NetConfig;
import com.boyzhang.projectmobilesafe.utils.StreamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import net.youmi.android.AdManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 闪屏页面
 *
 * @author HaiFeng
 */
public class SplashActivity extends Activity {

    protected static final int CODE_UPDATE_DIALOG = 1;

    protected static final int CODE_URL_ERROR = 2;

    protected static final int CODE_NET_ERROR = 3;

    protected static final int CODE_JSON_ERROR = 4;

    protected static final int CODE_ENTER_HOME = 5;

    protected static final int CODE_INSTALL_CANCLE = 6;// 用户取消了安装app是的状态码

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    // 弹出更新询问对话框
                    askUpdate();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT)
                            .show();
                    enterHome();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT)
                            .show();
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "数据解析出错",
                            Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
            }
        }
    };

    private TextView tvVersion;

    // 这些信息来自服务器
    private int versionCode;
    private String versionDesc;
    private String downloadUrl;

    private ProgressBar bar;

    private SharedPreferences prefConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 获取根节点
        RelativeLayout rlSplashRoot = (RelativeLayout) findViewById(R.id.rl_splashRoot);

        tvVersion = (TextView) findViewById(R.id.tv_version);
        // 设置splash页面的版本信息
        tvVersion.setText("版本:" + getAppVersionInfo()[0].toString());
        bar = (ProgressBar) findViewById(R.id.pb_downloadProgress);

        // 初始化数据
        initDatabases("address.db");//归属地数据库
        initDatabases("antivirus.db");//病毒数据库

        /**
         * 初始化有米广告
         */
        AdManager.getInstance(SplashActivity.this).init("4b0bce58084858e2", "834be8234147e19d", false);

        // 拿到配置文件
        prefConfig = getSharedPreferences("config", MODE_PRIVATE);
        // 根据用户是否开启了自动更新予以不同业务逻辑
        if (prefConfig.getBoolean("auto_update", true)) {
            // 检查更新
            checkUpdate();
        } else {
            // 否则就发送延迟消息,跳转home页面
            handler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
        }

        // 设置splash页面的启动动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        rlSplashRoot.startAnimation(alphaAnimation);
    }

    // 启动此activity的意图
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        // 用户下载更新,但是在安装更新时取消了安装或走此方法,让其返回主界面,否则会卡在splash界面
        if (requestCode == CODE_INSTALL_CANCLE) {
            enterHome();
        }
    }

    /**
     * 获取APP的版本名 和版本号
     *
     * @return
     */
    protected String[] getAppVersionInfo() {

        // 创建包管理器对象
        PackageManager manager = getPackageManager();
        String[] versioninfo = new String[2];
        versioninfo[0] = "X";
        try {
            // 使用getPackageInfo方法获取包信息
            // 参数:
            // getPackageName()获取应用包名
            // flags:标记信息,可以写0
            PackageInfo packageInfo = manager.getPackageInfo(getPackageName(),
                    0);
            versioninfo[0] = (String) packageInfo.versionName;
            versioninfo[1] = String.valueOf(packageInfo.versionCode);
        } catch (NameNotFoundException e) {
            // 获取包信息失败抛此异常
            e.printStackTrace();
        }

        return versioninfo;
    }

    /**
     * 版本更新
     */
    protected void checkUpdate() {
        Thread t = new Thread(new Runnable() {

            // 获取当前时间
            long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                HttpURLConnection conn = null;
                Message msg = handler.obtainMessage();
                try {
                    // 10.0.2.2 这个IP代表了模拟器所在PC的IP
                    // URL url = new
                    // URL("http://10.0.2.2/resource/update.json");
                    URL url = new URL(NetConfig.serverUpdateInfo);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String json = StreamUtils.readToString(inputStream);
                        // System.out.println(json);

                        // 创建Json解析器对象,解析Json对象
                        JSONObject jsonObject = new JSONObject(json);
                        versionCode = jsonObject.getInt("versionCode");
                        versionDesc = jsonObject.getString("versionDesc");
                        downloadUrl = jsonObject.getString("DownloadUrl");
                        // System.out.println(versionCode + "---" + versionDesc
                        // + "---" + downloadUrl);
                        // 判断当前app版本是否需要更新
                        if (Integer.parseInt(getAppVersionInfo()[1]) < versionCode) {
                            // 发送更新Message
                            msg.what = CODE_UPDATE_DIALOG;
                        } else {
                            // 没有版本更新就去主界面
                            msg.what = CODE_ENTER_HOME;
                        }
                    } else {
                        msg.what = CODE_NET_ERROR;
                    }
                } catch (MalformedURLException e) {
                    // URL地址出错走此异常
                    msg.what = CODE_URL_ERROR;

                } catch (IOException e) {
                    // 网络连接出错走此异常
                    msg.what = CODE_NET_ERROR;
                } catch (JSONException e) {
                    // Json数据解析异常走此异常
                    msg.what = CODE_JSON_ERROR;
                } finally {

                    // 在采取下一步之前,予以2秒的缓冲时间,否则有可能splash页面一闪而过
                    long endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 2000) {
                        try {
                            Thread.sleep(2000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    // 发送消息
                    handler.sendMessage(msg);
                    if (conn != null) {
                        // 关闭网络连接
                        conn.disconnect();
                    }
                }
            }
        });
        t.start();
    }

    /**
     * 询问是否更新
     */
    protected void askUpdate() {
        AlertDialog.Builder builder = new Builder(this);
        // 设置强制用户点击返回无效,解决业务逻辑问题
        // builder.setCancelable(false);//这样用户体验不好,选择了采用返回键监听
        builder.setTitle("版本更新");
        builder.setMessage(versionDesc);
        builder.setPositiveButton("立即更新", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 现在更新
                updateNow();
            }
        });
        builder.setNegativeButton("以后更新", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        // 设置返回键监听,解决业务逻辑问题
        builder.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // 点击返回也能去主界面
                enterHome();
            }
        });
        // 显示弹窗
        builder.show();
    }

    /**
     * 下载更新
     */
    protected void updateNow() {
        // 检测SD卡状态
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(SplashActivity.this, "未发现SDK 放弃了下载更新",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 开始下载后将进度条可见
        bar.setVisibility(View.VISIBLE);
        // 使用XUtils下载文件更新
        HttpUtils httpUtils = new HttpUtils();
        String storePath = Environment.getExternalStorageDirectory()
                + "/update.apk";
        httpUtils.download(downloadUrl, storePath, new RequestCallBack<File>() {

            // 文件下载进度
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                // 设置进度条
                bar.setMax((int) total);
                bar.setProgress((int) current);
            }

            // 下载成功
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                // 隐藏进度条
                // bar.setVisibility(View.GONE);
                // 下载成功后安装apk
                // 安装方法在系统源码中的packages\apps\PackageInstaller程序中
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                // Uri.fromFile(arg0.result)是设置下载后的apk路径
                intent.setDataAndType(Uri.fromFile(arg0.result),
                        "application/vnd.android.package-archive");
                // 如果用户取消安装会返回结果
                startActivityForResult(intent, CODE_INSTALL_CANCLE);
            }

            // 下载失败
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Toast.makeText(SplashActivity.this, "抱歉下载更新失败",
                        Toast.LENGTH_SHORT).show();
                // 隐藏进度条
                bar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 跳转到主界面
     */
    protected void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        // 把闪屏页面销毁
        finish();
    }

    /**
     * 初始化数据-拷贝assets文件夹中数据库到data/data/包名/databases文件夹中
     */
    protected void initDatabases(String dbName) {
        File destFile = new File(getFilesDir(), dbName);
        // 判断文件是否已经存在,存在就不再复制
        if (destFile.exists()) {
            return;
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getAssets().open(dbName);
            out = new FileOutputStream(destFile);
            int readLength = 0;
            byte[] buffer = new byte[1024];
            while ((readLength = in.read(buffer)) != -1) {
                out.write(buffer, 0, readLength);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
