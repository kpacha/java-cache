package com.github.kpacha.cache.core.mapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.github.kpacha.cache.core.JsonMapper;

public class JacksonMapper implements JsonMapper {

    private Class<?> clazz;
    private static ObjectMapper mapper = null;

    public JacksonMapper(final Class<?> clazz) {
	this.clazz = clazz;
	synchronized (this) {
	    if (mapper == null) {
		mapper = new ObjectMapper();
	    }
	}
    }

    private Object unserialize(final String serialized, final Class<?> clazz) {
	Object entity = null;
	try {
	    entity = mapper.readValue(serialized, clazz);
	} catch (JsonParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (JsonMappingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return entity;
    }

    @Override
    public String serialize(final Object entity) {
	String serialized = null;
	try {
	    serialized = mapper.writeValueAsString(entity);
	} catch (JsonGenerationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (JsonMappingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return serialized;
    }

    @Override
    public Object unserialize(final String serialized) {
	return unserialize(serialized, clazz);
    }

    @Override
    public Object unserializeList(final String serialized) {
	return unserialize(serialized, List.class);
    }

    @Override
    public Object unserializeMap(final String serialized) {
	return unserialize(serialized, Map.class);
    }
}
