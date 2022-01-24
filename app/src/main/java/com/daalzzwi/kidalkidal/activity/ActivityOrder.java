package com.daalzzwi.kidalkidal.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.databinding.ActivityOrderBinding;
import com.daalzzwi.kidalkidal.function.FunctionConverter;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelUser;
import com.daalzzwi.kidalkidal.model.ModelVisitorPayload;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;

public class ActivityOrder extends AppCompatActivity {

    private ActivityOrderBinding bindingOrder;

    private Intent intent;
    private ModelIntent intentSend;
    private ModelIntent intentReceive;

    private ConfigRetrofit configRetrofit;
    private ModelUser modelUser;

    private FunctionConverter converter;

    private WebSocketClient webSocketClient;
    private StringBuffer sbf;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );

        bindingOrder = ActivityOrderBinding.inflate( getLayoutInflater() );
        View view = bindingOrder.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityOrder" );
        intentReceive = (ModelIntent) getIntent().getSerializableExtra( "data" );

        configRetrofit = getRetrofit();
        modelUser = new ModelUser();

        converter = new FunctionConverter();

        code = "";

        functionActivityReceive();
        functionCompanyInit();
    }

    @Override
    protected void onStart() {

        super.onStart();

        bindingOrder.btnMain.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                webSocketClient.close();

                intentSend.setIntentToActivity( "activityMain" );
                intentSend.setIntentData1( modelUser );
                functionActivitySend();
            }
        });
    }

    @Override
    public void onBackPressed() {

        webSocketClient.close();

        intentSend.setIntentToActivity( "activityMain" );
        intentSend.setIntentData1( modelUser );
        functionActivitySend();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        bindingOrder = null;

        intent = null;
        intentSend = null;
        intentReceive = null;

        modelUser = null;

        code = null;
    }

    public void functionCompanyInit() {

        configRetrofit.getFunctionRestApi().apiCompanyInit().enqueue( new Callback< Integer >() {

            @Override
            public void onResponse( Call< Integer > call, Response< Integer > response) {

                if( response.body() != null ) {

                    switch ( response.body() ) {
                        case 1 : functionShowLog( "success init" ); break;
                        case 0 : functionShowLog( "fail init" ); break;
                    }
                    functionSocketCreate();
                }
            }

            @Override
            public void onFailure( Call< Integer > call, Throwable t ) {

            }
        });
    }

    public void functionSocketCreate() {

        sbf = new StringBuffer();

        URI uri;
        String url = "";
        String parameters = "";

        parameters = "/" + modelUser.getUserEmail() + "/" + code;
        url = "ws://192.168.35.55:3232/order/customer" + parameters;

        functionShowLog(url);
        try {

            uri = new URI( url );
        } catch( Exception e ) {

            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient( uri ) {

            @Override
            public void onOpen() {

                functionShowLog( "opened" );
                functionOrderImageInit();
            }

            @Override
            public void onTextReceived( String message ) {

                functionShowLog( "message received" );
                functionShowLog( message );
                functionOrderApply( message );
            }

            @Override
            public void onBinaryReceived( byte[] data ) {

            }

            @Override
            public void onPingReceived( byte[] data ) {

            }

            @Override
            public void onPongReceived( byte[] data ) {

            }

            @Override
            public void onException( Exception e ) {

                e.printStackTrace();
            }

            @Override
            public void onCloseReceived() {

            }

            @Override
            public void close() {

                super.close();
                functionShowLog( "closed" );

                intentSend.setIntentToActivity( "activityMain" );
                intentSend.setIntentData1( modelUser );
                functionActivitySend();
            }
        };

        webSocketClient.setConnectTimeout( 10000 );
        webSocketClient.setReadTimeout( 180000 );
        webSocketClient.connect();
    }


    public void functionOrderApply( String message ) {

        String type = message.split( "/" )[0];

        functionShowLog( type );

        switch ( type ) {

            case "JOIN" : {

                String email = message.split( "/" )[1];
                String name = message.split( "/" )[2];
                String number = message.split( "/" )[3];

                new Thread( new Runnable() {

                    @Override
                    public void run() {

                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {

                                bindingOrder.tvOrderNumber.setText( number );
                                bindingOrder.tvCompanyName.setText( name );
                            }
                        });
                    }
                }).start();
                break;
            }

            case "PICK" : {

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(5000);
                new Thread( new Runnable() {

                    @Override
                    public void run() {

                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {

                                bindingOrder.tvExplain.setText( "고객님 순서가 되었어요!" );
                            }
                        });
                    }
                }).start();
                break;
            }
        }


    }

    public void functionOrderImageInit() {

        ModelVisitorPayload payloadOrder = new ModelVisitorPayload();
        Map< String , String > visitorImage = new HashMap< String , String >();

        visitorImage.put( modelUser.getUserEmail() , modelUser.getUserImage() );
        payloadOrder.setVisitorImage( visitorImage );
        payloadOrder.setVisitorRoomType( code );


        configRetrofit.getFunctionRestApi().apiCompanyImageInsert( payloadOrder ).enqueue( new Callback<ModelVisitorPayload>() {

            @Override
            public void onResponse(Call<ModelVisitorPayload> call, Response<ModelVisitorPayload> response ) {

                ModelVisitorPayload payloadResult = response.body();

                if( payloadResult != null ) {

                    functionShowLog( "success register" );

                    webSocketClient.send( "JOIN/" + modelUser.getUserEmail() + "/" + code );
                } else {

                    functionShowLog( "fail register" );
                }
            }

            @Override
            public void onFailure(Call<ModelVisitorPayload> call, Throwable t ) {

                functionShowMessage( "서버와 연결이 안 되었어요" );
                functionShowLog( "not connected to the server" );
            }
        });
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
                code = ( String ) intentReceive.getIntentData2();
                bindingOrder.ivProfile.setImageBitmap( converter.functionStringToBitmap( modelUser.getUserImage() ) );
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