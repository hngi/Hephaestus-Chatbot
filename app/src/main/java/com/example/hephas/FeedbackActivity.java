package com.example.hephas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class FeedbackActivity extends AppCompatActivity{

    private Button send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ((Button) findViewById(R.id.ButtonSendFeedback)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String to = ((EditText)findViewById(R.id.EditTextEmail)).getText().toString();
                String sub = ((EditText)findViewById(R.id.EditTextSubject)).getText().toString();
                String msg = ((EditText)findViewById(R.id.EditTextFeedbackBody)).getText().toString();
                Intent mail = new Intent(Intent.ACTION_SEND);
                mail.putExtra(Intent.EXTRA_EMAIL,new String[]{to});
                mail.putExtra(Intent.EXTRA_SUBJECT, sub);
                mail.putExtra(Intent.EXTRA_TEXT, msg);
                mail.setType("message/rfc822");
                startActivity(Intent.createChooser(mail, "Send email via:"));
            }
        });
    }
}
