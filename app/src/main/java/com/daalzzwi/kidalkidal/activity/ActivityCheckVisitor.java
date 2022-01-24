package com.daalzzwi.kidalkidal.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.daalzzwi.kidalkidal.adapter.AdapterChat;
import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.config.ConfigRoomDatabase;
import com.daalzzwi.kidalkidal.databinding.ActivityCheckVisitorBinding;
import com.daalzzwi.kidalkidal.function.FunctionConverter;
import com.daalzzwi.kidalkidal.model.ModelCompany;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelToggle;
import com.daalzzwi.kidalkidal.model.ModelUser;
import com.daalzzwi.kidalkidal.model.ModelVisitorPayload;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;

public class ActivityCheckVisitor extends AppCompatActivity {

    private ActivityCheckVisitorBinding bindingCheckVisitor;

    private Intent intent;
    private ModelIntent intentSend;
    private ModelIntent intentReceive;

    private ConfigRetrofit configRetrofit;
    private ModelUser modelUser;
    private ModelCompany modelCompany;

    private FunctionConverter converter;

    private WebSocketClient webSocketClient;
    private StringBuffer sbf;

    private Map< String , Bitmap> dataImage;
    private Map< Integer , String > dataList;
    private String type;
    int init;

    private int numberPrevious;
    private int numberPresent;
    private int numberNext;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingCheckVisitor = ActivityCheckVisitorBinding.inflate( getLayoutInflater() );
        View view = bindingCheckVisitor.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityCheckVisitor" );
        intentReceive = (ModelIntent) getIntent().getSerializableExtra( "data" );

        configRetrofit = getRetrofit();
        modelUser = new ModelUser();

        converter = new FunctionConverter();

        type = "";
        dataImage = new HashMap< String , Bitmap >();
        init = 0;
        dataList = new HashMap<>();

