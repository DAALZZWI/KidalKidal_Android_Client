package com.daalzzwi.kidalkidal.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.databinding.ActivityFindEmailBinding;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelUser;
import com.daalzzwi.kidalkidal.model.ModelUserPayload;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;

public class ActivityFindEmail extends AppCompatActivity {

    private ActivityFindEmailBinding bindingFindEmail;

    private Intent intent;
    private ModelIntent intentSend;
    private ModelIntent intentReceive;

    private ConfigRetrofit configRetrofit;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingFindEmail = ActivityFindEmailBinding.inflate( getLayoutInflater() );
        View view = bindingFindEmail.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityFindEmail" );
        intentReceive = ( ModelIntent ) getIntent().getSerializableExtra( "data" );

        configRetrofit = getRetrofit();

        functionActivityReceive();
    }

    @Override
    protected void onStart() {

        super.onStart();

        bindingFindEmail.pbProgressBar.setVisibility( View.INVISIBLE );

        bindingFindEmail.btnSubmit.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                String findEmailName = bindingFindEmail.etName.getText().toString().trim();

                if( functionCheckInformation( findEmailName ) == true ) {

                    functionUiSuccess();
                    functionFindEmail( findEmailName );
                } else {

                    functionUiFail();
                    functionShowMessage( "빈칸이 없도록 적어주세요" );
                }
            }
        } );

        bindingFindEmail.btnLogin.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                intentSend.setIntentToActivity( "activityLogin" );
                intentSend.setIntentData1( null );
                functionActivitySend();
            }
        } );
    }

    @Override
    public void onBackPressed() {

        intentSend.setIntentToActivity( "activityFind" );
        intentSend.setIntentData1( null );
        functionActivitySend();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        bindingFindEmail = null;

        intent = null;
        intentSend = null;
        intentReceive = null;
    }

    private void functionUiSuccess() {

        bindingFindEmail.etName.setEnabled( false );
        bindingFindEmail.btnSubmit.setVisibility( View.INVISIBLE );
        bindingFindEmail.btnLogin.setVisibility( View.INVISIBLE );
        bindingFindEmail.pbProgressBar.setVisibility( View.VISIBLE );
    }

    private void functionUiFail() {

        bindingFindEmail.etName.setEnabled( true );
        bindingFindEmail.btnSubmit.setVisibility( View.VISIBLE );
        bindingFindEmail.btnLogin.setVisibility( View.VISIBLE );
        bindingFindEmail.pbProgressBar.setVisibility( View.INVISIBLE );
    }

    private void functionShowMessage( String message ) {

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityFindEmail]" , message );
    }

    public boolean functionCheckInformation( String findemailName ) {

        if ( !TextUtils.isEmpty( findemailName ) ) {

            return true;
        } else {

            return false;
        }
    }

    private void functionFindEmail( String findemailName ) {

        ModelUser userFindEmail = new ModelUser();
        ModelUserPayload payloadFindEmail = new ModelUserPayload();


        userFindEmail.setUserName( findemailName );
        payloadFindEmail.setModelUser( userFindEmail );

        configRetrofit.getFunctionRestApi().apiFindEmail( payloadFindEmail ).enqueue( new Callback< ModelUserPayload >() {

            @Override
            public void onResponse( Call< ModelUserPayload > call , Response< ModelUserPayload > response ) {

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
                functionShowLog( "==============================================" );

                switch( payloadResult.getCode() ) {

                    case 100 : {

                        functionUiFail();
                        functionShowMessage( "해당 이름을 가진 계정이 없어요" );
                        break;
                    }

                    case 200 : {

                        functionShowMessage( "이메일 : " + userResult.getUserEmail() );

                        intentSend.setIntentToActivity( "activityLogin" );
                        intentSend.setIntentData1( userResult.getUserEmail() );
                        functionActivitySend();
                        break;
                    }
                    default: functionUiFail();
                }
            }

            @Override
            public void onFailure( Call< ModelUserPayload > call , Throwable t ) {

                functionShowMessage( "서버와 연결이 안 되었어요" );
                functionShowLog( "not connected to the server" );
                functionUiFail();
            }
        } );
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityFindEmail" : {

                functionShowLog( "move from activityFindEmail" );
                break;
            }

            case "activityFind" : {

                functionShowLog( "move from activityFind");
                break;
            }

            default : {

                break;
            }
        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityFindEmail" : {

                intent = new Intent( this, ActivityFindEmail.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityFind" : {

                intent = new Intent( this, ActivityFind.class );
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