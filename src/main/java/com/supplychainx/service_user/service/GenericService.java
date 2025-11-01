package com.supplychainx.service_user.service;

import java.util.List;

public interface GenericService<T, ID> {

    T create(T entity);

    T getById(ID id);

    T update(T entity);

    void delete(ID id);

    List<T> getAll();

}
