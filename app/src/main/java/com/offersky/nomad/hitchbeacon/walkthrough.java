package com.offersky.nomad.hitchbeacon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class walkthrough extends AppCompatActivity {

    public Button nextBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        nextBt = (Button)findViewById(R.id.nextBt);
        nextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Hitchbeacon.user == null){
                    startActivity(new Intent(walkthrough.this,OtpAuth.class));

                }else {
                    startActivity(new Intent(walkthrough.this,IconTabsActivity.class));

                }
            }
        });
    }
}
