/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.service;

import com.realcoderz.model.ProfessionalTaxNew;
import java.util.Map;

/**
 *
 * @author tause
 */
public interface ProfessionalTaxServiceNew
{
    public Map saveTaxSlab(String data);
    public Map getProfessionalTaxSlabs(Integer pageNo, Integer pageSize,String searchWord);
    public Map getSingleProfessionalTaxSlabs(String data);
}
