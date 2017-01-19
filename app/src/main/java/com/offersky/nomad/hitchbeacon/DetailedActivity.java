package com.offersky.nomad.hitchbeacon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

public class DetailedActivity extends AppCompatActivity {

    public TextView title,offer,code;
    public ImageView imView;
    public String titleString,offerString,codeString;
    private ImageLoader imageLoader;
    private FeedImageView offerImage,logofiv;
    public String offerURL,logoURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_with_logo);
        imageLoader = Hitchbeacon.getInstance().getImageLoader();
        offerImage = (FeedImageView)findViewById(R.id.imageView);
        code = (TextView)findViewById(R.id.textViewCode);
        title = (TextView)findViewById(R.id.tvOffer);
        offer = (TextView)findViewById(R.id.textView2);
        logofiv = (FeedImageView)findViewById(R.id.feedImageView);
        imView = (ImageView)findViewById(R.id.imageView);
        titleString = getIntent().getStringExtra("title");
        offerURL = getIntent().getStringExtra("URL");
        logoURL = getIntent().getStringExtra("logoURL");
        try {
            codeString = getIntent().getStringExtra("code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        offerString = getIntent().getStringExtra("note");
        try {
            offerImage.setImageUrl(offerURL,imageLoader);
            logofiv.setImageUrl(logoURL,imageLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        title.setText(offerString);
//        offer.setText(offerString);
        code.setText(codeString);

    }
}
