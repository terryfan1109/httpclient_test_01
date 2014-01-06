package org.ops.httpclient.test;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Hello world!
 *
 */
public class App 
{
  public static void main( String[] args )
  {
    Security.addProvider(new BouncyCastleProvider());
    System.out.println( "Hello World!" );
    
    DefaultHttpRestClient httpClient = new DefaultHttpRestClient();
    DefaultHttpRestClient.Response response = httpClient.sendGetRequest(args[0]);
    
    System.out.println(String.format("code:%d\r\n%s", response.code, response.msg));
  }
}
