package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.Perquisite;
import com.realcoderz.repository.PerquisiteRepository;
import com.realcoderz.service.PerquisiteService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Astha
 */
@Service
public class PerquisiteServiceImpl implements PerquisiteService{

    ObjectMapper mapper = new ObjectMapper();
    
    static final Logger logger = LoggerFactory.getLogger(CessServiceImpl.class);

    @Autowired
    PerquisiteRepository repo;
    
    @Override
    public Map savePerquisite(Map map) {
       Map resultMap = new HashMap<>();
        try {
            Perquisite perquisite = mapper.convertValue(map, Perquisite.class);
            if (perquisite != null) {
                repo.save(perquisite);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while saving Perquisite..!..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in PerquisiteServiceImpl -> savePerquisite() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getPerquisite(Long organization_id) {
    Map resultMap = new HashMap<>();
    List<Perquisite> perquisite = repo.getPerquisite(organization_id);
        try {
//            List<String> perquisite = repo.getPerquisite(Long.parseLong(map.get("organization_id").toString()));
            if (perquisite != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("perquisitename", perquisite);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Perquisite's list is not available..!");
            }
            
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in PerquisiteServiceImpl -> getPerquisite() :: ", ex);

        }
        return resultMap;
        }

    @Override
    public Map deletePerquisite(Long id) {
         Map resultMap = new HashMap<>();
        try {
            Optional<Perquisite> perquisite = repo.findById(id);
            if (perquisite.isPresent()) {
                repo.delete(perquisite.get());
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Perquisite is not deleted !");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in PerquisiteServiceImpl -> deletePerquisite() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map findByPerquisiteId(Long id) {
     Map resultMap = new HashMap();
        try {
            Optional<Perquisite> perquisite = repo.findById(id);
            if (perquisite.isPresent()) {
                resultMap.clear();
                resultMap.put("list", perquisite.get());
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Perquisite not found.!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in PerquisiteServiceImpl -> findByPerquisiteId() :: ", ex);

        }
        return resultMap;    }
    
}