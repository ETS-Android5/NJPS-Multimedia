package dev.nurujjamanpollob.njpollobutilities.WebRequestMaker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebRequestMaker
{

    String url = "";

    public WebRequestMaker(String url){

        this.url = url;

    }

    public String getSource(){

        StringBuilder result = new StringBuilder();
        try
        {
            URL urls = new URL(url);

            HttpURLConnection connct = (HttpURLConnection) urls.openConnection();

            connct.setDoInput(true);

            connct.setRequestMethod("GET");
            connct.setReadTimeout(15000);

            int response = connct.getResponseCode();

            if(response == HttpURLConnection.HTTP_OK){

                InputStream is = connct.getInputStream();
                BufferedReader bfr = new BufferedReader(new InputStreamReader(is));
                String line;
                while((line = bfr.readLine()) != null){

                    result.append(line);
                    result.append("\n");
                }
            }

        }
        catch (Exception e)
        {

            return e.toString();
        }

        return result.toString();
    }
}
