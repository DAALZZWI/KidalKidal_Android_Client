package com.daalzzwi.kidalkidal.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daalzzwi.kidalkidal.R;
import com.daalzzwi.kidalkidal.adapter.AdapterDialog;
import com.daalzzwi.kidalkidal.config.ConfigRetrofit;
import com.daalzzwi.kidalkidal.config.ConfigRoomDatabase;
import com.daalzzwi.kidalkidal.databinding.ActivityProfileBinding;
import com.daalzzwi.kidalkidal.function.FunctionConverter;
import com.daalzzwi.kidalkidal.model.ModelIntent;
import com.daalzzwi.kidalkidal.model.ModelToggle;
import com.daalzzwi.kidalkidal.model.ModelUser;
import com.daalzzwi.kidalkidal.model.ModelUserPayload;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.daalzzwi.kidalkidal.config.ConfigRetrofit.getRetrofit;

public class ActivityProfile extends AppCompatActivity {

    private ActivityProfileBinding bindingProfile;

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

    private ActivityResultLauncher< Intent > resultFindProfileImage;
    private Bitmap imageProfile;
    private String tempName;
    private String tempEmail;
    private String tempPasswordOld;
    private String tempPasswordNew;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        bindingProfile = ActivityProfileBinding.inflate( getLayoutInflater() );
        View view = bindingProfile.getRoot();
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

        resultFindProfileImage = null;
        imageProfile = null;
        tempName = null;
        tempEmail = null;
        tempPasswordOld = null;
        tempPasswordNew = null;

