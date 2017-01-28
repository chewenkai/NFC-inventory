package com.kevin.demo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.acra.ACRAConstants;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 2017/1/28.
 */

public class LocalReportSender implements ReportSender {

    private final Map<ReportField, String> mMapping = new HashMap<ReportField, String>();
    private FileWriter crashReport = null;

    public LocalReportSender(Context ctx) {


    }

    private boolean isNull(String aString) {
        return aString == null || ACRAConstants.NULL_VALUE.equals(aString);
    }


    @Override
    public void send(Context context, CrashReportData errorContent) throws ReportSenderException {
        File logFile = new File(Environment.getExternalStorageDirectory(), "RFID_log.txt");

        try {
            //create the file
            logFile.createNewFile();

//text you want to write to your file
            String text = errorContent.toString();

//check if file exists
            if (logFile.exists()) {
                OutputStream fo = new FileOutputStream(logFile);

                //write the data
                fo.write(Byte.parseByte(text));

                //close to avoid memory leaks
                fo.close();
            }
        } catch (IOException e) {
            Log.e("TAG", "IO ERROR", e);
        }
    }

}