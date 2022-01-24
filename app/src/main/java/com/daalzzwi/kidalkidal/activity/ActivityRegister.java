package com.daalzzwi.kidalkidal.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.databinding.ActivityRegisterBinding;
import com.daalzzwi.kidalkidal.function.FunctionConverter;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelUser;
import com.daalzzwi.kidalkidal.model.ModelUserPayload;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;

public class ActivityRegister extends AppCompatActivity {

    private ActivityRegisterBinding bindingRegister;

    private Intent intent;
    private ModelIntent intentSend;
    private ModelIntent intentReceive;

    private ConfigRetrofit configRetrofit;

    private FunctionConverter converter;

    private ActivityResultLauncher< Intent > resultFindProfileImage;
    private Bitmap imageProfile;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingRegister = ActivityRegisterBinding.inflate( getLayoutInflater() );
        View view = bindingRegister.getRoot();
        setContentView( view );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        intent = new Intent();
        intentSend = new ModelIntent();
        intentReceive = new ModelIntent();
        intentSend.setIntentFromActivity( "activityRegister" );
        intentReceive = (ModelIntent) getIntent().getSerializableExtra( "data" );

        configRetrofit = getRetrofit();

        converter = new FunctionConverter();

        resultFindProfileImage = null;
        imageProfile = null;

