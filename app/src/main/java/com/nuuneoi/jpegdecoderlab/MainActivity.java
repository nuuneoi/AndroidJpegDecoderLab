package com.nuuneoi.jpegdecoderlab;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ImageView ivImage;
    private TextView tvFps;

    private boolean isActive;

    List<Long> fpsTimestamp = new ArrayList<Long>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        tvFps = (TextView) findViewById(R.id.tvFps);

        loadJpegImage();
    }

    @Override
    protected void onResume() {
        super.onResume();

        isActive = true;

        loadJpegImage();
    }

    @Override
    protected void onPause() {
        super.onPause();

        isActive = false;
    }

    private void calculateFps() {
        long currentTimestamp = System.currentTimeMillis();
        int intervalSecond = 3;

        // Prune
        for (int i = fpsTimestamp.size() - 1; i >= 0; i--)
            if (fpsTimestamp.get(i) < currentTimestamp - intervalSecond * 1000)
                fpsTimestamp.remove(i);

        int fps = fpsTimestamp.size() / intervalSecond;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvFps.setText("FPS: " + fps);
            }
        });
    }

    private void loadJpegImage() {
        try {
            InputStream ims = getAssets().open("test.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(ims);
            ivImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e("JpegDecoderLab", e.getMessage());
        }

        fpsTimestamp.add(System.currentTimeMillis());
        calculateFps();

        if (!isActive)
            return;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                loadJpegImage();
            }
        }, 1);
    }
}