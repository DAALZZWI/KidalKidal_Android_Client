package com.daalzzwi.kidalkidal.function;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.RecyclerView;

import com.daalzzwi.kidalkidal.R;
import com.daalzzwi.kidalkidal.adapter.AdapterDialog;
import com.daalzzwi.kidalkidal.config.ConfigContext;

import java.util.ArrayList;
import java.util.Arrays;

public class FunctionDialog {

    private static FunctionDialog functionDialog = null;
    private Context context = null;
    private static Display display = null;
    private static Point point = null;
    private static Dialog dialog = null;
    private static WindowManager.LayoutParams lp = null;
    private static LayoutInflater layoutInflater = null;
    private static View view = null;
    private static ArrayList< String > arrayList = null;
    private static AdapterDialog adapterDialog = null;
    private static RecyclerView dialogRecyclerView = null;
    private static String[] menu = { "로그아웃 하기" , "프로필 보기" , "문의 하기" , "Q&A 보기" };

    private FunctionDialog() {

        context = ConfigContext.getContext();

        point = new Point();
        dialog = new Dialog( context );
        lp = new WindowManager.LayoutParams();
        arrayList = new ArrayList<>();

        display = ( ( Activity ) context ).getWindowManager().getDefaultDisplay();
        display.getSize( point );

        layoutInflater = ( ( Activity ) context ).getLayoutInflater();

        view = layoutInflater.inflate( R.layout.activity_rcv_item_dialog , null );

        lp.copyFrom( dialog.getWindow().getAttributes() );
        lp.width = point.x * 80 / 100;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.setContentView( view );
        dialog.setCanceledOnTouchOutside( true );
        dialog.getWindow().setAttributes( lp );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

        arrayList.addAll( Arrays.asList( menu ) );


        dialog.show();
    };

    public static AdapterDialog getAdapterDialog() {

        return adapterDialog;
    }

    public static FunctionDialog getDialog() {

        if( functionDialog == null ) {

            functionDialog = new FunctionDialog();
        }

        return functionDialog;
    }

    public ArrayList getArrayList() {

        return arrayList;
    }

    public View getView() {

        return view;
    }
}