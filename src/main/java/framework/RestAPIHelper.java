package framework;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;

public class RestAPIHelper {

    private CloseableHttpResponse response;

    public void makeGetRequest(String url, HashSet<BasicHeader> headers) {

        response = null;

        try {
            // Create an HTTP client to submit our request with
            CloseableHttpClient httpclient = HttpClients.createDefault();

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

    public void makePostRequest(String url, HashSet<BasicHeader> headers, JSONObject requestBodyJSON) {

        response = null;

        try {
            // Create an HTTP client to submit our request with
            CloseableHttpClient httpclient = HttpClients.createDefault();

            // Create the POST request
            HttpPost postRequest = new HttpPost(url);

            // Add headers
            if (headers != null) { headers.forEach(postRequest::addHeader); }

            // Add the body
            if (requestBodyJSON != null) { postRequest.setEntity(new StringEntity(requestBodyJSON.toString())); }

            // Submit the request and store the response
            response = httpclient.execute(postRequest);

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
}
