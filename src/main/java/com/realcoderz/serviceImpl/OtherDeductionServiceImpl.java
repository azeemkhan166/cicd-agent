/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.IncomeTax;
import com.realcoderz.repository.OtherDeductionRepository;
import com.realcoderz.service.OtherDeductionService;
import static com.realcoderz.serviceImpl.EmployeeNetPayServiceImpl.logger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 */

@Service
public class OtherDeductionServiceImpl implements OtherDeductionService{
    
     ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(OtherDeductionServiceImpl.class);

    @Autowired
    private OtherDeductionRepository otherDeductionRepo;
    
    @Override
    public Map getTDS(Map map) {
        Map resultMap=new HashMap<>();
        double tds=0;
        try{
        List<LinkedCaseInsensitiveMap> incomeTax=otherDeductionRepo.fetchDataInPdf(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
            for(LinkedCaseInsensitiveMap i:incomeTax){
                if(i.containsKey("amount")){
                tds=(Double)i.get("amount");
                }
            }
            resultMap.put("tds", tds);
            resultMap.put("status", "success");
        }
        catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in OtherDeductionServiceImpl -> getTDS() :: ", ex);

        }
        return resultMap;
    }
    
}
