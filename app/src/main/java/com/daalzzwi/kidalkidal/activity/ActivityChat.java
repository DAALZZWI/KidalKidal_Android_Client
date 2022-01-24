package com.daalzzwi.kidalkidal.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.daalzzwi.kidalkidal.R;
import com.daalzzwi.kidalkidal.adapter.AdapterChat;
import com.daalzzwi.kidalkidal.adapter.AdapterDialog;
import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.config.ConfigRoomDatabase;
import com.daalzzwi.kidalkidal.databinding.ActivityChatBinding;
import com.daalzzwi.kidalkidal.function.FunctionConverter;
import com.daalzzwi.kidalkidal.model.ModelChat;
import com.daalzzwi.kidalkidal.model.ModelChatPayload;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelToggle;
import com.daalzzwi.kidalkidal.model.ModelUser;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;

public class ActivityChat extends AppCompatActivity {

    private ActivityChatBinding bindingChat;

    private Intent intent;
    private ModelIntent intentSend;
    private ModelIntent intentReceive;

    private ConfigRetrofit configRetrofit;
    private ConfigRoomDatabase configRoomDatabase;
    private ModelUser modelUser;
    private ModelToggle modelToggle;

    private FunctionConverter converter;
    private Dialog dialog;
    private RecyclerView dialogRecyclerView;
    String[] text = { "로그아웃 하기" , "프로필 보기" , "문의 하기" , "Q&A 보기" };

    private AdapterChat adapterChat;
    private WebSocketClient webSocketClient;
    private Map< String , Bitmap > dataImage;
    private String type;
    int init;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingChat = ActivityChatBinding.inflate( getLayoutInflater() );
        View view = bindingChat.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityChat" );
        intentReceive = ( ModelIntent ) getIntent().getSerializableExtra( "data" );

        configRetrofit = getRetrofit();
        configRoomDatabase = new ConfigRoomDatabase(this );
        modelUser = new ModelUser();
        modelToggle = new ModelToggle();

        converter = new FunctionConverter();

        type = "";
        dataImage = new HashMap< String , Bitmap >();
        init = 0;

