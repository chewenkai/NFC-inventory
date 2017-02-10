package com.example.AnReaderDemo;

public class ScanReport {
    private String dataStr;
    private long findCnt = 0;

    public ScanReport() {
        super();
    }

    public ScanReport(String data) {
        super();
        this.setDataStr(data);
        this.setFindCnt(1);
    }

    public long getFindCnt() {
        return findCnt;
    }

    public void setFindCnt(long findCnt) {
        this.findCnt = findCnt;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }
}
