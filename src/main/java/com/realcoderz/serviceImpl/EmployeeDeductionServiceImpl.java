/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.EmployeeDeduction;
import com.realcoderz.repository.EmployeeDeductionRepository;
import com.realcoderz.service.EmployeeDeductionService;
import static com.realcoderz.serviceImpl.DeductionServiceImpl.logger;
import static com.realcoderz.serviceImpl.SalaryBreakupServiceImpl.LOGGER;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Astha
 */
@Service
@RequiredArgsConstructor
public class EmployeeDeductionServiceImpl implements EmployeeDeductionService {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final EmployeeDeductionRepository deductionRepo;

    //    Get Employee Deductions By Id
    @Override
    public Map getEmployeeDeduction(Long id) {
        Map resultMap = new HashMap<>();
        try {
            Optional<EmployeeDeduction> deduction = deductionRepo.findById(id);
            if (deduction.isPresent()) {
                resultMap.clear();
                resultMap.put("list", deduction);
                resultMap.put("status", "success");

            } else {
                resultMap.clear();
                resultMap.put("status", "error");

            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in EmployeeDeductionServiceImpl -> getEmployeeDeduction() :: ", ex);
        }

        return resultMap;
    }

    //    Save Employee Deductions
    @Override
    public Map saveEmployeeDeduction(Map map) {
        Map resultMap = new HashMap<>();
        try {
            List<EmployeeDeduction> empDeduction = mapper.convertValue(map, new TypeReference<List<EmployeeDeduction>>() {
            });
            //            Deduction is Empty
            if (!empDeduction.isEmpty()) {
                deductionRepo.saveAll(empDeduction);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Deduction not saved.!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeDeductionServiceImpl -> saveEmployeeDeduction() :: ", ex);

        }
        return resultMap;

    }
}
