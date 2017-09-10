package com.example.shanxiazhiye.idolrssreader;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

/**
 * Created by shanxiazhiye on 2017/07/02.
 */

public class RssParserTask extends AsyncTask<String,Integer,RssListAdapter> {
    private MainActivity mActivity;
    private RssListAdapter mAdapter;
    private ProgressDialog mprogressDialog;
    Context context;


    public RssParserTask(MainActivity activity, RssListAdapter adapter, Context context){
        this.context = context;
        mActivity = activity;
        mAdapter = adapter;
    }
    @Override
    protected void onPreExecute() {
        mprogressDialog = new ProgressDialog(mActivity);
        mprogressDialog.setMessage("Now Loading");
        mprogressDialog.show();
    }

    @Override
    protected RssListAdapter doInBackground(String... params) {
        RssListAdapter result = null;
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(
                    context,
                    params[0],
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String result = new String(responseBody);
                            Log.d("result", result);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d("onFailure", error.toString());
                        }
                    }
            );
            URL url = new URL(params[0]);
            Log.d("URL", url.openConnection().toString());
            InputStream is = url.openConnection().getInputStream();
            Log.d("inputstream", convertInputStreamToString(is));
            result = parseXml(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    @Override
    protected void onPostExecute(RssListAdapter result){
        mprogressDialog.dismiss();
//        mActivity.setListAdapter(result);
    }
    public RssListAdapter parseXml(InputStream is)throws IOException,
            XmlPullParserException {
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            Blog currentItem = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (tag.equals("entry")) {
                            currentItem = new Blog();
                        } else if (currentItem != null) {
                            if (tag.equals("title")) {
                                currentItem.setTitle(parser.nextText());
                            } else if (tag.equals("description")) {
                                currentItem.setLink(parser.nextText());
                            }
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if (tag.equals("item")) {
                            mAdapter.add(currentItem);
                        }
                        break;
                }

                    eventType = parser.next();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mAdapter;
    }

    static String convertInputStreamToString(InputStream is) throws IOException {
        InputStreamReader reader = new InputStreamReader(is);
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[512];
        int read;
        while (0 <= (read = reader.read(buffer))) {
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }

}






