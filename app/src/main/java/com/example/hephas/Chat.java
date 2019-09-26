package com.example.hephas;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import io.kommunicate.KmConversationBuilder;
import io.kommunicate.Kommunicate;
import io.kommunicate.callbacks.KmCallback;

public class Chat extends AppCompatActivity {
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Kommunicate.init(context,"3c365368a5a503b3e5cbbb9112604b9e2");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        final Context activityContext = this;

        new KmConversationBuilder(activityContext).launchConversation(new KmCallback() {
            @Override
            public void onSuccess(Object message) {
                Log.d("Conversation", "Success : " + message);
            }

            @Override
            public void onFailure(Object error) {
                Log.d("Conversation", "Failure : " + error);
            }
        });

    }

}
