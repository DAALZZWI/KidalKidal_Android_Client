package com.daalzzwi.kidalkidal.function;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;


public class FunctionConverter {

    @TypeConverter
    public String functionBitmapToString( Bitmap data ) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        data.compress( Bitmap.CompressFormat.PNG , 100 , stream );
        byte[] byteArray = stream.toByteArray();
        return Base64.getUrlEncoder().encodeToString( byteArray );
    }

    @TypeConverter
    public Bitmap functionStringToBitmap( String data ) {

        byte[] byteArray = Base64.getUrlDecoder().decode( data );
        ByteArrayInputStream stream = new ByteArrayInputStream( byteArray );
        return BitmapFactory.decodeStream( stream );
    }

    @TypeConverter
    public byte[] functionBitmapToByteArray( Bitmap data ) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        data.compress( Bitmap.CompressFormat.PNG , 100 , stream );
        return stream.toByteArray();
    }

    @TypeConverter
    public Bitmap functionByteArrayToBitmap( byte[] data ) {

        byte[] byteArray = Base64.getUrlDecoder().decode( data );
        ByteArrayInputStream stream = new ByteArrayInputStream( byteArray );
        return BitmapFactory.decodeStream( stream );
    }

    @TypeConverter
    public byte[] functionStringToByteArray( String data ) {

        byte[] byteArray = Base64.getUrlDecoder().decode( data );
        return byteArray;
    }

    @TypeConverter
    public String functionByteArrayToString( byte[] data ) {

        byte[] byteArray = Base64.getUrlDecoder().decode( data );
        return new String( byteArray );
    }
}
