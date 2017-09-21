package com.memreas.legal;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.base.BaseActivity;

public class TermsOfServiceActivity extends BaseActivity {

	private WebView mWebView;
	private TextView txtTosPolicyView;

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.termsofservice_layout);
		mWebView = (WebView) findViewById(R.id.webView);
		// mWebView.setAlwaysDrawnWithCacheEnabled(false);
		// mWebView.setAnimationCacheEnabled(false);
		mWebView.setBackgroundColor(Color.TRANSPARENT);
		// mWebView.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
		// mWebView.setWillNotCacheDrawing(true);
		// mWebView.setDrawingCacheEnabled(false);
		// Paint p = new Paint();
		// mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, p);
		mWebView.loadUrl("file:///android_asset/www/terms_of_service.html");
	}

	public void onBack(View v) {
		finish();
	}

}
