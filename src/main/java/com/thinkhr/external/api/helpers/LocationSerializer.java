package com.thinkhr.external.api.helpers;

import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thinkhr.external.api.db.entities.Location;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;

/**
 * Custom serializer to seriallize Location  list in Company Entity
 * 
 * @author Ajay Jain
 * @Since 2018-02-16
 *
 */
public class LocationSerializer extends JsonSerializer<List<Location>> {

    @Override
    public void serialize(List<Location> locations, JsonGenerator jGen, SerializerProvider serializerProvider) {
        try {
            Location location = locations.get(0);
            location.setCompany(null);
            jGen.writeObject(location);
        } catch (Exception ex) {
            throw ApplicationException.createInternalError(APIErrorCodes.ERROR_WRITING_JSON_OUTPUT, null);
        }
    }
}