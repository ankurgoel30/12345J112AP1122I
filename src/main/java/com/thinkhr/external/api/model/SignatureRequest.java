package com.thinkhr.external.api.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Ajay Jain
 *
 */
@Data
@NoArgsConstructor
public class SignatureRequest {
    List<Signer> signers;
}
