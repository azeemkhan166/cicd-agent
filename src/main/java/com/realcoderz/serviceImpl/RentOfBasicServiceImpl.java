package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.realcoderz.model.RentOFBasic;
import com.realcoderz.repository.RentOfBasicRepository;
import com.realcoderz.service.RentOfBasicService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Lalit Raghav
 * edited By Astha
 */
@Service
public class RentOfBasicServiceImpl implements RentOfBasicService {

    static final Logger LOGGER = LoggerFactory.getLogger(RentOfBasicServiceImpl.class);
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public RentOfBasicRepository rentOfBasicrepo;

    @Override
    public Map saveforRent(Map map) {
        Map resultMap = new HashMap();
        try {
            RentOFBasic rentofBasic = mapper.convertValue(map, RentOFBasic.class);
            if (rentofBasic != null) {
                rentOfBasicrepo.save(rentofBasic);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "Exception");
            LOGGER.info("Problem in RentOfBasicServiceImpl -> saveforRent() :: ", ex);
        }

        return resultMap;

    }

    @Override
    public Map getRentOFBasic() {
        Map resultMap = new HashMap<>();
        try {
            LinkedCaseInsensitiveMap rentOfBasic = rentOfBasicrepo.getBasicRent();
            if (rentOfBasic != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", rentOfBasic);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Basic rent is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in RentOfBasicServiceImpl -> get() :: ", ex);

        }
        return resultMap;
    }
}


