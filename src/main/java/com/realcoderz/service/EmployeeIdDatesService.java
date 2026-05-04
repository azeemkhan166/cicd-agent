/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author tause
 */
public interface EmployeeIdDatesService {
   public Map saveEmployeesIdDeclerationDates(Long organizationId,String startDate,String endDate,Integer fyYear,String token);
   public Map getEmployeeInvestmentDates(Long employeeId,String fyYear);
   public Map saveEmployeeInvestmentDates(Long employeeId,Integer fyYear,Long organizationId,Boolean employeeRequested );
   public Map updateEmployeeInvestmentDates(Long employeeId,Integer fyYear,Long organizationId,String startDate,String endDate);
   public Map getRequestedInvestmentWindowDates(Long organizationId,Integer fyYear);
   public Map updateEmployeeWindowRequestWindow(Long rowId,String status);
   public void sendNotification(Set<Long> updateEmployeeIds,List<Long> newEmployeeIds,String startDate,String endDate,Long organizationId,String token);
  
}
