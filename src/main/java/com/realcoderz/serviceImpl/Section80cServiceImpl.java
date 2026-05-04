/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.repository.Section80cRepository;
import com.realcoderz.service.Section80cService;
import static com.realcoderz.serviceImpl.SalaryBreakupServiceImpl.LOGGER;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Lalit Raghav
 */
@Service
public class Section80cServiceImpl implements Section80cService {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public Section80cRepository section80crepo;

    @Override
    public Map findSectionById(String data) {

        Map resultMap = new HashMap();
        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            LinkedCaseInsensitiveMap section80c = section80crepo.findSection80cByIds(Long.parseLong(map.get("employeeId").toString()));

            if (section80c != null) {
                resultMap.put("status", "success");
                resultMap.put("list", section80c);

            } else {
                resultMap.clear();
                resultMap.put("status", "error");

            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in OtherSectionServiceImpl -> save() :: ", ex);
        }
        return resultMap;
    }

}
