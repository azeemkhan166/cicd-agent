package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.Employee;
import com.realcoderz.repository.NewEmployeeRepository;
import com.realcoderz.service.NewEmployeeService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Astha
 */
@Service
public class NewEmployeeServiceImpl implements NewEmployeeService {

    static final Logger LOGGER = LoggerFactory.getLogger(NewEmployeeServiceImpl.class);
    ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private NewEmployeeRepository repo;
    
    @Override
    public Map saveSalary(Map map) {
     Map resultMap = new HashMap<>();
        try {
            Employee employee = mapper.convertValue(map, Employee.class);
            if (employee != null) {
                repo.save(employee);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in NewEmployeeServiceImpl -> saveSalary() :: ", ex);
        }
        return resultMap;    }
    
    @Override
     public Map isExistSalary(Long id) {
     Map resultMap = new HashMap<>();
        try {
            long employee = repo.getSalaryById(id);
            if (employee >0) {
                resultMap.clear();
                 resultMap.put("result", true);
                resultMap.put("status", "success");
            } else {
                resultMap.put("result", false);
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in NewEmployeeServiceImpl -> saveSalary() :: ", ex);
        }
        return resultMap;    }
}