package com.memreas.more;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.base.SessionManager;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MoreActivity extends BaseActivity {

    protected DisplayImageOptions optionsGallery;
    protected DisplayImageOptions optionsStorage;
    protected AnimateFirstDisplayListener animateFirstListener;
    protected ImageLoader memreasImageLoader;

    private LinearLayout layoutMorePageAccount;
    private LinearLayout layoutMorePageMemberGuidelines;
    private LinearLayout layoutMorePagePrivacyPolicy;
    private LinearLayout layoutMorePageDmcaPolicy;
    private LinearLayout layoutMorePageTermsOfService;
    private LinearLayout layoutMorePageVersion;

    private Button btnAccount;
    private Button btnMemberGuidelines;
    private Button btnPrivacyPolicy;
    private Button btnDmcaPolicy;
    private Button btnTermsOfService;
    private Button btnVersion;

    private ImageView morePageAccountImageView;
    private TextView morePageAccountTextView;
    private WebView morePageMemberGuidelinesWebView;
    private WebView morePagePrivacyPolicyWebView;
    private WebView morePageDmcaPolicyWebView;
    private WebView morePageTermsOfServiceWebView;
    private TextView morePageVersionTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_more_page_ref);

        //Fetch image loader
        animateFirstListener = new AnimateFirstDisplayListener();
        animateFirstListener.setFailImage(R.drawable.profile_img_small);
        memreasImageLoader = MemreasImageLoader.getInstance();
        optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
        optionsStorage = MemreasImageLoader
                .getDefaultDisplayImageOptionsStorage();


        // Button Sections
        btnAccount = (Button) findViewById(R.id.btnAccount);
        btnAccount.setTag("btnAccount");
        btnMemberGuidelines = (Button) findViewById(R.id.btnMemberGuidelines);
        btnMemberGuidelines.setTag("btnMemberGuidelines");
        btnPrivacyPolicy = (Button) findViewById(R.id.btnPrivacyPolicy);
        btnPrivacyPolicy.setTag("btnPrivacyPolicy");
        btnDmcaPolicy = (Button) findViewById(R.id.btnDmcaPolicy);
        btnDmcaPolicy.setTag("btnDmcaPolicy");
        btnTermsOfService = (Button) findViewById(R.id.btnTermsOfService);
        btnTermsOfService.setTag("btnTermsOfService");
        btnVersion = (Button) findViewById(R.id.btnVersion);
        btnVersion.setTag("btnVersion");

        layoutMorePageAccount = (LinearLayout) findViewById(R.id.layoutMorePageAccount);
        layoutMorePageMemberGuidelines = (LinearLayout) findViewById(R.id.layoutMorePageMemberGuidelinesPolicy);
        layoutMorePagePrivacyPolicy = (LinearLayout) findViewById(R.id.layoutMorePagePrivacyPolicy);
        layoutMorePageDmcaPolicy = (LinearLayout) findViewById(R.id.layoutMorePageDmcaPolicy);
        layoutMorePageTermsOfService = (LinearLayout) findViewById(R.id.layoutMorePageTermsOfService);
        layoutMorePageVersion = (LinearLayout) findViewById(R.id.layoutMorePageVersion);

        //Content Sections
        morePageAccountImageView = (ImageView) findViewById(R.id.profilePicMorePageImageView);
        morePageAccountTextView = (TextView) findViewById(R.id.morePageAccountTextView);
        morePageMemberGuidelinesWebView = (WebView) findViewById(R.id.morePageMemberGuidelinesWebView);
        morePagePrivacyPolicyWebView = (WebView) findViewById(R.id.morePagePrivacyPolicyWebView);
        morePageDmcaPolicyWebView = (WebView) findViewById(R.id.morePageDmcaPolicyWebView);
        morePageTermsOfServiceWebView = (WebView) findViewById(R.id.morePageTermsOfServiceWebView);
        morePageVersionTextView = (TextView) findViewById(R.id.morePageVersionTextView);

        //Add button listeners
        btnAccount.setOnClickListener(btnListener);
        btnMemberGuidelines.setOnClickListener(btnListener);
        btnPrivacyPolicy.setOnClickListener(btnListener);
        btnDmcaPolicy.setOnClickListener(btnListener);
        btnTermsOfService.setOnClickListener(btnListener);
        btnVersion.setOnClickListener(btnListener);

        // Show Ads...
        AdmobView();
    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btn = (Button) v;
            String tag = (String) btn.getTag();

            if (tag.equalsIgnoreCase("btnAccount")) {
                if (layoutMorePageAccount.getVisibility() == View.GONE) {
                    layoutMorePageAccount.setVisibility(View.VISIBLE);
                } else {
                    layoutMorePageAccount.setVisibility(View.GONE);
                }
                layoutMorePageMemberGuidelines.setVisibility(View.GONE);
                layoutMorePagePrivacyPolicy.setVisibility(View.GONE);
                layoutMorePageDmcaPolicy.setVisibility(View.GONE);
                layoutMorePageTermsOfService.setVisibility(View.GONE);
                layoutMorePageVersion.setVisibility(View.GONE);
            } else if (tag.equalsIgnoreCase("btnMemberGuidelines")) {
                if (layoutMorePageMemberGuidelines.getVisibility() == View.GONE) {
                    layoutMorePageMemberGuidelines.setVisibility(View.VISIBLE);
                } else {
                    layoutMorePageMemberGuidelines.setVisibility(View.GONE);
                }
                layoutMorePageAccount.setVisibility(View.GONE);
                layoutMorePagePrivacyPolicy.setVisibility(View.GONE);
                layoutMorePageDmcaPolicy.setVisibility(View.GONE);
                layoutMorePageTermsOfService.setVisibility(View.GONE);
                layoutMorePageVersion.setVisibility(View.GONE);
            } else if (tag.equalsIgnoreCase("btnPrivacyPolicy")) {
                if (layoutMorePagePrivacyPolicy.getVisibility() == View.GONE) {
                    layoutMorePagePrivacyPolicy.setVisibility(View.VISIBLE);
                } else {
                    layoutMorePagePrivacyPolicy.setVisibility(View.GONE);
                }
                layoutMorePageAccount.setVisibility(View.GONE);
                layoutMorePageMemberGuidelines.setVisibility(View.GONE);
                layoutMorePageDmcaPolicy.setVisibility(View.GONE);
                layoutMorePageTermsOfService.setVisibility(View.GONE);
                layoutMorePageVersion.setVisibility(View.GONE);
            } else if (tag.equalsIgnoreCase("btnDmcaPolicy")) {
                if (layoutMorePageDmcaPolicy.getVisibility() == View.GONE) {
                    layoutMorePageDmcaPolicy.setVisibility(View.VISIBLE);
                } else {
                    layoutMorePageDmcaPolicy.setVisibility(View.GONE);
                }
                layoutMorePageAccount.setVisibility(View.GONE);
                layoutMorePageMemberGuidelines.setVisibility(View.GONE);
                layoutMorePagePrivacyPolicy.setVisibility(View.GONE);
                layoutMorePageTermsOfService.setVisibility(View.GONE);
                layoutMorePageVersion.setVisibility(View.GONE);
            } else if (tag.equalsIgnoreCase("btnTermsOfService")) {
                if (layoutMorePageTermsOfService.getVisibility() == View.GONE) {
                    layoutMorePageTermsOfService.setVisibility(View.VISIBLE);
                } else {
                    layoutMorePageTermsOfService.setVisibility(View.GONE);
                }
                layoutMorePageAccount.setVisibility(View.GONE);
                layoutMorePageMemberGuidelines.setVisibility(View.GONE);
                layoutMorePagePrivacyPolicy.setVisibility(View.GONE);
                layoutMorePageDmcaPolicy.setVisibility(View.GONE);
                layoutMorePageVersion.setVisibility(View.GONE);
            } else if (tag.equalsIgnoreCase("btnVersion")) {
                if (layoutMorePageVersion.getVisibility() == View.GONE) {
                    layoutMorePageVersion.setVisibility(View.VISIBLE);
                } else {
                    layoutMorePageVersion.setVisibility(View.GONE);
                }
                layoutMorePageAccount.setVisibility(View.GONE);
                layoutMorePageMemberGuidelines.setVisibility(View.GONE);
                layoutMorePagePrivacyPolicy.setVisibility(View.GONE);
                layoutMorePageDmcaPolicy.setVisibility(View.GONE);
                layoutMorePageTermsOfService.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        memreasImageLoader.displayImage(SessionManager.getInstance().getUser_profile_picture(), morePageAccountImageView, optionsGallery,
                animateFirstListener);
        morePageAccountTextView.setText(SessionManager.getInstance().getUser_name());
        morePageMemberGuidelinesWebView.loadUrl("file:///android_asset/www/member_guidelines.html");
        morePagePrivacyPolicyWebView.loadUrl("file:///android_asset/www/privacy.html");
        morePageDmcaPolicyWebView.loadUrl("file:///android_asset/www/dmca.html");
        morePageTermsOfServiceWebView.loadUrl("file:///android_asset/www/terms_of_service.html");

    }

}


