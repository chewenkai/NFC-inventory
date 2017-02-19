package com.kevin.rfidmanager.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by Kevin on 2017/1/25.
 * Mail: chewenkaich@gmail.com
 */

@Entity
public class Items {
    @Unique
    @Id(autoincrement = true)
    private Long id;

    private String userName;  // user name

    private String rfid;  // id of card and items

    private String itemName;  // the name of item

    private float price;  // item price

    private String mainImagePath;  // thumb of item

    private String detailDescription;  // description of item

    @Generated(hash = 371423760)
    public Items(Long id, String userName, String rfid, String itemName,
                 float price, String mainImagePath, String detailDescription) {
        this.id = id;
        this.userName = userName;
        this.rfid = rfid;
        this.itemName = itemName;
        this.price = price;
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


}
