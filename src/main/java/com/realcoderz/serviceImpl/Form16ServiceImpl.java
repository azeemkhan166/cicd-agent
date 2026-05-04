package com.realcoderz.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.realcoderz.model.Form16;
import com.realcoderz.repository.EmployeeDeductionRepository;
import com.realcoderz.repository.Form16Repository;
import com.realcoderz.repository.IncomeTaxRepository;
import com.realcoderz.repository.OtherSectionRepository;
import com.realcoderz.repository.PerksandPerquisiteRepository;
import com.realcoderz.service.Form16Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Form16ServiceImpl implements Form16Service {

    @Autowired
    private Form16Repository form16Repository;
    @Autowired
    private IncomeTaxRepository incomeTaxRepository;
    @Autowired
    private PerksandPerquisiteRepository perksandPerquisiteRepository;
    @Autowired
    private OtherSectionRepository otherSectionRepository;
    @Autowired
    private EmployeeDeductionRepository employeeDeductionRepository;
    
	@Override
	public Map save(Form16 data) {
		Map<String, Object> resultMap = new HashMap<>();
		System.out.println(data);
		try {
			Form16 findByCertificateNo = form16Repository.findByCertificateNoAndYear(data.getCertificateNo(), data.getYear());
			if(findByCertificateNo == null) {
				form16Repository.save(data);
				resultMap.put("msg", "Added successfully!");
			}else {
				//update
				findByCertificateNo.setOneC(data.getOneC());
				findByCertificateNo.setTwoA(data.getTwoA());
				findByCertificateNo.setTwoB(data.getTwoB());
				findByCertificateNo.setTwoC(data.getTwoC());
				findByCertificateNo.setTwoD(data.getTwoD());
				findByCertificateNo.setTwoF(data.getTwoF());
				findByCertificateNo.setTwoG(data.getTwoG());
				findByCertificateNo.setFourA(data.getFourA());
				findByCertificateNo.setFourB(data.getFourB());
				findByCertificateNo.setSevenB(data.getSevenB());
				findByCertificateNo.setTenCA(data.getTenCA());
				findByCertificateNo.setTenCB(data.getTenCB());
				findByCertificateNo.setTenEA(data.getTenEA());
				findByCertificateNo.setTenEB(data.getTenEB());
				findByCertificateNo.setTenFA(data.getTenFA());
				findByCertificateNo.setTenFB(data.getTenFB());
				findByCertificateNo.setTenJA(data.getTenJA());
				findByCertificateNo.setTenJB(data.getTenJB());
				findByCertificateNo.setTenJC(data.getTenJC());
				findByCertificateNo.setTenLA(data.getTenLA());
				findByCertificateNo.setTenLB(data.getTenLB());
				findByCertificateNo.setTenLC(data.getTenLC());
				findByCertificateNo.setName(data.getName());
				findByCertificateNo.setFatherName(data.getFatherName());
				findByCertificateNo.setDesignation(data.getDesignation());
				findByCertificateNo.setPlace(data.getPlace());
				
				form16Repository.save(findByCertificateNo);
				resultMap.put("msg", "Updated successfully!");
			}
			resultMap.put("status", "success");
		}catch(Exception e) {
			log.info(e.getLocalizedMessage());
		}
		return resultMap;
	}

	@Override
	public Map get(Form16 data) {
		System.out.println(data);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("status", "success");
		try {
			Form16 form16 = form16Repository.findByEmployeeIdAndYear(data.getEmployeeId(), data.getYear());
			resultMap.put("data", form16);
			String subTotal = incomeTaxRepository.getSubTotal(data.getOrganizationId(), data.getEmployeeId(), data.getYear());
			String perquisiteSum = perksandPerquisiteRepository.getPerquisiteSum(data.getEmployeeId(), data.getOrganizationId());
			String professionalTax = employeeDeductionRepository.getProfessionalTax(data.getEmployeeId(), data.getOrganizationId(), data.getYear());
			LinkedCaseInsensitiveMap inverstment_declaration_map = otherSectionRepository.getIncomeFromPreviousEmployer(data.getEmployeeId(), data.getOrganizationId(), data.getYear());
			
			resultMap.put("oneA", subTotal != null ? subTotal : "0.00");
			resultMap.put("oneB", perquisiteSum != null ? perquisiteSum : "0.00");
			resultMap.put("oneE", inverstment_declaration_map != null ? inverstment_declaration_map.get("income_from_previous_employer").toString() : "0.00");
			resultMap.put("twoE", inverstment_declaration_map != null ? inverstment_declaration_map.get("total_rent").toString() : "0.00");
			resultMap.put("fourC", professionalTax != null ? professionalTax : "0.00");
			resultMap.put("sevenA", inverstment_declaration_map != null ? inverstment_declaration_map.get("interest_on_housing_loan_before").toString() : "0.00");
			
			resultMap.put("tenA", inverstment_declaration_map != null ? inverstment_declaration_map.get("total_allowances").toString() : "0.00");
			resultMap.put("tenB", inverstment_declaration_map != null ? inverstment_declaration_map.get("sec80ccc").toString() : "0.00");
			resultMap.put("tenG", inverstment_declaration_map != null ? inverstment_declaration_map.get("sec80d").toString() : "0.00");
			resultMap.put("tenH", inverstment_declaration_map != null ? inverstment_declaration_map.get("sec80e").toString() : "0.00");
			resultMap.put("tenI", inverstment_declaration_map != null ? inverstment_declaration_map.get("sec80g").toString() : "0.00");
			resultMap.put("thirteen", incomeTaxRepository.getTaxOnTotalIncome(data.getOrganizationId(), data.getEmployeeId(), data.getYear()));
			resultMap.put("fourteen", incomeTaxRepository.getRelief89(data.getOrganizationId(), data.getEmployeeId(), data.getYear()));
			resultMap.put("fifteen", incomeTaxRepository.getSurcharge(data.getOrganizationId(), data.getEmployeeId(), data.getYear()));
			resultMap.put("sixteen", incomeTaxRepository.getHealthAndEducationCess(data.getOrganizationId(), data.getEmployeeId(), data.getYear()));
			resultMap.put("eighteen", incomeTaxRepository.getTotalTaxDeductedTillDate(data.getOrganizationId(), data.getEmployeeId(), data.getYear()));
			

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception occured in checkAssetStatusAndGetEmpId {}" + e);
			resultMap.put("status","Please try again!");
		}

		log.info("Returning from checkAssetStatusAndGetEmpId().");
		System.out.println(resultMap);
		return resultMap;
	}
   
}
