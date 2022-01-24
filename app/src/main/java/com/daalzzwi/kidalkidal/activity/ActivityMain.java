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
import com.daalzzwi.kidalkidal.adapter.AdapterDialog;
import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.config.ConfigRoomDatabase;
import com.daalzzwi.kidalkidal.databinding.ActivityMainBinding;
import com.daalzzwi.kidalkidal.function.FunctionConverter;
import com.daalzzwi.kidalkidal.model.ModelToggle;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelUser;

import java.util.ArrayList;
import java.util.Arrays;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;


public class ActivityMain extends AppCompatActivity {

    private ActivityMainBinding bindingMain;

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

    private long backKeyPressedTime;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingMain = ActivityMainBinding.inflate( getLayoutInflater() );
        View view = bindingMain.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityMain" );
        intentReceive =  (ModelIntent) getIntent().getSerializableExtra( "data" ) ;

        configRetrofit = getRetrofit();
        configRoomDatabase = new ConfigRoomDatabase( ActivityMain.this );
        modelUser = new ModelUser();
        modelToggle = new ModelToggle();

        converter = new FunctionConverter();

        backKeyPressedTime = 0;


        functionActivityReceive();
    }

    @Override
    protected void onStart() {

        super.onStart();

        bindingMain.titleBar.ivProfile.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                functionDialog();
            }
        } );

        bindingMain.btnScan.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                intentSend.setIntentToActivity( "activityQrScanner" );
                intentSend.setIntentData1( modelUser );
                functionActivitySend();
            }
        } );
        
        bindingMain.btnCheckVisitor.setOnClickListener( new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {

                intentSend.setIntentToActivity( "activityCheckVisitorCompany" );
                intentSend.setIntentData1( modelUser );
                functionActivitySend();
            }
        } );
        

        bindingMain.fabLiveChat.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if( modelUser.getUserEmail().equals( "kidalkidal.inc@gmail.com" ) ) {

                    intentSend.setIntentToActivity( "activityChatCompany" );
                    intentSend.setIntentData1( modelUser );
                    functionActivitySend();
                } else {

                    intentSend.setIntentToActivity( "activityChat" );
                    intentSend.setIntentData1( modelUser );
                    functionActivitySend();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if( System.currentTimeMillis() > backKeyPressedTime + 2000 ) {

            backKeyPressedTime = System.currentTimeMillis();
            functionShowMessage( "뒤로 가기 버튼을 한 번 더 누르시면 종료되요" );
            return;
        }

        if( System.currentTimeMillis() <= backKeyPressedTime + 2000) {

            functionShowMessage( "이용해 주셔서 고마워요" );
            finish();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        bindingMain = null;

        intent = null;
        intentSend = null;
        intentReceive = null;

        configRoomDatabase = null;
        modelUser = null;
        modelToggle = null;

        converter = null;
        dialogRecyclerView = null;
        text = null;
    }

    public void functionInit() {

        if( modelUser.getUserEmail().equals( "kidalkidal.inc@gmail.com" ) ) {

            bindingMain.btnScan.setVisibility( View.INVISIBLE );
            bindingMain.btnCheckVisitor.setVisibility( View.VISIBLE );
        } else {

            bindingMain.btnScan.setVisibility( View.VISIBLE );
            bindingMain.btnCheckVisitor.setVisibility( View.INVISIBLE );
        }
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

        configRoomDatabase.functionToggleUpdate( new ModelToggle() );
        configRoomDatabase.functionUserUpdate( new ModelUser() );

        intentSend.setIntentToActivity( "activityLogin" );
        intentSend.setIntentData1( null );
        functionActivitySend();
    }

    public void functionShowMessage( String message ) {

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityMain]" , message );
    }

    public void functionTitleBarSet() {

        bindingMain.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
        bindingMain.titleBar.ivProfile.setImageBitmap( converter.functionStringToBitmap( modelUser.getUserImage() ) );
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityMain" : {

                functionShowLog( "move from activityMain" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                functionInit();
                break;
            }

            case "activityLogin" : {

                functionShowLog( "move from activityLogin" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                functionInit();
                break;
            }

            case "activityProfile" : {

                functionShowLog( "move from activityProfile" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                functionInit();
                break;
            }

            case "activityDesk" : {

                functionShowLog( "move from activityDesk" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                functionInit();
                break;
            }

            case "activityQna" : {

                functionShowLog( "move from activityQna" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                functionInit();
                break;
            }

            case "activityQrScanner" : {

                functionShowLog( "move from activityQrScanner" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                functionInit();
                break;
            }

            case "activityOrder" : {

                functionShowLog( "move from activityQrder" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                functionInit();
                break;
            }

            case "activityChat" : {

                functionShowLog( "move from activityChat" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                functionInit();
                break;
            }

            case "activityChatCompany" : {

                functionShowLog( "move from activityChatCompany" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                functionInit();
                break;
            }

            case "activityCheckVisitor" : {

                functionShowLog( "move from activityCheckVisitor" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                break;
            }

            case "activityCheckVisitorCompany" : {

                functionShowLog( "move from activityCheckVisitorCompany" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                break;
            }

            default : {

                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                functionInit();
                break;
            }
        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityMain" : {

                intent = new Intent( this, ActivityMain.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityLogin" : {

                intent = new Intent( this, ActivityLogin.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityProfile" : {

                intent = new Intent( this, ActivityProfile.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityDesk" : {

                intent = new Intent( this, ActivityDesk.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityQna" : {

                intent = new Intent( this, ActivityQna.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityQrScanner" : {

                intent = new Intent( this, ActivityQrScanner.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityChat" : {

                intent = new Intent( this, ActivityChat.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityChatCompany" : {

                intent = new Intent( this, ActivityChatCompany.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityCheckVisitorCompany" : {

                intent = new Intent( this, ActivityCheckVisitorCompany.class );
                intent.putExtra( "data" , intentSend );
                break;
            }
        }

        startActivity( intent );
        finish();
    }
}