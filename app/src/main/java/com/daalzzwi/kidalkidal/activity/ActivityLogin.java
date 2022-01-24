package com.daalzzwi.kidalkidal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.config.ConfigRoomDatabase;
import com.daalzzwi.kidalkidal.databinding.ActivityLoginBinding;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelToggle;
import com.daalzzwi.kidalkidal.model.ModelUser;
import com.daalzzwi.kidalkidal.model.ModelUserPayload;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;

public class ActivityLogin extends AppCompatActivity {

    private ActivityLoginBinding bindingLogin;

    private Intent intent;
    private ModelIntent intentSend;
    private ModelIntent intentReceive;

    private ConfigRetrofit configRetrofit;
    private ConfigRoomDatabase configRoomDatabase;
    private ModelUser modelUser;
    private ModelToggle modelToggle;

    private int toggleSettingAutoLogin;
    private long backKeyPressedTime;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingLogin = ActivityLoginBinding.inflate( getLayoutInflater() );
        View view = bindingLogin.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityLogin" );
        intentReceive =  (ModelIntent) getIntent().getSerializableExtra( "data" ) ;

        configRetrofit = getRetrofit();
        configRoomDatabase = new ConfigRoomDatabase( this );
        modelUser = new ModelUser();
        modelToggle = new ModelToggle();

        toggleSettingAutoLogin = 0;
        backKeyPressedTime = 0;

