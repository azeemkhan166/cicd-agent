package com.realcoderz.service;

import java.util.Map;

/**
 *
 * @author Astha
 */
public interface PerquisiteService {
    
    public Map savePerquisite(Map map);
    
    public Map getPerquisite(Long organization_id);
    
    public Map deletePerquisite(Long id);
    
    public Map findByPerquisiteId(Long id);
    
}