/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.service;


import com.realcoderz.model.OrganizationIdDates;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author tause
 */
public interface OrganizationIdDatesService {

    public Map saveOrganizationIdDeclerationDates(OrganizationIdDates dates,String token);
    public Map getOrganizationFyYearData(Integer fyYear,Long organizationId);

}
