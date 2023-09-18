package com.atguigu.yygh.user.Repository;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HospitalRepository extends MongoRepository<Hospital,String> {

    Hospital findByHoscode(String hoscode);
    List<Hospital> findHospitalByHosnameLike(String name);
}
