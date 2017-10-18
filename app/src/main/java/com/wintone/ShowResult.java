package com.wintone;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by wanglu on 2017/2/7.
 */

public class ShowResult extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView result = (TextView) findViewById(R.id.number);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        int[] picR = getIntent().getIntArrayExtra("PicR");
        char[] StringR = getIntent().getCharArrayExtra("StringR");
        result.setText(String.valueOf(StringR));
        Bitmap bitmap = Bitmap.createBitmap(picR, 400, 80, Bitmap.Config.ARGB_8888);
        imageView.setImageBitmap(bitmap);
    }
}
