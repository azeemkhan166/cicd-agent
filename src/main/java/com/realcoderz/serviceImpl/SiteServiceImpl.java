/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.realcoderz.config.GcpConfig;
import com.realcoderz.model.ComplianceDocument;
import com.realcoderz.model.Site;
import com.realcoderz.repository.ComplianceDocumentRepository;
import com.realcoderz.repository.SiteRepository;
import com.realcoderz.service.SiteService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Service
public class SiteServiceImpl implements SiteService {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SiteRepository siteRepository;
    
    @Autowired
    private ComplianceDocumentRepository  complianceDocumentRepository;
    
    @Autowired
    private Storage storages;
    
    @Value("${bucketName}")
    String bucketName;
    
    @Value("${gcpFilePath}")
    private String gcpFilePath;
    
    @Autowired
    private GcpConfig gcpConfig;

    @Override
    public Map save(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Site siteData = mapper.convertValue(map, Site.class);
            
            System.out.println(siteData);
            
//            if (isNullOrEmpty(siteData.getSiteName())
//                    || isNullOrEmpty(siteData.getClientName())
//                    || isNullOrEmpty(siteData.getAddress())
//                    || isNullOrEmpty(siteData.getCity())
//                    || isNullOrEmpty(siteData.getState())
//                    || isNullOrEmpty(siteData.getPinCode())
//                    || isNullOrEmpty(siteData.getClientEmailId())
//                    || isNullOrEmpty(siteData.getServiceOrderRef())
//                    || isNullOrEmpty(siteData.getAuthorizedPerson())) {
//                resultMap.put("status", "error");
//                resultMap.put("msg", "All fields are Required");
//                return resultMap;
//            }
            
           Site value= siteRepository.save(siteData);
            resultMap.put("status", "success");
            resultMap.put("value", value);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    @Override
    public Map getAllSite(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("organizationId").toString());
            List<Site> data = siteRepository.findSiteById(ids);
            resultMap.put("status", "success");
            resultMap.put("value", data);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    @Override
    public Map findById(Map map) {
        
        
    Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("id").toString());
            Optional<Site> data = siteRepository.findById(ids);
            resultMap.put("status", "success");
            if(data.isPresent()){
                resultMap.put("value", data.get());
                resultMap.put("document", complianceDocumentRepository.findDocumentById(data.get().getId()));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap; }

    @Override
    public Map deleteById(Map map) {
        
            Map resultMap = new HashMap<>();
        try {

            Long ids = Long.parseLong(map.get("id").toString());
            siteRepository.deleteById(ids);
            resultMap.put("status", "success");
            resultMap.put("msg", "Data Deleted Success");
           
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
     }
    
    private boolean isNullOrEmpty(String value) {
    return value == null || value.trim().isEmpty();
}

    @Override
    public Map uploadSite(MultipartFile file, String fileName, Long siteId, Long organizationId,String siteName,String complianceName,String validTillDate) {
        
         Map resultMap = new HashMap<>();
        try {
            
       
       
        System.out.println("api hit");
        System.out.println(file);
        System.out.println(fileName);
        System.out.println(siteId);
        String url=this.uploadSiteFileOnGCP(file, organizationId, complianceName);

        if(url==null){
            
          resultMap.put("status", "Exception");
          resultMap.put("msg", "File not uploaded");
          return resultMap;
        }
        ComplianceDocument data=new ComplianceDocument();
        data.setComplianceName(complianceName);
        data.setOrganizationId(organizationId);
        data.setSiteId(siteId);
        data.setSiteName(siteName);
        data.setValidTillDate(validTillDate);
        data.setUrl(url);
        data.setFileName(fileName);
        complianceDocumentRepository.save(data);
        
        resultMap.put("status", "success");
        resultMap.put("msg", "File uploaded Successfully");
       
         } catch (Exception e) {
             
             e.printStackTrace();
        }
        
         return resultMap;
    }

    
     public String uploadSiteFileOnGCP(MultipartFile fileStream, Long organizationId,String complianceName) {
        
        try {
            
            String fileObject = "" + fileStream.getOriginalFilename();
            byte[] resfile = fileStream.getBytes();
            BlobId blobId = BlobId.of(bucketName, ("Site/" + organizationId + "/" + complianceName   + "/" + fileObject));
            System.out.println("uploadSiteFileOnGCP called");
             System.out.println(blobId);
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

            String path = gcpFilePath + bucketName + "/" + "Site/" + organizationId + "/" + complianceName   + "/" + fileObject;
            return path;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
     
        @Override
    public Map fetchDocument(Map map) {
        
        Map resultMap = new HashMap<>();
        try {
           
            String docUrl=complianceDocumentRepository.findUrl(Long.parseLong(map.get("id").toString()));
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
        }
        return resultMap;
    }
     
}
