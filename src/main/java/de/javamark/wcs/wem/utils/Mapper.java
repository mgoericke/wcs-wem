/**
 * 
 */
package de.javamark.wcs.wem.utils;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.fatwire.rest.beans.FieldInfo;

/**
 * 	@author mark
 *	Simple ObjectMapper to map Sites XML Entities to JSON Feeds
 */
public class Mapper {
	public static String toJSON(Object value) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();		
		return mapper.writeValueAsString(value);
	}
	
	/**
	 * extract asset fields from fieldinfo
	 * @param fieldName
	 * @param fields
	 * @return
	 */
	public static String getField( String fieldName, List<FieldInfo> fields ){
        for (FieldInfo finfo : fields){
        	        	
            if (finfo.getFieldname().equals(fieldName)){
                return finfo.getData();
            }
        }
        
        return "";
    }
}
