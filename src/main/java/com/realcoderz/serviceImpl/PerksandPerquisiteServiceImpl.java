package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.PerksandPerquisite;
import com.realcoderz.model.PerquisiteData;
import com.realcoderz.repository.PerksandPerquisiteRepository;
import com.realcoderz.repository.PerquisiteDataRepository;
import com.realcoderz.repository.PerquisiteRepository;
import com.realcoderz.service.PerksandPerquisiteService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Astha
 */
@Service
public class PerksandPerquisiteServiceImpl implements PerksandPerquisiteService{

    ObjectMapper mapper = new ObjectMapper();
    
    static final Logger logger = LoggerFactory.getLogger(CessServiceImpl.class);

    @Autowired
    PerksandPerquisiteRepository repo;
    
    @Autowired
    PerquisiteDataRepository Datarepo;
    
    @Autowired
    PerquisiteRepository Perquisiterepo;
    
    @Override
    public Map savePerksandPerquisite(Map map,HttpServletRequest request, String data) {
        Map resultMap = new HashMap<>();
        try {
            PerksandPerquisite perksandPerquisite = mapper.convertValue(map, PerksandPerquisite.class);            
            if (perksandPerquisite !=null) {
                perksandPerquisite = repo.save(perksandPerquisite);
            } 
            
            List<PerquisiteData> perksData = perksandPerquisite.getPerquisiteData();
                if(perksData!=null){
                    for (PerquisiteData p : perksData) {
                        p.setPerksandPerquisite(perksandPerquisite);
//                        p.setPerquisite_name(perksandPerquisite.getPerquisite_name());
                        Datarepo.save(p);
                    };
                }
            resultMap.put("status", "success");
//            else {
//                resultMap.clear();
//                resultMap.put("status", "error");
//                resultMap.put("msg", "Error while saving Perks and Perquisite..!..!");
//            }
       
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in PerksandPerquisiteServiceImpl -> savePerksandPerquisite() :: ", ex);

        }
        return resultMap;
        }

    @Override
    public Map getPerksandPerquisite(Long organization_id,Long employee_id) {
    Map resultMap = new HashMap<>();
      try{
          
        List<String> perquisite = Perquisiterepo.getPerquisiteName(organization_id);
//        List<PerquisiteData> perquisiteData = Datarepo.getPerquisite(organization_id);
        List<LinkedCaseInsensitiveMap> Employeeperquisite = repo.getPerksandPerquisite(employee_id);
        List<LinkedCaseInsensitiveMap> perquisiteId = repo.getPerquisiteById(organization_id);
         resultMap.put("perquisiteId", perquisiteId);
             resultMap.put("perquisitename", perquisite);
                if(Employeeperquisite !=null){
                   resultMap.put("employeePerksPerquisite", Employeeperquisite);
                    resultMap.put("status", "success");
//                     resultMap.put("isExists", true);
               } 
                
          else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "perksandperquisite's list is not available..!");
            }
     } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in PerksandPerquisiteServiceImpl -> getPerksandPerquisite() :: ", ex);
        }
    return resultMap;
    }
    
     @Override
    public Map getDataById(Map map) {
        Map resultMap = new HashMap();
        try {
            if (map.containsKey("organization_id") && (map.get("organization_id") != null)) {
                List<LinkedCaseInsensitiveMap> perkData = repo.getDataById(Long.parseLong(map.get("organization_id").toString()));
                if (perkData != null) {
                        resultMap.put("list", perkData);
                        resultMap.put("status", "success");
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Employee perk perquisite is not created yet !");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.error("Problem in PerksandPerquisiteServiceImpl :: getDataById() => Total perquisite amount", ex);
        }
        return resultMap;
    }
    
}