package com.rj.business;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Db<T> {
    List<T> listAll();

    Optional<T> findById(Long id);

    boolean insert();

    boolean update();

    boolean delete();

    List<T> listAllFiltered(Map<String, String> filters);
}
