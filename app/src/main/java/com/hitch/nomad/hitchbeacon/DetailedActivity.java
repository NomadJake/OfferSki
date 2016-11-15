package com.hitch.nomad.hitchbeacon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedActivity extends AppCompatActivity {

    public TextView title,offer;
    public ImageView imView;
    public String titleString,offerString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        title = (TextView)findViewById(R.id.tvOffer);
        offer = (TextView)findViewById(R.id.textView2);
        imView = (ImageView)findViewById(R.id.imageView);

        titleString = getIntent().getStringExtra("title");
        offerString = getIntent().getStringExtra("note");

        title.setText(titleString);
        offer.setText(offerString);

    }
}
