/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;

/**
 * @author Mayank
 * edited By Astha
 */

public interface ExemptionsService {

    public Map getExemptions(Map map);

    public Map saveExemptions(Map map);
    
    public Map getExemptionsPreviousVersion(Map map) ;
}
