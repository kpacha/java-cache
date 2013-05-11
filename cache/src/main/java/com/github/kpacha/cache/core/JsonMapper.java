package com.github.kpacha.cache.core;

public interface JsonMapper {

    public Object unserialize(String serialized);

    public Object unserializeList(String serialized);

    public Object unserializeMap(String serialized);

    public String serialize(Object entity);
}
