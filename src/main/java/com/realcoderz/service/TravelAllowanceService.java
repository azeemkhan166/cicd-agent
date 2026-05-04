/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Bipul Singh
 */
public interface TravelAllowanceService {

    public Map findById(Map map);

    public Map saveOrUpdate(Map map);

    public Map list(Map map);

    public Map getTravelAllowanceAmount(String data, HttpServletRequest request);
    
     public Map deleteTravelAllowance(Map map);

}