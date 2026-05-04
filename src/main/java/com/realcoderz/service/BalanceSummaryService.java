/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.service;

import com.realcoderz.model.RunPayRoll;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tauseef
 */
public interface BalanceSummaryService {
    
    public Map saveBalanceSummary(List<RunPayRoll> runpayROllList);
    public Map getMonthlyOrgBalanceSummary(String request);
    public Map updatePayment(String data);
    public Map getSummaryHistory(String data); 
    
}