        functionActivityReceive();
        functionSocketCreate();
        functionRcvSet();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onStart() {

        super.onStart();
        bindingChat.titleBar.ivProfile.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                functionDialog();
            }
        } );

        bindingChat.ivSend.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                functionChatSend();
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
        
        bindingChat = null;

        intent = null;
        intentSend = null;
        intentReceive = null;

        configRoomDatabase = null;
        modelUser = null;
        modelToggle = null;

        converter = null;
        dialog = null;
        dialogRecyclerView = null;
        text = null;

        webSocketClient = null;
        type = null;
    }

    public void functionDialog() {
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        dialog = new Dialog( this );

        display.getRealSize( size );
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        LayoutInflater inf = getLayoutInflater();
        View dialogView = inf.inflate( R.layout.activity_rcv_dialog, null );

        lp.copyFrom( dialog.getWindow().getAttributes() );
        lp.width = size.x * 80 / 100;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.setContentView( dialogView );
        dialog.setCanceledOnTouchOutside( true );
        dialog.getWindow().setAttributes( lp );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

        ArrayList< String > arrayList = new ArrayList<>();
        arrayList.addAll( Arrays.asList( text ) );

        dialogRecyclerView = ( RecyclerView ) dialogView.findViewById( R.id.rcvItemDialog );
        dialogRecyclerView.setLayoutManager( new LinearLayoutManager(this ) );

        AdapterDialog adapter = new AdapterDialog( arrayList );

        adapter.setOnItemClickListener( new AdapterDialog.OnItemClickListener() {

            @Override
            public void onItemClick( View v , String pos ) {

                switch( pos ) {

                    case "로그아웃 하기" : {

                        dialog.dismiss();
                        functionUserLogout();
                        break;
                    }

                    case "프로필 보기" : {

                        dialog.dismiss();
                        intentSend.setIntentToActivity( "activityProfile" );
                        intentSend.setIntentData1( modelUser );

                        functionActivitySend();
                        break;
                    }

                    case "문의 하기" : {

                        dialog.dismiss();
                        intentSend.setIntentToActivity( "activityDesk" );
                        intentSend.setIntentData1( modelUser );

                        functionActivitySend();
                        break;
                    }

                    case "Q&A 보기" : {

                        dialog.dismiss();
                        intentSend.setIntentToActivity( "activityQna" );
                        intentSend.setIntentData1( modelUser );

                        functionActivitySend();
                        break;
                    }

                    default : {

                        break;
                    }
                }
            }
        } );

        dialogRecyclerView.setAdapter( adapter );
        dialog.show();
    }

    public void functionUserLogout() {

        webSocketClient.close();

        configRoomDatabase.functionToggleUpdate( new ModelToggle() );
        configRoomDatabase.functionUserUpdate( new ModelUser() );

        intentSend.setIntentToActivity( "activityLogin" );
        intentSend.setIntentData1( null );
        functionActivitySend();
    }

    public void functionSocketCreate() {

        URI uri;
        String url = "";
        String parametersUser = "";
        String parametersCompany = "";

        if ( type.isEmpty() ) {

            parametersUser = "/" + modelUser.getUserEmail() + "/" + modelUser.getUserName() + "/" + modelUser.getUserEmail();
            url = "ws://192.168.35.55:3232/chat" + parametersUser;
        } else {

            parametersCompany = "/" + modelUser.getUserEmail() + "/" + modelUser.getUserName() + "/" + type;
            url = "ws://192.168.35.55:3232/chat" + parametersCompany;
        }

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
                functionChatImageInit();
                init = 1;
            }

            @Override
            public void onTextReceived( String message ) {

                functionShowLog( "message received" );
                functionShowLog( message );
                functionChatApply( message );
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

    public void functionChatSend() {

        String result = "CHAT/" + bindingChat.etMessage.getText().toString();
        bindingChat.etMessage.setText( "" );
        webSocketClient.send( result );
    }

    public void functionChatApply( String message ) {

        ModelChat modelChat = new ModelChat();
        String type = message.split( "/" )[0];
        modelChat.setChatType( type );

        switch ( type ) {

            case "JOIN" : {

                String email = message.split( "/" )[1];

                modelChat.setChatMessage( email.split("@")[0] + "님이 입장했어요" );
                modelChat.setChatViewType( 0 );

                if( dataImage.get( email ) == null ) {

                    ConfigRetrofit configRetrofit = getRetrofit();

                    configRetrofit.getFunctionRestApi().apiChatImageAdd( email ).enqueue( new Callback<ModelChatPayload>() {

                        @Override
                        public void onResponse(Call<ModelChatPayload> call, Response<ModelChatPayload> response ) {

                            ModelChatPayload payloadResult = response.body();

                            if( payloadResult.getChatImage() != null ) {

                                loop:
                                for( String key : payloadResult.getChatImage().keySet() ) {

                                    if( payloadResult.getChatImage().get( key ) == null ) {

                                        break loop;
                                    } else {

                                        String value = payloadResult.getChatImage().get( key );
                                        dataImage.put( key , converter.functionStringToBitmap( value ) );
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelChatPayload> call, Throwable t ) {

                            functionShowMessage( "서버와 연결이 안 되었어요" );
                            functionShowLog( "not connected to the server" );
                        }
                    });
                }

                break;
            }

            case "LEAVE" : {

                String email = message.split( "/" )[1];

                modelChat.setChatMessage( email.split("@")[0] + "님이 퇴장했어요" );
                modelChat.setChatViewType( 0 );

                dataImage.remove( email );
                break;
            }

            case "CHAT" : {

                String email = message.split( "/" )[1];
                String time = message.split( "/" )[2];
                String msg = message.split( "/" )[3];

                modelChat.setChatUserEmail( email );
                modelChat.setChatTime( time );
                modelChat.setChatUserName( email.split("@")[0] );
                modelChat.setChatMessage( msg );

                if( modelUser.getUserEmail().equals( modelChat.getChatUserEmail() ) ) {

                    modelChat.setChatViewType( 2 );
                } else {

                    modelChat.setChatViewType( 1 );
                }

                break;
            }
        }

        if( modelChat.getChatType() == "LEAVE" ) {

            if( modelChat.getChatMessage().contains( modelUser.getUserEmail() ) ) {

                functionShowLog("self killed");
                return;
            }
        }

        adapterChat.addItem( modelChat );
    }

    public void functionChatImageInit() {

        ModelChatPayload payloadChat = new ModelChatPayload();
        Map< String , String > chatImage = new HashMap< String , String >();

        chatImage.put( modelUser.getUserEmail() , modelUser.getUserImage() );
        payloadChat.setChatImage( chatImage );

        if( type.isEmpty() ) {

            payloadChat.setChatRoomType( modelUser.getUserEmail() );
        } else {

            payloadChat.setChatRoomType( type );
        }

        configRetrofit.getFunctionRestApi().apiChatImageSelect( payloadChat ).enqueue( new Callback<ModelChatPayload>() {

            @Override
            public void onResponse(Call<ModelChatPayload> call, Response<ModelChatPayload> response ) {

                ModelChatPayload payloadResult = response.body();

                if( payloadResult != null ) {

                    for( String key : payloadResult.getChatImage().keySet()  ) {

                        String value = payloadResult.getChatImage().get( key );
                        dataImage.put( key , converter.functionStringToBitmap( value ) );
                        functionShowLog("-0");
                    }

                    functionShowLog("-1");
                    webSocketClient.send( "JOIN/" + modelUser.getUserEmail() );
                }
            }

            @Override
            public void onFailure(Call<ModelChatPayload> call, Throwable t ) {

                functionShowMessage( "서버와 연결이 안 되었어요" );
                functionShowLog( "not connected to the server" );
            }
        });
    }

    public void functionRcvSet() {

        ModelChat modelChat = new ModelChat();

        String msg = "실시간 채팅이 시작되었습니다";
        modelChat.setChatMessage( msg );
        modelChat.setChatViewType( 0 );

        RecyclerView recyclerView = bindingChat.rvcChat;
        LinearLayoutManager manager = new LinearLayoutManager( this );
        manager.setStackFromEnd( true );
        recyclerView.setLayoutManager( manager );
        
        adapterChat = new AdapterChat();
        recyclerView.setAdapter( adapterChat );

        adapterChat.addItem( modelChat );

        adapterChat.setOnItemUpdateListener( new AdapterChat.OnItemUpdateListener() {

            @Override
            public void onItemUpdate(RecyclerView.ViewHolder holder, ArrayList<ModelChat> dataChat , int position ) {

                new Thread( new Runnable() {

                    @Override
                    public void run() {

                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {

                                if( holder instanceof AdapterChat.viewHolderCenter) {

                                    ( (AdapterChat.viewHolderCenter) holder ).chatMessage.setText( dataChat.get( position ).getChatMessage() );
                                }

                                if( holder instanceof AdapterChat.viewHolderLeft) {

                                    ( (AdapterChat.viewHolderLeft) holder ).chatName.setText( dataChat.get( position ).getChatUserName() );
                                    ( (AdapterChat.viewHolderLeft) holder ).chatDate.setText( dataChat.get( position ).getChatTime() );
                                    ( (AdapterChat.viewHolderLeft) holder ).chatMessage.setText( dataChat.get( position ).getChatMessage() );
                                    ( (AdapterChat.viewHolderLeft) holder ).chatImage.setImageBitmap( dataImage.get( dataChat.get( position ).getChatUserEmail() ) );
                                }

                                if( holder instanceof AdapterChat.viewHolderRight) {

                                    ( (AdapterChat.viewHolderRight) holder ).chatName.setText( dataChat.get( position ).getChatUserName() );
                                    ( (AdapterChat.viewHolderRight) holder ).chatDate.setText( dataChat.get( position ).getChatTime() );
                                    ( (AdapterChat.viewHolderRight) holder ).chatMessage.setText( dataChat.get( position ).getChatMessage() );
                                    ( (AdapterChat.viewHolderRight) holder ).chatImage.setImageBitmap( dataImage.get( dataChat.get( position ).getChatUserEmail() ) );
                                }

                                adapterChat.notifyItemChanged( position );
                            }
                        });
                    }
                }).start();
            }
        });
    }

    public void functionShowMessage( String message ) {

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityChat]" , message );
    }

    public void functionTitleBarSet() {

        bindingChat.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
        bindingChat.titleBar.ivProfile.setImageBitmap( converter.functionStringToBitmap( modelUser.getUserImage() ) );
    }

    public void functionActivityReceive() {
        
        switch( intentReceive.getIntentFromActivity() ) {

            case "activityChat" : {

                functionShowLog( "move from activityChat" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                bindingChat.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
                functionTitleBarSet();
                break;
            }

            case "activityChatCompany" : {

                functionShowLog( "move from activityChatCompany" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                type = ( String ) intentReceive.getIntentData2();
                bindingChat.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
                functionTitleBarSet();
                break;
            }

            case "activityMain" : {

                functionShowLog( "move from activityMain" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                bindingChat.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
                functionTitleBarSet();
                break;
            }

            default : {

                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                break;
            }
        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityChat": {

                intent = new Intent( this, ActivityChat.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityMain": {

                intent = new Intent( this, ActivityMain.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityLogin": {

                intent = new Intent( this, ActivityLogin.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityProfile": {

                intent = new Intent( this, ActivityProfile.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityDesk": {

                intent = new Intent( this, ActivityDesk.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityQna": {

                intent = new Intent( this, ActivityQna.class );
                intent.putExtra( "data" , intentSend );
                break;
            }
        }

        startActivity( intent );
        finish();
    }
}
