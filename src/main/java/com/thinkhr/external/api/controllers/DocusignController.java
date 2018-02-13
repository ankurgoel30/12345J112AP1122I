package com.thinkhr.external.api.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.ViewUrl;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.model.SignatureRequest;
import com.thinkhr.external.api.model.Signer;
import com.thinkhr.external.api.services.docusign.DocusignService;

/**
 * Controller class for Docusign related API
 * @author Ajay Jain
 *
 */
@RestController
@Validated
@RequestMapping(path = "/v1/docusign")
public class DocusignController {

    private Logger logger = LoggerFactory.getLogger(DocusignController.class);

    @Autowired
    DocusignService docusignService;

    @Autowired
    MessageResourceHandler resourceHandler;


    /**
     * Creates signature request from existing Template and returns envelope information
     * @throws ApiException 
     * 
     */
    @RequestMapping(method = RequestMethod.POST, value = "/signature")
    public ResponseEntity<EnvelopeSummary> requestSignatreFromTemplate(
            @Valid @RequestBody SignatureRequest signatureReq) throws ApiException {
        EnvelopeSummary envelope = docusignService.requestSignatureFromTemplate(signatureReq);

        return new ResponseEntity<EnvelopeSummary>(envelope, HttpStatus.CREATED);
    }


    /**
     * Creates a signature request from template and generate ViewUrl for each signer
     * @param signatureReq
     * @return
     * @throws ApiException 
     */
    @RequestMapping(method = RequestMethod.POST, value = "/signature/viewUrl")
    public ResponseEntity<ViewUrl[]> requestSignatreFromTemplateandGenerateViewUrl(
            @Valid @RequestBody SignatureRequest signatureReq) throws ApiException {
        EnvelopeSummary envelope = docusignService.requestSignatureFromTemplate(signatureReq);
        List<ViewUrl> viewUrls = new ArrayList<ViewUrl>();
        for (Signer signer : signatureReq.getSigners()) {
            ViewUrl viewUrl = docusignService.generateRecipientViewForEmbeddedSigning(envelope.getEnvelopeId(), signer);
            viewUrls.add(viewUrl);
        }
        
        ViewUrl[] viewUrlArray = new ViewUrl[viewUrls.size()];
        return new ResponseEntity<ViewUrl[]>(viewUrls.toArray(viewUrlArray), HttpStatus.CREATED);
    }

    /**
     * Get signer status for given envelopeId and signer.
     * @param envelopeId
     * @param signer
     * @return
     * @throws ApiException 
     */
    @RequestMapping(method = RequestMethod.POST, value = "/signature/{envelopeId}/status")
    public ResponseEntity<Signer> getSignerStatus(
            @PathVariable(name = "envelopeId", value = "envelopeId") String envelopeId,
            @Valid @RequestBody Signer signer) throws ApiException {
        Signer signerStatus = docusignService.getRecipientsStatus(envelopeId, signer);

        return new ResponseEntity<Signer>(signerStatus, HttpStatus.CREATED);
    }

    /**
     * Get ViewUrl for given envelopId and signer.
     * @param envelopeId
     * @param signer
     * @return
     * @throws ApiException 
     */
    @RequestMapping(method = RequestMethod.POST, value = "/signature/{envelopeId}/viewUrl")
    public ResponseEntity<ViewUrl> getViewUrl(
            @PathVariable(name = "envelopeId", value = "envelopeId") String envelopeId,
            @Valid @RequestBody Signer signer) throws ApiException {
        ViewUrl viewUrl = docusignService.generateRecipientViewForEmbeddedSigning(envelopeId, signer);

        return new ResponseEntity<ViewUrl>(viewUrl, HttpStatus.CREATED);
    }


}