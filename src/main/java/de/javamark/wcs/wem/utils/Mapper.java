/**
 * 
 */
package de.javamark.wcs.wem.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 	@author mark
 *	Simple ObjectMapper to map Sites XML Entities to JSON Feeds
 */
public class Mapper {
	public static String toJSON(Object value) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();		
		return mapper.writeValueAsString(value);
	}
}
