package com.daalzzwi.kidalkidal.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
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
import com.daalzzwi.kidalkidal.adapter.AdapterChatCompany;
import com.daalzzwi.kidalkidal.adapter.AdapterDialog;
import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.config.ConfigRoomDatabase;
import com.daalzzwi.kidalkidal.databinding.ActivityChatCompanyBinding;
import com.daalzzwi.kidalkidal.function.FunctionConverter;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelToggle;
import com.daalzzwi.kidalkidal.model.ModelUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;

public class ActivityChatCompany extends AppCompatActivity {

    private ActivityChatCompanyBinding bindingChatCompany;

    private Intent intent;
    private ModelIntent intentSend;
    private ModelIntent intentReceive;

    private ConfigRetrofit configRetrofit;
    private ConfigRoomDatabase configRoomDatabase;
    private ModelUser modelUser;

    private FunctionConverter converter;
    private Dialog dialog;
    private RecyclerView dialogRecyclerView;
    String[] text = { "로그아웃 하기" , "프로필 보기" , "문의 하기" , "Q&A 보기" };

    private AdapterChatCompany adapterChatCompany;
    private WebSocketClient webSocketClient;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingChatCompany = ActivityChatCompanyBinding.inflate( getLayoutInflater() );
        View view = bindingChatCompany.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityChatCompany" );
        intentReceive = (ModelIntent) getIntent().getSerializableExtra( "data" );

        configRetrofit = getRetrofit();
        configRoomDatabase = new ConfigRoomDatabase( this );
        modelUser = new ModelUser();

        converter = new FunctionConverter();

        functionActivityReceive();
        functionChatReceive();
        functionRcvSet();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onStart() {

        super.onStart();
        bindingChatCompany.titleBar.ivProfile.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                functionDialog();
            }
        } );
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

        bindingChatCompany = null;

        intent = null;
        intentSend = null;
        intentReceive = null;

        configRoomDatabase = null;
        modelUser = null;

        converter = null;
        dialog = null;
        dialogRecyclerView = null;
        text = null;

        adapterChatCompany = null;
        webSocketClient = null;
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

    public void functionChatReceive() {

        configRetrofit.getFunctionRestApi().apiChatSelect().enqueue( new Callback< List< String > >() {

            @Override
            public void onResponse( Call< List< String > > call, Response< List< String > > response ) {

                List< String > payloadResult = response.body();

                functionShowLog( "==============================================" );
                for( String room : payloadResult ) {

                    adapterChatCompany.addItem( room );
                    functionShowLog( room );
                }
                functionShowLog( "==============================================" );
                adapterChatCompany.notifyDataSetChanged();
            }

            @Override
            public void onFailure( Call< List< String > > call, Throwable t ) {

                functionShowMessage( "서버와 연결이 안 되었어요" );
                functionShowLog( "not connected to the server" );
            }
        } );
    }

    public void functionRcvSet() {

        RecyclerView recyclerView = bindingChatCompany.rcvChatCompany;
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        adapterChatCompany = new AdapterChatCompany();
        recyclerView.setAdapter( adapterChatCompany );

        adapterChatCompany.setOnItemClickListener( new AdapterChatCompany.OnItemClickListener() {

            @Override
            public void onItemClick( ArrayList< String > dataRoom, View v, int pos ) {

                String room = "";
                room = dataRoom.get( pos );

                intentSend.setIntentToActivity( "activityChat" );
                intentSend.setIntentData1( modelUser );
                intentSend.setIntentData2( room );
                functionActivitySend();
            }
        } );
    }

    public void functionShowMessage( String message ) {

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityChatCompany]" , message );
    }

    public void functionTitleBarSet() {

        bindingChatCompany.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
        bindingChatCompany.titleBar.ivProfile.setImageBitmap( converter.functionStringToBitmap( modelUser.getUserImage() ) );
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityChatCompany" : {

                functionShowLog( "move from activityChatCompany" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                break;
            }

            case "activityChat" : {

                functionShowLog( "move from activityChat" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                break;
            }

            case "activityMain" : {

                functionShowLog( "move from activityMain" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
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

            case "activityChatCompany": {

                intent = new Intent( this, ActivityChatCompany.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

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