package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.utils.SmsUtils;
import com.boyzhang.projectmobilesafe.utils.UiUtils;

/**
 * 短信备份Activity
 */
public class BackupSMS extends Activity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_sms);
    }

    /**
     * 备份短信
     */
    public void backUp(View v) {

        /**
         * 进度显示
         */
        progressDialog = new ProgressDialog(BackupSMS.this);
        progressDialog.setTitle("备份进度");
        progressDialog.setMessage("备份中,稍安勿躁....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置样式
        progressDialog.show();

        //备份短信可能需要较长时间,所以在子线程中进行
        new Thread() {
            @Override
            public void run() {
                super.run();

                boolean backUpSmsRes = SmsUtils.backUpSms(BackupSMS.this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmsBackUp.xml", "utf-8",
                        new SmsUtils.backUpSmsCallback() {
                            //回调

                            @Override
                            public void beforeBackUp(int count) {
                                //设置中长度
                                progressDialog.setMax(count);
                            }

                            @Override
                            public void onBackUp(int progress) {
                                //设置进度
                                progressDialog.setProgress(progress);
                            }
                        });

                if (backUpSmsRes) {

                    /*
                    //------------------------------------自己取出消息队列中的消息,实现在子线程中刷新UI!!!!!!!!!!^^^^^^^^&&&&&&&&
                    Looper.prepare();
                    Toast.makeText(BackupSMS.this, "成功了！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    //------------------------------------
                    */

                    //备份成功
                    UiUtils.showToast(BackupSMS.this, "恭喜备份成功!");
                } else {
                    UiUtils.showToast(BackupSMS.this, "备份失败喽!");
                }
                //销毁弹框
                progressDialog.dismiss();
            }
        }.start();

    }

    /**
     * 恢复短信
     */
    public void recovery(View v) {

    }

}
