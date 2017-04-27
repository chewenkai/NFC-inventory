package com.kevin.rfidmanager.Utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Kevin on 2017/1/26.
 * Mail: chewenkaich@gmail.com
 */

public class BackupDatabaseUtil {

    /**
     * Copy the database file to fixed path
     *
     * @param currentDBPath String currentDBPath = "//data//{package name}//databases//{database name}";
     * @param backupDBPath  String backupDBPath = "{database name}";
     * @return
     */
    public static int copyDBOut(String currentDBPath, String backupDBPath) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }
}
