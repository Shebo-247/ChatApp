package com.shebo.chatapp.Fragments;

import com.shebo.chatapp.Notification.MyResponse;
import com.shebo.chatapp.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
        {
            "Content-Type:application/json",
            "Authorization:key=AAAASc07aEw:APA91bH50-kqYddjdkFng8lVguWipbvlggx6zZBrfiMKb4W_6jzjLJ6bNSgoTNozOOhLJm9omUcSfkq26wRDnyx8n1iCd_w8mmpqfcyK-q2AZ_yPGnsYOWrp4GPqFKJHUk6z2I01elfw"
        }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
