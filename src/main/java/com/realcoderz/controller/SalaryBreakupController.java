package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.SalaryBreakupService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Lalit Raghav edited By Astha
 */
@RestController
@RequestMapping(path = "/salarybreakup")
public class SalaryBreakupController {

    static final Logger logger = LoggerFactory.getLogger(SalaryBreakupController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SalaryBreakupService salarybreakupservice;

    @PostMapping(path = "/addsalary")
    public Map addsalarybreakup(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = salarybreakupservice.save(map);
        } catch (Exception ex) {
            logger.info("Problem in SalaryBreakupController -> addsalarybreakup() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;

    }
    // ----------------------updtae----------------

    @PostMapping(path = "/findBysalarybreakupUpdate")

    public Map getSalaryBreakupDetailsById(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

            resultMap = salarybreakupservice.findSalaryDetailsById(Long.parseLong(map.get("id").toString()));
            System.out.println(map.get("id"));
        } catch (Exception ex) {
            logger.info("Problem in SalaryBreakupController -> getSalaryBreakupDetailsById() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getCalculatedData")

    public Map getCalculatedData(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {
//            Map<String, Object> map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            String employeeType = "Full time";
            String employeeType1 = "Consultant";
            String employeeType2 = "Intern";
            String employeeType3 = "Permanent";
            String employeeType4 = "Probation";
            String employeeType5= "Worker";
            String employeeType6 = "Contract";
            Map<String, Object> map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            System.out.println("search emptype1" + map);
            if (map.containsKey("employee_Type") && (map.get("employee_Type").toString().equalsIgnoreCase(employeeType) || (map.get("employee_Type").toString().equalsIgnoreCase(employeeType4)) || (map.get("employee_Type").toString().equalsIgnoreCase(employeeType3)) || (map.get("employee_Type").toString().equalsIgnoreCase(employeeType5)))) {
                resultMap = salarybreakupservice.calculateSalaryDataNew(data, request);
                //  resultMap = salarybreakupservice.calculateSalaryDataPreviousVersion(data, request);
            } else if (map.containsKey("employee_Type") && (map.get("employee_Type").toString().equalsIgnoreCase(employeeType1) || map.get("employee_Type").toString().equalsIgnoreCase(employeeType6))) {
                resultMap = salarybreakupservice.SalaryBreakUporConsultant(data, request);
//                System.out.println("emp consultant"+resultMap);
            } else if (map.containsKey("employee_Type") && map.get("employee_Type").toString().equalsIgnoreCase(employeeType2)) {
                resultMap = salarybreakupservice.SalaryBreakUporIntern(data, request);
            } else {
                resultMap.put("status", "Not Valid Employee Type");

            }

        } catch (Exception ex) {
            logger.info("Problem in SalaryBreakupController -> getCalculatedData() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    // API for getting calculated Salary data in PDF format
    @PostMapping(path = "/getCalculatedDataInPDF")
    public Map getcalculateSalaryDataInPDF(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            String employeeType = "Full time";
            String employeeType1 = "Consultant";
            String employeeType2 = "Intern";
            String employeeType3 = "Permanent";
            String employeeType4 = "Probation";
            String employeeType5 = "Worker";
            String employeeType6 = "Contract";
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);           
            if (map.containsKey("employeeType") && (employeeType.equalsIgnoreCase((String) map.get("employeeType")) || employeeType3.equalsIgnoreCase((String) map.get("employeeType")) || employeeType4.equalsIgnoreCase((String) map.get("employeeType"))) || employeeType5.equalsIgnoreCase((String) map.get("employeeType"))) {
                resultMap = salarybreakupservice.calculateSalaryDataInPDF(data);
            } else if (map.containsKey("employeeType") && employeeType1.equalsIgnoreCase((String) map.get("employeeType")) || map.containsKey("employeeType") && employeeType6.equalsIgnoreCase((String) map.get("employeeType"))) {
                resultMap = salarybreakupservice.calSalaryDataInPdfForConsultant(data);
            } else if (map.containsKey("employeeType") && employeeType2.equalsIgnoreCase((String) map.get("employeeType"))) {
                resultMap = salarybreakupservice.calSalaryDataInPdfForIntern(data);
            }
        } catch (Exception ex) {
            logger.info("Problem in SalaryBreakupController -> getcalculateSalaryDataInPDF() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getEPF")
    public Map getEPFData(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            System.out.println("epf checked" + map);
            resultMap = salarybreakupservice.calculateEPF(map.get("epf").toString(), Long.parseLong(map.get("organization_id").toString()), Double.parseDouble(map.get("basic").toString()), Double.parseDouble(map.get("payableBasic").toString()), Double.parseDouble(map.get("payableDeduction").toString()), Double.parseDouble(map.get("netAmount").toString()), Double.parseDouble(map.get("gross").toString()), data, request, Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), Double.parseDouble(map.get("working_day").toString()), Double.parseDouble(map.get("total_days").toString()));
        } catch (Exception ex) {
            logger.info("Problem in SalaryBreakupController -> getEPFData() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getEmployeeCurrentDeatils")
    public Map fetchDataInEmployeeDashBoard(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = salarybreakupservice.getEmployeeCurrentDeatils(data);
        } catch (Exception ex) {
            logger.info("Problem in SalaryBreakupController -> fetchDataInEmployeeDashBoard() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/savePdf")
    public Map addPdfTemplate(@RequestParam("pdf") MultipartFile fileStream, @RequestParam("employee_id") int empId, @RequestParam("month") int month, @RequestParam("year") int year, @RequestParam("organization_id") Long orgId) {
        Map resultMap = new HashMap<>();
        try {
            resultMap = salarybreakupservice.savePdf(fileStream, empId, month, year, orgId);
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.error("Problem in SalaryBreakupController -> addPdfTemplate() :: ", ex);
        }
        return resultMap;
    }

    @PostMapping(path = "/getPdfurl")
    public Map getPdfurl(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            resultMap = salarybreakupservice.getPdfurl(data);
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.error("Problem in SalaryBreakupController -> getPdfurl() :: ", ex);
        }
        return resultMap;
    }

    @PostMapping(path = "/dropdownForYear")
    public Map financialYearDropdown(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            if (map.containsKey("joining_date") && map.get("joining_date") != null) {
                resultMap = salarybreakupservice.financialYearDropdown(map.get("joining_date").toString());
            } else {
                resultMap.clear();
                resultMap.put("msg", "Joining Date not found");
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.error("Problem in SalaryBreakupController -> financialYearDropdown() :: ", ex);
        }
        return resultMap;
    }

    @PostMapping(path = "/dropdownForMonth")
    public Map financialMonthDropdown(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            if (map.containsKey("joining_date") && map.get("joining_date") != null) {
                if (map.containsKey("year") && map.get("year") != null) {
                    resultMap = salarybreakupservice.financialMonthDropdown(map.get("joining_date").toString(), map.get("year").toString());
                } else {
                    resultMap.clear();
                    resultMap.put("msg", "Financial Year not found");
                    resultMap.put("status", "error");
                }
            } else {
                resultMap.clear();
                resultMap.put("msg", "Joining Date not found");
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.error("Problem in SalaryBreakupController -> financialMonthDropdown() :: ", ex);
        }
        return resultMap;
    }

    // API for fetching offer salary from offer letter and gross salary from payroll
    @PostMapping(path = "/getGrossSalary")
    public Map getGrossSalary(@RequestBody String data, HttpServletRequest request) {
        logger.info("In SalaryBreakupController -> getGrossSalary method started.");
        Map resultMap = new HashMap<>();
        try {
            resultMap = salarybreakupservice.getGrossSalary(data, request);
            logger.info("In SalaryBreakupController -> getGrossSalary :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.info("Problem in SalaryBreakupController -> getGrossSalary() :: ", ex);
            resultMap.put("status", "exception");
        }
        logger.info("In SalaryBreakupController -> getGrossSalary method executed succcessfuly !!");
        return resultMap;
    }

    // API for fetching Organization Epf Status
    @PostMapping(path = "/fetchOrganizationEpfStatus")
    public Map fetchOrganizationEpfStatus(@RequestBody String data) {
        logger.info("In SalaryBreakupController -> fetchOrganizationEpfStatus method started.");
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            logger.info("In SalaryBreakupController -> fetchOrganizationEpfStatus :: Request Data :-" + map);
            resultMap = salarybreakupservice.fetchOrganizationEpfStatus(Long.parseLong(map.get("organization_id").toString()));
            logger.info("In SalaryBreakupController -> fetchOrganizationEpfStatus :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.error("Problem in SalaryBreakupController -> fetchOrganizationEpfStatus() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        logger.info("In SalaryBreakupController -> fetchOrganizationEpfStatus method executed succcessfuly !!");
        return resultMap;
    }

    // API for fetching Organization Esic Status
    @PostMapping(path = "/fetchOrganizationEsicStatus")
    public Map fetchOrganizationEsicStatus(@RequestBody String data) {
        logger.info("In SalaryBreakupController -> fetchOrganizationEsicStatus method started.");
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            logger.info("In SalaryBreakupController -> fetchOrganizationEsicStatus :: Request Data :-" + map);
            resultMap = salarybreakupservice.fetchOrganizationEsicStatus(Long.parseLong(map.get("organization_id").toString()));
            logger.info("In SalaryBreakupController -> fetchOrganizationEsicStatus :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.error("Problem in SalaryBreakupController -> fetchOrganizationEsicStatus() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        logger.info("In SalaryBreakupController -> fetchOrganizationEsicStatus method executed succcessfuly !!");
        return resultMap;
    }

    @PostMapping(path = "/getBifurcation")
    public Map calculateBifurcation(@RequestBody String data) {
        logger.info("In SalaryBreakupController -> calculateBifurcation method started.");
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            logger.info("In SalaryBreakupController -> calculateBifurcation :: Request Data :-" + map);
            resultMap = salarybreakupservice.calculateBifurcationOnGross(map);
            logger.info("In SalaryBreakupController -> calculateBifurcation :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.error("Problem in SalaryBreakupController -> calculateBifurcation() :: ", ex);
            resultMap.clear();
            resultMap.put("msg", ex.getMessage());
            resultMap.put("status", "exception");
        }
        logger.info("In SalaryBreakupController -> calculateBifurcation method executed succcessfuly !!");
        return resultMap;
    }
    
    
        // API for fetching offer salary from offer letter and gross salary from payroll
    @PostMapping(path = "/getGrossSalaryOfEmployee")
    public Map getGrossSalaryOfEmployee(@RequestBody String data, HttpServletRequest request) {
        logger.info("In SalaryBreakupController -> getGrossSalaryOfEmployee method started.");
        Map resultMap = new HashMap<>();
        try {
            resultMap = salarybreakupservice.getGrossSalaryOfEmployee(data, request);
            logger.info("In SalaryBreakupController -> getGrossSalaryOfEmployee :: Response Data :-" + resultMap);
        } catch (Exception ex) {
            logger.info("Problem in SalaryBreakupController -> getGrossSalaryOfEmployee() :: ", ex);
            resultMap.put("status", "exception");
        }
        logger.info("In SalaryBreakupController -> getGrossSalaryOfEmployee method executed succcessfuly !!");
        return resultMap;
    }

}