        functionActivityReceive();
        functionPickFromGallerySet();
    }

    @Override
    protected void onStart() {

        super.onStart();

        bindingRegister.pbProgressBar.setVisibility( View.INVISIBLE );

        bindingRegister.btnLogin.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                intentSend.setIntentToActivity( "activityLogin" );
                intentSend.setIntentData1( null );
                functionActivitySend();
            }
        } );

        bindingRegister.btnRegister.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                String registerName = bindingRegister.etName.getText().toString().trim();
                String registerEmail = bindingRegister.etEmail.getText().toString().trim();
                String registerPassword1 = bindingRegister.etPasswordOld.getText().toString().trim();
                String registerPassword2 = bindingRegister.etPasswordNew.getText().toString().trim();
                Bitmap registerProfileImage = imageProfile;

                functionUiFail();

                if( functionCheckInformation( registerName , registerEmail , registerPassword1 , registerPassword2 ) == true ) {

                    if( registerProfileImage == null ) {

                        FileInputStream fis;
                        BufferedInputStream bis;

                        try {

                            AssetManager assetManager = getAssets();
                            bis = new BufferedInputStream( assetManager.open("logo.png") );

                            Bitmap imageGet = BitmapFactory.decodeStream( bis );
                            imageProfile = functionImageCrop( imageGet );
                        } catch ( Exception e ) {

                            e.printStackTrace();
                        }
                    }

                    functionUiSuccess();
                    functionRegister( registerName , registerEmail , registerPassword1 , imageProfile );
                } else {

                    functionUiFail();
                }
            }
        } );

        bindingRegister.tvFindProfileImage.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                functionPickFromGalleryExecute();
            }
        } );
    }

    @Override
    public void onBackPressed() {

        intentSend.setIntentToActivity( "activityLogin" );
        intentSend.setIntentData1( null );
        functionActivitySend();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        bindingRegister = null;

        intent = null;
        intentSend = null;
        intentReceive = null;

        converter = null;

        resultFindProfileImage = null;
        imageProfile = null;
    }

    private void functionRegister( String registerName , String registerEmail , String registerPassword , Bitmap registerProfileImage) {

        ModelUser userRegister = new ModelUser();
        ModelUserPayload payloadRegister = new ModelUserPayload();

        String byteConverted = converter.functionBitmapToString( registerProfileImage );

        userRegister.setUserName( registerName );
        userRegister.setUserEmail( registerEmail );
        userRegister.setUserPassword( registerPassword );
        userRegister.setUserImage( byteConverted );

        payloadRegister.setModelUser( userRegister );

        configRetrofit.getFunctionRestApi().apiRegister( payloadRegister ).enqueue( new Callback<ModelUserPayload>() {

            @Override
            public void onResponse( Call< ModelUserPayload > call, Response< ModelUserPayload > response ) {

                ModelUserPayload payloadResult = response.body();
                ModelUser userResult = payloadResult.getModelUser();

                functionShowLog( "==============================================" );
                functionShowLog( "code : " + payloadResult.getCode() );
                functionShowLog( "msg : " + payloadResult.getMsg() );
                functionShowLog( "modelUser : " + userResult );
                functionShowLog( "==============================================" );

                switch( payloadResult.getCode() ) {

                    case 100 : {

                        functionShowMessage( "이미 가입된 계정이에요" );
                        functionUiFail();
                        break;
                    }
                    case 101 : {

                        functionShowMessage( "회원가입 실패했어요" );
                        functionUiFail();
                        break;
                    }
                    case 200 :
                    case 201 : {

                        intentSend.setIntentToActivity( "activityLogin" );
                        intentSend.setIntentData1( null );

                        functionShowMessage( "회원가입 성공했어요" );
                        functionActivitySend();
                        break;
                    }
                    default : {

                        functionShowMessage( "회원가입 실패했어요" );
                        functionUiFail();
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

    private void functionUiSuccess() {

        bindingRegister.etName.setEnabled( false );
        bindingRegister.etEmail.setEnabled( false );
        bindingRegister.etPasswordOld.setEnabled( false );
        bindingRegister.etPasswordNew.setEnabled( false );
        bindingRegister.btnRegister.setVisibility( View.INVISIBLE );
        bindingRegister.btnLogin.setVisibility( View.INVISIBLE );
        bindingRegister.ivImageSelected.setVisibility( View.INVISIBLE );
        bindingRegister.pbProgressBar.setVisibility( View.VISIBLE );
    }

    private void functionUiFail() {

        bindingRegister.etName.setEnabled( true );
        bindingRegister.etEmail.setEnabled( true );
        bindingRegister.etPasswordOld.setEnabled( true );
        bindingRegister.etPasswordNew.setEnabled( true );
        bindingRegister.btnRegister.setVisibility( View.VISIBLE );
        bindingRegister.btnLogin.setVisibility( View.VISIBLE );
        bindingRegister.ivImageSelected.setVisibility( View.VISIBLE );
        bindingRegister.pbProgressBar.setVisibility( View.INVISIBLE );
    }

    public void functionPickFromGallerySet() {

        resultFindProfileImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {

                    @Override
                    public void onActivityResult( ActivityResult result ) {

                        if( result.getResultCode() == Activity.RESULT_OK ) {

                            if( result.getData() != null ) {

                                functionShowMessage( "사진을 선택 했어요" );
                                functionShowLog( "chose the picture" );

                                try {

                                    InputStream in = getContentResolver().openInputStream( result.getData().getData() );
                                    Bitmap imageGet = BitmapFactory.decodeStream( in );
                                    imageProfile = functionImageCrop( imageGet );
                                    bindingRegister.ivImageSelected.setImageBitmap( imageProfile );
                                } catch (FileNotFoundException e) {

                                    e.printStackTrace();
                                }
                            }
                        } else {

                            functionShowMessage( "사진을 선택 안했어요" );
                            functionShowLog( "didn't choose the picture " );
                            return;
                        }
                    }
                } );
    }

    public void functionPickFromGalleryExecute() {

        intent = new Intent( Intent.ACTION_PICK );
        intent.setType( "image/*" );
        String[] imageType = { "image/jpg" , "image/png" };
        intent.putExtra( Intent.EXTRA_MIME_TYPES , imageType );
        resultFindProfileImage.launch( intent );
    }

    public Bitmap functionImageCrop( Bitmap data ) {

        Bitmap dataProcessed;
        Canvas canvas;
        Paint paint;
        int size;
        Rect rect;

        dataProcessed = Bitmap.createBitmap( data.getWidth() , data.getHeight() , Bitmap.Config.ARGB_8888 );
        canvas = new Canvas( dataProcessed );
        paint = new Paint();
        rect = new Rect( 0 , 0 , data.getWidth() , data.getHeight() );

        size = ( data.getWidth() / 2 );
        paint.setAntiAlias( true );
        canvas.drawARGB( 0 ,0 ,0 ,0 );
        canvas.drawCircle( size , size , size , paint );
        paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.SRC_IN ) );
        canvas.drawBitmap( data , rect ,rect , paint );

        return dataProcessed;
    }

    public void functionShowMessage( String message ) {

        Toast.makeText( ActivityRegister.this,message,Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityRegister]" , message );
    }

    public boolean functionCheckInformation( String registerName , String registerEmail , String registerPassword1 , String registerPassword2 ) {

        boolean checkEmpty = false;
        boolean checkName = false;
        boolean checkEmail = false;
        boolean checkPassword = false;
        boolean checkPasswordMatch = false;
        boolean checkFinal = false;

        boolean matcherName = Pattern.matches( "[a-zA-Zㄱ-ㅎ|ㅏ-ㅣ|가-힣0-9]{2,12}$" , registerName );
        boolean matcherEmail = Pattern.matches( "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$" , registerEmail );
        boolean matcherPassword1 = Pattern.matches( "[a-zA-Z0-9]{8,20}$" , registerPassword1 );
        boolean matcherPassword2= Pattern.matches( "[a-zA-Z0-9]{8,20}$" , registerPassword2 );

        if( !TextUtils.isEmpty( registerName ) && !TextUtils.isEmpty( registerEmail ) && !TextUtils.isEmpty( registerPassword1 ) && !TextUtils.isEmpty( registerPassword2 ) ) {

            checkEmpty = true;
        } else {

            checkEmpty = false;
            functionShowLog( "blank on here" );
            functionShowMessage( "빈칸이 없도록 적어주세요" );
        }

        if( matcherName == true ) {

            checkName = true;
        } else {

            checkName = false;
            functionShowLog( "the format of the name is wrong" );
            functionShowMessage( "이름은 특수문자 제외하고\n 최소 2글자부터 12글자까지 적어주세요" );
        }

        if( matcherEmail == true ) {

            checkEmail = true;
        } else {

            checkEmail = false;
            functionShowLog( "the format of the email is wrong" );
            functionShowMessage( "이메일은 ***@***.com 처럼 적어주세요" );
        }

        if( matcherPassword1 == true && matcherPassword2 == true ) {

            checkPassword = true;
        } else {

            checkPassword = false;
            functionShowLog( "the format of the password is wrong" );
            functionShowMessage( "비밀번호는 특수문자 제외하고\n 최소 8글자부터 20글자까지 적어주세요" );
        }

        if ( registerPassword1.equals( registerPassword2 ) ) {

            checkPasswordMatch = true;
        } else {

            checkPasswordMatch = false;
            functionShowLog( "the password doesn't match" );
            functionShowMessage( "비밀번호가 서로 일치하는지 확인주세요" );
        }

        if( checkEmpty == true && checkName == true && checkEmail == true && checkPassword == true && checkPasswordMatch == true ) {

            checkFinal = true;
        } else {

            checkFinal = false;
        }

        return checkFinal;
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

            case "activityRegister" : {

                functionShowLog("move from activityRegister");
                break;
            }

            case "activityLogin" : {

                functionShowLog("move from activityLogin");
                break;
            }

            case "activitySplash" : {

                functionShowLog("move from activitySplash");
                break;
            }

            default : {

                break;
            }
        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityRegister" : {

                intent = new Intent( this , ActivityRegister.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityLogin" : {

                intent = new Intent( this , ActivityLogin.class );
                intent.putExtra( "data" , intentSend );
                break;
            }
        }

        startActivity( intent );
        finish();
    }
}