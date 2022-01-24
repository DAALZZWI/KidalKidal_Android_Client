package com.daalzzwi.kidalkidal.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.widget.Toast;

import com.daalzzwi.kidalkidal.R;
import com.daalzzwi.kidalkidal.adapter.AdapterQna;
import com.daalzzwi.kidalkidal.adapter.AdapterDialog;
import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.config.ConfigRoomDatabase;
import com.daalzzwi.kidalkidal.databinding.ActivityQnaBinding;
import com.daalzzwi.kidalkidal.function.FunctionConverter;
import com.daalzzwi.kidalkidal.model.ModelDesk;
import com.daalzzwi.kidalkidal.model.ModelDeskPayload;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelToggle;
import com.daalzzwi.kidalkidal.model.ModelUser;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;

public class ActivityQna extends AppCompatActivity {

    private ActivityQnaBinding bindingQna;

    private Intent intent;
    private ModelIntent intentSend;
    private ModelIntent intentReceive;

    private ConfigRetrofit configRetrofit;
    private ConfigRoomDatabase configRoomDatabase;
    private ModelUser modelUser;
    private ModelToggle modelToggle;
    private ModelDesk modelDesk;

    private FunctionConverter converter;
    private Dialog dialog;
    private RecyclerView dialogRecyclerView;
    String[] text = { "로그아웃 하기", "프로필 보기", "문의 하기", "Q&A 보기" };

    private AdapterQna adapterQna;
    private boolean toggleSettingVisible;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingQna = ActivityQnaBinding.inflate( getLayoutInflater() );
        View view = bindingQna.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityQna" );
        intentReceive = (ModelIntent) getIntent().getSerializableExtra( "data" );

        configRetrofit = getRetrofit();
        configRoomDatabase = new ConfigRoomDatabase( this );
        modelUser = new ModelUser();
        modelToggle = new ModelToggle();
        modelDesk = new ModelDesk();

        converter = new FunctionConverter();

        toggleSettingVisible = false;

