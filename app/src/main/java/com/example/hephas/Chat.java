package com.example.hephas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import com.google.cloud.dialogflow.v2beta1.TextInput;

import java.io.InputStream;
import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import de.hdodenhof.circleimageview.CircleImageView;


public class Chat extends AppCompatActivity {

    private  static  final  String TAG = Chat.class.getSimpleName();
    private  static  final  int USER = 10001;
    private  static  final  int BOT = 10002;
    final Context context = this;
    final Context activityContext = this;

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;
    private LinearLayout inputLayout;
    private ImageButton sendBtn;
    private CircleImageView botImg;
    private static String inviteMessage;
    private EditText et_query;


    // Android client
    private AIRequest aiReq;
    private AIDataService aiDataService;
    private AIServiceContext customAIServiceContext;

    // Java V2
    private SessionsClient sessionsClient;
    private SessionName session;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));

        chatLayout = findViewById(R.id.chatLayout);

        ImageButton sendBtn = findViewById(R.id.chatbox_send_btn);
        sendBtn.setOnClickListener(this::sendMessage);

        et_query = findViewById(R.id.et_chatbox);
        et_query.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        sendMessage(sendBtn);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });


        // Java V2
        initV2Chatbot();

    }
    private void initChatbot() {
        final AIConfiguration config = new AIConfiguration("<Client Access Code>",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(this, config);
        customAIServiceContext = AIServiceContextBuilder.buildFromSessionId(uuid);// helps to create new session whenever app restarts
        aiReq = new AIRequest();
    }

    private void initV2Chatbot() {
        try {
            InputStream stream =getResources().openRawResource(R.raw.heph_agent);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials)credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(View view) {
        String msg = et_query.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(Chat.this, "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            showTextView(msg, USER);
            et_query.setText("");
            // Java V2
            QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(msg).setLanguageCode("en-US")).build();
            new RequestJavaV2Task(Chat.this, session, sessionsClient, queryInput).execute();
        }
    }

    public void callback(AIResponse aiResponse) {
        if (aiResponse != null) {
            // process aiResponse here
            String botReply = aiResponse.getResult().getFulfillment().getSpeech();
            Log.d(TAG, "Bot Reply: " + botReply);
            showTextView(botReply, BOT);
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was some communication issue. Please Try again!", BOT);
        }
    }

    public void callbackV2(DetectIntentResponse response) {
        if (response != null) {
            // process aiResponse here
            String botReply = response.getQueryResult().getFulfillmentText();
            Log.d(TAG, "V2 Bot Reply: " + botReply);
            showTextView(botReply, BOT);
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was some communication issue. Please Try again!", BOT);
        }
    }

//    private void showTextView(String message, int type) {
//        FrameLayout layout;
//        switch (type) {
//            case USER:
//                layout = getUserLayout();
//                break;
//            case BOT:
//                layout = getBotLayout();
//                break;
//            default:
//                layout = getBotLayout();
//                break;
//        }
//        layout.setFocusableInTouchMode(true);
//        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
//        TextView tv = layout.findViewById(R.id.chatLayout);
//        tv.setText(message);
//        layout.requestFocus();
//        et_query.requestFocus(); // change focus back to edit text to continue typing
//    }

    //Appends username to every bot reply
    private void showTextView(String message, int type) {
        FrameLayout layout;
        switch (type) {
            case USER:
                layout = getUserLayout();
                TextView tv2 = layout.findViewById(R.id.chatMsg);
                tv2.setText(message);
                break;
            case BOT:
                layout = getBotLayout();
                botImg = layout.findViewById(R.id.botImg);
                TextView tv = layout.findViewById(R.id.chatMsg);
                if(!Constants.getUserName().isEmpty()) {
                    tv.setText(Constants.getUserName() + "\n" + message);
                }
                else{
                    tv.setText(message);
                }
                break;
            default:
                layout = getBotLayout();
                TextView tv3 = layout.findViewById(R.id.chatMsg);
                tv3.setText(Constants.getUserName() + "\n" + message);
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
        layout.requestFocus();
        et_query.requestFocus(); // change focus back to edit text to continue typing
    }


    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(Chat.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(Chat.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }


    //Inflate menu options

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.clear:

                return true;
            case R.id.Settings:
                            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                            return  true;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setAction(Intent.ACTION_SEND)
                        .setType("text/plain").putExtra(Intent.EXTRA_TEXT, inviteMessage);
                startActivity(Intent.createChooser(intent, "Share Via"));
                return super.onOptionsItemSelected(item);

            case R.id.Feedback:
                startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }




}







