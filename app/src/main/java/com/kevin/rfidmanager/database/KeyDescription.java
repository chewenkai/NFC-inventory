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
public class KeyDescription {
    @Unique
    @Id(autoincrement = true)
    private Long id;

    private String rfid;  // id of card and items

    private String keyDescription;  // key description of the item

    @Generated(hash = 1670657695)
    public KeyDescription(Long id, String rfid, String keyDescription) {
        this.id = id;
        this.rfid = rfid;
        this.keyDescription = keyDescription;
    }

    @Generated(hash = 625617026)
    public KeyDescription() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRfid() {
        return this.rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getKeyDescription() {
        return this.keyDescription;
    }

    public void setKeyDescription(String keyDescription) {
        this.keyDescription = keyDescription;
    }

}
