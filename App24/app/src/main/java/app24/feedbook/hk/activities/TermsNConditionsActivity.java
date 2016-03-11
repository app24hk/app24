package app24.feedbook.hk.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import app24.feedbook.hk.R;
import app24.feedbook.hk.utils.APIsConstants;
import app24.feedbook.hk.utils.Utils;

/**
 * Created by amritpal on 2/11/15.
 */
public class TermsNConditionsActivity extends BaseActivity {

    private TextView txt_activity_header;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_n_conditions);
        setHeader(getResources().getString(R.string.terms_of_use), true, false, false, false, false,
                null);
        initializeViews();
        updateUI();


    }

    private void initializeViews() {
        txt_activity_header = (TextView) findViewById(R.id.txt_activity_header);
        webView = (WebView) findViewById(R.id.webview_aboutus);

    }

    private void updateUI() {
        String link = APIsConstants.TERMS_LINK;
        webView.setWebViewClient(new myWebClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(link);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setInitialScale(100);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Utils.error("Webview ", "page started");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Utils.error("Webview ", url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
        }
    }

    private void loadError() {
        String html = "<html><body><table width=\"100%\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">"
                + "<tr>"
                + "<td><div align=\"center\"><font color=\"red\" size=\"20pt\">Your device don't have active internet connection</font></div></td>"
                + "</tr>" + "<img src=\"file:///android_assets/error.png" + "/>" + "</table><html><body>";
        //     webView.loadUrl("file:///android_assets/msg.html");
        System.out.println("html " + html);
        String base64 = android.util.Base64.encodeToString(html.getBytes(), android.util.Base64.DEFAULT);
        webView.loadData(base64, "text/html; charset=utf-8", "base64");
        //     webView.loadDataWithBaseURL("", base64, "text/html","utf-8", "");
        System.out.println("loaded html");
    }

//    private void loadWebViewLoad(String url) {
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.getSettings().setSupportMultipleWindows(true);
//        webView.setWebViewClient(new WebViewClient());
//        webView.setWebChromeClient(new WebChromeClient());
//        webView.loadUrl("http://www.google.com");
//
//    }


}
