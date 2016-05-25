package dong.lan.tuyi.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import dong.lan.tuyi.R;
import dong.lan.tuyi.activity.MainActivity;

/**
 * Created by Dooze on 2015/8/24.
 */
public class Weather extends AsyncTask<String,Void,String> {

    public static String httpUrl;
    public static String urlCode = "http://apis.baidu.com/apistore/weatherservice/cityid";
    public static String urlNmae = "http://apis.baidu.com/apistore/weatherservice/cityname";
    public Context context;
    public TextView textView;
    public static final int NAME = 1;
    public static final int CODE = 2;
    private int Tag;

    public Weather(Context context, TextView textView, int tag) {
        this.context = context;
        this.textView = textView;
        Tag = tag;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s == null || s.equals("")) {
            textView.setText(R.string.getWeatherFail);
            MainActivity.isGetWeather = false;
        } else {
            String text = "Error";
            try {
                JSONObject json = new JSONObject(s);
                text = "";
                JSONObject object = new JSONObject(json.getString("retData"));
                text +=" "+object.getString("city");
                text += "  " + object.getString("weather");
                text += " 气温 " + object.getString("temp");
                System.out.println(text);
                MainActivity.isGetWeather =true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            textView.setText(text);
        }

    }

    @Override
    protected String doInBackground(String... params) {
        BufferedReader reader;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        if(Tag==NAME)
        httpUrl = urlNmae + "?" + params[0];
        else if(Tag==CODE)
            httpUrl=urlCode+"?"+params[0];
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", "c723501528e9a20b6076377ac0606c38");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("result " + result);
        return result;
    }
}
