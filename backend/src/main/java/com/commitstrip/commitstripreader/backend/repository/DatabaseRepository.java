package com.commitstrip.commitstripreader.backend.repository;

import com.commitstrip.commitstripreader.backend.dao.StripDao;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface DatabaseRepository extends PagingAndSortingRepository<StripDao, Long> {
    StripDao findFirst1ByOrderByReleaseDateDesc();
    StripDao findOneByTitle(String title);
    StripDao findFirst1ByOrderByIdDesc();
}
