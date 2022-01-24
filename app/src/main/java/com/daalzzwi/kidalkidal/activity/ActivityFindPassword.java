package com.daalzzwi.kidalkidal.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.databinding.ActivityFindPasswordBinding;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelUser;
import com.daalzzwi.kidalkidal.model.ModelUserPayload;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;

public class ActivityFindPassword extends AppCompatActivity {

    private ActivityFindPasswordBinding bindingFindPassword;

    private Intent intent;
    private ModelIntent intentSend;
    private ModelIntent intentReceive;

    private ConfigRetrofit configRetrofit;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingFindPassword = ActivityFindPasswordBinding.inflate( getLayoutInflater() );
        View view = bindingFindPassword.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityFindPassword" );
        intentReceive = (ModelIntent) getIntent().getSerializableExtra( "data" );

        configRetrofit = getRetrofit();

        functionActivityReceive();
    }

    @Override
    protected void onStart() {

        super.onStart();

        bindingFindPassword.pbProgressBar.setVisibility( View.INVISIBLE );

        bindingFindPassword.btnSubmit.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                String findpasswordEmail = bindingFindPassword.etEmail.getText().toString().trim();

                if( functionCheckInformation( findpasswordEmail ) == true ) {

                    functionUiSuccess();
                    functionFindPassword( findpasswordEmail );
                } else {

                    functionUiFail();
                    functionShowMessage( "빈칸이 없도록 적어주세요" );
                }
            }
        } );

        bindingFindPassword.btnLogin.setOnClickListener( new View.OnClickListener() {

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

        bindingFindPassword = null;

        intent = null;
        intentSend = null;
        intentReceive = null;
    }

    private void functionUiSuccess() {

        bindingFindPassword.etEmail.setEnabled( false );
        bindingFindPassword.btnSubmit.setVisibility( View.INVISIBLE );
        bindingFindPassword.btnLogin.setVisibility( View.INVISIBLE );
        bindingFindPassword.pbProgressBar.setVisibility( View.VISIBLE );
    }

    private void functionUiFail() {

        bindingFindPassword.etEmail.setEnabled( true );
        bindingFindPassword.btnSubmit.setVisibility( View.VISIBLE );
        bindingFindPassword.btnLogin.setVisibility( View.VISIBLE );
        bindingFindPassword.pbProgressBar.setVisibility( View.INVISIBLE );
    }

    private void functionShowMessage( String message ) {

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityFindPassword]" , message );
    }

    public boolean functionCheckInformation( String findpasswordEmail ) {

        if ( !TextUtils.isEmpty( findpasswordEmail ) ) {

            return true;
        } else {

            return false;
        }
    }

    private void functionFindPassword( String findpasswordEmail ) {

        ModelUser userFindPassword = new ModelUser();
        ModelUserPayload payloadFindPassword = new ModelUserPayload();


        userFindPassword.setUserEmail( findpasswordEmail );
        payloadFindPassword.setModelUser( userFindPassword );

        configRetrofit.getFunctionRestApi().apiFindPassword( payloadFindPassword ).enqueue( new Callback<ModelUserPayload>() {

            @Override
            public void onResponse(Call<ModelUserPayload> call , Response<ModelUserPayload> response ) {

                ModelUserPayload payloadResult = response.body();
                ModelUser userResult = payloadResult.getModelUser();

                functionShowLog( "==============================================" );
                functionShowLog( "code : " + payloadResult.getCode() );
                functionShowLog( "msg : " + payloadResult.getMsg() );
                functionShowLog( "modelUser : " + userResult );
                functionShowLog( "==============================================" );

                switch( payloadResult.getCode() ) {

                    case 100 : {

                        functionShowMessage( "비밀번호를 지정된 이메일로 발송못했어요" );
                        functionUiFail();
                        break;
                    }

                    case 101 : {

                        functionShowMessage( "미등록 계정이에요" );
                        functionUiFail();
                        break;
                    }

                    case 200 : {

                        functionShowMessage( "비밀번호를 지정된 이메일로 발송했어요" );
                        intentSend.setIntentToActivity( "activityLogin" );
                        intentSend.setIntentData1( null );
                        functionActivitySend();
                        break;
                    }

                    default : {

                        functionUiFail();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelUserPayload> call, Throwable t ) {

                functionShowMessage( "서버와 연결이 안 되었어요" );
                functionShowLog( "not connected to the server" );
                functionUiFail();
            }
        } );
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityFindPassword" : {

                functionShowLog( "move from activityFindPassword" );
                break;
            }

            case "activityFind" : {

                functionShowLog( "move from activityFind" );
                break;
            }

            default : {

                break;
            }
        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityFindPassword" : {

                intent = new Intent( this, ActivityFindPassword.class );
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