package com.example.teamcity.api.requests;

public interface CrudInterface {
    Object create(Object obj);

    Object get(String id);

    Object update(String id, Object obj);

    Object delete(String id);
}