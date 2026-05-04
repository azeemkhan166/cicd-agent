/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;

/**
 *
 * @author Admin
 */
public interface DaIndexService {
    
    public Map getDaIndexCalculation(Map map);
    public Map getCPICalculation(Map map);
    public Map saveCPICalculation(Map map);
    public Map getAllDaIndexData(Map map);
    public Map getDaIndexById(Map map);
    
}
