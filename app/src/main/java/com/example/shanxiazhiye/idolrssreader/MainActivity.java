package com.example.shanxiazhiye.idolrssreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    boolean isContent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        RssParserTask task = new RssParserTask(this, null, getApplicationContext());
//        task.execute("http://blog.nogizaka46.com/atom.xml");

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getApplicationContext(),
                "http://blog.nogizaka46.com/atom.xml",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String result = new String(responseBody);
                        try {
                            List<Blog> blogList = parseXml(result);
                            for (Blog b : blogList) {
                                Log.d("title", b.getTitle());
                                Log.d("link", b.getLink());
                            }
                        } catch (IOException | XmlPullParserException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("onFailure", error.toString());
                    }
                }
        );
    }

    public List<Blog> parseXml(String data) throws IOException, XmlPullParserException {
        List<Blog> blogList = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(data));
            int eventType = parser.getEventType();
            Blog currentItem = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if (tag.equals("entry")) {
                            currentItem = new Blog();
                            isContent = true;
                        }
                        if (isContent) {
                            if (tag.equals("title")) {
                                currentItem.setTitle(parser.nextText());
                            }else if(tag.equals("link")) {
                                currentItem.setLink(parser.getAttributeValue(2));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if (tag.equals("entry")) {
                            blogList.add(currentItem);
                        }
                        break;
                }

                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blogList;
    }
}
