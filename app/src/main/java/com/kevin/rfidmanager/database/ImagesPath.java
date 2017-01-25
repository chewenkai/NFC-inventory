package com.kevin.rfidmanager.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Kevin on 2017/1/25.
 */

@Entity
public class ImagesPath {
    @Id(autoincrement = true)
    private Long id;

    @Index(unique = true)
    private Long rfid;  // id of card and items

    private String imagePath;  // image path of the item

    @Generated(hash = 1713909050)
    public ImagesPath(Long id, Long rfid, String imagePath) {
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

    public Long getRfid() {
        return this.rfid;
    }

    public void setRfid(Long rfid) {
        this.rfid = rfid;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