        functionActivityReceive();
        functionAutoLogin();
    }

    @Override
    protected void onStart() {

        super.onStart();

        bindingLogin.swAutoLogin.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if( b == true ) {

                    toggleSettingAutoLogin = 1;
                } else {

                    toggleSettingAutoLogin = 0;
                }
            }
        } );

        bindingLogin.btnLogin.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                String loginEmail = bindingLogin.etEmail.getText().toString().trim();
                String loginPassword = bindingLogin.etPassword.getText().toString().trim();

                if( functionCheckInformation( loginEmail , loginPassword ) == true ) {

                    functionUiSuccess();
                    functionLogin( loginEmail , loginPassword );
                } else {

                    functionUiFail();
                    functionShowLog( "blank on here" );
                    functionShowMessage( "빈칸이 없도록 적어주세요" );
                }
            }
        } );

        bindingLogin.btnRegister.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                intentSend.setIntentToActivity( "activityRegister" );
                intentSend.setIntentData1( null );
                functionActivitySend();
            }
        } );

        bindingLogin.tvFindAccount.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                intentSend.setIntentToActivity( "activityFind" );
                intentSend.setIntentData1( null );
                functionActivitySend();
            }
        } );
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

        bindingLogin = null;

        intent = null;
        intentSend = null;
        intentReceive = null;

        configRoomDatabase = null;
        modelUser = null;
        modelToggle = null;
    }

    public void functionAutoLogin() {

        functionUiSuccess();

        modelToggle = configRoomDatabase.functionToggleSelect();

        if( modelToggle.getToggleValue() == 1 ) {

            modelUser = configRoomDatabase.functionUserSelect();
            functionLogin( modelUser.getUserEmail() , modelUser.getUserPassword() );
        } else {

            functionUiFail();
            configRoomDatabase.functionToggleUpdate( new ModelToggle() );
        }
    }

    public void functionLogin( String loginEmail , String loginPassword ) {

        ModelUserPayload payloadLogin = new ModelUserPayload();
        ModelUser userLogin = new ModelUser();

        userLogin.setUserEmail( loginEmail );
        userLogin.setUserPassword( loginPassword );

        payloadLogin.setModelUser( userLogin );

        configRetrofit.getFunctionRestApi().apiLogin( payloadLogin ).enqueue( new Callback<ModelUserPayload>() {

            @Override
            public void onResponse(Call<ModelUserPayload> call , Response<ModelUserPayload> response ) {

                ModelUserPayload payloadResult = response.body();
                ModelUser userResult = payloadResult.getModelUser();

                functionShowLog( "==============================================" );
                functionShowLog( "code : " + payloadResult.getCode());
                functionShowLog( "msg : " + payloadResult.getMsg() );
                functionShowLog( "modelUser.userId : " + userResult.getUserId() );
                functionShowLog( "modelUser.userPassword : " + userResult.getUserPassword() );
                functionShowLog( "modelUser.userName : " + userResult.getUserName() );
                functionShowLog( "modelUser.userEmail : " + userResult.getUserEmail() );
                functionShowLog( "modelUser.userRegisterDate : " + userResult.getUserRegisterDate() );
                functionShowLog( "modelUser.userStatus : " + userResult.getUserStatus() );
                functionShowLog( "modelUser.userImage : " + userResult.getUserImage() );
                functionShowLog( "==============================================" );

                switch( payloadResult.getCode() ) {

                    case 100 :
                    case 101 :
                    case 102 :
                    case 103 :
                    case 104 :
                    case 105 :
                    case 106 :
                    case 107 :
                    case 108 :
                    case 109 :
                    case 110 :
                    case 111 :
                    case 112 :
                    case 113 :
                    case 114 :
                    case 115 : {

                        configRoomDatabase.functionToggleDelete();
                        configRoomDatabase.functionUserDelete();
                        configRoomDatabase.functionUserInsert( new ModelUser() );
                        configRoomDatabase.functionToggleInsert( new ModelToggle() );

                        functionShowMessage( "로그인 실패했어요" );
                        functionUiFail();
                        break;
                    }
                    case 200 :
                    case 201 :
                    case 202 :
                    case 203 :
                    case 204 :
                    case 205 :
                    case 206 :
                    case 207 : {


                        intentSend.setIntentToActivity( "activityMain" );
                        intentSend.setIntentData1( userResult );

                        configRoomDatabase.functionUserDelete();
                        configRoomDatabase.functionUserInsert( userResult );

                        if( configRoomDatabase.functionToggleSelect().getToggleValue() != 1 ) {

                            modelToggle.setToggleValue( toggleSettingAutoLogin );
                            configRoomDatabase.functionToggleDelete();
                            configRoomDatabase.functionToggleInsert( modelToggle );
                        }

                        functionShowMessage( "로그인 성공했어요" );
                        functionActivitySend();
                    }
                    break;
                    default: {

                        configRoomDatabase.functionToggleDelete();
                        configRoomDatabase.functionUserDelete();
                        configRoomDatabase.functionUserInsert( new ModelUser() );
                        configRoomDatabase.functionToggleInsert( new ModelToggle() );

                        functionShowMessage( "로그인 실패했어요" );
                        functionUiFail();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelUserPayload> call , Throwable t ) {

                functionShowLog(t.getMessage());

                configRoomDatabase.functionToggleDelete();
                configRoomDatabase.functionUserDelete();
                configRoomDatabase.functionUserInsert( new ModelUser() );
                configRoomDatabase.functionToggleInsert( new ModelToggle() );

                functionShowMessage( "서버와 연결이 안 되었어요" );
                functionShowLog( "not connected to the server" );
                functionUiFail();
            }
        } );
    }

    public void functionUiSuccess() {

        bindingLogin.etEmail.setEnabled( false );
        bindingLogin.etPassword.setEnabled( false );
        bindingLogin.swAutoLogin.setVisibility( View.INVISIBLE );
        bindingLogin.tvFindAccount.setVisibility( View.INVISIBLE );
        bindingLogin.btnRegister.setVisibility( View.INVISIBLE );
        bindingLogin.btnLogin.setVisibility( View.INVISIBLE );
        bindingLogin.pbProgressBar.setVisibility( View.VISIBLE );
    }

    public void functionUiFail() {

        bindingLogin.etEmail.setEnabled( true );
        bindingLogin.etPassword.setEnabled( true );
        bindingLogin.swAutoLogin.setVisibility( View.VISIBLE );
        bindingLogin.tvFindAccount.setVisibility( View.VISIBLE );
        bindingLogin.btnRegister.setVisibility( View.VISIBLE );
        bindingLogin.btnLogin.setVisibility( View.VISIBLE );
        bindingLogin.pbProgressBar.setVisibility( View.INVISIBLE );
    }

    public void functionShowMessage( String message ) {

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityLogin]" , message );
    }

    public boolean functionCheckInformation( String loginEmail , String loginPassword ) {

        if ( !TextUtils.isEmpty( loginEmail ) && !TextUtils.isEmpty( loginPassword ) ) {

            return true;
        } else {

            return false;
        }
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityLogin" : {

                functionShowLog( "move from activityLogin" );
                break;
            }

            case "activityRegister" : {

                functionShowLog( "move from activityRegister" );
                break;
            }

            case "activityFind" : {

                functionShowLog( "move from activityFind" );
                break;
            }

            case "activityFindEmail" : {

                functionShowLog( "move from activityFindEmail" );
                bindingLogin.etEmail.setText( intentReceive.getIntentData1().toString() );
                break;
            }

            case "activityFindPassword" : {

                functionShowLog( "move from activityFindPassword" );
                break;
            }

            case "activitySplash" : {

                functionShowLog( "move from activitySplash" );
                break;
            }

            case "activityProfile" : {

                functionShowLog( "move from activityProfile" );
                break;
            }

            case "activityMain" : {

                functionShowLog( "move from activityMain" );
                break;
            }

            case "activityQna" : {

                functionShowLog( "move from activityQna" );
                break;
            }

            case "activityDesk" : {

                functionShowLog( "move from activityDesk" );
                break;
            }

            case "activityChat" : {

                functionShowLog( "move from activityChat" );
                break;
            }

            case "activityChatCompany" : {

                functionShowLog( "move from activityChatCompany" );
                break;
            }

            case "activityCheckVisitorCompany" : {

                functionShowLog( "move from activityCheckVisitorCompany" );
                break;
            }

            default: {

                modelUser = (ModelUser) intentReceive.getIntentData1();
                break;
            }
        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityLogin": {

                intent = new Intent( this , ActivityLogin.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityRegister": {

                intent = new Intent( this , ActivityRegister.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityFind": {

                intent = new Intent( this , ActivityFind.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityMain": {

                intent = new Intent( this , ActivityMain.class );
                intent.putExtra( "data" , intentSend );
                break;
            }
        }

        startActivity( intent );
        finish();
    }
}
