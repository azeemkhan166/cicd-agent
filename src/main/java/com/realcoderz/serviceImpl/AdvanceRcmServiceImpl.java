/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.realcoderz.config.JWTAuthenticationFilter;
import com.realcoderz.model.AdvanceRcm;
import com.realcoderz.model.NotificationByWebsocket;
import com.realcoderz.model.employeeDetails;
import com.realcoderz.repository.AdvanceRcmRepository;
import com.realcoderz.repository.SiteRepository;
import com.realcoderz.repository.employeeDetailsRepository;
import com.realcoderz.service.AdvanceRcmService;
import com.realcoderz.service.NotificationByWebsocketService;
import com.realcoderz.util.CommonExcelData;
import com.realcoderz.util.EncryptDecryptUtils;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Admin
 */
@Service
public class AdvanceRcmServiceImpl implements AdvanceRcmService {
    
    ObjectMapper mapper = new ObjectMapper();

    @Value("${reimburshment_url}")
    private String reimburshment_url;

    private final AdvanceRcmRepository advanceRcmRepository;
    private final SiteRepository SiteRepository;
    private final CommonExcelData commonExcelData;
    private final employeeDetailsRepository employeedetailsrepo;
    private final JWTAuthenticationFilter authenticationFilter;
    private final RestTemplate restTemplate;
    private final NotificationByWebsocketService notificationService;

    public AdvanceRcmServiceImpl(AdvanceRcmRepository advanceRcmRepository,
                                 SiteRepository SiteRepository,
                                 CommonExcelData commonExcelData,
                                 employeeDetailsRepository employeedetailsrepo,
                                 JWTAuthenticationFilter authenticationFilter,
                                 RestTemplate restTemplate,
                                 NotificationByWebsocketService notificationService,
                                 @Value("${reimburshment_url}") String reimburshment_url) {
        this.advanceRcmRepository = advanceRcmRepository;
        this.SiteRepository = SiteRepository;
        this.commonExcelData = commonExcelData;
        this.employeedetailsrepo = employeedetailsrepo;
        this.authenticationFilter = authenticationFilter;
        this.restTemplate = restTemplate;
        this.notificationService = notificationService;
        this.reimburshment_url = reimburshment_url;
    }
    
    @Override
    public Map getAllAdvanceForSupervisor(Map map,HttpServletRequest request) {
        
        Map resultMap = new HashMap<>();
        
        try {
            
            System.out.println("map");
            System.out.println(map);
          
            Long id = Long.parseLong(map.get("id").toString());
            String date = map.get("month").toString();
            Long year = Long.parseLong(map.get("year").toString());
            String supervisorId =  map.get("supervisorId")!=null ? map.get("supervisorId").toString():null;
            String[] siteName=new String[1];
            siteName[0]="";
            
              String bearerToken = authenticationFilter.getJwtFromRequest(request);
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(bearerToken);
            header.setContentType(MediaType.TEXT_PLAIN);
            
            JSONObject payload = new JSONObject();
            
            payload.put("id", id);
            payload.put("month", date);
            payload.put("year", year);
            payload.put("supervisorId", supervisorId);
            
        String encryptedPayload = EncryptDecryptUtils.encrypt(payload.toString());

        Map employeeListResp = null;

        HttpEntity<?> entity = new HttpEntity<>(encryptedPayload, header);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(reimburshment_url + "/site/getAdvanceDetailsById");
        String url = builder.toUriString();

        Map employeeListReq = restTemplate.exchange(url, HttpMethod.POST, entity, HashMap.class).getBody();

        
        
        try {            
            employeeListResp = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListReq.get("data").toString()), LinkedCaseInsensitiveMap.class);
            
            System.out.println("employeeListResp from manage");
            System.out.println(employeeListResp);
            
          
            
        } catch (Exception ex) {
            ex.printStackTrace();
          //  logger.info("Unable to employee list from the manage :: ", ex);
        }
        
          List<LinkedCaseInsensitiveMap> data1 = mapper.convertValue(
                    employeeListResp.get("value"),
                    new TypeReference<List<LinkedCaseInsensitiveMap>>() {
            }
            );
          
          data1.stream().forEach(action->{
          if(Objects.equals(id, Long.parseLong(action.get("siteId").toString()))){
              
              siteName[0]=action.get("site_name").toString();
          }
              
          });
           
            List<LinkedCaseInsensitiveMap> data = SiteRepository.getSavedAdvanceDetails(siteName[0],id, date, year);
            
            System.out.println("Saved Data");
            System.out.println(data);
            
            
            
           // List<LinkedCaseInsensitiveMap> data1 = SiteRepository.getAdvanceDetails(id, date, year);

