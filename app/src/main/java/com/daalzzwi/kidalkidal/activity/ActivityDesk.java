package com.daalzzwi.kidalkidal.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daalzzwi.kidalkidal.R;
import com.daalzzwi.kidalkidal.adapter.AdapterDialog;
import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.config.ConfigRoomDatabase;
import com.daalzzwi.kidalkidal.databinding.ActivityDeskBinding;
import com.daalzzwi.kidalkidal.function.FunctionConverter;
import com.daalzzwi.kidalkidal.model.ModelDesk;
import com.daalzzwi.kidalkidal.model.ModelToggle;
import com.daalzzwi.kidalkidal.model.ModelDeskPayload;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelUser;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;

public class ActivityDesk extends AppCompatActivity {

    private ActivityDeskBinding bindingDesk;

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

    private boolean toggleSettingVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );

        bindingDesk = ActivityDeskBinding.inflate( getLayoutInflater() );
        View view = bindingDesk.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityProfile" );
        intentReceive = (ModelIntent) getIntent().getSerializableExtra( "data" );

        configRetrofit = getRetrofit();
        configRoomDatabase = new ConfigRoomDatabase( this );
        modelUser = new ModelUser();
        modelToggle = new ModelToggle();

        converter = new FunctionConverter();

        toggleSettingVisible = false;

        functionActivityReceive();
    }

    @Override
    protected void onStart() {

        super.onStart();

        functionUiFail();

        bindingDesk.titleBar.ivProfile.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                functionDialog();
            }
        } );

        bindingDesk.btnMain.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                intentSend.setIntentToActivity( "activityMain" );
                intentSend.setIntentData1( modelUser );
                functionActivitySend();
            }
        } );
        
        bindingDesk.btnSend.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                String deskTitle = bindingDesk.etTitle.getText().toString().trim();
                String deskSubTitle = bindingDesk.etSubTitle.getText().toString().trim();

                if( functionCheckInformation( deskTitle , deskSubTitle ) == true ) {

                    functionUiSuccess();
                    deskSubTitle = functionStringSerialize( deskSubTitle );
                    functionDesk( deskTitle , deskSubTitle );
                } else {

                    functionUiFail();
                    functionShowLog( "blank on here" );
                    functionShowMessage( "빈칸이 없도록 적어주세요" );
                }
            }
        } );

        bindingDesk.swVisible.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                toggleSettingVisible = b;
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

        bindingDesk = null;

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

        toggleSettingVisible = new Boolean( null );
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

    public void functionDesk( String deskTitle , String deskSubTitle ) {

        ModelDesk deskProfile = new ModelDesk();
        ModelDeskPayload payloadProfile = new ModelDeskPayload();


        deskProfile.setDeskWriter( modelUser.getUserEmail() );
        deskProfile.setDeskTitle( deskTitle );
        deskProfile.setDeskSubTitle( deskSubTitle );

        if( toggleSettingVisible == true ) {

            deskProfile.setDeskVisible( "y" );
        } else {

            deskProfile.setDeskVisible( "n" );
        }

        payloadProfile.setModelDesk( deskProfile );

        configRetrofit.getFunctionRestApi().apiDeskInsert( payloadProfile ).enqueue( new Callback<ModelDeskPayload>() {

            @Override
            public void onResponse(Call<ModelDeskPayload> call, Response<ModelDeskPayload> response) {

                ModelDeskPayload payloadResult = response.body();
                ModelDesk deskResult = payloadResult.getModelDesk();

                functionShowLog( "==============================================" );
                functionShowLog( "code : " + payloadResult.getCode() );
                functionShowLog( "msg : " + payloadResult.getMsg() );
                functionShowLog( "modelUser : " + deskResult );
                functionShowLog( "==============================================" );

                switch( payloadResult.getCode() ) {

                    case 100 : {

                        functionShowMessage( "문의등록 실패했어요" );
                        functionUiFail();
                        break;
                    }

                    case 200 : {

                        functionShowMessage( "문의등록 성공했어요" );
                        functionUiFail();
                        break;
                    }

                    default : {

                        functionShowMessage( "문의등록 실패했어요" );
                        functionUiFail();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelDeskPayload> call, Throwable t) {

                functionShowMessage( "서버와 연결이 안 되었어요" );
                functionShowLog( "not connected to the server" );
                functionUiFail();
            }
        } );
    }

    public boolean functionCheckInformation( String deskTitle , String deskSubTitle  ) {

        if ( !TextUtils.isEmpty( deskTitle ) && !TextUtils.isEmpty( deskSubTitle ) ) {

            return true;
        } else {

            return false;
        }
    }

    public String functionStringSerialize( String deskString ) {

        String[] string = deskString.split( "\\n" );
        String deskStringResult = "";

        for( String result : string ) {

            result = result + "\\n";
            deskStringResult = deskStringResult + result;
        }

        if( deskStringResult.isEmpty() ) {

            return deskString;
        }

        return deskStringResult;
    }

    public String functionStringDeserialize( String deskString ) {

        String stringResult = deskString.replace( "\\n" , " " );

        return stringResult;
    }

    public void functionUserLogout() {

        configRoomDatabase.functionToggleUpdate( new ModelToggle() );
        configRoomDatabase.functionUserUpdate( new ModelUser());

        intentSend.setIntentToActivity( "activityLogin" );
        intentSend.setIntentData1( null );
        functionActivitySend();
    }

    public void functionUiSuccess() {

        bindingDesk.btnSend.setVisibility( View.INVISIBLE );
        bindingDesk.btnMain.setVisibility( View.INVISIBLE );
        bindingDesk.swVisible.setVisibility( View.INVISIBLE );
        bindingDesk.pbProgressBar.setVisibility( View.VISIBLE );
    }

    public void functionUiFail() {

        bindingDesk.btnSend.setVisibility( View.VISIBLE );
        bindingDesk.btnMain.setVisibility( View.VISIBLE );
        bindingDesk.swVisible.setVisibility( View.VISIBLE );
        bindingDesk.pbProgressBar.setVisibility( View.INVISIBLE );
    }

    public void functionShowMessage( String message ) {

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityDesk]" , message );
    }

    public void functionTitleBarSet() {

        bindingDesk.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
        bindingDesk.titleBar.ivProfile.setImageBitmap( converter.functionStringToBitmap( modelUser.getUserImage() ) );
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityDesk" : {

                functionShowLog( "move from activityDesk" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                break;
            }

            case "activityProfile" : {

                functionShowLog( "move from activityProfile" );
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

            case "activityQna" : {

                functionShowLog( "move from activityQna" );
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

            case "activityChatCompany" : {

                functionShowLog( "move from activityChatCompany" );
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
                break;
            }
        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityDesk" : {

                intent = new Intent( this , ActivityDesk.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityProfile" : {

                intent = new Intent( this , ActivityProfile.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityMain" : {

                intent = new Intent( this , ActivityMain.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityLogin" : {

                intent = new Intent( this, ActivityLogin.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityQna" : {

                intent = new Intent( this, ActivityQna.class );
                intent.putExtra( "data" , intentSend );
                break;
            }
        }

        startActivity( intent );
        finish();
    }
}