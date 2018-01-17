package com.thinkhr.external.api.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class RequestCampaign {
    private String endpoint;
    public List<Integer> leads = new ArrayList<Integer>();
    public List<JsonObject> tokens = new ArrayList<JsonObject>();

    public RequestCampaign(String baseUri, String campaignId) {
        this.endpoint = baseUri + "/rest/v1/campaigns/" + campaignId + "/trigger.json";
    }

    public RequestCampaign setLeads(ArrayList<Integer> leads) {
        this.leads = leads;
        return this;
    }

    public RequestCampaign addLead(int lead) {
        leads.add(lead);
        return this;
    }

    public RequestCampaign setTokens(ArrayList<JsonObject> tokens) {
        this.tokens = tokens;
        return this;
    }

    public RequestCampaign addToken(String tokenKey, String val) {
        JsonObject jo = new JsonObject().add("name", tokenKey);
        jo.add("value", val);
        tokens.add(jo);
        return this;
    }

    public JsonObject postData(String accessToken) {
        JsonObject result = null;
        try {
            JsonObject requestBody = buildRequest(); //builds the Json Request Body

            String s = endpoint + "?access_token=" + accessToken; //takes the endpoint URL and appends the access_token parameter to authenticate

            //System.out.println("Executing RequestCampaign calln" + "Endpoint: " + s + "nRequest Body:n" + requestBody);
            URL url = new URL(s);
            HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection(); //Return a URL connection and cast to HttpsURLConnection
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-type", "application/json");
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
            wr.write(requestBody.toString());
            wr.flush();

            InputStream inStream = urlConn.getInputStream(); //get the inputStream from the URL connection
            Reader reader = new InputStreamReader(inStream);
            result = JsonObject.readFrom(reader); //Read from the stream into a JsonObject

            System.out.println("Result:n" + result);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * Takes the input and assembles a json representation to submit
     * @return
     */
    private JsonObject buildRequest() {
        JsonObject requestBody = new JsonObject(); //Create a new JsonObject for the Request Body
        JsonObject input = new JsonObject();
        JsonArray leadsArray = new JsonArray();
        for (int lead : leads) {
            JsonObject jo = new JsonObject().add("id", lead);
            leadsArray.add(jo);
        }
        input.add("leads", leadsArray);

        //assemble array of tokens and add to input if present
        if (tokens != null) {
            JsonArray tokensArray = new JsonArray();
            for (JsonObject jo : tokens) {
                tokensArray.add(jo);
            }
            input.add("tokens", tokensArray);
        }
        requestBody.add("input", input);
        return requestBody;
    }
}