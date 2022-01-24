package com.daalzzwi.kidalkidal.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daalzzwi.kidalkidal.R;
import com.daalzzwi.kidalkidal.config.ConfigRoomDatabase;
import com.daalzzwi.kidalkidal.databinding.ActivitySplashBinding;
import com.daalzzwi.kidalkidal.model.ModelIntent;

public class ActivitySplash extends AppCompatActivity {

    private ActivitySplashBinding bindingSplash;

    private Intent intent;
    private ModelIntent intentSend;

    private ConfigRoomDatabase configRoomDatabase;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_splash );
        bindingSplash = ActivitySplashBinding.inflate( getLayoutInflater() );
        View view = bindingSplash.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentSend.setIntentFromActivity( "activitySplash" );

        configRoomDatabase = new ConfigRoomDatabase( this );

        configRoomDatabase.functionRoomInit();
    }

    @Override
    protected void onStart() {

        super.onStart();

        intentSend.setIntentToActivity( "activityLogin" );
        functionActivitySend();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        bindingSplash = null;

        intent = null;
        intentSend = null;
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityLogin" : {

                intent = new Intent( this, ActivityLogin.class );
                intent.putExtra( "data" , intentSend );
                break;
            }
        }

        startActivity( intent );
        finish();
    }
}