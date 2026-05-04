/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;
import org.springframework.stereotype.Service;

/**
 *
 * @author lalit Raghav
 */
@Service
public interface NewTaxRegimeSlabService {
    
    public Map saveNewTaxSlab(Map map);
    
    public Map fetchNewTaxSlab(Map map);
    
    public Map saveAllNewTaxSlabs(Map map);
    
     public Map getAllNewTaxSlabs();
    
}
