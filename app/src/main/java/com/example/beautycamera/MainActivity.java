package com.example.beautycamera;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beautycamera.databinding.ActivityMainBinding;
import com.example.beautycamera.view.MyRecordButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 100;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO)
                        != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.CAMERA,
                            Manifest.permission.READ_MEDIA_VIDEO},
                    PERMISSION_CODE);

        } else {
            initView();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                initView();
            } else {
                Toast.makeText(this, "需要相关的权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView() {
        setContentView(binding.getRoot());
        MyGLSurfaceView mGLSurfaceView = findViewById(R.id.glSurfaceView);
        ((MyRecordButton) findViewById(R.id.btn_record)).setOnRecordListener(
                new MyRecordButton.OnRecordListener() {
                    @Override
                    public void onStartRecording() {
                        mGLSurfaceView.startRecording();
                    }

                    @Override
                    public void onStopRecording() {
                        mGLSurfaceView.stopRecording();
                        Toast.makeText(MainActivity.this, "录制完成！", Toast.LENGTH_SHORT).show();
                    }
                });

        ((RadioGroup) findViewById(R.id.group_record_speed)).setOnCheckedChangeListener(
                (group, checkedId) -> {
                    if (checkedId == R.id.rbtn_record_speed_extra_slow) {
                        mGLSurfaceView.setSpeed(MyGLSurfaceView.Speed.MODE_EXTRA_SLOW);
                    } else if (checkedId == R.id.rbtn_record_speed_slow) {
                        mGLSurfaceView.setSpeed(MyGLSurfaceView.Speed.MODE_SLOW);
                    } else if (checkedId == R.id.rbtn_record_speed_normal) {
                        mGLSurfaceView.setSpeed(MyGLSurfaceView.Speed.MODE_NORMAL);
                    } else if (checkedId == R.id.rbtn_record_speed_fast) {
                        mGLSurfaceView.setSpeed(MyGLSurfaceView.Speed.MODE_FAST);
                    } else if (checkedId == R.id.rbtn_record_speed_extra_fast) {
                        mGLSurfaceView.setSpeed(MyGLSurfaceView.Speed.MODE_EXTRA_FAST);
                    }
                });

        ((CheckBox) findViewById(R.id.chk_bigeye)).setOnCheckedChangeListener(
                (buttonView, isChecked) -> mGLSurfaceView.enableBigEye(isChecked));
    }

}