package com.gradle.hw.service;

import java.util.Collection;
import java.util.Optional;

public interface IService<T> {
    Collection<T> findAll();
    T saveOrUpdate(T t);

    Optional<T> findById(Long id);
}
