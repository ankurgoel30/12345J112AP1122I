package com.thinkhr.external.api.helpers;

import static com.thinkhr.external.api.ApplicationConstants.DATE_PATTERN;
import static com.thinkhr.external.api.response.APIMessageUtil.getMessageFromResourceBundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkhr.external.api.db.entities.Location;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.MessageResourceHandler;

/**
 * This is custom deserializer class to deserialize location values in company payload
 * @author Ajay Jain
 *
 */
@Component
public class LocationDeserializer extends JsonDeserializer<List<Location>> {

    @Autowired
    protected MessageResourceHandler resourceHandler;

    @Override
    public List<Location> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        try {
            JsonNode node = p.getCodec().readTree(p);

            ObjectMapper mapper = new ObjectMapper();
            Location location = mapper.readValue(node.toString(), Location.class);
            List<Location> locationList = new ArrayList<Location>();
            locationList.add(location);

            return locationList;
        } catch (Exception e) {
            String customMessage = getMessageFromResourceBundle(resourceHandler, APIErrorCodes.DATE_PARSE_ERROR,
                    DATE_PATTERN);

            throw new JsonParseException(e.getLocalizedMessage() + ". " + customMessage, null, e);
        }
    }
}