        functionActivityReceive();
        functionPickFromGallerySet();
    }

    @Override
    protected void onStart() {

        super.onStart();

        functionUiModeRead();
        functionUiSetInit();

        bindingProfile.pbProgressBar.setVisibility( View.INVISIBLE );

        bindingProfile.btnEditProfile.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                functionUiEditWrite();
                functionUiSetEdit();
            }
        } );

        bindingProfile.btnMain.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                intentSend.setIntentToActivity( "activityMain" );
                intentSend.setIntentData1( modelUser );
                functionActivitySend();
            }
        } );

        bindingProfile.btnSave.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {

                String profileName = bindingProfile.etName.getText().toString().trim();
                String profileEmail = bindingProfile.etEmail.getText().toString().trim();
                String profilePasswordOld = bindingProfile.etPasswordOld.getText().toString().trim();
                String profilePasswordNew = bindingProfile.etPasswordNew.getText().toString().trim();

                functionUiFail();

                if( functionCheckInformation( profileName , profileEmail , profilePasswordOld , profilePasswordNew ) == true ) {

                    functionUiSuccess();

                    if( TextUtils.isEmpty( profilePasswordOld ) || TextUtils.isEmpty( profilePasswordNew ) ) {

                        profilePasswordNew = modelUser.getUserPassword();
                    }

                    functionProfile( profileName , profileEmail , profilePasswordNew );
                } else {

                    functionUiFail();
                }
            }
        } );

        bindingProfile.btnCancel.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                functionUiModeRead();
                functionUiSetInit();
            }
        } );

        bindingProfile.titleBar.ivProfile.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                functionDialog();
            }
        } );

        bindingProfile.btnUnRegister.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                functionUiSuccess();
                functionUnRegister();
            }
        } );

        bindingProfile.tvFindProfileImage.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                functionPickFromGalleryExecute();
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

        bindingProfile = null;

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

                    default: {

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
        configRoomDatabase.functionUserUpdate( new ModelUser());

        intentSend.setIntentToActivity( "activityLogin" );
        intentSend.setIntentData1( null );
        functionActivitySend();
    }

    public void functionUiSuccess() {

        bindingProfile.etName.setEnabled( false );
        bindingProfile.etEmail.setEnabled( false );
        bindingProfile.etPasswordOld.setEnabled( false );
        bindingProfile.etPasswordNew.setEnabled( false );

        bindingProfile.tvFindProfileImage.setVisibility( View.INVISIBLE );
        bindingProfile.ivImageSelected.setVisibility( View.INVISIBLE );

        bindingProfile.btnCancel.setVisibility( View.INVISIBLE );
        bindingProfile.btnSave.setVisibility( View.INVISIBLE );
        bindingProfile.btnUnRegister.setVisibility( View.INVISIBLE );
        bindingProfile.pbProgressBar.setVisibility( View.VISIBLE );
    }

    public void functionUiFail() {

        bindingProfile.etName.setEnabled( true );
        bindingProfile.etEmail.setEnabled( false );
        bindingProfile.etPasswordOld.setEnabled( true );
        bindingProfile.etPasswordNew.setEnabled( true );

        bindingProfile.tvFindProfileImage.setVisibility( View.VISIBLE );
        bindingProfile.ivImageSelected.setVisibility( View.VISIBLE );

        bindingProfile.btnCancel.setVisibility( View.VISIBLE );
        bindingProfile.btnSave.setVisibility( View.VISIBLE );
        bindingProfile.btnUnRegister.setVisibility( View.VISIBLE );
        bindingProfile.pbProgressBar.setVisibility( View.INVISIBLE );
    }

    public void functionUiEditWrite() {

        bindingProfile.etName.setEnabled( true );
        bindingProfile.etEmail.setEnabled( false );
        bindingProfile.etPasswordOld.setEnabled( true );
        bindingProfile.etPasswordNew.setEnabled( true );

        bindingProfile.tvFindProfileImage.setVisibility( View.INVISIBLE );
        bindingProfile.ivImageSelected.setVisibility( View.INVISIBLE );

        bindingProfile.btnCancel.setVisibility( View.VISIBLE );
        bindingProfile.btnSave.setVisibility( View.VISIBLE );
        bindingProfile.btnEditProfile.setVisibility( View.INVISIBLE );
        bindingProfile.btnMain.setVisibility( View.INVISIBLE );
        bindingProfile.btnUnRegister.setVisibility( View.VISIBLE );
        bindingProfile.tvTitle.setText( "프로필 수정하기" );
    }

    public void functionUiModeRead() {

        bindingProfile.etName.setEnabled( false );
        bindingProfile.etEmail.setEnabled( false );
        bindingProfile.etPasswordOld.setEnabled( false );
        bindingProfile.etPasswordNew.setEnabled( false );

        bindingProfile.tvFindProfileImage.setVisibility( View.INVISIBLE );
        bindingProfile.ivImageSelected.setVisibility( View.INVISIBLE );

        bindingProfile.btnCancel.setVisibility( View.INVISIBLE );
        bindingProfile.btnSave.setVisibility( View.INVISIBLE );
        bindingProfile.btnEditProfile.setVisibility( View.VISIBLE );
        bindingProfile.btnMain.setVisibility( View.VISIBLE );
        bindingProfile.btnUnRegister.setVisibility( View.INVISIBLE );
        bindingProfile.tvTitle.setText( "프로필 보기" );
    }

    public void functionUiSetInit() {

        bindingProfile.etPasswordOld.setTransformationMethod(null);
        bindingProfile.etPasswordNew.setTransformationMethod(null);

        bindingProfile.etName.setText( "이름 : " + modelUser.getUserName() );
        bindingProfile.etEmail.setText( "이메일 : " + modelUser.getUserEmail() );
        bindingProfile.etPasswordOld.setText( "비밀번호 : " + "********************" );
        bindingProfile.etPasswordNew.setText( "가입일 : " + modelUser.getUserRegisterDate()  );

        bindingProfile.tvFindProfileImage.setVisibility( View.INVISIBLE );
        bindingProfile.ivImageSelected.setVisibility( View.VISIBLE );

        bindingProfile.ivImageSelected.setImageBitmap( converter.functionStringToBitmap( modelUser.getUserImage() ) );
    }

    public void functionUiSetEdit() {

        bindingProfile.etPasswordOld.setTransformationMethod( PasswordTransformationMethod.getInstance() );
        bindingProfile.etPasswordNew.setTransformationMethod( PasswordTransformationMethod.getInstance() );

        bindingProfile.etName.setText( modelUser.getUserName() );
        bindingProfile.etEmail.setText( modelUser.getUserEmail() );
        bindingProfile.etPasswordOld.setText( "" );
        bindingProfile.etPasswordNew.setText( "" );

        bindingProfile.tvFindProfileImage.setVisibility( View.VISIBLE );
        bindingProfile.ivImageSelected.setVisibility( View.VISIBLE );
    }

    public void functionUiRestore() {

        functionUiEditWrite();
        functionUiSetEdit();

        bindingProfile.etName.setText( tempName );
        bindingProfile.etEmail.setText( tempEmail );
        bindingProfile.etPasswordOld.setText( tempPasswordOld );
        bindingProfile.etPasswordNew.setText( tempPasswordNew );

        tempName = null;
        tempEmail = null;
        tempPasswordOld = null;
        tempPasswordNew = null;
    }

    public void functionPickFromGallerySet() {

        resultFindProfileImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback< ActivityResult >() {

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
                                    bindingProfile.ivImageSelected.setImageBitmap( imageProfile );
                                } catch (FileNotFoundException e) {

                                    e.printStackTrace();
                                }

                                functionUiRestore();
                            }
                        } else {

                            functionShowMessage( "사진을 선택 안했어요" );
                            functionShowLog( "didn't choose the picture " );
                            functionUiRestore();
                            return;
                        }
                    }
                } );
    }

    public void functionPickFromGalleryExecute() {

        tempName = bindingProfile.etName.getText().toString().trim();
        tempEmail = bindingProfile.etEmail.getText().toString().trim();
        tempPasswordOld = bindingProfile.etPasswordOld.getText().toString().trim();
        tempPasswordNew = bindingProfile.etPasswordNew.getText().toString().trim();

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

        Toast.makeText( getApplicationContext() , message , Toast.LENGTH_SHORT ).show();
    }

    public void functionShowLog( String message ) {

        Log.i( "[activityProfile]" , message );
    }

    public void functionUnRegister() {

        ModelUser userProfile = (ModelUser) modelUser.clone();
        ModelUserPayload payloadProfile = new ModelUserPayload();


        payloadProfile.setModelUser( userProfile );

        configRetrofit.getFunctionRestApi().apiUnRegister(payloadProfile).enqueue( new Callback< ModelUserPayload >() {

            @Override
            public void onResponse( Call< ModelUserPayload > call, Response< ModelUserPayload > response) {

                ModelUserPayload payloadResult = response.body();
                ModelUser userResult = payloadResult.getModelUser();

                functionShowLog( "==============================================" );
                functionShowLog( "code : " + payloadResult.getCode() );
                functionShowLog( "msg : " + payloadResult.getMsg() );
                functionShowLog( "modelUser : " + userResult );
                functionShowLog( "==============================================" );

                switch( payloadResult.getCode() ) {

                    case 100: {

                        functionShowMessage("회원탈퇴 실패했어요");
                        functionUiFail();
                        break;
                    }

                    case 101:
                    case 200:
                    case 201: {

                        intentSend.setIntentToActivity("activityLogin");
                        intentSend.setIntentData1(null);

                        functionShowMessage("회원탈퇴 성공했어요");
                        functionUserLogout();
                        break;
                    }

                    default: {

                        functionShowMessage("회원탈퇴 실패했어요");
                        functionUiFail();
                        break;
                    }
                }
            }

            @Override
            public void onFailure( Call< ModelUserPayload > call, Throwable t ) {

                functionShowMessage( "서버와 연결이 안 되었어요");
                functionShowLog( "not connected to the server");
                functionUiFail();
            }
        });
    }

    public void functionProfile( String profileName , String profileEmail , String profilePassword ) {

        ModelUser userProfile = (ModelUser) modelUser.clone();
        ModelUserPayload payloadProfile = new ModelUserPayload();


        userProfile.setUserName( profileName );
        userProfile.setUserEmail( profileEmail );
        userProfile.setUserPassword( profilePassword );

        if( imageProfile != null ) {

            userProfile.setUserImage( converter.functionBitmapToString( imageProfile ) );
        }

        payloadProfile.setModelUser( userProfile );

        configRetrofit.getFunctionRestApi().apiProfile( payloadProfile ).enqueue( new Callback< ModelUserPayload >() {

            @Override
            public void onResponse( Call< ModelUserPayload > call, Response< ModelUserPayload > response ) {

                ModelUserPayload payloadResult = response.body();
                ModelUser userResult = payloadResult.getModelUser();

                functionShowLog( "==============================================" );
                functionShowLog( "code : " + payloadResult.getCode() );
                functionShowLog( "msg : " + payloadResult.getMsg() );
                functionShowLog( "modelUser.userId : " + userResult.getUserId() );
                functionShowLog( "modelUser.userPassword : " + userResult.getUserPassword() );
                functionShowLog( "modelUser.userName : " + userResult.getUserName() );
                functionShowLog( "modelUser.userEmail : " + userResult.getUserEmail() );
                functionShowLog( "modelDesk.userImage : " + userResult.getUserImage() );
                functionShowLog( "==============================================" );

                switch( payloadResult.getCode() ) {

                    case 100 : {

                        functionShowMessage( "미 가입된 계정이에요" );
                        functionUiFail();
                        break;
                    }

                    case 101 :
                    case 102 :
                    case 103 :
                    case 104 : {

                        functionShowMessage( "프로필 편집 실패했어요" );
                        functionUiFail();
                        break;
                    }

                    case 200 : {

                        configRoomDatabase.functionUserDelete();
                        configRoomDatabase.functionUserInsert( userResult );

                        intentSend.setIntentToActivity( "activityProfile" );
                        intentSend.setIntentData1( userResult );

                        functionShowMessage( "프로필 편집 성공했어요" );
                        functionUiFail();
                        functionActivitySend();
                        break;
                    }

                    default: {

                        functionShowMessage( "프로필 편집 실패했어요" );
                        functionUiFail();
                    }
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

    public boolean functionCheckInformation( String profileName , String profileEmail , String profilePasswordOld , String profilePasswordNew ) {

        boolean checkEmpty = false;
        boolean checkName = false;
        boolean checkPasswordOld = false;
        boolean checkPasswordNew = false;
        boolean checkPasswordMatch = false;
        boolean checkFinal = false;

        boolean matcherName = Pattern.matches( "[a-zA-Zㄱ-ㅎ|ㅏ-ㅣ|가-힣0-9]{2,12}$" , profileName );
        boolean matcherPasswordOld = Pattern.matches( "[a-zA-Z0-9]{8,20}$" , profilePasswordOld );
        boolean matcherPasswordNew = Pattern.matches( "[a-zA-Z0-9]{8,20}$" , profilePasswordNew );

        if( !TextUtils.isEmpty( profilePasswordOld ) || !TextUtils.isEmpty( profilePasswordNew ) ) {

            if( !TextUtils.isEmpty( profileName ) && !TextUtils.isEmpty( profileEmail ) && !TextUtils.isEmpty( profilePasswordOld ) && !TextUtils.isEmpty( profilePasswordNew ) ) {

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

            if( matcherPasswordOld == true ) {

                checkPasswordOld = true;
            } else {

                checkPasswordOld = false;
                functionShowLog( "the format of the Old password is wrong" );
                functionShowMessage( "오래된 비밀번호는 특수문자 제외하고\n 최소 8글자부터 20글자까지 적어주세요" );
            }

            if( matcherPasswordNew == true ) {

                checkPasswordNew = true;
            } else {

                checkPasswordNew = false;
                functionShowLog( "the format of the Old password is wrong" );
                functionShowMessage( "새로운 비밀번호는 특수문자 제외하고\n 최소 8글자부터 20글자까지 적어주세요" );
            }

            if( profilePasswordOld.equals( modelUser.getUserPassword() ) ) {

                checkPasswordMatch = true;
            } else {

                checkPasswordMatch = false;
                functionShowLog( "the format of the Old password is wrong" );
                functionShowMessage( "오래된 비밀번호가 맞는지 확인 해 주세요" );
            }

            if( checkEmpty == true && checkName == true && checkPasswordOld == true && checkPasswordNew == true && checkPasswordMatch == true ) {

                checkFinal = true;
            } else {

                checkFinal = false;
            }

            return checkFinal;
        } else {

            if( !TextUtils.isEmpty( profileName ) && !TextUtils.isEmpty( profileEmail ) ) {

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

            if( checkEmpty == true && checkName == true ) {

                checkFinal = true;
            } else {

                checkFinal = false;
            }

            return checkFinal;
        }
    }

    public void functionTitleBarSet() {

        bindingProfile.titleBar.tvName.setText( modelUser.getUserName() + "님!" );
        bindingProfile.titleBar.ivProfile.setImageBitmap( converter.functionStringToBitmap( modelUser.getUserImage() ) );
    }

    public void functionActivityReceive() {

        switch( intentReceive.getIntentFromActivity() ) {

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

            case "activityDesk" : {

                functionShowLog( "move from activityDesk" );
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

            default: {

                modelUser = (ModelUser) intentReceive.getIntentData1();
                functionTitleBarSet();
                break;
            }

        }
    }

    public void functionActivitySend() {

        switch( intentSend.getIntentToActivity() ) {

            case "activityProfile": {

                intent = new Intent( this , ActivityProfile.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityMain": {

                intent = new Intent( this , ActivityMain.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityDesk": {

                intent = new Intent( this, ActivityDesk.class );
                intent.putExtra( "data" , intentSend );
                break;
            }

            case "activityLogin": {

                intent = new Intent( this, ActivityLogin.class );
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