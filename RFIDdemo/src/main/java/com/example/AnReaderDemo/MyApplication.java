package com.example.AnReaderDemo;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        formUri = "http://www.backendofyourchoice.com/reportpath"
)
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        ACRA.init(this);
        LocalReportSender yourSender = new LocalReportSender(getApplicationContext());
        ACRA.getErrorReporter().setReportSender(yourSender);
        super.onCreate();
    }

}