package com.daalzzwi.kidalkidal.activity;

import android.content.Intent;
import android.os.Bundle;

import com.daalzzwi.kidalkidal.databinding.ActivityQrScannerBinding;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ActivityQrScanner extends AppCompatActivity {

    private ActivityQrScannerBinding bindingQrScanner;

    private Intent intent;
    private ModelIntent intentSend;
    private ModelIntent intentReceive;

    private ModelUser modelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );

        bindingQrScanner = ActivityQrScannerBinding.inflate( getLayoutInflater() );
        View view = bindingQrScanner.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityQrScanner" );
        intentReceive = (ModelIntent) getIntent().getSerializableExtra( "data" );

        modelUser = new ModelUser();

        functionActivityReceive();
    }

    @Override
    protected void onStart() {

        super.onStart();

        IntentIntegrator intentIntegrator = new IntentIntegrator( ActivityQrScanner.this );
        intentIntegrator.setBeepEnabled( true );
        intentIntegrator.setTimeout(15000);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {

        IntentResult result = IntentIntegrator.parseActivityResult( requestCode , resultCode , data );

        if( result != null ) {

            if( result.getContents() == null ) {

                functionShowMessage( "QR코드 스캔이 안 됬어요" );
                onBackPressed();
            } else {

                String code = result.getContents();
                if( code.contains( "kidalkidal" )  == true ) {

                    intentSend.setIntentToActivity( "activityOrder" );
                    intentSend.setIntentData1( modelUser );
                    intentSend.setIntentData2( code );
                    functionActivitySend();
                } else {

                    functionShowMessage( "기달기달과 협력한 업체가 아니에요" );
                    onBackPressed();
                }
            }
        } else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {

        intentSend.setIntentToActivity( "activityMain" );
        intentSend.setIntentData1( modelUser );
        functionActivitySend();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        bindingQrScanner = null;

        intent = null;
        intentSend = null;
        intentReceive = null;

        modelUser = null;
    }

    public void functionShowMessage( String message ) {

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityQrScanner]" , message );
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityQrScanner" : {

                functionShowLog( "move from activityQrScanner" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                break;
            }

            case "activityOrder" : {

                functionShowLog( "move from activityOrder" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                break;
            }

            case "activityMain" : {

                functionShowLog( "move from activityMain" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                break;
            }

            default : {

                modelUser = (ModelUser) intentReceive.getIntentData1();
                break;
            }
        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityQrScanner" : {

                intent = new Intent( this, ActivityQrScanner.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityOrder" : {

                intent = new Intent( this, ActivityOrder.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityMain" : {

                intent = new Intent( this, ActivityMain.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

        }

        startActivity( intent );
        finish();
    }
}