package com.example.AnReaderDemo;

//Download by http://www.codefans.net
public class InventoryReport {
    private String uidStr;
    private String TagTypeStr;
    private long findCnt = 0;

    public InventoryReport() {
        super();
    }

    public InventoryReport(String uid, String tayType) {
        super();
        this.setUidStr(uid);
        this.setTagTypeStr(tayType);
        this.setFindCnt(1);
    }

    public String getUidStr() {
        return uidStr;
    }

    public void setUidStr(String uidStr) {
        this.uidStr = uidStr;
    }

    public String getTagTypeStr() {
        return TagTypeStr;
    }

    public void setTagTypeStr(String tagTypeStr) {
        TagTypeStr = tagTypeStr;
    }

    public long getFindCnt() {
        return findCnt;
    }

    public void setFindCnt(long findCnt) {
        this.findCnt = findCnt;
    }
}