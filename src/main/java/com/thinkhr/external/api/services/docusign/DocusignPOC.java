package com.thinkhr.external.api.services.docusign;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DocusignPOC {
    private String authUrl = "https://demo.docusign.net/restapi/v2/login_information";
    private String userName = "ankur.goel@pepcus.com";
    private String password = "abc12345";
    private String integratorKey = "7bab69ad-2c2a-480f-aad7-20366237d9c9";

    private String templateId = "13c0637e-da20-4b18-8b1c-30979d1329d9";

    public static void main(String[] args) {
        DocusignPOC poc = new DocusignPOC();
        poc.authenticateDocuSign();
    }

    public String authenticateDocuSign() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("X-DocuSign-Authentication", createDocuSignAuthHeader());
        
        HttpEntity<String> httpEntity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<JsonNode> response = null;
        try {
            response = restTemplate.exchange(authUrl, HttpMethod.GET, httpEntity, JsonNode.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        JsonNode res = response.getBody().findValue("loginAccounts");
        JsonNode loginInfo = res.get(0);
        JsonNode value = loginInfo.findValue("baseUrl");
        String baseurl = value.textValue();

        return baseurl;
    }

    private String createDocuSignAuthHeader() {
        DocusignAuthHeader authHeader = new DocusignAuthHeader(userName, password, integratorKey);
        ObjectMapper mapper = new ObjectMapper();
        String jsonValue = null;
        try {
            jsonValue = mapper.writeValueAsString(authHeader);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        jsonValue = "{\"Username\":\"ankur.goel@pepcus.com\",\"Password\":\"abc12345\",\"IntegratorKey\":\"7bab69ad-2c2a-480f-aad7-20366237d9c9\"}";
        return jsonValue;
    }
}
