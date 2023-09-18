package com.atguigu.yygh.user.Repository;

import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentRepository extends MongoRepository<Department, String> {

    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);

    //Department findDepartmentByHoscodeAndDepcode(String hoscode);
    //Department findByhoscodeAndDepcode(String hoscode, String depcode);

    Department findByHoscodeAndDepcode(String hoscode, String depcode);
}
