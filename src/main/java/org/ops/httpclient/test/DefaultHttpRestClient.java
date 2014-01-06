package org.ops.httpclient.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

public class DefaultHttpRestClient {

  public class Response {
    public int code = 0;
    public String msg = null;
    
    public Response(int code, String msg) {
      this.code = code;
      this.msg = msg;
    }
  }

  HttpParams params = null;

  DefaultHttpClient httpClient = null;

  public DefaultHttpRestClient() {
    HttpParams params = new BasicHttpParams();
    HttpProtocolParams.setContentCharset(params, "UTF-8");
    HttpClientParams.setRedirecting(params, false);
    HttpConnectionParams.setLinger(params, 0);
    HttpConnectionParams.setConnectionTimeout(params, 5000);
    HttpConnectionParams.setSoTimeout(params, 30000);

    ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();  
    cm.setMaxTotal(40);
    cm.setDefaultMaxPerRoute(40);

    httpClient = new DefaultHttpClient(cm, params);
  }
   
  public Response sendGetRequest(String url) {

    HttpGet httpGet = new HttpGet(url);      
    HttpResponse response = null;
    int statusCode = 0;
    String result = "";

    try {
      response = httpClient.execute(httpGet);
      statusCode = response.getStatusLine().getStatusCode();

      if(null != response.getEntity() && 0 != response.getEntity().getContentLength())
        result = convertStreamToString(response.getEntity().getContent());

      EntityUtils.consume(response.getEntity());
    }
    catch (Exception e) {
        statusCode = HttpStatus.SC_SERVICE_UNAVAILABLE;
        if(e.getMessage() != null)
          result = e.getMessage();
        httpGet.abort();
    }

    return new Response(statusCode, result.trim());
  }
  
  private String convertStreamToString(InputStream is) throws IOException, UnsupportedEncodingException {

    BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    StringBuilder builder = new StringBuilder();

    try {
      String line = rd.readLine();
      while (null != line) {
        builder.append(line + "\n");
        line = rd.readLine();
      }
    } finally {
      rd.close();
    }

    return builder.toString();
  }
}
