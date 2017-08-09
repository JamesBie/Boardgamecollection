package com.james.android.boardgamecollection;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by 100599223 on 8/8/2017. This package will download the string from the website through ASYNC TASK
 * ...Implementation through a load manager will be upcoming updates
 */

public class DownloadXmlTask extends AsyncTask<URL, Void, String> {
    public AsyncResponse delegate = null;

    @Override
    protected String doInBackground(URL... urls) {
        URL url = createURL(INPUT_URL);
        String xmlOutput = "";
        try {
            xmlOutput = makeHttpRequest(url);
        }catch (IOException e){
            Log.e("Do in background", "problem making http request", e);
        }
        return xmlOutput;
    }
    @Override
    protected void onPostExecute (String xmlOutput){
        if (xmlOutput == null ){

            Log.v("Post Execute", "had no xml output");
        }
        Log.v("Post execute", xmlOutput);
        delegate.processFinish(xmlOutput);
    }

    //Uploads XML , parses it and combines it with HTML markup. Returns HTML string

    private String makeHttpRequest (URL url) throws IOException{
        String xmlOutput = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000/*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                Log.i("NetworkActivity", "entered with "+ urlConnection.getResponseCode());
                inputStream = urlConnection.getInputStream();
                xmlOutput = readFromStream(inputStream);
            }

        } catch (IOException e){
            Log.e("NetworkActivity", "problem retrieving data", e);
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();

            }if (inputStream !=null){
                inputStream.close();
            }

        }
        return xmlOutput;
    }

    //use async task to download the xml feed from url
    private URL createURL (String stringURL){
        URL url = null;
        try {
            url = new URL(INPUT_URL);

        } catch ( MalformedURLException e){
            Log.e("NetworkActivity.java", "Error with creating the URL", e);
            return null;
        }
        return url;
    }
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String INPUT_URL = "https://www.boardgamegeek.com/xmlapi2/collection?username=jamoskullis&stats=1";


    //Whither there is a wi-fi connection
    private static boolean wifiConnected = false;

    //whether there is a mobile connection
    private static boolean mobileConnected = false;

    //Whether the display should be refreshed
    private static boolean refreshDisplay = true;

    public static String sPref = null;

    public static class Items{
        public final String name;
        public final String player;


        private Items (String Name, String player) {
            this.name = Name;
            this.player = player;
        }
    }



    public void loadPage (URL url) {
        if ((sPref.equals(ANY)) && (wifiConnected || mobileConnected)) {
            new DownloadXmlTask().execute(createURL(INPUT_URL));
        } else if ((sPref.equals(WIFI) && (wifiConnected))){
            new DownloadXmlTask().execute(createURL(INPUT_URL));
        } else {
            Log.v("downloadxmltask", "downloadxml else statmeent line 129");

        }
    }
    private String readFromStream (InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader (inputStreamReader);
            String line = reader.readLine();
            while(line != null) {
                output.append(line);
                line=reader.readLine();
            }
        }
        return output.toString();
    }
}
