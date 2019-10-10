package framework;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashSet;

/**
 * Simple helper class for a RESTful CRUD API
 */
public class RestAPIHelper {

    private BasicCookieStore requestCookies;
    private HashSet<BasicHeader> requestHeaders;
    private CloseableHttpResponse response;
    private String requestURL;
    private String requestBody;

    public RestAPIHelper() {
        requestCookies = new BasicCookieStore();
        requestHeaders = new HashSet<>();
    }

    public void addRequestHeader(String name, String value) {
        requestHeaders.add(new BasicHeader(name, value));
    }

    public void addRequestCookie(String name, String value, String domain, String path, boolean secure) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setSecure(secure);

        requestCookies.addCookie(cookie);
    }

    public void setRequestURL(String url) {
        requestURL = url;
    }

    public void setRequestBody(String body) {
        requestBody = body;
    }

    public void submitGetRequest() {
        submitRequest(requestType.GET);
    }

    public void submitPostRequest() {
        submitRequest(requestType.POST);
    }

    public void submitDeleteRequest() {
        submitRequest(requestType.DELETE);
    }

    public void submitPutRequest() {
        submitRequest(requestType.PUT);
    }

    private void submitRequest(requestType type) {
        response = null;

        try {
            CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(requestCookies).build();

            switch(type){

                case GET:
                    HttpGet getRequest = new HttpGet(requestURL);
                    if (requestHeaders != null) { requestHeaders.forEach(getRequest::addHeader); }
                    response = httpclient.execute(getRequest);
                    break;

                case POST:
                    HttpPost postRequest = new HttpPost(requestURL);
                    if (requestHeaders != null) { requestHeaders.forEach(postRequest::addHeader); }
                    if (requestBody != null) { postRequest.setEntity(new StringEntity(requestBody)); }
                    response = httpclient.execute(postRequest);
                    break;

                case PUT:
                    // Create the POST request
                    HttpPut putRequest = new HttpPut(requestURL);
                    if (requestHeaders != null) { requestHeaders.forEach(putRequest::addHeader); }
                    if (requestBody != null) { putRequest.setEntity(new StringEntity(requestBody)); }
                    response = httpclient.execute(putRequest);
                    break;

                case DELETE:
                    HttpDelete deleterRequest = new HttpDelete(requestURL);
                    if (requestHeaders != null) { requestHeaders.forEach(deleterRequest::addHeader); }
                    response = httpclient.execute(deleterRequest);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getResponseCode() {

        int code = 0;

        if (response != null) {
            code = response.getStatusLine().getStatusCode();
        }

        return code;
    }

    public String getResponseBody() {
        String body = null;

        if (response != null) {
            try {
                body = EntityUtils.toString(response.getEntity());
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        return body;
    }

    // TODO need to get cookies from the response as well

    private enum requestType {
        GET(),
        POST(),
        PUT(),
        DELETE();
    }
}