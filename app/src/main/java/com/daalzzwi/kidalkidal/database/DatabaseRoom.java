package com.daalzzwi.kidalkidal.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.daalzzwi.kidalkidal.model.ModelToggle;
import com.daalzzwi.kidalkidal.model.ModelUser;

@Database( entities = { ModelUser.class , ModelToggle.class } , version = 2 )
public abstract class DatabaseRoom extends RoomDatabase {

    public abstract DaoUser daoUser();
    public abstract DaoToggle daoToggle();
}
