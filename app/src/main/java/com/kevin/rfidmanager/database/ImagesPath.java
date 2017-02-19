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
public class ImagesPath {
    @Unique
    @Id(autoincrement = true)
    private Long id;

    private String rfid;  // id of card and items

    private String imagePath;  // image path of the item

    @Generated(hash = 1127292842)
    public ImagesPath(Long id, String rfid, String imagePath) {
        this.id = id;
        this.rfid = rfid;
        this.imagePath = imagePath;
    }

    @Generated(hash = 467095524)
    public ImagesPath() {
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

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
