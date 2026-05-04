package com.realcoderz.service;

import java.util.Map;
import org.springframework.stereotype.Service;

/**
 *
 * @author Astha
 */
@Service
public interface NewEmployeeService {
    
     public Map saveSalary(Map map);
     
     public Map isExistSalary(Long id);
    
}