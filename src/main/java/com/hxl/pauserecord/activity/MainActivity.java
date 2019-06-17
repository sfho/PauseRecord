package com.hxl.pauserecord.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.hxl.pauserecord.R;
import com.hxl.pauserecord.record.AudioRecorder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    Button start;
    Button pause;
    Button pcmList;
    Button wavList;

    AudioRecorder audioRecorder;
    private static final int REQUEST_PERMISSIONS = 1;
    private Runnable doAfterAllPermissionsGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void addListener() {
        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        pcmList.setOnClickListener(this);
        wavList.setOnClickListener(this);
    }

    private void init() {
        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        pcmList = (Button) findViewById(R.id.pcmList);
        wavList = (Button) findViewById(R.id.wavList);
        pause.setVisibility(View.GONE);
        audioRecorder = AudioRecorder.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                try {
                    if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY) {
                        //初始化录音
                        String fileName =
                                new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
                        audioRecorder.createDefaultAudio(fileName);
                        audioRecorder.startRecord(null);

                        start.setText("停止录音");

                        pause.setVisibility(View.VISIBLE);

                    } else {
                        //停止录音
                        audioRecorder.stopRecord();
                        start.setText("开始录音");
                        pause.setText("暂停录音");
                        pause.setVisibility(View.GONE);
                    }

                } catch (IllegalStateException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.pause:
                try {
                    if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START) {
                        //暂停录音
                        audioRecorder.pauseRecord();
                        pause.setText("继续录音");
                        break;

                    } else {
                        audioRecorder.startRecord(null);
                        pause.setText("暂停录音");
                    }
                } catch (IllegalStateException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.pcmList:
                Intent showPcmList = new Intent(MainActivity.this, ListActivity.class);
                showPcmList.putExtra("type", "pcm");
                startActivity(showPcmList);
                break;

            case R.id.wavList:
                Intent showWavList = new Intent(MainActivity.this, ListActivity.class);
                showWavList.putExtra("type", "wav");
                startActivity(showWavList);
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START) {
            audioRecorder.pauseRecord();
            pause.setText("继续录音");
        }

    }

    @Override
    protected void onDestroy() {
        audioRecorder.release();
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (doAfterAllPermissionsGranted != null) {
            doAfterAllPermissionsGranted.run();
            doAfterAllPermissionsGranted = null;
        } else {
            String[] neededPermissions = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            List<String> deniedPermissions = new ArrayList<>();
            for (String permission : neededPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission);
                }
            }
            if (deniedPermissions.isEmpty()) {
                // All permissions are granted
                doAfterAllPermissionsGranted();
            } else {
                String[] array = new String[deniedPermissions.size()];
                array = deniedPermissions.toArray(array);
                ActivityCompat.requestPermissions(this, array, REQUEST_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean permissionsAllGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsAllGranted = false;
                    break;
                }
            }
            if (permissionsAllGranted) {
                doAfterAllPermissionsGranted = new Runnable() {
                    @Override
                    public void run() {
                        doAfterAllPermissionsGranted();
                    }
                };
            } else {
                doAfterAllPermissionsGranted = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, R.string.permissions_denied_exit,
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }

                };
            }
        }
    }

    private void doAfterAllPermissionsGranted() {
                addListener();
    }

}
