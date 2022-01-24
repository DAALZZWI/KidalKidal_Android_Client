package com.daalzzwi.kidalkidal.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.daalzzwi.kidalkidal.model.ModelToggle;

@Dao
public interface DaoToggle {

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    void daoInsertToggle( ModelToggle modelToggle );

    @Query( "UPDATE ModelToggle SET toggleValue = :toggleValue" )
    void daoUpdateToggle( int toggleValue );

    @Query( "DELETE FROM ModelToggle" )
    void daoDeleteToggle();

    @Query( "SELECT * FROM ModelToggle" )
    ModelToggle daoSelectToggle();
}
