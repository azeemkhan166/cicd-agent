/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;

/**
 * @author Astha
 */
public interface PayrollSettingService {
    
    public Map save(Map map);

    public Map fetch(Long id);

    public Map findById(Long id);
    
//    public Map update(Long id, PayrollSetting payroll);
    
}
