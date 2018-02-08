package com.thinkhr.external.api.services.docusign;

import java.util.ArrayList;
import java.util.List;

import com.docusign.esign.api.AuthenticationApi;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.LoginAccount;
import com.docusign.esign.model.LoginInformation;
import com.docusign.esign.model.TemplateRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Docusign {
    private String authUrl = "https://demo.docusign.net/restapi";
    private String userName = "ankur.goel@pepcus.com";
    private String password = "abc12345";
    private String integratorKey = "7bab69ad-2c2a-480f-aad7-20366237d9c9";

    private String templateId = "13c0637e-da20-4b18-8b1c-30979d1329d9";

    public static void main(String[] args) {
        Docusign poc = new Docusign();

        poc.RequestSignatureFromTemplate();
    }

    public List<LoginAccount> authenticateDocuSign() {
        // Initialize the api client
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(authUrl);

        String creds = createDocuSignAuthHeader();
        apiClient.addDefaultHeader("X-DocuSign-Authentication", creds);

        // assign api client to the Configuration object
        Configuration.setDefaultApiClient(apiClient);

        // list of user account(s)
        List<LoginAccount> loginAccounts = null;

        try {
            AuthenticationApi authApi = new AuthenticationApi();

            AuthenticationApi.LoginOptions loginOps = authApi.new LoginOptions();
            loginOps.setApiPassword("true");
            loginOps.setIncludeAccountIdGuid("true");

            LoginInformation loginInfo = authApi.login(loginOps);

            loginAccounts = loginInfo.getLoginAccounts();
        } catch (com.docusign.esign.client.ApiException ex) {
            System.out.println("Exception: " + ex);
        }

        return loginAccounts;
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

    /*****************************************************************************************************************
     * RequestSignatureFromTemplate() 
     * 
     * This recipe demonstrates how to request a signature from a template in your account.  Templates are design-time
     * objects that contain documents, tabs, routing, and recipient roles.  To run this recipe you need to provide a 
     * valid templateId from your account along with a role name that the template has configured. 
     ******************************************************************************************************************/
    public void RequestSignatureFromTemplate() {

        String signerName = "Surabhi Bhavsar";
        String signerEmail = "surabhi.bhavsar@gmail.com";
        String templateId = "746ef30e-0598-4feb-9f53-22401065bf55";
        String templateRoleName = "Admin";

        String signerName2 = "Ajay Jain";
        String signerEmail2 = "ajay.jain@pepcus.com";

        // list of user account(s)
        List<LoginAccount> loginAccounts = authenticateDocuSign();


        //===============================================================================
        // Step 2:  Create Envelope API (AKA Signature Request) from a Template
        //===============================================================================

        // create a new envelope object that we will manage the signature request through
        EnvelopeDefinition envDef = new EnvelopeDefinition();
        envDef.setEmailSubject("Please sign this document sent for POC )");

        // assign template information including ID and role(s)
        envDef.setTemplateId(templateId);

        // create a template role with a valid templateId and roleName and assign signer info
        TemplateRole tRole = new TemplateRole();
        tRole.setRoleName(templateRoleName);
        tRole.setName(signerName);
        tRole.setEmail(signerEmail);

        TemplateRole tRole2 = new TemplateRole();
        tRole2.setRoleName(templateRoleName);
        tRole2.setName(signerName2);
        tRole2.setEmail(signerEmail2);

        // create a list of template roles and add our newly created role
        List<TemplateRole> templateRolesList = new ArrayList<TemplateRole>();
        templateRolesList.add(tRole);
        templateRolesList.add(tRole2);

        // assign template role(s) to the envelope 
        envDef.setTemplateRoles(templateRolesList);

        // send the envelope by setting |status| to "sent". To save as a draft set to "created"
        envDef.setStatus("sent");

        try {
            // use the |accountId| we retrieved through the Login API to create the Envelope
            String accountId = loginAccounts.get(0).getAccountId();

            // instantiate a new EnvelopesApi object
            EnvelopesApi envelopesApi = new EnvelopesApi();

            // call the createEnvelope() API
            EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);

            System.out.println("EnvelopeSummary: " + envelopeSummary);
        } catch (com.docusign.esign.client.ApiException ex) {
            System.out.println("Exception: " + ex);
        }
    } // end RequestSignatureFromTemplate()
}
