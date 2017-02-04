package com.kevin.rfidmanager.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by Kevin on 2017/1/25.
 */

@Entity
public class Items {
    @Unique
    @Id(autoincrement = true)
    private Long id;

    private String userName;  // user name

    private Long rfid;  // id of card and items

    private String itemName;  // the name of item

    private String mainImagePath;  // thumb of item

    private String detailDescription;  // description of item

    @Generated(hash = 168323232)
    public Items(Long id, String userName, Long rfid, String itemName,
            String mainImagePath, String detailDescription) {
        this.id = id;
        this.userName = userName;
        this.rfid = rfid;
        this.itemName = itemName;
        this.mainImagePath = mainImagePath;
        this.detailDescription = detailDescription;
    }

    @Generated(hash = 1040818858)
    public Items() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRfid() {
        return this.rfid;
    }

    public void setRfid(Long rfid) {
        this.rfid = rfid;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}
