/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.AccountDetails;
import com.realcoderz.service.EmployeeService;
import com.realcoderz.service.LabourLawDeductionService;
import com.realcoderz.serviceImpl.EmployeeServiceImpl;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Lalit Raghav
 */
@RestController

//@CrossOrigin(origins = "*")
@RequestMapping(path = "/employee")
public class Employeecontroller {

    static final Logger logger = LoggerFactory.getLogger(Employeecontroller.class);
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private EmployeeService employeeservice;

    @Autowired
    private EmployeeServiceImpl employeeServiceImpl;

    @Autowired
    private LabourLawDeductionService labourLawDeductionService;
    
    @Value("${vedantId}")
    private Long vedantId;

    // save data in payroll database;
    @PostMapping(path = "/add")
    public Map addemployeedetails(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = employeeservice.save(map);
        } catch (Exception ex) {
            logger.info("Problem in Employeecontroller -> addemployeedetails() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    @PostMapping(path = "/saveBankDetails")
    public Map addemployeeBankDetails(@RequestBody Map map) {
        Map resultMap = new HashMap<>();
        try {
            Map mp = mapper.readValue(EncryptDecryptUtils.decrypt(map.get("data").toString()), LinkedCaseInsensitiveMap.class);
            resultMap = employeeservice.save(mp);
        } catch (Exception ex) {
            logger.info("Problem in Employeecontroller -> addemployeeBankDetails() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    // save data in payroll database;
    @PostMapping(path = "/addBankDetails_Bulk")
    public Map addBankDetailsBulk(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = employeeservice.saveBUlkBankDetails(map);
        } catch (Exception ex) {
            logger.info("Problem in Employeecontroller -> addemployeedetails() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    // update data  like bankaccount,bank name etc.
    @PostMapping(path = "/getaccountsdetails")
    public Map getAccountDetailsById(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = employeeservice.findAccountDetailsById(data);
        } catch (Exception ex) {
            logger.info("Problem in Employeecontroller -> getAccountDetailsById() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    // data grid table data show on datagrid table
    @PostMapping(path = "/gets")
    public Map getAccountDetails(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {
            resultMap = employeeservice.findAll(request, data);
        } catch (Exception ex) {
            logger.info("Problem in Employeecontroller -> getAccountDetails() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(value = "/filter")

    public Map filterdataid() {
        Map resultMap = new HashMap();
        try {
            resultMap = employeeservice.filterID();
        } catch (Exception ex) {
            logger.info("Problem in Employeecontroller -> filterdataid() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    // This Api use by Performace

    @PostMapping(path = "/fetchbankdetails")
    public Map fetchBankDetailsByEmpId(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = employeeservice.fetchBankDetailsByEmpId(data);
        } catch (Exception ex) {
            logger.info("Problem in Employeecontroller -> fetchBankDetailsByEmpId() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getepdreport")
    public Map getEPFReport(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        try {
            
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

            Long orgId = Long.parseLong(map.get("id").toString());
            if(Objects.equals(orgId, vedantId)){
                System.out.println("Vedant EPF Report");
                resultMap = employeeservice.getEPFReportForVedant(data, request);
            }
            else{
                System.out.println("Other Client EPF Report");
                resultMap = employeeservice.getEPFReport(data, request);
            }
            
            
            
            
            //resultMap = employeeservice.getEpfReportNew(data,request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getEPFReport() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getesicreport")
    public Map getESICReport(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        try {
            
             Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

            Long orgId = Long.parseLong(map.get("id").toString());
            if(Objects.equals(orgId, vedantId)){
                System.out.println("Vedant ESIC Report");
                resultMap = employeeservice.getCustomESICReport(data, request);
            }
            else{
                System.out.println("Other Client ESIC Report");
                resultMap = employeeservice.getESICReport(data, request);
            }
            
            
           
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getESICReport() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getincometaxreport")
    public Map getIncomeTaxReport(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = employeeservice.getIncomTaxReport(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getIncomeTaxReport() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getincometaxreportforquarter")
    public Map getIncomeTaxReportForQuarter(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = employeeservice.getIncomTaxReportforquarter(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getIncomeTaxReportForQuarter() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getemployees")
    public Map getEmployess(@RequestBody String data, HttpServletRequest request, @RequestParam(required = false) String pageNumber, @RequestParam(required = false) String pageSize, @RequestParam(required = false) String search) {
        Map resultMap = new HashMap<>();
        logger.info("getEmployess method called in Employeecontroller");
        try {
            Integer page = pageNumber != null ? Integer.parseInt(pageNumber.toString()) : 1;
            Integer size = pageSize != null ? Integer.parseInt(pageSize.toString()) : 100000;
            String searchWord = search != null ? search.toString() : "";
            resultMap = employeeservice.getEmployess(data, request, page, size, searchWord);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getIncomeTaxReportForQuarter() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getemployeeswithsite")
    public Map getEmployessWithSite(@RequestBody String data, HttpServletRequest request, @RequestParam(required = false) String pageNumber, @RequestParam(required = false) String pageSize, @RequestParam(required = false) String search) {
        Map resultMap = new HashMap<>();
        logger.info("getEmployessWithSite method called in Employeecontroller");
        try {
            Integer page = pageNumber != null ? Integer.parseInt(pageNumber.toString()) : 1;
            Integer size = pageSize != null ? Integer.parseInt(pageSize.toString()) : 100000;
            String searchWord = search != null ? search.toString() : "";
            resultMap = employeeservice.getEmployessWithSite(data, request, page, size, searchWord);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getEmployessWithSite() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getgeneratedpdf")
    public Map getGeneratedPDfOfEmployee(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        logger.info("getGeneratedPDfOfEmployee method called in Employeecontroller");
        try {
            resultMap = employeeservice.getGeneratedPDfOfEmployee(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getGeneratedPDfOfEmployee() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getstandardDataofEmployee")
    public Map getStandardDataOfEmployee(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        logger.info("getStandardDataOfEmployee method called in Employeecontroller");
        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

            if (map.get("employee_Type").toString().equalsIgnoreCase("Worker")) {

                resultMap = employeeservice.getStandardDataOfEmployeeForWorker(data, request);
            } else if (map.get("employee_Type").toString().equalsIgnoreCase("Consultant") || map.get("employee_Type").toString().equalsIgnoreCase("Contract")) {

                resultMap = employeeservice.getStandardDataOfEmployeeForConsultant(data, request);
            } else if (map.get("employee_Type").toString().equalsIgnoreCase("Intern")) {

                resultMap = employeeservice.getStandardDataOfEmployeeForIntern(data, request);
            } else {
                if (map.containsKey("PayrollBasedOn") && map.get("PayrollBasedOn") != null && map.get("PayrollBasedOn").toString().equalsIgnoreCase("Basic Salary")) {
                    resultMap = employeeservice.getStandardOnTheBasicOfBasicSalary(data, request);
                } else {
                    resultMap = employeeservice.getStandardDataOfEmployee(data, request);
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getStandardDataOfEmployee() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getallappraisalsalary")
    public Map getAllAppraisalList(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        logger.info("getAllAppraisalList method called in Employeecontroller");
        try {
            resultMap = employeeservice.getAllAppraisalList(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getAllAppraisalList() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/isRunpayrollDone")
    public Map isRunPayrollDoneInMonth(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        logger.info("getGeneratedPDfOfEmployee method called in Employeecontroller");
        try {
            resultMap = employeeservice.isRunPayrollDoneInMonth(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getGeneratedPDfOfEmployee() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/updatesalaryholdflag")
    public Map updateSalaryHoldFlag(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        logger.info("updateSalaryHoldFlag method called in Employeecontroller");
        try {
            resultMap = employeeservice.updateSalaryHoldFlag(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> updateSalaryHoldFlag() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getsalaryholdemployee")
    public Map getSalaryHoldEmployee(@RequestBody String data, HttpServletRequest request, @RequestParam(required = false) String search) {
        Map resultMap = new HashMap<>();

        logger.info("getSalaryHoldEmployee method called in Employeecontroller");
        try {
            resultMap = employeeservice.getSalaryHoldEmployee(data, request, search);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getSalaryHoldEmployee() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/bulkBankDetails")
    public Map bulkBankDetails(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            List<AccountDetails> bulkList = mapper.convertValue(map.get("bulkList"), new TypeReference<List<AccountDetails>>() {
            });
            resultMap = employeeservice.bulkBankDetailsService(bulkList);
        } catch (Exception ex) {
            logger.info("Problem in Employeecontroller -> bulkBankDetails() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getfnfdetails")
    public Map getFAFDetails(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        logger.info("getFAFDetails method called in Employeecontroller");
        try {
            resultMap = employeeservice.getFAFDetails(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getFAFDetails() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/holdsalaryofemployee")
    public Map holdSalaryOfEmployee(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        logger.info("getFAFDetails method called in Employeecontroller");
        try {
            resultMap = employeeservice.holdSalaryOfEmployee(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in holdSalaryOfEmployee -> holdSalaryOfEmployee() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/updateAllowancefaf")
    public Map updateAllowanceFAF(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        logger.info("updateAllowanceFAF method called in Employeecontroller");
        try {
            resultMap = employeeservice.updateAllowanceFAF(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in employeeController -> updateAllowance() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/savefafdetails")
    public Map saveFAFDetails(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        logger.info("saveFAFDetails method called in Employeecontroller");
        try {
            resultMap = employeeservice.saveFAFDetails(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in employeeController -> saveFAFDetails() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getStandardWhileUpdating")
    public Map getStandardWhileUpdating(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        logger.info("saveFAFDetails method called in Employeecontroller");
        try {
            resultMap = employeeservice.redirectFunctionOfStandard(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in employeeController -> saveFAFDetails() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/updateAllowaaces")
    public Map updateAllowances(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap();
        try {
            resultMap = employeeservice.updatestandardOfEmployee(data, request);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in employeeController -> saveFAFDetails() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

//            @PostMapping(path = "/getsalarysheetreport")
//    public Map getSalarySheetReport(@RequestBody String data,HttpServletRequest request) {
//        Map resultMap = new HashMap<>();
//
//        try {
//            resultMap = employeeservice.getSalarySheetReport(data,request);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            logger.info("Problem in Employeecontroller -> getEPFReport() :: ", ex);
//            resultMap.put("status", "exception");
//        }
//        return resultMap;
//    }
    @GetMapping("/getsalarysheetreport")
    public ResponseEntity<byte[]> getSalarySheetReport(@RequestParam() String organizationId, @RequestParam() String employeeType, @RequestParam() String month, @RequestParam() String year) {
        try {
            Long orgId = Long.parseLong(organizationId);
            Long month1 = Long.parseLong(month);
            Long year1 = Long.parseLong(year);
            if (employeeType.equalsIgnoreCase("Consultant") || employeeType.equalsIgnoreCase("contract")) {
                return employeeservice.getSalarySheetReportForConsultant(orgId, employeeType, month1, year1);
            } else {
                return employeeservice.getSalarySheetReport(orgId, employeeType, month1, year1);
            }

        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }

    @PostMapping(path = "/getannexture")
    public Map getAnnextureOfEmployee(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        logger.info("getAnnextureOfEmployee method called in Employeecontroller");
        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

            System.out.println("Payload from recruit " + map.toString());
            if ((map.get("employeeType") == null || map.get("organizationId") == null || map.get("gender") == null || map.get("grossSalary") == null)) {

                resultMap.put("status", "error");
                resultMap.put("msg", "Please check your Payload");
                System.out.println("response from payroll " + resultMap);
                return resultMap;
            }

            String employee_type = map.get("employeeType").toString();
            if (employee_type.equalsIgnoreCase("permanent") || employee_type.equalsIgnoreCase("probation")) {
                employee_type = "full time";
            }

            if (employee_type.equalsIgnoreCase("full time")) {

                if (map.get("handleFor") != null && map.get("handleFor").toString().equalsIgnoreCase("CTC")) {

                    resultMap = employeeservice.getAnnextureOfFulltimeEmployeeOnCTC(data, request);
                    System.out.println("Api call For CTC ");
                } else {
                    resultMap = employeeservice.getAnnextureOfFulltimeEmployee(data, request);
                    System.out.println("Api call For Annexture");
                }

            } else if (employee_type.equalsIgnoreCase("consultant") || employee_type.equalsIgnoreCase("contract")) {

                if (map.get("handleFor") != null && map.get("handleFor").toString().equalsIgnoreCase("CTC")) {

                    resultMap.put("status", "error");
                    resultMap.put("msg", "CTC not handle for this employee type");
                    return resultMap;
                }
                resultMap = employeeservice.getAnnextureOfConsultantEmployee(data, request);
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Annexture not handle for this employee type");
                System.out.println("response " + resultMap);
                return resultMap;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getAnnextureOfEmployee() :: ", ex);
            resultMap.put("status", "exception");
            resultMap.put("msg", "something went wrong");
        }
        System.out.println("response of getAnnextureOfEmployee " + resultMap);
        return resultMap;

    }

    @PostMapping("/updateEmployeePersonalDetails")
    public Map updateEmployeePersonalDetails(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {

            List<Map> mp = Arrays.asList(mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap[].class));

            resultMap = employeeservice.updateEmployeePersonalDetails(mp);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Problem in Employeecontroller -> getEPFReport() :: ", e.getMessage());
            resultMap.put("status", "exception");
        }
        return resultMap;

    }

    @PostMapping(path = "/getlastrunpayrollofemployee")
    public Map getLastRunPayrollOfEmployee(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        logger.info("getLastRunPayrollOfEmployee method called in Employeecontroller");
        try {
            resultMap = employeeservice.getLastRunPayrollOfEmployee(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getLastRunPayrollOfEmployee() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @GetMapping("/downloadSalaryStandard")
    public ResponseEntity<byte[]> downloadSalaryStandard(@RequestParam() String organizationId, @RequestParam() String employeeType) {
        try {
            Long orgId = Long.parseLong(organizationId);
            return employeeservice.downloadSalaryStandard(orgId, employeeType);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }

    @GetMapping("/downloadEsicExcelReport")
    public ResponseEntity<byte[]> downloadesicInexcelFormate(@RequestParam() String organizationId, @RequestParam() String month, @RequestParam() String year,@RequestParam(required = false) String siteId, HttpServletRequest request) {
        try {
            Long orgId = Long.parseLong(organizationId);
            Long m = Long.parseLong(month);
            Long y = Long.parseLong(year);
            
              if(Objects.equals(orgId, vedantId)){
                System.out.println("Vedant ESIC Report 644");
                return employeeservice.CustomDownloadesicInexcelFormate(orgId, m, y,siteId, request);
            }
            else{
                System.out.println("Other ESIC Report");
                return employeeservice.downloadesicInexcelFormate(orgId, m, y, request);
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }

    @PostMapping(path = "/recalculateEmployeeStandard")
    public Map recalculateEmployeeStandard(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        logger.info("saveFAFDetails method called in Employeecontroller");
        try {
            resultMap = employeeservice.recalculateEmployeeStandard(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in employeeController -> saveFAFDetails() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/updateStandardInBulk")
    public Map<String, Object> updateStandardInBulk(@RequestParam("ExcelFile") MultipartFile file, @RequestParam("organizationId") Long orgId) throws Exception {

        return employeeServiceImpl.updateStandardInBulk(file, orgId);
    }

    @PostMapping(path = "/getBonusData")
    public Map getBonusData(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = employeeservice.getBonusData(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getBonusData() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/saveBonusData")
    public Map saveBonusData(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = employeeservice.saveBonusData(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> saveBonusData() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getAllStandardOfEmployee")
    public Map getAllStandardOfEmployee(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = employeeservice.getAllStandardOfEmployee(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> saveBonusData() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getStandardById")
    public Map getStandardById(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = employeeservice.getStandardById(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> saveBonusData() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @GetMapping("/getMultipleReports")
    public ResponseEntity<byte[]> getMultipleReports(
            @RequestParam() String organizationId,
            @RequestParam() String fromDate,
            @RequestParam() String toDate,
            @RequestParam() String reportType,
            @RequestParam() Long employeeId,
            @RequestParam(required = false) String location) {
        try {
            Long orgId = Long.parseLong(organizationId);
            // Normalize optional params
            location = (location == null || location.equalsIgnoreCase("null")) ? "" : location.trim();
            if ("bonusRegister".equalsIgnoreCase(reportType)) {
                
                if (Objects.equals(orgId, vedantId)) {
                    System.out.println("Vedant Bonus Report");
                    return employeeservice.getBonusRegisterForVedant(orgId, fromDate, toDate);
                }
                else{
                    return employeeservice.getBonusRegister(orgId, fromDate, toDate);
                }
                
            } else if ("lwfReport".equalsIgnoreCase(reportType)) {
                String month = fromDate;
                return labourLawDeductionService.getLwfReport(orgId, month);
            } else if ("employeeWiseReport".equalsIgnoreCase(reportType)) {
                return employeeservice.individualEmployeeMonthWiseSalaryReport(orgId, fromDate, toDate, employeeId);
            } else if ("erForm".equalsIgnoreCase(reportType)) {
                return employeeservice.getErForm(orgId, fromDate, toDate);
            } else if ("ptForm".equalsIgnoreCase(reportType)) {
                return employeeservice.getPtForm(orgId, fromDate, toDate);
            } else if ("ptReport".equalsIgnoreCase(reportType)) {
                return employeeservice.getPtReport(orgId, fromDate);
            } else if ("ldreport".equalsIgnoreCase(reportType)) {
                return employeeservice.locationAndDepartmentWiseReport(orgId, fromDate, toDate, location);
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(("Invalid report type: " + reportType).getBytes());
            }
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(("Invalid organization ID").getBytes());
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }

    @PostMapping(path = "/getEmployeeDataFromRunPayRoll")
    public Map getEmployeeDataFromRunPayRoll(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = employeeservice.getEmployeeDataFromRunPayRoll(data);
        } catch (Exception ex) {
            logger.info("Problem in Employeecontroller -> getAccountDetailsById() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
        @GetMapping("/downloadEmployeeList")
    public ResponseEntity<byte[]> DownloadEmployeeList(@RequestParam() String organizationId, @RequestParam() String siteId, @RequestParam() String siteName, HttpServletRequest request) {
        try {
            Long orgId = Long.parseLong(organizationId);
            Long sId = Long.parseLong(siteId);
           
            return employeeservice.downloadEmployeeList(orgId, sId, siteName, request);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }
    
    @GetMapping("/downloadPlanName")
    public ResponseEntity<byte[]> DownloadPlanName(@RequestParam() String organizationId, @RequestParam() String siteId, @RequestParam() String siteName, HttpServletRequest request) {
        try {
            Long orgId = Long.parseLong(organizationId);
            Long sId = Long.parseLong(siteId);
           
            return employeeservice.downloadPlanName(orgId, sId, siteName, request);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }
    
    @PostMapping("/updateCustomStandardInBulk")
    public Map<String, Object> UpdateCustomStandardInBulk(@RequestParam("ExcelFile") MultipartFile file, @RequestParam("organizationId") Long orgId,@RequestParam("siteId") Long siteId,HttpServletRequest request) throws Exception {

        return employeeServiceImpl.updateCustomStandardInBulk(file, orgId,siteId, request);
    }

    @GetMapping("/getcustomsalarysheetreport")
    public ResponseEntity<byte[]> getCustomSalarySheetReport(@RequestParam() String organizationId, @RequestParam() String employeeType, @RequestParam() String month, @RequestParam() String year) {
        try {
            Long orgId = Long.parseLong(organizationId);
            Long month1 = Long.parseLong(month);
            Long year1 = Long.parseLong(year);  
            return employeeservice.getCustomSalarySheetReport(orgId, employeeType, month1, year1);
            

        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }
    
        @PostMapping(path = "/getptreport")
    public Map getPtReport(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = employeeservice.getPtReport(data, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getIncomeTaxReport() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @GetMapping("/downloadRunPayrollSheet")
    public ResponseEntity<byte[]> DownloadRunPayrollSheet(@RequestParam() String organizationId, @RequestParam() String siteId, @RequestParam() String siteName,@RequestParam() String netPayable,@RequestParam() String date,@RequestParam() String year, HttpServletRequest request) {
        try {
            Long orgId = Long.parseLong(organizationId);
            return employeeservice.DownloadRunPayrollSheet(orgId, siteId, siteName,netPayable,date,year, request);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }
    
    @PostMapping("/updateRunPayrollStatus")
    public Map<String, Object> UpdateRunPayrollPaidStatus(@RequestParam("ExcelFile") MultipartFile file, @RequestParam("organizationId") Long orgId,@RequestParam(value="siteId" ,required = false) String siteId,@RequestParam("month") Integer month,@RequestParam("year") Integer year,@RequestParam("netPayable") String netPayable,HttpServletRequest request) throws Exception {

        return employeeServiceImpl.updateRunPayrollStatus(file, orgId,siteId,month,netPayable,year, request);
    }
    
    @GetMapping("/downloadFAF")
    public ResponseEntity<byte[]> downloadFAF(@RequestParam() String organizationId,@RequestParam() String year, HttpServletRequest request) {
        try {
            Long orgId = Long.parseLong(organizationId);
            return employeeservice.getFAFForVedant(orgId,year, request);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
    }
    
    @PostMapping(path = "/getRunPayRollSheetData")
    public Map getRunPayRollSheetData(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = employeeservice.getRunPayRollSheetData(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in Employeecontroller -> getRunPayRollSheetData() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
}
