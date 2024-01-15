
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class TestHttp {
    final static String url = "https://mvnrepository.com/artifact/org.apache.activemq/activemq-openwire-legacy";
    public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, UnsupportedEncodingException {
        //get2();
        testGet();

    }

    public static void testGet() throws UnsupportedEncodingException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext =
                new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();

        try {

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    SSLContexts.createDefault(),
                    new String[] {   "TLSv1.3" },
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept", "https://mvnrepository.com/artifact/org.apache.activemq/activemq-openwire-legacy");
            httpGet.setHeader("User-Agent",
                    "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/116.0");

            HttpResponse response = httpClient.execute(httpGet);

            System.out.println("Response: " + response);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
