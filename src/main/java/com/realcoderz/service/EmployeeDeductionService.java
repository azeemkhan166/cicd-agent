/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;

/**
 *
 * @author Astha
 */
public interface EmployeeDeductionService {

    public Map getEmployeeDeduction(Long id);

    public Map saveEmployeeDeduction(Map map);
}
