package com.daalzzwi.kidalkidal.config;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import com.daalzzwi.kidalkidal.database.DatabaseRoom;
import com.daalzzwi.kidalkidal.model.ModelToggle;
import com.daalzzwi.kidalkidal.model.ModelUser;

public class ConfigRoomDatabase {

    private static DatabaseRoom databaseRoom;

    private Context databaseContext;
    private static ConfigRoomDatabase databaseInstance;

    public ConfigRoomDatabase(Context context) {

        this.databaseContext = context;
        databaseRoom = Room.databaseBuilder( context , DatabaseRoom.class , "databaseRoom" ).allowMainThreadQueries().build();
    }

    public static synchronized ConfigRoomDatabase getInstance(Context context ) {

        if( databaseInstance == null ) {

            databaseInstance = new ConfigRoomDatabase( context );
        }

        return databaseInstance;
    }

    public DatabaseRoom getDatabaseRoom() {

        return databaseRoom;
    }

    public void functionRoomInit() {

        functionToggleCheck();
        functionUserCheck();
    }

    public void functionToggleCheck() {

        ModelToggle modelToggle = new ModelToggle();
        modelToggle = functionToggleSelect();

        if( modelToggle != null ) {

            if( modelToggle.getToggleValue() >= 2 ) {

                functionToggleDelete();
                functionToggleInsert(new ModelToggle());
            }
        } else {

            functionToggleInsert( new ModelToggle() );
        }
    }

    public void functionUserCheck() {

        ModelUser modelUser = new ModelUser();
        modelUser = functionUserSelect();

        if( modelUser != null ) {

        } else {

            functionUserInsert( new ModelUser() );
        }
    }

    public void functionToggleInsert( ModelToggle modelToggle ) {

        new AsyncTask< Void , Void , Void >() {
            @Override
            protected Void doInBackground(Void... voids) {

                ConfigRoomDatabase.getInstance( databaseContext )
                        .getDatabaseRoom()
                        .daoToggle()
                        .daoInsertToggle( modelToggle );

                return null;
            }
        }.execute();
    }

    public ModelToggle functionToggleSelect() {

        ModelToggle modelToggle = new ModelToggle();

        modelToggle = ConfigRoomDatabase.getInstance( databaseContext )
                .getDatabaseRoom()
                .daoToggle()
                .daoSelectToggle();

        return modelToggle;
    }

    public void functionToggleUpdate( ModelToggle modelToggle ) {

        new AsyncTask< Void, Void, Void >() {
            @Override
            protected Void doInBackground(Void... voids) {

                ConfigRoomDatabase.getInstance( databaseContext )
                        .getDatabaseRoom()
                        .daoToggle()
                        .daoUpdateToggle( modelToggle.getToggleValue() );

                return null;
            }
        }.execute();
    }

    public void functionToggleDelete() {

        new AsyncTask< Void, Void, Void >() {
            @Override
            protected Void doInBackground(Void... voids) {

                ConfigRoomDatabase.getInstance( databaseContext )
                        .getDatabaseRoom()
                        .daoToggle()
                        .daoDeleteToggle();

                return null;
            }
        }.execute();
    }

    public void functionUserInsert( ModelUser modelUser ) {

        new AsyncTask< Void, Void, Void >() {
            @Override
            protected Void doInBackground(Void... voids) {

                ConfigRoomDatabase.getInstance( databaseContext )
                        .getDatabaseRoom()
                        .daoUser()
                        .daoInsertUser( modelUser );

                return null;
            }
        }.execute();
    }

    public ModelUser functionUserSelect() {

        ModelUser modelUser = new ModelUser();

        modelUser = ConfigRoomDatabase.getInstance( databaseContext )
                .getDatabaseRoom()
                .daoUser()
                .daoSelectUser();

        return modelUser;
    }

    public void functionUserUpdate( ModelUser modelUser ) {

        new AsyncTask< Void, Void, Void >() {
            @Override
            protected Void doInBackground(Void... voids) {

                ConfigRoomDatabase.getInstance( databaseContext )
                        .getDatabaseRoom()
                        .daoUser()
                        .daoUpdateUser( modelUser.getUserPk() , modelUser.getUserId() , modelUser.getUserPassword() ,
                                modelUser.getUserName() , modelUser.getUserEmail() , modelUser.getUserRegisterDate() , modelUser.getUserStatus() , modelUser.getUserImage() );

                return null;
            }
        }.execute();
    }

    public void functionUserDelete() {

        new AsyncTask< Void, Void, Void >() {
            @Override
            protected Void doInBackground(Void... voids) {

                ConfigRoomDatabase.getInstance( databaseContext )
                        .getDatabaseRoom()
                        .daoUser()
                        .daoDeleteUser();

                return null;
            }
        }.execute();
    }
}
