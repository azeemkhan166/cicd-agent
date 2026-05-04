package com.realcoderz.serviceImpl;

import java.util.Map;
import java.util.List;
import java.util.Date;
import org.slf4j.Logger;
import java.util.HashMap;
import java.time.LocalDate;
import org.slf4j.LoggerFactory;
import com.realcoderz.model.Employee;
import org.springframework.stereotype.Service;
import com.realcoderz.model.SalaryHistoryRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.repository.NewEmployeeRepository;
import com.realcoderz.service.SalaryHistoryRecordService;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.beans.factory.annotation.Autowired;
import com.realcoderz.repository.SalaryHistoryRecordRepository;
import java.text.SimpleDateFormat;

/**
 *
 * @author Astha
 */
@Service
public class SalaryHistoryRecordServiceImpl implements SalaryHistoryRecordService {

    @Autowired
    SalaryHistoryRecordRepository historyRepo;

    @Autowired
    NewEmployeeRepository repo;

    Employee e = new Employee();

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(SalaryHistoryRecordServiceImpl.class);

    @Override
    public Map saveSalaryHistoryRecord(Map map) {
        Map resultMap = new HashMap<>();
        try {
            SalaryHistoryRecord history = mapper.convertValue(map, SalaryHistoryRecord.class);
            if (history != null) {
                historyRepo.save(history);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while saving SalaryHistoryRecord..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in SalaryHistoryRecordServiceImpl -> saveSalaryHistoryRecord() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getSalaryHistoryRecord(Map map) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> history = historyRepo.getSalaryHistoryRecordById(Long.parseLong(map.get("organization_id").toString()), Long.parseLong(map.get("employee_id").toString()));
            if (history != null) {
                resultMap.clear();
                logger.info("Salary History Record fetch with employee Id" + map.get("employee_id"));
                resultMap.put("status", "success");
                resultMap.put("list", history);
            } else {
                resultMap.clear();
                logger.error("Salary History Record is not available..!");
                resultMap.put("status", "error");
                resultMap.put("msg", "Salary History Record is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in SalaryHistoryRecordServiceImpl -> getSalaryHistoryRecord() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map GrossSalaryUpdate(Map map) {
        Map resultMap = new HashMap<>();
        try {
            Employee emp = mapper.convertValue((map), Employee.class);
            if (emp != null) {
                if (map.containsKey("effective_date") && map.get("effective_date") != null) {
                    if (map.get("effective_date").toString().equals(LocalDate.now().toString())) {
                        if (map.containsKey("gross_salary") && map.get("gross_salary") != null) {
                            repo.updatingEffectiveDate(Double.parseDouble(map.get("gross_salary").toString()), repo.getSalaryById(emp.getEmployee_id()));
                            resultMap.put("salary", map.get("gross_salary"));
                        }
                    }
                }
            }
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in SalaryHistoryRecordServiceImpl -> updateSalaryHistoryRecord() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map saveOrUpdateGrossSalary(Map map) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> history = historyRepo.getSalaryHistoryRecordById(Long.parseLong(map.get("organization_id").toString()), Long.parseLong(map.get("employee_id").toString()));
            if (history != null) {
                resultMap.clear();
                logger.info("Salary History Record fetch with employee Id" + map.get("employee_id"));
                resultMap.put("status", "success");
                resultMap.put("list", history);
            } else {
                resultMap.clear();
                logger.error("Salary History Record is not available..!");
                resultMap.put("status", "error");
                resultMap.put("msg", "Salary History Record is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in SalaryHistoryRecordServiceImpl -> getSalaryHistoryRecord() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map saveGrossSalaryInEmployee() {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> salaryRecord = historyRepo.getLatestAppraisal();
            salaryRecord.stream().forEach(s -> {
                Date d = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
                String currentdate=formatter.format(d).toString();
                String effectiveDate = s.get("effective_date").toString();
//                String year = effectiveDate.split("-")[0];
//                String month = effectiveDate.split("-")[1];
//                String date = effectiveDate.split("-")[2];
//                if (Integer.parseInt(year) <= (d.getYear() + 1900)) {
//                    if (Integer.parseInt(month) < (d.getMonth() + 1)) {
//                        int isSalaryUpdatedInEmployee = repo.isEmployeeSalaryExist(Long.parseLong(s.get("employee_id").toString()), Long.parseLong(s.get("organization_id").toString()));
//                        if (isSalaryUpdatedInEmployee > 0) {
//                            repo.updateGrossSalary((Double) s.get("appraisal_salary"), Long.parseLong(s.get("employee_id").toString()), Long.parseLong(s.get("organization_id").toString()));
//                        } else {
//                            e = new Employee();
//                            e.setGross_salary((Double) s.get("appraisal_salary"));
//                            e.setEmployee_id(Long.parseLong(s.get("employee_id").toString()));
//                            e.setOrganization_id(Long.parseLong(s.get("organization_id").toString()));
//                            repo.save(e);
//                        }
//                    } else if (Integer.parseInt(month) == (d.getMonth() + 1)) {
//                        if (Integer.parseInt(date) == 1) {
//                            int isSalaryUpdatedInEmployee = repo.isEmployeeSalaryExist(Long.parseLong(s.get("employee_id").toString()), Long.parseLong(s.get("organization_id").toString()));
//                            if (isSalaryUpdatedInEmployee > 0) {
//                                repo.updateGrossSalary((Double) s.get("appraisal_salary"), Long.parseLong(s.get("employee_id").toString()), Long.parseLong(s.get("organization_id").toString()));
//                            } else {
//                                e = new Employee();
//                                e.setGross_salary((Double) s.get("appraisal_salary"));
//                                e.setEmployee_id(Long.parseLong(s.get("employee_id").toString()));
//                                e.setOrganization_id(Long.parseLong(s.get("organization_id").toString()));
//                                repo.save(e);
//                            }
//                        }
//                    }
//                }
                       if(s.containsKey("effective_date") && s.get("effective_date").toString()!=null){
               if(effectiveDate.equals(currentdate)){
                   repo.updateGrossSalary((Double) s.get("appraisal_salary"), Long.parseLong(s.get("employee_id").toString()), Long.parseLong(s.get("organization_id").toString()));
               }
               }else {
                  e = new Employee();
                   e.setGross_salary((Double) s.get("gross_salary"));
                   e.setEmployee_id(Long.parseLong(s.get("employee_id").toString()));
                   e.setOrganization_id(Long.parseLong(s.get("organization_id").toString()));
                   repo.save(e);
               }
            });
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in SalaryHistoryRecordServiceImpl -> saveGrossSalaryInEmployee() :: ", ex);

        }
        return resultMap;
    }
}