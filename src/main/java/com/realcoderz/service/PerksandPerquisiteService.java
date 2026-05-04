package com.realcoderz.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Astha
 */
public interface PerksandPerquisiteService {
    
     public Map savePerksandPerquisite(Map map,HttpServletRequest request,String data);
    
    public Map getPerksandPerquisite(Long organization_id,Long employee_id);
    
     public Map<String, Object> getDataById (Map map);
}