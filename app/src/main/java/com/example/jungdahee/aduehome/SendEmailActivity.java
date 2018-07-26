package com.example.jungdahee.aduehome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Jung dahee on 2017-08-24.
 */

public class SendEmailActivity extends AppCompatActivity{
    Button buttonSend;
    EditText textTo;
    EditText textSubject;
    EditText textMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_main);

        /**send_main에서 설정한 id를 가져와 선언**/
        buttonSend = (Button) findViewById(R.id.buttonSend);
        textTo = (EditText) findViewById(R.id.editTextTo);
        textSubject = (EditText) findViewById(R.id.editTextSubject);
        textMessage = (EditText) findViewById(R.id.editTextMessage);

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /**text를 가져와 String형식으로 변경**/
                String to = textTo.getText().toString();
                String subject = textSubject.getText().toString();
                String message = textMessage.getText().toString();

                /**Intent로 전달, 추가로 데이터를 더 전달하기 위해 putExtra 사용**/
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{to, "jundahee010@naver.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, message);

                email.setType("message/rfc822"); //MIME 설정

                startActivity(email);

            }
        });
    }
}
