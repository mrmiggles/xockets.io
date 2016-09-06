/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tc.websocket.valueobjects;


import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonDeserializer;

import com.tc.utils.DateUtils;
import com.tc.utils.StrUtils;
import com.tc.websocket.Const;


// TODO: Auto-generated Javadoc
/**
 * The Class CustomDateDeserializer.
 *
 * @author mwambler
 */
public class CustomDateDeserializer extends JsonDeserializer<Date> {

    
    

    /* (non-Javadoc)
     * @see org.codehaus.jackson.map.JsonDeserializer#deserialize(org.codehaus.jackson.JsonParser, org.codehaus.jackson.map.DeserializationContext)
     */
    @Override
	public Date deserialize(JsonParser jp, org.codehaus.jackson.map.DeserializationContext dc) throws IOException, JsonProcessingException {
        String date = jp.getText();

        if (StrUtils.isNumber(date)) {
            long l = Long.parseLong(date);
            return new Date(l); 
        } else {
            Date dt = DateUtils.getDateByString(Const.DATE_FORMAT, date);
            return dt;
        }
    }
}
