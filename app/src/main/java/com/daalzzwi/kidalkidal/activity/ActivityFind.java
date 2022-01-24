package com.daalzzwi.kidalkidal.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.daalzzwi.kidalkidal.databinding.ActivityFindBinding;
import com.daalzzwi.kidalkidal.model.ModelIntent;

public class ActivityFind extends AppCompatActivity {

    private ActivityFindBinding bindingFind;

    private Intent intent;
    private ModelIntent intentSend;
    private ModelIntent intentReceive;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingFind = ActivityFindBinding.inflate( getLayoutInflater() );
        View view = bindingFind.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityFind" );
        intentReceive = (ModelIntent) getIntent().getSerializableExtra( "data" );

        functionActivityReceive();
    }

    @Override
    protected void onStart() {

        super.onStart();

        bindingFind.btnFindEmail.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                intentSend.setIntentToActivity( "activityFindEmail" );
                intentSend.setIntentData1( null );
                functionActivitySend();
            }
        } );

        bindingFind.btnFindPassword.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                intentSend.setIntentToActivity( "activityFindPassword" );
                intentSend.setIntentData1( null );
                functionActivitySend();
            }
        } );

        bindingFind.btnLogin.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                intentSend.setIntentToActivity( "activityLogin" );
                intentSend.setIntentData1( null );
                functionActivitySend();
            }
        } );
    }

    @Override
    public void onBackPressed() {

        intentSend.setIntentToActivity( "activityLogin" );
        intentSend.setIntentData1( null );
        functionActivitySend();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        bindingFind = null;

        intent = null;
        intentSend = null;
        intentReceive = null;
    }

    public void functionShowMessage( String message ) {

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityFind]" , message );
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityFind" : {

                functionShowLog( "move from activityFind" );
                break;
            }

            case "activityLogin" : {

                functionShowLog( "move from activityLogin" );
                break;
            }

            case "activityFindEmail" : {

                functionShowLog( "move from activityFindEmail" );
                break;
            }

            case "activityFindPassword" : {

                functionShowLog( "move from activityFindPassword" );
                break;
            }

            default : {

                break;
            }
        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityFind" : {

                intent = new Intent( this , ActivityFind.class );
                intent.putExtra( "data" , intentSend);
                break;
            }

            case "activityLogin" : {

                intent = new Intent( this , ActivityLogin.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityFindEmail" : {

                intent = new Intent( this , ActivityFindEmail.class );
                intent.putExtra( "data" , intentSend);
                break;
            }

            case "activityFindPassword" : {

                intent = new Intent( this , ActivityFindPassword.class );
                intent.putExtra( "data" , intentSend );
                break;
            }
        }

        startActivity( intent );
        finish();
    }
}