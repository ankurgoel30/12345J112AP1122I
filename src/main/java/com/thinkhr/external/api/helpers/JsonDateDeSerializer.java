package com.thinkhr.external.api.helpers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonDateDeSerializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);

        String date = p.getText();

        try {
            return format.parse(date);
        } catch (ParseException e) {
            String customMessage = ".Date is not in format 'yyyy-MM-dd' or date is invalid calender date";
            throw new JsonParseException(e.getLocalizedMessage() + customMessage, null, e);
        }
    }
}
