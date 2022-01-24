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
import com.daalzzwi.kidalkidal.adapter.AdapterCheckVisitorCompany;
import com.daalzzwi.kidalkidal.adapter.AdapterDialog;
import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.config.ConfigRoomDatabase;
import com.daalzzwi.kidalkidal.databinding.ActivityCheckVisitorCompanyBinding;
import com.daalzzwi.kidalkidal.function.FunctionConverter;
import com.daalzzwi.kidalkidal.model.ModelCompany;
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

public class ActivityCheckVisitorCompany extends AppCompatActivity {

    private ActivityCheckVisitorCompanyBinding bindingCheckVisitorCompany;

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

    private AdapterCheckVisitorCompany adapterCheckVisitorCompany;
    private WebSocketClient webSocketClient;
    private StringBuffer sbf;
    private ArrayList< String > dataChatCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );

        bindingCheckVisitorCompany= ActivityCheckVisitorCompanyBinding.inflate( getLayoutInflater() );
        View view = bindingCheckVisitorCompany.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityCheckVisitorCompany" );
        intentReceive = (ModelIntent) getIntent().getSerializableExtra( "data" );

        configRetrofit = getRetrofit();
        configRoomDatabase = new ConfigRoomDatabase( this );
        modelUser = new ModelUser();
        modelToggle = new ModelToggle();

        converter = new FunctionConverter();

        dataChatCompany = new ArrayList<>();

        functionActivityReceive();
        functionCompanyReceive();
        functionRcvSet();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onStart() {

        super.onStart();
        bindingCheckVisitorCompany.titleBar.ivProfile.setOnClickListener( new View.OnClickListener() {

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

        bindingCheckVisitorCompany = null;

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

        adapterCheckVisitorCompany = null;
        webSocketClient = null;
        sbf = null;
        dataChatCompany = null;
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

    public void functionCompanyReceive() {

        configRetrofit.getFunctionRestApi().apiCompanySelect().enqueue( new Callback< List<ModelCompany> >() {

            @Override
            public void onResponse(Call< List<ModelCompany> > call, Response< List<ModelCompany> > response ) {

                List<ModelCompany> payloadResult = response.body();

                functionShowLog( "==============================================" );
                for( ModelCompany room : payloadResult ) {

                    adapterCheckVisitorCompany.addItem( room );
                }

                functionShowLog( "==============================================" );
                adapterCheckVisitorCompany.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call< List<ModelCompany> > call, Throwable t ) {

                functionShowMessage( "서버와 연결이 안 되었어요" );
                functionShowLog( "not connected to the server" );
            }
        } );
    }

    public void functionRcvSet() {

        RecyclerView recyclerView = bindingCheckVisitorCompany.rcvCheckVisitorCompany;
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        adapterCheckVisitorCompany = new AdapterCheckVisitorCompany();
        recyclerView.setAdapter( adapterCheckVisitorCompany );

        adapterCheckVisitorCompany.setOnItemClickListener( new AdapterCheckVisitorCompany.OnItemClickListener() {

            @Override
            public void onItemClick(ArrayList<ModelCompany> dataRoom, View v, int pos ) {

                ModelCompany room = new ModelCompany();
                room = dataRoom.get( pos );

                intentSend.setIntentToActivity( "activityCheckVisitor" );
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

        Log.i( "[activityCheckVisitorCompany]" , message );
    }

    public void functionTitleBarSet() {

        bindingCheckVisitorCompany.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
        bindingCheckVisitorCompany.titleBar.ivProfile.setImageBitmap( converter.functionStringToBitmap( modelUser.getUserImage() ) );
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityCheckVisitorCompany" : {

                functionShowLog( "move from activityCheckVisitorCompany" );
                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                break;
            }

            case "activityCheckVisitor" : {

                functionShowLog( "move from activityCheckVisitor" );
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

            case "activityCheckVisitorCompany": {

                intent = new Intent( this, ActivityCheckVisitorCompany.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityCheckVisitor": {

                intent = new Intent( this, ActivityCheckVisitor.class );
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