/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;
import com.realcoderz.model.OrganizationSetUp;
import java.util.Map;

/**
 *
 * @author Astha
 */
public interface OrganizationSetUpService {

    public Map save(Map map);

    public Map fetch();
    
    public Map fetchByOrgId(Long organization_id);
    
    public Map findById(Long id);
    
    public Map update(Long id, OrganizationSetUp orgs);
     
    public Map delete(Long id);
    
    public Map isAlreadyExist(Long id);
    
    public Map saveOrUpdateLogo(String data);
    
    public Map getAuthorizatory(Long organization_id);
    
    public Map saveAuthorizatory(Map map);

}
