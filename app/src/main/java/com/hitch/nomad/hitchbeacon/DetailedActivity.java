package com.hitch.nomad.hitchbeacon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.suitebuilder.TestMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class DetailedActivity extends AppCompatActivity {

    public TextView title,offer,code;
    public ImageView imView;
    public String titleString,offerString,codeString;
    private ImageLoader imageLoader;
    private FeedImageView offerImage;
    public String offerURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        imageLoader = Hitchbeacon.getInstance().getImageLoader();
        offerImage = (FeedImageView)findViewById(R.id.imageView);
        code = (TextView)findViewById(R.id.textViewCode);
        title = (TextView)findViewById(R.id.tvOffer);
        offer = (TextView)findViewById(R.id.textView2);
        imView = (ImageView)findViewById(R.id.imageView);
        titleString = getIntent().getStringExtra("title");
        offerURL = getIntent().getStringExtra("URL");
        try {
            codeString = getIntent().getStringExtra("code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        offerString = getIntent().getStringExtra("note");
        try {
            offerImage.setImageUrl(offerURL,imageLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        title.setText(titleString);
        offer.setText(offerString);

    }
}
