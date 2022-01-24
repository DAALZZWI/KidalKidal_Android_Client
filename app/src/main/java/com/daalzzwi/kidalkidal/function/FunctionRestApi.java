package com.daalzzwi.kidalkidal.function;

import com.daalzzwi.kidalkidal.model.ModelChatPayload;
import com.daalzzwi.kidalkidal.model.ModelCompany;
import com.daalzzwi.kidalkidal.model.ModelDeskPayload;
import com.daalzzwi.kidalkidal.model.ModelQrPayload;
import com.daalzzwi.kidalkidal.model.ModelTokenPayload;
import com.daalzzwi.kidalkidal.model.ModelUserPayload;
import com.daalzzwi.kidalkidal.model.ModelVisitorPayload;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;

public interface FunctionRestApi {

    @HTTP( method = "POST" , path = "/user/register" , hasBody = true )
    Call< ModelUserPayload > apiRegister( @Body ModelUserPayload modelUserPayload );

    @HTTP( method = "POST" , path = "/user/unregister" , hasBody = true )
    Call< ModelUserPayload > apiUnRegister( @Body ModelUserPayload modelUserPayload );

    @HTTP( method = "POST" , path = "/user/login" , hasBody = true )
    Call< ModelUserPayload > apiLogin( @Body ModelUserPayload modelUserPayload );

    @HTTP( method = "POST" , path = "/user/find/email" , hasBody = true )
    Call< ModelUserPayload > apiFindEmail( @Body ModelUserPayload modelUserPayload );

    @HTTP( method = "POST" , path = "/user/find/password" , hasBody = true )
    Call< ModelUserPayload > apiFindPassword( @Body ModelUserPayload modelUserPayload );

    @HTTP( method = "POST" , path = "/user/profile" , hasBody = true )
    Call< ModelUserPayload > apiProfile( @Body ModelUserPayload modelUserPayload );

    @HTTP( method = "POST" , path = "/desk/insert" , hasBody = true )
    Call< ModelDeskPayload > apiDeskInsert( @Body ModelDeskPayload modelDeskPayload );

    @HTTP( method = "POST" , path = "/desk/select" , hasBody = true )
    Call< ModelDeskPayload > apiDeskSelect();

    @HTTP( method = "POST" , path = "/desk/delete" , hasBody = true )
    Call< ModelDeskPayload > apiDeskDelete( @Body ModelDeskPayload modelDeskPayload );

    @HTTP( method = "POST" , path = "/desk/increase" , hasBody = true )
    Call< ModelDeskPayload > apiDeskIncrease( @Body ModelDeskPayload modelDeskPayload );

    @HTTP( method = "POST" , path = "/token/notification/insert" , hasBody = true )
    Call< ModelTokenPayload > apiNotificationInsert( @Body ModelTokenPayload modelTokenPayload );

    @HTTP( method = "POST" , path = "/token/notification/delete" , hasBody = true )
    Call< ModelTokenPayload > apiNotificationDelete( @Body ModelTokenPayload modelTokenPayload );

    @HTTP( method = "POST" , path = "/qr/insert" , hasBody = true )
    Call< ModelQrPayload > apiQrInsert( @Body ModelQrPayload modelQrPayload );

    @HTTP( method = "POST" , path = "/qr/delete" , hasBody = true )
    Call< ModelQrPayload > apiQrDelete( @Body ModelQrPayload modelQrPayload );

    @HTTP( method = "POST" , path = "/chat/select" , hasBody = true )
    Call< List< String > > apiChatSelect();

    @HTTP( method = "POST" , path = "/chat/image/select" , hasBody = true )
    Call< ModelChatPayload > apiChatImageSelect( @Body ModelChatPayload modelChatPayload );

    @HTTP( method = "POST" , path = "/chat/image/add" , hasBody = true)
    Call< ModelChatPayload > apiChatImageAdd( @Body String email );

    @HTTP( method = "POST" , path = "/company/init" , hasBody = true )
    Call< Integer > apiCompanyInit();

    @HTTP( method = "POST" , path = "/company/select" , hasBody = true )
    Call< List< ModelCompany > > apiCompanySelect();

    @HTTP( method = "POST" , path = "/company/image/select" , hasBody = true )
    Call< ModelVisitorPayload > apiCompanyImageSelect( @Body ModelVisitorPayload modelVisitorPayload );

    @HTTP( method = "POST" , path = "/company/image/insert" , hasBody = true )
    Call< ModelVisitorPayload > apiCompanyImageInsert( @Body ModelVisitorPayload modelVisitorPayload );

    @HTTP( method = "POST" , path = "/order/list" , hasBody = true )
    Call< Map< String , Integer > > apiOrderList( @Body String companyId );
}

