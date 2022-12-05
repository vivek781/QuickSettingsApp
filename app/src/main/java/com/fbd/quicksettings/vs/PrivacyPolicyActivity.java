package com.fbd.quicksettings.vs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class PrivacyPolicyActivity extends Activity
{
	String dialog_message = "Fetching Privacy & Policy";

	protected WebView privacy_web_view;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//Remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetView();
	}

	private void SetView()
	{
		setContentView(R.layout.activity_privacy_policy);

		EUGeneralHelper.is_show_open_ad = true;

		ShowLoadingDialog(dialog_message);

		privacy_web_view = (WebView)findViewById(R.id.privacy_web_view);
		privacy_web_view.setWebViewClient(new MyWebViewClient());

		privacy_web_view.getSettings().setUseWideViewPort(true);
		privacy_web_view.getSettings().setLoadWithOverviewMode(true);
		privacy_web_view.getSettings().setSupportZoom(true);
		privacy_web_view.getSettings().setBuiltInZoomControls(true);

		privacy_web_view.loadUrl(EUGeneralHelper.privacy_policy_url);
	}

	private class MyWebViewClient extends WebViewClient
	{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			super.onPageFinished(view, url);
			if (privacy_web_view.getProgress() == 100)
			{
				DismissLoadingDialog();
			}
		}
	}

	private static Dialog loading_dialog;
	private static TextView loading_dialog_message;
	public void LoadingDialog(final String message)
	{
		loading_dialog = new Dialog(PrivacyPolicyActivity.this,R.style.TransparentBackground);
		loading_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loading_dialog.setContentView(R.layout.dialog_loading);

		loading_dialog_message = (TextView)loading_dialog.findViewById(R.id.dialog_loading_txt_message);

		loading_dialog_message.setText(message);

		loading_dialog.show();
	}

	private void ShowLoadingDialog(String message)
	{
		LoadingDialog(message);
	}

	private static void DismissLoadingDialog()
	{
		if(loading_dialog != null)
		{
			loading_dialog.dismiss();
		}
	}

	@Override
	public void onBackPressed()
	{
		// TODO Auto-generated method stub
		BackScreen();
	}

	private void BackScreen()
	{
		EUGeneralHelper.is_show_open_ad = true;
		finish();
		AppConstants.overridePendingTransitionExit(this);
	}
}
