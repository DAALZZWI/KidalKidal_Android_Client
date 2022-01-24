package com.daalzzwi.kidalkidal.activity;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daalzzwi.kidalkidal.R;
import com.daalzzwi.kidalkidal.adapter.AdapterDialog;
import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.config.ConfigRoomDatabase;
import com.daalzzwi.kidalkidal.databinding.ActivityQnaShowBinding;
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

public class ActivityQnaShow extends AppCompatActivity {

    private ActivityQnaShowBinding bindingQnaShow;

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

    private boolean toggleSettingVisible;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingQnaShow = ActivityQnaShowBinding.inflate( getLayoutInflater() );
        View view = bindingQnaShow.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityQnaShow" );
        intentReceive = (ModelIntent) getIntent().getSerializableExtra( "data" );

        configRetrofit = getRetrofit();
        configRoomDatabase = new ConfigRoomDatabase( this );
        modelUser = new ModelUser();
        modelToggle = new ModelToggle();
        modelDesk = new ModelDesk();

        converter = new FunctionConverter();

        toggleSettingVisible = false;

        functionActivityReceive();
    }

    @Override
    protected void onStart() {

        super.onStart();

        bindingQnaShow.pbProgressBar.setVisibility( View.VISIBLE );
        functionUiNotSelf();

        bindingQnaShow.btnQna.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                intentSend.setIntentToActivity( "activityQna" );
                intentSend.setIntentData1( modelUser );
                functionActivitySend();
            }
        } );

        bindingQnaShow.titleBar.ivProfile.setOnClickListener( new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                functionDialog();
            }
        } );

        bindingQnaShow.btnQnaDelete.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                functionQnaDelete();
            }
        } );
    }

    @Override
    public void onBackPressed() {

        intentSend.setIntentToActivity( "activityQna" );
        intentSend.setIntentData1( modelUser );
        functionActivitySend();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        bindingQnaShow = null;

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
    }

    public void functionUiSelf() {

        bindingQnaShow.btnQnaDelete.setVisibility( View.VISIBLE );
    }

    public void functionUiNotSelf() {

        bindingQnaShow.btnQnaDelete.setVisibility( View.INVISIBLE );
    }

    public void functionUiSuccess() {

        bindingQnaShow.btnQna.setVisibility( View.INVISIBLE );
        bindingQnaShow.btnQnaDelete.setVisibility( View.INVISIBLE );
    }

    public void functionUiFail() {

        bindingQnaShow.btnQna.setVisibility( View.VISIBLE );
        bindingQnaShow.btnQnaDelete.setVisibility( View.VISIBLE );
    }

    public void functionQnaEdit() {

        intentSend.setIntentToActivity( "activityQna" );
        intentSend.setIntentData1( modelUser );
        functionActivitySend();
    }

    public void functionQnaDelete() {


        ModelDesk deskQnaShow = new ModelDesk();
        ModelDeskPayload payloadQnaShow = new ModelDeskPayload();

        if( modelDesk.getDeskWriter().equals( modelUser.getUserEmail() ) ) {

            functionUiSuccess();
            deskQnaShow.setDeskId( modelDesk.getDeskId() );
            payloadQnaShow.setModelDesk( deskQnaShow );

            configRetrofit.getFunctionRestApi().apiDeskDelete( payloadQnaShow ).enqueue( new Callback<ModelDeskPayload>() {

                @Override
                public void onResponse(Call<ModelDeskPayload> call, Response<ModelDeskPayload> response) {

                    ModelDeskPayload payloadResult = response.body();

                    functionShowLog("==============================================");
                    functionShowLog("code : " + payloadResult.getCode() );
                    functionShowLog("msg : " + payloadResult.getMsg() );
                    functionShowLog("==============================================");

                    bindingQnaShow.pbProgressBar.setVisibility(View.INVISIBLE);
                    functionUiFail();

                    switch( payloadResult.getCode() ) {

                        case 100 : {

                            functionShowMessage( "삭제 실패했어요" );
                            break;
                        }

                        case 200 : {

                            functionShowMessage( "삭제 성공했어요" );

                            intentSend.setIntentToActivity( "activityQna" );
                            intentSend.setIntentData1( modelUser );
                            functionActivitySend();
                            break;
                        }

                        default : {

                            functionShowMessage( "삭제 실패했어요" );
                            break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<ModelDeskPayload> call, Throwable t) {

                    functionShowMessage("서버와 연결이 안 되었어요");
                    functionShowLog("not connected to the server");
                }
            } );
        }
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

        dialogRecyclerView.setAdapter(adapter);
        dialog.show();
    }

    public void functionUserLogout() {

        configRoomDatabase.functionToggleUpdate( new ModelToggle() );
        configRoomDatabase.functionUserUpdate( new ModelUser());

        intentSend.setIntentToActivity( "activityLogin" );
        intentSend.setIntentData1( null );
        functionActivitySend();
    }

    private void functionQnaShow() {

        String result = functionStringDeserialize( modelDesk.getDeskSubTitle() );

        bindingQnaShow.tvDeskTitle.setText( modelDesk.getDeskTitle() );
        bindingQnaShow.tvDesk.setText( "\uD83E\uDDD1\u200D\uD83E\uDDB0" + modelDesk.getDeskWriter() + "\n \uD83D\uDCC5" + modelDesk.getDeskRegisterDate() + "\n \uD83D\uDC41"  + modelDesk.getDeskSee() );
        bindingQnaShow.tvDeskSubTitle.setText( result );

        if( modelDesk.getDeskWriter().equals( modelUser.getUserEmail() ) ) {

            functionUiSelf();
        } else {

            functionUiNotSelf();
        }
    }

    public String functionStringDeserialize( String deskString ) {

        String stringResult = deskString.replace( "\\n" , "\n" );

        return stringResult;
    }

    private void functionQnaIncrease() {

        ModelDesk deskQnaShow = new ModelDesk();
        ModelDeskPayload payloadQnaShow = new ModelDeskPayload();


        deskQnaShow.setDeskId( modelDesk.getDeskId() );

        payloadQnaShow.setModelDesk( deskQnaShow );

        configRetrofit.getFunctionRestApi().apiDeskIncrease( payloadQnaShow ).enqueue( new Callback<ModelDeskPayload>() {

            @Override
            public void onResponse(Call<ModelDeskPayload> call, Response<ModelDeskPayload> response ) {

                ModelDeskPayload payloadResult = response.body();

                functionShowLog( "==============================================" );
                functionShowLog( "code : " + payloadResult.getCode() );
                functionShowLog( "msg : " + payloadResult.getMsg() );
                functionShowLog( "==============================================" );

                bindingQnaShow.pbProgressBar.setVisibility(View.INVISIBLE);

                switch( payloadResult.getCode() ) {

                    case 100 : {

                        functionShowMessage( "불러오기 실패했어요" );
                        break;
                    }

                    case 200 : {

                        functionShowMessage( "불러오기 성공했어요" );
                        functionQnaShow();
                        break;
                    }

                    default : {

                        functionShowMessage( "불러오기 실패했어요" );
                        break;
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


    public void functionShowMessage( String message ) {

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityDesk]" , message );
    }

    public void functionTitleBarSet() {

        bindingQnaShow.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
        bindingQnaShow.titleBar.ivProfile.setImageBitmap( converter.functionStringToBitmap( modelUser.getUserImage() ) );
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityQnaShow" : {

                functionShowLog( "move from activityQnaShow" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                modelDesk = (ModelDesk) intentReceive.getIntentData2();
                bindingQnaShow.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
                functionTitleBarSet();
                functionQnaIncrease();
                break;
            }

            case "activityQna" : {

                functionShowLog( "move from activityQna" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                modelDesk = (ModelDesk) intentReceive.getIntentData2();
                bindingQnaShow.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
                functionTitleBarSet();
                functionQnaIncrease();
                break;
            }

            default : {

                modelUser = (ModelUser) intentReceive.getIntentData1();
                modelDesk = (ModelDesk) intentReceive.getIntentData2();
                bindingQnaShow.titleBar.tvName.setText( modelUser.getUserName() + "님!" );

                functionTitleBarSet();
                break;
            }
        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityQnaShow" : {

                intent = new Intent( this , ActivityQnaShow.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityQna" : {

                intent = new Intent( this, ActivityQna.class );
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