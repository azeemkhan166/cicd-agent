/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.realcoderz.config.GcpConfig;
import com.realcoderz.config.JWTAuthenticationFilter;
import com.realcoderz.model.NotificationByWebsocket;
import com.realcoderz.model.POIDocument;
import com.realcoderz.model.employeeDetails;
import com.realcoderz.repository.ProofOfInvestmentRepository;
import com.realcoderz.repository.employeeDetailsRepository;
import com.realcoderz.service.EmployeeIdDatesService;
import com.realcoderz.service.NotificationByWebsocketService;
import com.realcoderz.service.ProofOfInvestmentService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Bipul Singh
 */
@Service
public class ProofOfInvestmentServiceImpl implements ProofOfInvestmentService {

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(ProofOfInvestmentServiceImpl.class);

    @Value("${reimburshment_url}")
    private String reimburshment_url;

    @Value("${bucketName}")
    String bucketName;
    @Value("${gcp.config.file}")
    private String gcpConfigFile;
    
    @Value("${gcpFilePath}")
    private String gcpFilePath;
    
    @Autowired
    private GcpConfig gcpConfig;
    
    @Autowired
    private ProofOfInvestmentRepository poiRepo;
    
    @Autowired
    private Storage storages;

    // Storage storage = StorageOptions.getDefaultInstance().getService();
    @Autowired
    private JWTAuthenticationFilter authenticationFilter;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProofOfInvestmentRepository poirepo;
    
    @Autowired
    private FCMCommonNotification fcmommonNotification;
    
    @Autowired
    private employeeDetailsRepository empdetailsrepo;
    
    @Autowired
    private NotificationByWebsocketService notificationService;
    
    @Autowired
    private EmployeeIdDatesService dateService;

