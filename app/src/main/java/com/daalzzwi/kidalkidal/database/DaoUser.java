package com.daalzzwi.kidalkidal.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.daalzzwi.kidalkidal.model.ModelUser;

@Dao
public interface DaoUser {

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    void daoInsertUser( ModelUser modelUser );

    @Query("UPDATE ModelUser SET userPk = :userPk , userId = :userId , userPassword = :userPassword , userName = :userName , userEmail = :userEmail , userRegisterDate = :userRegisterDate , userStatus = :userStatus , userImage = :userImage" )
    void daoUpdateUser( int userPk , int userId , String userPassword ,
                        String userName , String userEmail , String userRegisterDate , String userStatus , String userImage );

    @Query( "DELETE FROM ModelUser" )
    void daoDeleteUser();

    @Query( "SELECT * FROM ModelUser" )
    ModelUser daoSelectUser();
}
