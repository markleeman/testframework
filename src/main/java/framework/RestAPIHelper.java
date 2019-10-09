package framework;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashSet;

// TODO lots of duplicate code to cleanup

/**
 * Simple helper class for a RESTful CRUD API
 */
public class RestAPIHelper {

    private CloseableHttpResponse response;

    public void submitGetRequest(String url, HashSet<BasicHeader> headers, CookieStore cookies) {

        response = null;

        try {
            // Create an HTTP client to submit our request with
            CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookies).build();

            // Create the GET request
            HttpGet getRequest = new HttpGet(url);

            // Add headers
            if (headers != null) { headers.forEach(getRequest::addHeader); }

            // Submit the request and store the response
            response = httpclient.execute(getRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void submitPostRequest(String url, HashSet<BasicHeader> headers, CookieStore cookies, String requestBody) {

        response = null;

        try {
            // Create an HTTP client to submit our request with
            CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookies).build();

            // Create the POST request
            HttpPost request = new HttpPost(url);

            // Add headers
            if (headers != null) { headers.forEach(request::addHeader); }

            // Add the body
            if (requestBody != null) { request.setEntity(new StringEntity(requestBody)); }

            // Submit the request and store the response
            response = httpclient.execute(request);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void submitDeleteRequest(String url, HashSet<BasicHeader> headers, CookieStore cookies) {

        response = null;

        try {
            // Create an HTTP client to submit our request with
            CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookies).build();

            // Create the POST request
            HttpDelete request = new HttpDelete(url);

            // Add headers
            if (headers != null) { headers.forEach(request::addHeader); }

            // Submit the request and store the response
            response = httpclient.execute(request);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void submitPutRequest(String url, HashSet<BasicHeader> headers, CookieStore cookies, String requestBody) {

        response = null;

        try {
            // Create an HTTP client to submit our request with
            CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookies).build();

            // Create the POST request
            HttpPut request = new HttpPut(url);

            // Add headers
            if (headers != null) { headers.forEach(request::addHeader); }

            // Add the body
            if (requestBody != null) { request.setEntity(new StringEntity(requestBody)); }

            // Submit the request and store the response
            response = httpclient.execute(request);

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
}
