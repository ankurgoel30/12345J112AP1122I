package com.thinkhr.external.api.model;

import lombok.Data;

@Data
public class SignerViewRequest  {
    String envelopeId;
    Signer signer;
}