    public Storage getGCPStorage() throws IOException {
        InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(inputStream))
               // .setProjectId("production-303006")
                .build().getService();
        return storage;
    }

    public String uploadFileOnGCP(MultipartFile fileStream, String financeYear, Long declarationId, String investmentSection, String subInvestmentName) {
        
        try {
            
            String fileObject = "" + fileStream.getOriginalFilename();
            byte[] resfile = fileStream.getBytes();
            BlobId blobId = BlobId.of(bucketName, ("POI/" + financeYear + "/" + investmentSection + "/" + declarationId + "/" + subInvestmentName + "/" + fileObject));
            if(fileObject.contains(".pdf")){
                BlobInfo blobInfo = BlobInfo.
                    newBuilder(blobId).
                    setContentType(fileStream.getContentType()).setCacheControl("Cache-Control: max-age=0, no-cache").
                    build();
             storages.create(blobInfo, resfile);
            }
            else{
            BlobInfo blobInfo = BlobInfo.
                    newBuilder(blobId).
                    setCacheControl("Cache-Control: max-age=0, no-cache").
                    build();
             storages.create(blobInfo, resfile);
            }

            String path = gcpFilePath + bucketName + "/" + "POI/" + financeYear + "/" + investmentSection + "/" + declarationId + "/" + subInvestmentName + "/" + fileObject;
            return path;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Problem occured while uploading file on gcp with this data" + " financeYear: " + financeYear + " investmentSection : " + investmentSection + " declarationId : " + declarationId);
            return null;
        }
    }
    
      @Override
    public Map fetchDocument(Map map) {
        
        Map resultMap = new HashMap<>();
        try {
           
            String docUrl=poiRepo.findUrl(Long.parseLong(map.get("id").toString()));
            if (docUrl !=null) {
            String url=gcpConfig.getSignedUrl(bucketName,docUrl);   
                resultMap.put("url", url);
                resultMap.put("status", "success");
            } else {
                resultMap.put("msg", "Please provide valid key and value");
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            resultMap.put("msg", ex.getMessage());
            ex.printStackTrace();
            logger.error("Problem occur while fetch document bytes : " + map.toString());
        }
        return resultMap;
    }

//    @Override
//    public Map fetchDocument(Map map) {
//        
//        Map resultMap = new HashMap<>();
//        try {
           
//            if (map.containsKey("investmentName") && map.containsKey("declarationId") && map.containsKey("fileName") && map.containsKey("financialYear") && map.containsKey("subInvestmentName")) {
//                Storage storage = this.getGCPStorage();
//                Blob blob = storage.get(BlobId.of(bucketName, ("POI/" + map.get("financialYear").toString() + "/" + map.get("investmentName").toString() + "/" + map.get("declarationId").toString() + "/" + map.get("subInvestmentName").toString() + "/" + map.get("fileName").toString())));
//                if (blob == null) {
//                    resultMap.put("msg", "Corrupted file found! Please upload the file again!");
//                    resultMap.put("status", "error");
//                    return resultMap;
//                }
//                resultMap.clear();
//                byte[] fileBytes = blob.getContent();
//                resultMap.put("doc", fileBytes);
//                resultMap.put("status", "success");
//            } else {
//                resultMap.put("msg", "Please provide valid key and value");
//                resultMap.put("status", "error");
//            }
//        } catch (Exception ex) {
//            resultMap.put("status", "exception");
//            resultMap.put("msg", ex.getMessage());
//            ex.printStackTrace();
//            logger.error("Problem occur while fetch document bytes : " + map.toString());
//        }
//        return resultMap;
//    }
    

    @Override
    public Map findAllByEmployeeId(Map map) {
        Map resultMap = new HashMap<>();
        try {
            if (map.containsKey("employeeId") && map.get("employeeId") != null) {
                String startYear= map.get("financialYear").toString();
                Integer endYear= Integer.parseInt(map.get("financialYear").toString())+1;
                String fyYear= startYear +"-"+endYear;
                List<POIDocument> list = poirepo.findByEmployeeIdAndFinancialYear(Long.parseLong(map.get("employeeId").toString()),fyYear);
                resultMap.put("list", list);
                resultMap.put("status", "success");
                Map datesMap=dateService.getEmployeeInvestmentDates(Long.parseLong(map.get("employeeId").toString()), startYear);
                if(datesMap.containsKey("status")&&datesMap.get("status").toString().equalsIgnoreCase("success")){
                    resultMap.put("dates", datesMap.get("data"));
                }
                //System.out.println(map.get("employeeId")+" 145");
            } else {
                resultMap.put("msg", "Please provide valid key and value");
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            resultMap.put("msg", ex.getMessage());
        }
        return resultMap;
    }

    @Override
    public Map uploadPOI(MultipartFile file, String fileName, String investmentName, String subInvestmentName, Long declarationId, Long employeeId, Long organizationId, String financialYear) {
        Map resultMap = new HashMap<>();
        try {
            Optional<POIDocument> otherObj = poirepo.findByDeclarationIdAndInvestmentNameAndSubInvestmentName(declarationId, investmentName, subInvestmentName);
            if (otherObj.isPresent()) {
                POIDocument obj = otherObj.get();
                obj.setFileName(fileName);
                obj.setFileUrl(this.uploadFileOnGCP(file, financialYear, declarationId, investmentName, subInvestmentName));
                obj.setVerified(false);
                obj.setRejected(false);
                obj = poirepo.save(obj);
                resultMap.put("data", obj);
                resultMap.put("status", "success");
                if(obj.getFileUrl() !=null){
//                      this.sendNotification(employeeId, organizationId, investmentName);
                
                    try {
                        
                        List<String> RecipientEmails = new ArrayList<>();
                        List<String> RecipientNames = new ArrayList<>();

                       List<LinkedCaseInsensitiveMap> getOrgAccountant=empdetailsrepo.getAccountantOfOrg(organizationId);
                       logger.info("Accountant Details for notification "+getOrgAccountant);
                        getOrgAccountant.stream().forEach(action -> {
                            RecipientEmails.add(action.get("email").toString());
                            RecipientNames.add(action.get("name").toString());

                        });
                       
                        employeeDetails findByEmployeeId =empdetailsrepo.findByEmployeeId(employeeId);
                        logger.info("employeeDetails for notification "+findByEmployeeId);
                        
                        NotificationByWebsocket employeeNotification=new NotificationByWebsocket();
                
                        employeeNotification.setPriority("MODERATE");
                        employeeNotification.setType("INFO");
    			employeeNotification.setPushNotify(0);
    			employeeNotification.setMailNotify(0);
    			employeeNotification.setModuleName("Payroll");
    			employeeNotification.setOrganizationId(organizationId);
    			employeeNotification.setSenderId(0);
    			employeeNotification.setSenderName("");
    			employeeNotification.setTitle("There is a new update for the "+investmentName);
    			employeeNotification.setBody(findByEmployeeId.getName() +" Updated Investment Declaration");
    			employeeNotification.setRecipientEmails(RecipientEmails);
    			employeeNotification.setRecipientNames(RecipientNames);
                        
                        new Thread(() -> notificationService.sendNotification(employeeNotification)).start();
                 } catch (Exception e) {
                     logger.info("exception when send notification");
                     e.printStackTrace();
                     
                    }
                }
              
            } else {
                POIDocument obj = new POIDocument();
                obj.setOrganizationId(organizationId);
                obj.setEmployeeId(employeeId);
                obj.setDeclarationId(declarationId);
                obj.setInvestmentName(investmentName);
                obj.setSubInvestmentName(subInvestmentName);
                obj.setFinancialYear(financialYear);
                obj.setFileName(fileName);
                obj.setFileUrl(this.uploadFileOnGCP(file, financialYear, declarationId, investmentName, subInvestmentName));
                obj.setVerified(false);
                obj.setRejected(false);
                obj = poirepo.save(obj);
                resultMap.put("data", obj);
                resultMap.put("status", "success");
                 if(obj.getFileUrl() !=null){
                     
                     try {
                        
                        List<String> RecipientEmails = new ArrayList<>();
                        List<String> RecipientNames = new ArrayList<>();

                       List<LinkedCaseInsensitiveMap> getOrgAccountant=empdetailsrepo.getAccountantOfOrg(organizationId);
                       logger.info("Accountant Details for notification "+getOrgAccountant);
                        getOrgAccountant.stream().forEach(action -> {
                            RecipientEmails.add(action.get("email").toString());
                            RecipientNames.add(action.get("name").toString());

                        });
                       
                        employeeDetails findByEmployeeId =empdetailsrepo.findByEmployeeId(employeeId);
                         logger.info("employeeDetails for notification "+findByEmployeeId);
                        
                        NotificationByWebsocket employeeNotification=new NotificationByWebsocket();
                
                        employeeNotification.setPriority("MODERATE");
                        employeeNotification.setType("INFO");
    			employeeNotification.setPushNotify(0);
    			employeeNotification.setMailNotify(0);
    			employeeNotification.setModuleName("Payroll");
    			employeeNotification.setOrganizationId(organizationId);
    			employeeNotification.setSenderId(0);
    			employeeNotification.setSenderName("");
    			employeeNotification.setTitle("There is a new update for the "+investmentName);
    			employeeNotification.setBody(findByEmployeeId.getName() +" Updated Investment Declaration");
    			employeeNotification.setRecipientEmails(RecipientEmails);
    			employeeNotification.setRecipientNames(RecipientNames);
                        
                        new Thread(() -> notificationService.sendNotification(employeeNotification)).start();
                 } catch (Exception e) {
                     
                       logger.info("exception when send notification");
                       e.printStackTrace();
                        
                    }
                     
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            resultMap.put("msg", ex.getMessage());
        }
        return resultMap;
    }

    @Override
    public Map getEmployeeList(String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            if (map.containsKey("id") && map.get("id") != null) {
                String bearerToken = authenticationFilter.getJwtFromRequest(request);
                HttpHeaders header = new HttpHeaders();
                header.setBearerAuth(bearerToken);
                header.setContentType(MediaType.TEXT_PLAIN);
                HttpEntity<String> entity = new HttpEntity<>(data, header);
                Map req = restTemplate.exchange(reimburshment_url + "/users/getEmployeebyOrgId", HttpMethod.POST, entity, HashMap.class).getBody();
                Map res = mapper.readValue(EncryptDecryptUtils.decrypt(req.get("data").toString()), LinkedCaseInsensitiveMap.class);
                List<LinkedHashMap> employeeList = (List<LinkedHashMap>) res.get("list");
                //List<LinkedHashMap> employeeDepartments = (List<LinkedHashMap>) res.get("departments");
                Set<Long> empIdList = poirepo.findAllEmployeeIds(Long.parseLong(map.get("id").toString()));
                System.out.println("poi employee" + empIdList);
                System.out.println("All employee" + employeeList);
                employeeList = employeeList.stream().filter(e -> empIdList.contains(Long.parseLong(e.get("employeeId").toString()))).collect(Collectors.toList());
                List<LinkedCaseInsensitiveMap> pendingDocList=poirepo.findPendingDocument(Long.parseLong(map.get("id").toString()));
               
                if(!pendingDocList.isEmpty()){
  
                    // Iterate over the employee list and update the status
                    for (LinkedHashMap employee : employeeList) {
                       Long employeeId = Long.parseLong(employee.get("employee_id").toString());
                       employee.put("docStatus", "Doc not uploaded");
                        pendingDocList.stream().forEach(action->{
                          Long empId=Long.parseLong(action.get("employee_id").toString());
                          if(Objects.equals(empId, employeeId)){
                              if(Long.parseLong(action.get("pending").toString())>0){
                                  employee.put("docStatus", "Pending");
                              }
                              else{
                               employee.put("docStatus", "Verified");
                              }
                                  
                          }
                         
                        });
                    }

                }
                
                resultMap.put("status", "success");
                resultMap.put("list", employeeList);
//                System.out.println("employeeList"+employeeList.toString());
            } else {
                resultMap.put("msg", "Please provide valid key and value");
                resultMap.put("status", "error");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            resultMap.put("msg", ex.getMessage());
        }
        return resultMap;
    }

    @Override
    public Map rejectOrApproveDocument(Map map) {
        Map resultMap = new HashMap<>();
        try {
            if (map.containsKey("id") && map.get("id") != null) {
                Optional<POIDocument> poiObj = poirepo.findById(Long.parseLong(map.get("id").toString()));
                if (poiObj.isPresent()) {
                    POIDocument poiDoc = poiObj.get();
                    poiDoc.setVerified(Boolean.parseBoolean(map.get("verified").toString()));
                    poiDoc.setRejected(Boolean.parseBoolean(map.get("rejected").toString()));
                    poirepo.save(poiDoc);
                    resultMap.put("status", "success");
                } else {
                    resultMap.put("msg", "Data not found with this id");
                    resultMap.put("status", "error");
                }
            } else {
                resultMap.put("msg", "Please provide valid key and value");
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            resultMap.put("msg", ex.getMessage());
        }
        return resultMap;
    }
    
    public Map sendNotification(Long employeeId, Long organizationId,String investmentName){
        Map resultMap=new HashMap<>();
        try {
            List<String> RecipientEmails = new ArrayList<>();
            List<String> RecipientNames = new ArrayList<>();
            
            List<LinkedCaseInsensitiveMap> getOrgAccountant=empdetailsrepo.getAccountantOfOrg(organizationId);
            
            getOrgAccountant.stream().forEach(action->{
                RecipientEmails.add(action.get("email").toString());
                RecipientNames.add(action.get("name").toString());
               
            });
            
            employeeDetails findByEmployeeId =empdetailsrepo.findByEmployeeId(employeeId);
            new Thread(() -> {
                String title = investmentName +"Document uploaded by "+findByEmployeeId.getName();
                String body = investmentName +"Document uploaded by "+findByEmployeeId.getName();
                String right_url1 = "/poidoc";
                String service = "Proof Of Investment";
                fcmommonNotification.fcmcommonNotification(organizationId, employeeId, RecipientEmails, RecipientNames, title, body, right_url1, service,findByEmployeeId.getName());
            }).start();
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
            resultMap.put("msg", "Exception in Notifiaction ");
        }
        return resultMap;
    }

    @Override
    public Map getPoiAccordingToFyYear(String request) {
        Map response=new HashMap();
         try{
          Map data = mapper.readValue(EncryptDecryptUtils.decrypt(request), LinkedCaseInsensitiveMap.class);
         Long organizationId=Long.parseLong(data.get("id").toString());
       //  String fyYear= data.get("fyYear").toString();
          String startYear= data.get("fyYear").toString();
          Integer endYear= Integer.parseInt(startYear)+1;
          String fyYear= startYear +"-"+endYear;
         List<LinkedCaseInsensitiveMap> employeeInvestments=poiRepo.employeeInvestements(organizationId,startYear);
         List<LinkedCaseInsensitiveMap> pendingDocList=poirepo.findPendingDocumentAccordingToFyYear(organizationId,fyYear);
//         List<Long> employeeIds = pendingDocList.stream()
//            .map(map -> (Number) map.get("employee_id")) // Extract employee_id as a Number
//            .map(Number::longValue) // Convert Number to Long
//            .collect(Collectors.toList());
//         List<LinkedCaseInsensitiveMap> employeeList=poirepo.employeeDetails(employeeIds);
         if(!pendingDocList.isEmpty()){
                    // Iterate over the employee list and update the status
                    for (LinkedCaseInsensitiveMap employee : employeeInvestments) {
                       Long employeeId = Long.parseLong(employee.get("employee_id").toString());
                       employee.put("docStatus", "Doc not uploaded");
                       employee.put("year",startYear);
                        pendingDocList.stream().forEach(action->{
                          Long empId=Long.parseLong(action.get("employee_id").toString());
                          if(Objects.equals(empId, employeeId)){
                              if(Long.parseLong(action.get("pending").toString())>0){
                                  employee.put("docStatus", "Pending");
                                  employee.put("submitted_by",action.get("submitted_by"));
                              }
                              else{
                               employee.put("docStatus", "Verified");
                               employee.put("submitted_by",action.get("submitted_by"));
                              }
                                  
                          }
                         
                        });
                    }

                }
                
                response.put("status", "success");
                response.put("list", employeeInvestments);
  
             
         }catch(Exception e){
             response.put("status", "exception");
             response.put("list", e.getMessage());
         }
         return response;
    }
}
