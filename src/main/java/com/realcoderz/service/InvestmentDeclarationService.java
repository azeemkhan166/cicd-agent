/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;


/**
 *
 * @author Lalit Raghav
 */

public interface InvestmentDeclarationService {
     public Map save(Map map);

     public Map getDeclarationByEmployeeId(String data);

     public Map getTaxSlabKeyByEmpIdOrFy(String data);

     public Map getCurrentFYYear();

//     public Map saveUploadPOIFiles();

    public Map saveForAcc(Map map);
}
