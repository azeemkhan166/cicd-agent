
package com.realcoderz.controller;

import com.realcoderz.service.BalanceSummaryService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tauseef
 */
@RestController
@RequestMapping("/balancesummary")
public class BalanceSummaryController 
{
    @Autowired
    private BalanceSummaryService balanceService;
     
    @PostMapping("/getbalancesummary")
    public Map getOrgBalanceSummary(@RequestBody String data){
        Map response=new HashMap();
        try{
         response=balanceService.getMonthlyOrgBalanceSummary(data);
            
        }catch(Exception e){
            response.put("status", "exception");
            e.printStackTrace();
        }
        return response;
    }
    
    @PostMapping("/updateBalance")
    public Map updateBalance(@RequestBody String data){
        Map response=new HashMap();
        try{
         response=balanceService.updatePayment(data);    
        }catch(Exception e){
            response.put("status", "exception");
               response.put("msg",e.getMessage());
            e.printStackTrace();
            
        }
        return response;
    }
     @PostMapping("/getSummaryHistory")
    public Map getSingleSummary(@RequestBody String data){
        Map response=new HashMap();
        try{
         response=balanceService.getSummaryHistory(data);    
        }catch(Exception e){
            response.put("status", "exception");
               response.put("msg",e.getMessage());
            e.printStackTrace();
            
        }
        return response;
    }
    
    
}