            // Collect employee_ids from 'data' into a Set for fast lookup
            Set<Long> employeeIdsInData = data.stream()
                    .map(m -> m.get("employee_id"))
                    .filter(Objects::nonNull)
                    .map(idd -> Long.valueOf(idd.toString()))
                    .collect(Collectors.toSet());

            // Remove matching entries from 'data1'
            data1.removeIf(m -> {
                Object empIdObj = m.get("employee_id");
                if (empIdObj == null) {
                    return false;
                }
                Long empId = Long.valueOf(empIdObj.toString());
                return employeeIdsInData.contains(empId);
            });
           
            data.addAll(data1);
            
            resultMap.put("status", "success");
            resultMap.put("value", data);
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }
        
        return resultMap;
        
    }
    
    @Override
    public Map saveOrUpdateStatus(Map map) {
        
        Map resultMap = new HashMap<>();
        
        try {
            
            String status = map.get("otStatus").toString();            
            List<Integer> ids = (List<Integer>) map.get("ids");
            Long orgId=Long.parseLong(map.get("organizationId").toString());
            List<AdvanceRcm> data = new ArrayList<>();
            List<LinkedCaseInsensitiveMap> list = mapper.convertValue(map.get("list"), new TypeReference<List<LinkedCaseInsensitiveMap>>() {
            });
            
            List<LinkedCaseInsensitiveMap> advanceRcm = mapper.convertValue(map.get("advanceRcm"), new TypeReference<List<LinkedCaseInsensitiveMap>>() {
            });
            
            List<LinkedCaseInsensitiveMap> advanceHo = mapper.convertValue(map.get("advanceHo"), new TypeReference<List<LinkedCaseInsensitiveMap>>() {
            });

            // Filter the  list
//            List<LinkedCaseInsensitiveMap> filteredData = list.stream()
//                    .filter(item -> ids.contains((Integer) item.get("employee_id")))
//                    .collect(Collectors.toList());

            List<LinkedCaseInsensitiveMap> filteredData = list.stream()
                    .filter(item -> {
                        Object empIdObj = item.get("employee_id");
                        if (empIdObj != null) {
                            try {
                                Integer empId = Integer.valueOf(empIdObj.toString());
                                return ids.contains(empId);
                            } catch (NumberFormatException e) {
                                // Optional: log or handle invalid number format
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            
            
            filteredData.stream().forEach(action -> {
                AdvanceRcm vv = new AdvanceRcm();
                vv.setAmount(Double.parseDouble(action.get("advance").toString()));
                vv.setRcmAmount(Double.parseDouble(action.get("advance").toString()));
                vv.setEmployeeId(Long.parseLong(action.get("employee_id").toString()));
                vv.setSiteId(Long.parseLong(action.get("siteId").toString()));
                vv.setSupervisorStatus(status);
                vv.setHoStatus("Pending");
                vv.setRcmStatus("Pending");
                vv.setPaid("unPaid");
                vv.setYear(Long.parseLong(action.get("year").toString()));
                vv.setDate(action.get("date").toString());
                vv.setHoAmount(Double.parseDouble(action.get("advance").toString()));
                data.add(vv);
            });
            
            advanceRcmRepository.saveAll(data);
             
            // notification code
            
              try {
                        
//                        List<String> RecipientEmails = new ArrayList<>();
//                        List<String> RecipientNames = new ArrayList<>();
//
//                        advanceRcm.stream().forEach(action -> {
//                            RecipientEmails.add(action.get("email").toString());
//                            RecipientNames.add(action.get("employeeName").toString());
//
//                        });
//                        
//                        advanceHo.stream().forEach(action -> {
//                            RecipientEmails.add(action.get("email").toString());
//                            RecipientNames.add(action.get("employeeName").toString());
//
//                        });
                        
                  List<String> RecipientEmails = Stream.concat(advanceRcm.stream(), advanceHo.stream())
                          .map(action -> action.get("email").toString())
                          .distinct()
                          .collect(Collectors.toList());

                  List<String> RecipientNames = Stream.concat(advanceRcm.stream(), advanceHo.stream())
                          .map(action -> action.get("employeeName").toString())
                          .distinct()
                          .collect(Collectors.toList());

                  System.out.println("RecipientEmails "+RecipientEmails);
                  System.out.println("RecipientNames "+RecipientNames);
                  
                        
                        data.stream().forEach(action->{
                        
                        Long empId=  action.getEmployeeId();
                          
                        employeeDetails findByEmployeeId =employeedetailsrepo.findByEmployeeId(empId);
                        
                        System.out.println("employeeDetails for notification "+findByEmployeeId);
                        NotificationByWebsocket employeeNotification=new NotificationByWebsocket();
                
                        employeeNotification.setPriority("MODERATE");
                        employeeNotification.setType("INFO");
    			employeeNotification.setPushNotify(0);
    			employeeNotification.setMailNotify(0);
    			employeeNotification.setModuleName("Payroll");
    			employeeNotification.setOrganizationId(orgId);
    			employeeNotification.setSenderId(0);
    			employeeNotification.setSenderName("");
    			employeeNotification.setTitle("Advance Apply by "+findByEmployeeId.getName() );
    			employeeNotification.setBody(findByEmployeeId.getName() +" Apply For Advance");
    			employeeNotification.setRecipientEmails(RecipientEmails);
    			employeeNotification.setRecipientNames(RecipientNames);
                        new Thread(() -> notificationService.sendNotification(employeeNotification)).start();
                        });  
                       
                        } catch (Exception e) {
                     System.out.println("exception when send notification");
                     e.printStackTrace();
                     
                    }


            
            resultMap.put("status", "success");
            resultMap.put("msg", "data save successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }
        
        return resultMap;
        
    }

    @Override
    public Map getAllAdvanceForRcm(Map map) {
       
                Map resultMap = new HashMap<>();
        
        try {
         
            Long id = Long.parseLong(map.get("id").toString());
            String date = map.get("date").toString();
            Long year = Long.parseLong(map.get("year").toString());
            String siteName=map.get("siteName").toString();
           
            List<LinkedCaseInsensitiveMap> data = SiteRepository.getAllAdvanceForRcm(siteName,id, date, year);
                                    
            resultMap.put("status", "success");
            resultMap.put("value", data);
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }
        
        return resultMap;
    
    }

    @Override
    public Map rcmApprovedOrReject(Map map) {
        
             Map resultMap = new HashMap<>();
        
        try {
          
            String status = map.get("otStatus").toString();            
          
            List<?> rawIds = (List<?>) map.get("ids");

            List<Long> ids = rawIds.stream()
                    .map(Object::toString) // safely convert to String first
                    .map(String::trim)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            
           
            List<LinkedCaseInsensitiveMap> list = mapper.convertValue(map.get("list"), new TypeReference<List<LinkedCaseInsensitiveMap>>() {
            });
            
//            List<LinkedCaseInsensitiveMap> filteredData = list.stream()
//                    .filter(item -> {
//                        Object empIdObj = item.get("employee_id");
//                        if (empIdObj != null) {
//                            try {
//                                Integer empId = Integer.valueOf(empIdObj.toString());
//                                return ids.contains(empId);
//                            } catch (NumberFormatException e) {
//                                // Optional: log or handle invalid number format
//                            }
//                        }
//                        return false;
//                    })
//                    .collect(Collectors.toList());
            
          
            
//            List<Long> primaryIds = filteredData.stream()
//                    .map(mapp -> mapp.get("id"))
//                    .filter(Objects::nonNull)
//                    .map(id -> Long.valueOf(id.toString()))
//                    .collect(Collectors.toList());
            
            
            List<AdvanceRcm> data= advanceRcmRepository.findAdvanceById(ids);
                        
            data.stream().forEach(a -> {
                Long id = a.getId();
                list.stream().forEach(d -> {

                    Long idss = Long.parseLong(d.get("id").toString());
                    Double amount = Double.parseDouble(d.get("rcmAdvance").toString());
                    if (Objects.equals(id, idss)) {

                        a.setRcmStatus(status);
                        a.setRcmAmount(amount);
                        a.setHoAmount(amount);
                    }

                });

            });            
            advanceRcmRepository.saveAll(data);
            resultMap.put("status", "success");
            resultMap.put("msg", "Data save successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }
        
        return resultMap;
     
    }

    @Override
    public Map hoApprovedOrReject(Map map) {
      
      
             Map resultMap = new HashMap<>();
        
        try {
          
            String status = map.get("otStatus").toString();            

              List<?> rawIds = (List<?>) map.get("ids");

            List<Long> ids = rawIds.stream()
                    .map(Object::toString) // safely convert to String first
                    .map(String::trim)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
           
            List<LinkedCaseInsensitiveMap> list = mapper.convertValue(map.get("list"), new TypeReference<List<LinkedCaseInsensitiveMap>>() {
            });
   
//List<LinkedCaseInsensitiveMap> filteredData = list.stream()
//                    .filter(item -> {
//                        Object empIdObj = item.get("employee_id");
//                       
//                        if (empIdObj != null) {
//                            try {
//                                String trimmed = empIdObj.toString().trim();
//                                Integer empId = Integer.valueOf(trimmed);
//                                boolean match = ids.contains(empId);
//                               
//                                return match;
//                            } catch (NumberFormatException e) {
//                              
//                            }
//                        }
//                        return false;
//                    })
//                    .collect(Collectors.toList());
            
            
//            List<Long> primaryIds = filteredData.stream()
//                    .map(mapp -> mapp.get("id"))
//                    .filter(Objects::nonNull)
//                    .map(id -> Long.valueOf(id.toString()))
//                    .collect(Collectors.toList());
            
            List<AdvanceRcm> data= advanceRcmRepository.findAdvanceById(ids);
            
            data.stream().forEach(a -> {
                Long id = a.getId();
                list.stream().forEach(d -> {

                    Long idss = Long.parseLong(d.get("id").toString());
                    Double amount = Double.parseDouble(d.get("hoAdvance").toString());
                    if (Objects.equals(id, idss)) {

                        a.setHoStatus(status);
                        a.setHoAmount(amount);
                    }

                });

            });
            advanceRcmRepository.saveAll(data);
            resultMap.put("status", "success");
            resultMap.put("msg", "Data save successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }
        
        return resultMap;
    
    }

    @Override
    public ResponseEntity<byte[]> downloadAdvanceDetailsInexcelFormate(String siteName,Long organizationId, String month, Long year,Long ids, HttpServletRequest request) {
        
         List<LinkedCaseInsensitiveMap> resultList = new ArrayList<>();
        try{
           
            
            resultList= SiteRepository.getAllAdvanceForExcel(siteName,ids, month, year);

            System.out.println("resultList");
            System.out.println(resultList);
            
            String[] combinedHeaderArray = {"S.No", "Employee", "Emp Code", "Advance", "Bank A/c No", "IFSC", "Adv Requested Date", "Status","Date","Site Name","Flag"};
            String[] combinedRowArray = {"name", "employeeCode", "hoAdvance", "bankaccount", "ifsc", "date","paid","ho_date","site_name","Flag"};
            return commonExcelData.excelData(resultList, combinedHeaderArray, combinedRowArray, "Advance-report", "AdvanceReport");

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        }
        
    }
    
    
     public Map<String, Object> updateAdvanceInBulk(MultipartFile file, Long orgId,Long year,String month,Long id) throws IOException, InvalidFormatException {

        Map<String, Object> resultMap = new HashMap<>();
         
        List<LinkedCaseInsensitiveMap> empData = new ArrayList();
        
          try ( Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                            
            List<AdvanceRcm>   valueForUpdate= advanceRcmRepository.getAllAdvanceDataForUpdate(id, month, year);
                        
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> getIndexOfEachColumn = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            
            /**
             * getting all header name and index no of header
               *
             */
            headerRow.forEach(cell -> {

                if (cell.getCellType() == CellType.STRING) {

                    switch (cell.getStringCellValue().toLowerCase().trim()) {

                        case "employee":
                            getIndexOfEachColumn.put("nameIndex", cell.getColumnIndex());
                            break;
                        case "emp code":
                            getIndexOfEachColumn.put("employeeCodeIndex", cell.getColumnIndex());
                            break;
                        case "date":
                            getIndexOfEachColumn.put("dateIndex", cell.getColumnIndex());
                            break;
                        case "site name":
                            getIndexOfEachColumn.put("siteNameIndex", cell.getColumnIndex());
                            break;
                        case "adv requested date":
                            getIndexOfEachColumn.put("advRequestedDateIndex", cell.getColumnIndex());
                            break;
                        case "flag":
                            getIndexOfEachColumn.put("flagIndex", cell.getColumnIndex());
                            break;

                        case "status":
                            getIndexOfEachColumn.put("statusIndex", cell.getColumnIndex());
                            break;

                        default:
                            break;
                    }
                }

            });
            
             sheet.removeRow(headerRow);
             
          
                      /**
             * Putting all excel sheet value into List
                 *
             */
            sheet.forEach((Row row) -> {

                LinkedCaseInsensitiveMap mp = new LinkedCaseInsensitiveMap();

                row.forEach(cell -> {

                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("employeeCodeIndex")) {
                        mp.put("emp code", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("nameIndex")) {
                        mp.put("employee", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("dateIndex")) {
                        mp.put("date", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("siteNameIndex")) {
                        mp.put("site name", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("advRequestedDateIndex")) {
                        mp.put("adv requested date", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("flagIndex")) {
                        mp.put("flag", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("statusIndex")) {
                        mp.put("status", printCellValue(cell));
                    }
                });

                empData.add(mp);

            });
         
              /**
             * Removing employee from empData List whose status is no 
               *
             */                        
            empData.removeIf(filter -> "no".equalsIgnoreCase(filter.get("flag").toString()));

            if (empData.isEmpty()) {
                resultMap.put("status", "error");
                resultMap.put("msg", "Can not update! Please check");
                return resultMap;
            }
            
            List<String> empcodeList = new ArrayList<>();
             
            empData.stream().forEach(empcode -> {
                String ecode = empcode.get("emp code").toString();
                empcodeList.add(ecode);
            });
            
             /**
             * geeting employee id based on employee code
                 *
             */
            List<LinkedCaseInsensitiveMap> empid = employeedetailsrepo.getEmployeeId(empcodeList, orgId);

            /**
             * merge employee_id of each employee
             *
             */
            
            empData.forEach(addempId -> {
                String empcode = addempId.get("emp code").toString();

                empid.stream()
                        .filter(action -> action.get("employee_code").toString().equalsIgnoreCase(empcode))
                        .findFirst() // Use findFirst to get the first matching element
                        .ifPresent(matchedEmp -> {
                            // Replace "newKey" and "newValue" with the actual key-value pair you want to add
                            addempId.put("employee_id", matchedEmp.get("employee_id"));
                            // Additional processing if needed
                        });
            });
            
          
            
               empData.forEach(action -> {
                   
                Long empId = Long.parseLong(action.get("employee_id").toString());
                String status = action.get("status").toString();
                String date = action.get("date").toString();
                String reqDate=action.get("adv requested date").toString();
                
                valueForUpdate.stream().forEach(value->{
                
                 Long eId=value.getEmployeeId();
                 String savedDate=value.getDate();
                 
                 if(Objects.equals(empId, eId) && Objects.equals(reqDate, savedDate)){
                  
                     value.setHoDate(date);
                     value.setPaid(status);
                 }
                
                });
                
               
              });
              
             
             advanceRcmRepository.saveAll(valueForUpdate);
          } catch (Exception e) {
              
            e.printStackTrace();
        }
        resultMap.put("status", "success");
        resultMap.put("msg", "file uploaded successfully");

        return resultMap;
    }
    
     
         private static Object printCellValue(Cell cell) {
        Object obj = null;
        SimpleDateFormat sdfDate2 = new SimpleDateFormat("yyyy-MM-dd");
        switch (cell.getCellType()) {

            case NUMERIC:
            try {
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                     Date dt = cell.getDateCellValue();
                    obj = sdfDate2.format(dt);
                } else {
                obj = cell.getNumericCellValue();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            break;
            case STRING:

                obj = cell.getStringCellValue();

                break;
            default:

                obj = " ";
        }

        return obj;
    }

    @Override
    public Map getDateFormAdvance(Map map) {
        
         Map resultMap = new HashMap<>();
        
        try {
         
            Long id = Long.parseLong(map.get("id").toString());
            Long year = Long.parseLong(map.get("year").toString());
            
            String yearStr = String.valueOf(year);         // "2025"
            String nextYearStr = String.valueOf(year + 1); // "2026"
           
            List<LinkedCaseInsensitiveMap> data = SiteRepository.getDateFormAdvance(id, yearStr,nextYearStr);
                                    
            resultMap.put("status", "success");
            resultMap.put("value", data);
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }
        
        return resultMap;
    }

    @Override
    public Map updateStatusInAdvance(Map map) {
       
                 Map resultMap = new HashMap<>();
        
        try {
            
            System.out.println("map");
            System.out.println(map);
            
            Long id = Long.parseLong(map.get("id").toString());
            Long year = Long.parseLong(map.get("year").toString());
            String month=map.get("month").toString();
            String date=map.get("date").toString();
            String status=map.get("status").toString();
            List<Long> primaryIds = ((List<?>) map.get("ids"))
                    .stream()
                    .map(idss -> Long.parseLong(idss.toString()))
                    .collect(Collectors.toList());
         
            System.out.println(primaryIds);          
            List<AdvanceRcm>   valueForUpdate= advanceRcmRepository.getAllAdvanceDataForUpdates(primaryIds);
            
               valueForUpdate.stream().forEach(value->{
                
//                 Long eId=value.getEmployeeId();
//                 
//                 if(empIds.contains(eId)){
                  
                     value.setHoDate(date);
                     value.setPaid(status);
//                 }
                
                });
            advanceRcmRepository.saveAll(valueForUpdate);
            resultMap.put("status", "success");
            resultMap.put("msg", "Updated Successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }
        
        return resultMap;
    
    }
     
}
