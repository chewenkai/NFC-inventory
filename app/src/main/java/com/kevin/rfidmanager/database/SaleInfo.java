package com.kevin.rfidmanager.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by kevin on 17-4-29.
 * Mail: chewenkaich@gmail.com
 */
@Entity
public class SaleInfo {
    @Unique
    @Id(autoincrement = true)
    private Long id;

    private String userName;  // user name

    private String rfid;  // id of card and items

    private String itemName;  // the name of item

    private float price;  // item price

    private String mainImagePath;  // thumb of item

    private String detailDescription;  // description of item

    private Long saleTime;  // time when item sale.

    private int saleVolume;  // sale volume

    @Generated(hash = 1273812454)
    public SaleInfo(Long id, String userName, String rfid, String itemName,
                    float price, String mainImagePath, String detailDescription,
                    Long saleTime, int saleVolume) {
        this.id = id;
        this.userName = userName;
        this.rfid = rfid;
        this.itemName = itemName;
        this.price = price;
        this.mainImagePath = mainImagePath;
        this.detailDescription = detailDescription;
        this.saleTime = saleTime;
        this.saleVolume = saleVolume;
    }

    @Generated(hash = 160017555)
    public SaleInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRfid() {
        return this.rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getMainImagePath() {
        return this.mainImagePath;
    }

    public void setMainImagePath(String mainImagePath) {
        this.mainImagePath = mainImagePath;
    }

    public String getDetailDescription() {
        return this.detailDescription;
    }

    public void setDetailDescription(String detailDescription) {
        this.detailDescription = detailDescription;
    }

    public Long getSaleTime() {
        return this.saleTime;
    }

    public void setSaleTime(Long saleTime) {
        this.saleTime = saleTime;
    }

    public int getSaleVolume() {
        return this.saleVolume;
    }

    public void setSaleVolume(int saleVolume) {
        this.saleVolume = saleVolume;
    }


}
