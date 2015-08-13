package com.rusdelphi.zipexample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {

    private TextView mTVLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTVLog = (TextView) findViewById(R.id.tv_log);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onUnzipZip(View v) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm:ss.SSS");
        String currentDateandTime = sdf.format(new Date());
        String log = mTVLog.getText().toString() + "\nStart unzip zip" + currentDateandTime;
        mTVLog.setText(log);
        InputStream is = getAssets().open("test_data.zip");
        File db_path = getDatabasePath("zip.db");
        if (!db_path.exists())
            db_path.getParentFile().mkdirs();
        OutputStream os = new FileOutputStream(db_path);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = zis.read(buffer)) > -1) {
                os.write(buffer, 0, count);
            }
            os.close();
            zis.closeEntry();
        }
        zis.close();
        is.close();
        currentDateandTime = sdf.format(new Date());
        log = mTVLog.getText().toString() + "\nEnd unzip zip" + currentDateandTime;
        mTVLog.setText(log);

    }

    public void onUnzip7Zip(View v) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm:ss.SSS");
        String currentDateandTime = sdf.format(new Date());

        String log = mTVLog.getText().toString() + "\nStart unzip 7zip" + currentDateandTime;
        mTVLog.setText(log);

        File db_path = getDatabasePath("7zip.db");
        if (!db_path.exists())
            db_path.getParentFile().mkdirs();

        SevenZFile sevenZFile = new SevenZFile(getAssetFile(this, "test_data.7z", "tmp"));
        SevenZArchiveEntry entry = sevenZFile.getNextEntry();
        OutputStream os = new FileOutputStream(db_path);
        while (entry != null) {
            byte[] buffer = new byte[8192];//
            int count;
            while ((count = sevenZFile.read(buffer, 0, buffer.length)) > -1) {

                os.write(buffer, 0, count);
            }
            entry = sevenZFile.getNextEntry();
        }
        sevenZFile.close();
        os.close();
        currentDateandTime = sdf.format(new Date());
        log = mTVLog.getText().toString() + "\nEnd unzip 7zip" + currentDateandTime;
        mTVLog.setText(log);

    }

    public static File getAssetFile(Context context, String asset_name, String name)
            throws IOException {
        File cacheFile = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getAssets().open(asset_name);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new IOException("Could not open file" + asset_name, e);
        }
        return cacheFile;
    }


}
