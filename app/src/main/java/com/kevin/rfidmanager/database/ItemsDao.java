package com.kevin.rfidmanager.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ITEMS".
*/
public class ItemsDao extends AbstractDao<Items, Long> {

    public static final String TABLENAME = "ITEMS";

    /**
     * Properties of entity Items.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserName = new Property(1, String.class, "userName", false, "USER_NAME");
        public final static Property Rfid = new Property(2, String.class, "rfid", false, "RFID");
        public final static Property ItemName = new Property(3, String.class, "itemName", false, "ITEM_NAME");
        public final static Property Price = new Property(4, float.class, "price", false, "PRICE");
        public final static Property MainImagePath = new Property(5, String.class, "mainImagePath", false, "MAIN_IMAGE_PATH");
        public final static Property DetailDescription = new Property(6, String.class, "detailDescription", false, "DETAIL_DESCRIPTION");
    }


    public ItemsDao(DaoConfig config) {
        super(config);
    }
    
    public ItemsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ITEMS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: id
                "\"USER_NAME\" TEXT," + // 1: userName
                "\"RFID\" TEXT," + // 2: rfid
                "\"ITEM_NAME\" TEXT," + // 3: itemName
                "\"PRICE\" REAL NOT NULL ," + // 4: price
                "\"MAIN_IMAGE_PATH\" TEXT," + // 5: mainImagePath
                "\"DETAIL_DESCRIPTION\" TEXT);"); // 6: detailDescription
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ITEMS\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Items entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(2, userName);
        }
 
        String rfid = entity.getRfid();
        if (rfid != null) {
            stmt.bindString(3, rfid);
        }
 
        String itemName = entity.getItemName();
        if (itemName != null) {
            stmt.bindString(4, itemName);
        }
        stmt.bindDouble(5, entity.getPrice());
 
        String mainImagePath = entity.getMainImagePath();
        if (mainImagePath != null) {
            stmt.bindString(6, mainImagePath);
        }
 
        String detailDescription = entity.getDetailDescription();
        if (detailDescription != null) {
            stmt.bindString(7, detailDescription);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Items entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(2, userName);
        }
 
        String rfid = entity.getRfid();
        if (rfid != null) {
            stmt.bindString(3, rfid);
        }
 
        String itemName = entity.getItemName();
        if (itemName != null) {
            stmt.bindString(4, itemName);
        }
        stmt.bindDouble(5, entity.getPrice());
 
        String mainImagePath = entity.getMainImagePath();
        if (mainImagePath != null) {
            stmt.bindString(6, mainImagePath);
        }
 
        String detailDescription = entity.getDetailDescription();
        if (detailDescription != null) {
            stmt.bindString(7, detailDescription);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Items readEntity(Cursor cursor, int offset) {
        Items entity = new Items( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // rfid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // itemName
            cursor.getFloat(offset + 4), // price
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // mainImagePath
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // detailDescription
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Items entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setRfid(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setItemName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPrice(cursor.getFloat(offset + 4));
        entity.setMainImagePath(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDetailDescription(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Items entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Items entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Items entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
