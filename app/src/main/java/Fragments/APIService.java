package Fragments;

import Notification.Myresponse;
import Notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface  APIService
{
    @Headers({
                    "Content-Type:application/json",
                    "Authorization:key=AAAAQFMSKsA:APA91bFOausSLx_QYH2KapuhK5rJgrU-Nlua5qmsUEGjmUCbVhJ02q7DK1qrIj0X7KZRdet7JJlTIFILy45riTgMls2hPkPQIA_Rk8_w3_40tqCfum4WCv_FGcrgvSIL70xiHJwDpv9U"
    })
    @POST("fcm/send")
    Call<Myresponse> sendNotification(@Body Sender body);
}
