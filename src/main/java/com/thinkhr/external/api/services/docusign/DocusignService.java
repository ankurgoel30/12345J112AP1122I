package com.thinkhr.external.api.services.docusign;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.docusign.esign.api.AuthenticationApi;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.LoginAccount;
import com.docusign.esign.model.LoginInformation;
import com.docusign.esign.model.RecipientViewRequest;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.TemplateRole;
import com.docusign.esign.model.ViewUrl;
import com.thinkhr.external.api.model.SignatureRequest;
import com.thinkhr.external.api.model.SignerViewRequest;

/**
 * Service class for Docusign related functionality
 * @author Ajay Jain
 *
 */
@Service
public class DocusignService {

    @Value("${docusign.api.baseUrl}")
    private String baseUrl;

    @Value("${docusign.username}")
    private String userName;

    @Value("${docusign.password}")
    private String password;

    @Value("${docusign.integratorKey}")
    private String integratorKey;

    private LoginAccount docusignLoginAccount;

    private static String templateId = "13c0637e-da20-4b18-8b1c-30979d1329d9";
    private static String templateRoleName = "Admin";

    /**
     * 
     */
    private void authenticateDocuSign() {
        if (docusignLoginAccount != null) {
            return;
        }

        // Initialize the api client
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(baseUrl);

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

        docusignLoginAccount = loginAccounts.get(0);
    }

    /**
     * Prepare authentication Header for Docusign
     * @return
     */
    private String createDocuSignAuthHeader() {
        String creds = "{\"Username\":\"" + userName + "\",\"Password\":\"" + password + "\",\"IntegratorKey\":\""
                + integratorKey + "\"}";

        return creds;
    }

    /**
     * 
     * @param templateId
     * @param templateRoleName
     * @param users
     * @return
     */
    public Recipients requestSignatureFromTemplate(SignatureRequest signatureRequest) {
        // Autheticate to Docusign Account for accessing docusign APIs
        authenticateDocuSign();

        //===============================================================================
        // Create Envelope API (AKA Signature Request) from a Template
        //===============================================================================

        // create a new envelope object that we will manage the signature request through
        EnvelopeDefinition envDef = new EnvelopeDefinition();
        envDef.setEmailSubject("Please sign this document");

        // assign template information including ID and role(s)
        envDef.setTemplateId(signatureRequest.getTemplateId());

        // create a template role with a valid templateId and roleName and assign signer info
        List<TemplateRole> templateRolesList = new ArrayList<TemplateRole>();
        signatureRequest.getSigners().stream().forEach(signer -> {
            TemplateRole tRole = new TemplateRole();
            tRole.setRoleName(signatureRequest.getTemplateRoleName());
            tRole.setName(signer.getName());
            tRole.setEmail(signer.getEmail());
            tRole.setClientUserId(signer.getClientId());

            templateRolesList.add(tRole);
        });

        // assign template role(s) to the envelope 
        envDef.setTemplateRoles(templateRolesList);

        // send the envelope by setting |status| to "sent". To save as a draft set to "created"
        envDef.setStatus("sent");

        EnvelopeSummary envelopeSummary = null;
        try {
            // use the |accountId| we retrieved through the Login API to create the Envelope
            String accountId = docusignLoginAccount.getAccountId();

            // instantiate a new EnvelopesApi object
            EnvelopesApi envelopesApi = new EnvelopesApi();

            // call the createEnvelope() API
            envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);

            System.out.println("EnvelopeSummary: " + envelopeSummary);
        } catch (com.docusign.esign.client.ApiException ex) {
            System.out.println("Exception: " + ex);
        }

        Recipients recipients = getRecipients(envelopeSummary.getEnvelopeId());

        return recipients;
    }

    /**
     * 
     * @param envelopeId
     */
    public Recipients getRecipients(String envelopeId) {
        // Autheticate to Docusign Account for accessing docusign APIs
        authenticateDocuSign();

        Recipients recips = null;
        try {
            // use the |accountId| we retrieved through the Login API
            String accountId = docusignLoginAccount.getAccountId();

            // instantiate a new EnvelopesApi object
            EnvelopesApi envelopesApi = new EnvelopesApi();

            // call the listRecipients() API
            recips = envelopesApi.listRecipients(accountId, envelopeId);

            System.out.println("Recipients: " + recips);
        } catch (com.docusign.esign.client.ApiException ex) {
            System.out.println("Exception: " + ex);
        }

        return recips;
    }

    public ViewUrl getRecipientViewForEmbeddedSigning(SignerViewRequest request) {
        // Autheticate to Docusign Account for accessing docusign APIs
        authenticateDocuSign();

        ViewUrl recipientView = null;
        try {
            // use the |accountId| we retrieved through the Login API 
            String accountId = docusignLoginAccount.getAccountId();

            // instantiate a new EnvelopesApi object
            EnvelopesApi envelopesApi = new EnvelopesApi();

            // set the url where recipient need to go once they are done signing
            RecipientViewRequest returnUrl = new RecipientViewRequest();
            returnUrl.setReturnUrl("https://www.google.com");
            returnUrl.setAuthenticationMethod("email");

            // recipient information must match embedded recipient info we provided in step #2
            returnUrl.setEmail(request.getSigner().getEmail());
            returnUrl.setUserName(request.getSigner().getName());
            returnUrl.setRecipientId("1");
            returnUrl.setClientUserId(request.getSigner().getClientId());

            // call the CreateRecipientView API
            recipientView = envelopesApi.createRecipientView(accountId, request.getEnvelopeId(), returnUrl);

            System.out.println("ViewUrl: " + recipientView);
        } catch (com.docusign.esign.client.ApiException ex) {
            System.out.println("Exception: " + ex);
        }

        return recipientView;
    }

}
