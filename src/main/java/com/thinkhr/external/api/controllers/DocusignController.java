package com.thinkhr.external.api.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.ViewUrl;
import com.thinkhr.external.api.exception.MessageResourceHandler;
import com.thinkhr.external.api.model.SignatureRequest;
import com.thinkhr.external.api.model.SignerViewRequest;
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
     * Creates an envelop from existing template
     * and send it for signing to desired recipients
     * 
     */
    @RequestMapping(method = RequestMethod.POST, value = "/signature")
    public ResponseEntity<Recipients> requestSignatreFromTemplate(@Valid @RequestBody SignatureRequest signatureReq) {
        Recipients reci = docusignService.requestSignatureFromTemplate(signatureReq);

        return new ResponseEntity<Recipients>(reci, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/signature/url")
    public ResponseEntity<ViewUrl> requestRecipientView(@Valid @RequestBody SignerViewRequest request) {
        ViewUrl viewUrl = docusignService.getRecipientViewForEmbeddedSigning(request);

        return new ResponseEntity<ViewUrl>(viewUrl, HttpStatus.CREATED);
    }


}