package com.thinkhr.external.api.helpers;

import static com.thinkhr.external.api.ApplicationConstants.DATE_PATTERN;
import static com.thinkhr.external.api.response.APIMessageUtil.getMessageFromResourceBundle;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.MessageResourceHandler;

/**
 * This is custom deserializer class to deserialize date values in json payload
 * @author Ajay Jain
 *
 */
@Component
public class JsonDateDeSerializer extends JsonDeserializer<Date> {

    @Autowired
    protected MessageResourceHandler resourceHandler;

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        format.setLenient(false);

        String date = p.getText();

        try {
            return format.parse(date);
        } catch (ParseException e) {
            String customMessage = getMessageFromResourceBundle(resourceHandler, APIErrorCodes.DATE_PARSE_ERROR,
                    DATE_PATTERN);

            throw new JsonParseException(e.getLocalizedMessage() + ". " + customMessage, null, e);
        }
    }
}