        functionActivityReceive();
    }

    @Override
    protected void onStart() {

        super.onStart();

        bindingCheckVisitor.btnMain.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                intentSend.setIntentToActivity( "activityMain" );
                intentSend.setIntentData1( modelUser );
                functionActivitySend();
            }
        });

        bindingCheckVisitor.btnPrevious.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                if( numberPrevious == -1 ) {

                    functionShowMessage( "이전 번호가 없어요" );
                    functionShowLog( "impossible" );
                    return;
                }

                numberPresent -= 1;
                numberNext = numberPresent + 1;
                numberPrevious = numberPresent - 1;

                if( dataList.get( numberPrevious ) == null ) {

                    numberPrevious = -1;
                }

                if( dataList.get( numberNext ) == null ) {

                    numberNext = -1;
                }

                functionUIApply( "USERPICK" );
            }
        });

        bindingCheckVisitor.btnNext.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                if( numberNext == -1 ) {

                    functionShowMessage( "다음 번호가 없어요" );
                    functionShowLog( "impossible" );
                    return;
                }

                numberPresent += 1;
                numberNext = numberPresent + 1;
                numberPrevious = numberPresent - 1;

                if( dataList.get( numberPrevious ) == null ) {

                    numberPrevious = -1;
                }

                if( dataList.get( numberNext ) == null ) {

                    numberNext = -1;
                }

                functionUIApply( "USERPICK" );
            }
        } );
    }

    @Override
    public void onBackPressed() {

        webSocketClient.close();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        bindingCheckVisitor = null;

        intent = null;
        intentSend = null;
        intentReceive = null;

        modelUser = null;

        converter = null;

        webSocketClient = null;
        sbf = null;
        type = null;
    }

    public void functionSocketCreate() {

        sbf = new StringBuffer();

        URI uri;
        String url = "";
        String parameters = "";

        parameters = "/" + modelUser.getUserEmail() + "/" + modelCompany.getCompanyId() ;
        url = "ws://192.168.35.55:3232/order/manager" + parameters;

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
                functionVisitorImageInit();
            }

            @Override
            public void onTextReceived( String message ) {

                functionShowLog( "message received" );
                functionShowLog( message );
                functionMessageApply( message );
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

    public void functionMessageApply( String message ) {

        String type = message.split("/")[0];

        switch (type) {

            case "LIST": {

                String[] base = message.split( "LIST/" );
                String[] list = base[1].split( "/" );

                for ( String user : list ) {

                    String userEmail = user.split( "," )[ 0 ];
                    String userNumber = user.split( "," )[ 1 ];

                    dataList.put( Integer.parseInt( userNumber ) , userEmail );
                }

                Object[] mapKey = dataList.keySet().toArray();
                Arrays.sort(mapKey);

                for (int key : dataList.keySet()) {

                    functionShowLog( Integer.toString(key) + "/" + dataList.get(key));
                }

                functionUIApply( "LIST" );
                break;
            }

            case "LISTADD" : {

                String[] base = message.split( "LISTADD/" );
                String[] list = base[1].split( "/" );

                for ( String user : list ) {

                    String userEmail = user.split(",")[0];
                    String userNumber = user.split(",")[1];

                    dataList.put( Integer.parseInt( userNumber ) , userEmail );
                }

                Object[] mapKey = dataList.keySet().toArray();
                Arrays.sort(mapKey);

                for (int key : dataList.keySet()) {

                    functionShowLog( Integer.toString(key) + "/" + dataList.get(key));
                }

                numberPresent = Integer.parseInt( bindingCheckVisitor.tvOrderNumberPresent.getText().toString().trim() );
                if( !( numberPresent + 1 < Integer.parseInt( base[1].split("/")[0].split(",")[1] ) ) ) {
                    functionUIInit();
                }
            }
        }
    }

    private void functionUIInit() {

        new Thread( new Runnable() {

            @Override
            public void run() {

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {

                        bindingCheckVisitor.tvOrderNumberPrevious.setText("-");
                        bindingCheckVisitor.ivProfilePrevious.setImageBitmap(null);
                        bindingCheckVisitor.tvOrderNumberPresent.setText("-");
                        bindingCheckVisitor.ivProfilePresent.setImageBitmap(null);
                        bindingCheckVisitor.tvOrderNumberNext.setText("-");
                        bindingCheckVisitor.ivProfileNext.setImageBitmap(null);
                    }
                });
            }
        }).start();

        functionUIApply( "LISTADD" );
    }

    private void functionUIApply( String type ) {

        new Thread( new Runnable() {

            @Override
            public void run() {

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {

                        if( type.equals( "LISTADD" ) ) {

//                            numberPresent
                            numberNext = numberPresent + 1;
                            numberPrevious = numberPresent - 1;
                        }

                        int keyPrevious = numberPrevious;
                        Bitmap valuePrevious = dataImage.get( dataList.get( keyPrevious ) );

                        int keyPresent = numberPresent;
                        Bitmap valuePresent = dataImage.get( dataList.get( keyPresent ) );

                        int keyNext = numberNext;
                        Bitmap valueNext = dataImage.get( dataList.get( keyNext ) );

                        if ( valuePrevious != null) {

                            bindingCheckVisitor.tvOrderNumberPrevious.setText( Integer.toString( numberPrevious ) );
                            bindingCheckVisitor.ivProfilePrevious.setImageBitmap( dataImage.get( dataList.get( numberPrevious ) ) );
                        } else {

                            bindingCheckVisitor.tvOrderNumberPrevious.setText("-");
                            bindingCheckVisitor.ivProfilePrevious.setImageBitmap(null);
                        }

                        if ( valuePresent != null) {

                            bindingCheckVisitor.tvOrderNumberPresent.setText(Integer.toString(numberPresent));
                            bindingCheckVisitor.ivProfilePresent.setImageBitmap(dataImage.get(dataList.get(numberPresent)));
                        } else {

                            bindingCheckVisitor.tvOrderNumberPresent.setText("-");
                            bindingCheckVisitor.ivProfilePresent.setImageBitmap(null);
                        }

                        if ( valueNext != null) {

                            bindingCheckVisitor.tvOrderNumberNext.setText(Integer.toString(numberNext));
                            bindingCheckVisitor.ivProfileNext.setImageBitmap(dataImage.get(dataList.get(numberNext)));
                        } else {

                            bindingCheckVisitor.tvOrderNumberNext.setText("-");
                            bindingCheckVisitor.ivProfileNext.setImageBitmap(null);
                        }

                        if( type.equals( "USERPICK" ) ) {

                            new Handler().postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    functionOrderSend();
                                }
                            }, 1000);
                        }
                    }
                });
            }
        }).start();
    }

    public void functionOrderSend() {

        String number = bindingCheckVisitor.tvOrderNumberPresent.getText().toString().trim();

        if( number != null ) {

            String uri = "USERPICK/" + dataList.get( Integer.parseInt( number ) );
            functionShowLog( uri );
            webSocketClient.send( uri );
        }
    }

    public void functionVisitorImageInit () {

        ModelVisitorPayload payloadCheckVisitor = new ModelVisitorPayload();

        payloadCheckVisitor.setVisitorRoomType(type);

        configRetrofit.getFunctionRestApi().apiCompanyImageSelect(payloadCheckVisitor).enqueue(new Callback<ModelVisitorPayload>() {

            @Override
            public void onResponse(Call<ModelVisitorPayload> call, Response<ModelVisitorPayload> response) {

                ModelVisitorPayload payloadResult = response.body();

                functionShowLog("what the");

                if (payloadResult != null) {

                    for (String key : payloadResult.getVisitorImage().keySet()) {

                        String value = payloadResult.getVisitorImage().get(key);
                        functionShowLog("key : " + key + " value : " + value);
                        dataImage.put(key, converter.functionStringToBitmap(value));
                    }
                    webSocketClient.send( "LIST/");
                }
            }

            @Override
            public void onFailure(Call<ModelVisitorPayload> call, Throwable t) {

                functionShowMessage("서버와 연결이 안 되었어요");
                functionShowLog("not connected to the server");
            }
        });
    }

    public void functionShowMessage (String message ){

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void functionShowLog (String message ){

        Log.i("[activityCheckVisitor]", message);
    }

    public void functionActivityReceive () {

        switch (intentReceive.getIntentFromActivity()) {

            case "activityCheckVisitor": {

                functionShowLog("move from activityCheckVisitor");
                modelUser = (ModelUser) intentReceive.getIntentData1();
                break;
            }

            case "activityMain": {

                functionShowLog("move from activityMain");
                modelUser = (ModelUser) intentReceive.getIntentData1();
                break;
            }

            case "activityCheckVisitorCompany": {

                functionShowLog("move from activityCheckVisitorCompany");
                modelUser = (ModelUser) intentReceive.getIntentData1();
                modelCompany = (ModelCompany) intentReceive.getIntentData2();
                bindingCheckVisitor.tvCompanyName.setText(modelCompany.getCompanyName());
                functionSocketCreate();
                functionVisitorImageInit();

                break;
            }

            default: {

                modelUser = (ModelUser) intentReceive.getIntentData1();
                break;
            }
        }
    }

    public void functionActivitySend () {

        switch ( intentSend.getIntentToActivity() ) {

            case "activityCheckVisitor": {

                intent = new Intent(this, ActivityCheckVisitor.class);
                intent.putExtra("data", intentSend);
                break;
            }

            case "activityMain": {

                intent = new Intent(this, ActivityMain.class);
                intent.putExtra("data", intentSend);
                break;
            }
        }

        startActivity(intent);
        finish();
    }
}
