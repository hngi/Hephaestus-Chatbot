package com.example.hephas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    private Button send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

       send_btn = findViewById(R.id.ButtonSendFeedback);
        send_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ButtonSendFeedback:
                // Do something
        }
    }

    final EditText nameField = (EditText) findViewById(R.id.EditTextName);
    String name = nameField.getText().toString();

    final EditText emailField = (EditText) findViewById(R.id.EditTextEmail);
    String email = emailField.getText().toString();

    final EditText feedbackField = (EditText) findViewById(R.id.EditTextFeedbackBody);
    String feedback = feedbackField.getText().toString();

    final Spinner feedbackSpinner = (Spinner) findViewById(R.id.SpinnerFeedbackType);
    String feedbackType = feedbackSpinner.getSelectedItem().toString();

    final CheckBox responseCheckbox = (CheckBox) findViewById(R.id.CheckBoxResponse);
    boolean bRequiresResponse = responseCheckbox.isChecked();
}
