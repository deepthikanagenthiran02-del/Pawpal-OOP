
package com.pawpal.dao;

import java.util.List;

public interface BaseDAO<T> {

    boolean add(T item);

    List<T> findAll();

    boolean delete(int id);
}

