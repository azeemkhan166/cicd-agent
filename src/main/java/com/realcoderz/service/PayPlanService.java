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
public interface PayPlanService {
    
    public Map save(Map map);
    public Map getAllPayPlan(Map map);
    public Map findById(Map map);
    public Map calculationAmount(Map map);
    public Map calculateStandard(Map map);
    public Map findLogsById(Map map);
    
    
}
