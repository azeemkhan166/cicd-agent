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
 * @author lalit
 */
@Service
public interface DeductionLogsService {
   
     public Map saveLogs(Map map);
    
    public Map getDeductionLogs(Map map);
}
