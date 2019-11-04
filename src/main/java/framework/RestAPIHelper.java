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

    /**
     * Create a new helper instance for the specified endpoint
     * @param url URL of the endpoint we will submit the request to
     */
    public RestAPIHelper(String url) {
        requestURL = url;
        requestCookies = new BasicCookieStore();
        requestHeaders = new HashSet<>();
    }

    /**
     * Adds a header to the request
     * @param name Name of the header
     * @param value Value of the header
     */
    public void addRequestHeader(String name, String value) {
        requestHeaders.add(new BasicHeader(name, value));
    }

    /**
     * Adds a cookie to the request
     * @param name Name of the cookie
     * @param value Value of the cookie
     * @param domain Domain the cookie applies to
     * @param path Path the cookie applies to
     * @param secure True to set the secure flag (submit over https only) on the cookie
     * @param httpOnly True to set the http only flag (prevents access to the cookie by JS)
     */
    public void addRequestCookie(String name, String value, String domain, String path, boolean secure, boolean httpOnly) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setSecure(secure);
        cookie.setAttribute("httponly", String.valueOf(httpOnly));

        requestCookies.addCookie(cookie);
    }

    /**
     * Adds a cookie to the request with the secure and httpOnly flags set
     * @param name Name of the cookie
     * @param value Value of the cookie
     * @param domain Domain the cookie applies to
     * @param path Path the cookie applies to
     */
    public void addRequestCookie(String name, String value, String domain, String path) {
        addRequestCookie(name, value, domain, path, true, true);
    }

    /**
     * Set the body value of the request
     * @param body Request body as a string
     */
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

    /**
     * Submit the request as the specified type
     * @param type Request type
     */
    private void submitRequest(requestType type) {
        response = null;

        try {

            if (requestURL == null) {
                throw new IllegalStateException("Request URL has not been specified");
            }

            CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(requestCookies).build();

            switch(type){

                case GET:
                    HttpGet getRequest = new HttpGet(requestURL);
                    requestHeaders.forEach(getRequest::addHeader);
                    response = httpclient.execute(getRequest);
                    break;

                case POST:
                    HttpPost postRequest = new HttpPost(requestURL);
                    requestHeaders.forEach(postRequest::addHeader);
                    if (requestBody != null) { postRequest.setEntity(new StringEntity(requestBody)); }
                    response = httpclient.execute(postRequest);
                    break;

                case PUT:
                    HttpPut putRequest = new HttpPut(requestURL);
                    requestHeaders.forEach(putRequest::addHeader);
                    if (requestBody != null) { putRequest.setEntity(new StringEntity(requestBody)); }
                    response = httpclient.execute(putRequest);
                    break;

                case DELETE:
                    HttpDelete deleterRequest = new HttpDelete(requestURL);
                    requestHeaders.forEach(deleterRequest::addHeader);
                    response = httpclient.execute(deleterRequest);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the http response code we received in response to our request
     * @return HTTP response code as a integer
     */
    public int getResponseCode() {

        int code = 0;

        if (response != null) {
            code = response.getStatusLine().getStatusCode();
        }

        return code;
    }

    /**
     * Returns the message body we received in response to our request
     * @return Message body as a string
     */
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
        DELETE()
    }
}