        functionActivityReceive();
        functionRcvSet();
        functionQnaReceive();
    }

    @Override
    protected void onStart() {

        super.onStart();

        bindingQna.pbProgressBar.setVisibility(View.VISIBLE);

        bindingQna.titleBar.ivProfile.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                functionDialog();
            }
        } );

        bindingQna.btnMain.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                intentSend.setIntentToActivity("activityMain");
                intentSend.setIntentData1( modelUser );
                functionActivitySend();
            }
        } );

        bindingQna.layoutRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                intentSend.setIntentToActivity( "activityQna" );
                intentSend.setIntentData1( modelUser );
                functionActivitySend();
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

        bindingQna = null;

        intent = null;
        intentSend = null;
        intentReceive = null;

        configRoomDatabase = null;
        modelUser = null;
        modelToggle = null;
        modelDesk = null;

        converter = null;
        dialog = null;
        dialogRecyclerView = null;
        text = null;

        adapterQna = null;
    }

    public void functionDialog() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        dialog = new Dialog(this );

        display.getRealSize(size);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        LayoutInflater inf = getLayoutInflater();
        View dialogView = inf.inflate( R.layout.activity_rcv_dialog, null );

        lp.copyFrom( dialog.getWindow().getAttributes() );
        lp.width = size.x * 80 / 100;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside( true );
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT) );

        ArrayList< String > arrayList = new ArrayList<>();
        arrayList.addAll( Arrays.asList(text) );

        dialogRecyclerView = ( RecyclerView ) dialogView.findViewById( R.id.rcvItemDialog );
        dialogRecyclerView.setLayoutManager( new LinearLayoutManager(this) );

        AdapterDialog adapter = new AdapterDialog( arrayList );

        adapter.setOnItemClickListener(new AdapterDialog.OnItemClickListener() {

            @Override
            public void onItemClick(View v, String pos) {

                switch( pos ) {

                    case "로그아웃 하기": {

                        dialog.dismiss();
                        functionUserLogout();
                        break;
                    }
                    case "프로필 보기": {

                        dialog.dismiss();
                        intentSend.setIntentToActivity( "activityProfile" );
                        intentSend.setIntentData1( modelUser );

                        functionActivitySend();
                        break;
                    }
                    case "문의 하기": {

                        dialog.dismiss();
                        intentSend.setIntentToActivity( "activityDesk" );
                        intentSend.setIntentData1( modelUser );

                        functionActivitySend();
                        break;
                    }
                    case "Q&A 보기": {

                        dialog.dismiss();
                        intentSend.setIntentToActivity( "activityQna" );
                        intentSend.setIntentData1( modelUser );

                        functionActivitySend();
                        break;
                    }
                    default: {

                        break;
                    }
                }
            }
        } );

        dialogRecyclerView.setAdapter( adapter );
        dialog.show();
    }

    public void functionRcvSet() {

        RecyclerView recyclerView = bindingQna.rcvQna;
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        adapterQna = new AdapterQna();
        recyclerView.setAdapter( adapterQna );

        adapterQna.setOnItemClickListener( new AdapterQna.OnItemClickListener() {

            @Override
            public void onItemClick(View view , ArrayList<ModelDesk> dataQna , int position ) {

                modelDesk = dataQna.get( position );

                modelDesk.setDeskImage( "" );

                if( modelUser.getUserEmail().equals( modelDesk.getDeskWriter() ) ) {

                    functionQnaSend();
                } else {

                    if( modelDesk.getDeskVisible().equals( "y" ) ) {

                        functionQnaSend();
                    } else {

                        functionShowMessage("본인만 확인 가능한 글이에요");
                    }
                }
            }
        } );

        adapterQna.setOnItemUpdateListener( new AdapterQna.OnItemUpdateListener() {

            @Override
            public void onItemUpdate(RecyclerView.ViewHolder holder, ArrayList<ModelDesk> dataQna, int position ) {

                new Thread( new Runnable() {

                    @Override
                    public void run() {

                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {

                                ( ( AdapterQna.viewHolder ) holder ).deskId.setText( "#" + Integer.toString( dataQna.get( position ).getDeskId() ) );
                                ( ( AdapterQna.viewHolder ) holder ).deskTitle.setText( dataQna.get( position ).getDeskTitle() );
                                ( ( AdapterQna.viewHolder ) holder ).deskSee.setText( "\uD83D\uDC41" + Integer.toString( dataQna.get( position ).getDeskSee() ) );
                                ( ( AdapterQna.viewHolder ) holder ).deskImage.setImageBitmap( converter.functionStringToBitmap( dataQna.get( position ).getDeskImage() ) );
                            }
                        });
                    }
                }).start();
            }
        });
    }

    public void functionQnaReceive() {

        configRetrofit.getFunctionRestApi().apiDeskSelect().enqueue( new Callback<ModelDeskPayload>() {

            @Override
            public void onResponse(Call<ModelDeskPayload> call, Response<ModelDeskPayload> response ) {

                ModelDeskPayload payloadResult = response.body();
                ArrayList<ModelDesk> deskResult = payloadResult.getModelDesks();

                functionShowLog( "==============================================" );
                functionShowLog( "code : " + payloadResult.getCode() );
                functionShowLog( "msg : " + payloadResult.getMsg() );

                for( ModelDesk desks : deskResult ) {

                    functionShowLog( "==============================================" );
                    functionShowLog( "modelDesk.deskId : " + desks.getDeskId() );
                    functionShowLog( "modelDesk.deskTitle : " + desks.getDeskTitle() );
                    functionShowLog( "modelDesk.deskSubTitle : " + desks.getDeskSubTitle() );
                    functionShowLog( "modelDesk.deskWriter : " + desks.getDeskWriter() );
                    functionShowLog( "modelDesk.deskRegisterDate : " + desks.getDeskRegisterDate() );
                    functionShowLog( "modelDesk.deskVisible : " + desks.getDeskVisible() );
                    functionShowLog( "modelDesk.deskStatus : " + desks.getDeskStatus() );
                    functionShowLog( "modelDesk.deskImage : " + desks.getDeskImage() );
                    functionShowLog( "modelDesk.deskSee : " + desks.getDeskSee() );
                    functionShowLog( "==============================================" );
                }

                bindingQna.pbProgressBar.setVisibility(View.INVISIBLE);

                switch( payloadResult.getCode() ) {

                    case 100 : {

                        functionShowMessage( "목록호출 실패했어요" );
                        break;
                    }

                    case 200 : {

                        for( ModelDesk desk : deskResult ) {

                            adapterQna.addItem( desk );
                        }

                        adapterQna.notifyDataSetChanged();
                        functionShowMessage( "목록호출 성공했어요" );
                        break;
                    }

                    default: {

                        functionShowMessage( "목록호출 실패했어요" );
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelDeskPayload> call, Throwable t ) {

                functionShowMessage( "서버와 연결이 안 되었어요" );
                functionShowLog( "not connected to the server" );
            }
        } );
    }

    public void functionQnaSend() {

        intentSend.setIntentToActivity( "activityQnaShow" );
        intentSend.setIntentData1( modelUser );
        intentSend.setIntentData2( modelDesk );

        functionActivitySend();
    }

    public boolean functionCheckInformation( String deskTitle , String deskSubTitle  ) {

        if ( !TextUtils.isEmpty( deskTitle ) && !TextUtils.isEmpty( deskSubTitle ) ) {

            return true;
        } else {

            return false;
        }
    }

    public void functionUserLogout() {

        configRoomDatabase.functionToggleUpdate( new ModelToggle() );
        configRoomDatabase.functionUserUpdate( new ModelUser());

        intentSend.setIntentToActivity( "activityLogin" );
        intentSend.setIntentData1( null );
        functionActivitySend();
    }

    public void functionShowMessage( String message ) {

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityDesk]" , message );
    }

    public void functionTitleBarSet() {

        bindingQna.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
        bindingQna.titleBar.ivProfile.setImageBitmap( converter.functionStringToBitmap( modelUser.getUserImage() ) );
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityQna" : {

                functionShowLog( "move from activityQna" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                break;
            }

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

            case "activityQnaShow" : {

                functionShowLog( "move from activityQnaShow" );
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

            default: {

                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                break;
            }
        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityQna" : {

                intent = new Intent( this, ActivityQna.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityQnaShow" : {

                intent = new Intent( this , ActivityQnaShow.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

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

            default: {
                functionShowLog("ddd");
            }
        }

        startActivity( intent );
        finish();
    }
}