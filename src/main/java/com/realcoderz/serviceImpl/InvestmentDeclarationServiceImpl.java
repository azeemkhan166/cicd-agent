/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.InvestmentDeclaration;
import com.realcoderz.model.NotificationByWebsocket;
import com.realcoderz.model.OtherSection;
import com.realcoderz.model.OtherSectionApproved;
import com.realcoderz.model.RentAmount;
import com.realcoderz.model.RentAmountApproved;
import com.realcoderz.model.Section80c;
import com.realcoderz.model.Section80cApproved;
import com.realcoderz.model.employeeDetails;
import com.realcoderz.repository.InvestmentDeclarationRepository;
import com.realcoderz.repository.OtherSectionApprovedRepository;
import com.realcoderz.repository.OtherSectionRepository;
import com.realcoderz.repository.RentAmountApprovedRepository;
import com.realcoderz.repository.RentAmountRepository;
import com.realcoderz.repository.Section80cApprovedRepository;
import com.realcoderz.repository.Section80cRepository;
import com.realcoderz.repository.employeeDetailsRepository;
import com.realcoderz.service.InvestmentDeclarationService;
import com.realcoderz.service.NotificationByWebsocketService;
import static com.realcoderz.serviceImpl.AllowanceServiceImpl.logger;
import com.realcoderz.util.EncryptDecryptUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Lalit Raghav
 */
@Service
public class InvestmentDeclarationServiceImpl implements InvestmentDeclarationService {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private InvestmentDeclarationRepository inverstmentdeclarationrepo;

    @Autowired
    private RentAmountRepository rentamountrepo;

    @Autowired
    private Section80cRepository section80crepo;

    @Autowired
    private OtherSectionRepository otherSectionrepo;

    @Autowired
    private RentAmountApprovedRepository rentamountApprovedRepo;

    @Autowired
    private Section80cApprovedRepository section80cApprovedRepo;

    @Autowired
    private OtherSectionApprovedRepository otherSectionApprovedRepo;

    @Autowired
    private employeeDetailsRepository empDetailsRepo;

    @Autowired
    private NotificationByWebsocketService notificationService;

    @Override
    public Map save(Map map) {
        Map resultMap = new HashMap<>();
        logger.info("Inverstment Declaration form data show at save time  -> :: "+map);
        try {
            InvestmentDeclaration invers = mapper.convertValue(map.get("declaration_key"), InvestmentDeclaration.class);

            int isInvestmentsave=inverstmentdeclarationrepo.isInvestmentDeclared(invers.getEmployeeid(),invers.getOrganizationid(),invers.getFy_year());
             List<Long> employeeIds=new ArrayList<>();
             employeeIds.add(invers.getEmployeeid());
            if(isInvestmentsave>0){
                 resultMap.put("status", "success");
                 resultMap.put("msg", "Investment already saved");
                 return resultMap;
            }

            if (invers.getDeclaration_id() == null || invers.getDeclaration_id() == 0) {
                invers = inverstmentdeclarationrepo.save(invers);
                invers.setApprovedByAcc(false);
                this.saveInvestmentDeclarationForAccountant(map, invers);
            }
            Section80c section80c = mapper.convertValue(map.get("section80C_key"), Section80c.class);
            section80c.setDeclaration_id(invers.getDeclaration_id());
            section80crepo.save(section80c);
            OtherSection otherSection = mapper.convertValue(map.get("section80D_key"), OtherSection.class);
            if (otherSection != null) {
                otherSection.setDeclaration_id(invers.getDeclaration_id());
            }
            otherSectionrepo.save(otherSection);
            List<RentAmount> rent = mapper.convertValue(map.get("rent_key"), new TypeReference<List<RentAmount>>() {
            });
            if (rent != null) {
                for (RentAmount amount : rent) {
                    amount.setDeclaration_id(invers.getDeclaration_id());
                }
            }
            rentamountrepo.saveAll(rent);
            new Thread(()->{
            List<employeeDetails> empDetails= empDetailsRepo.findByEmployeeIds(employeeIds);
             List<String> empName=new ArrayList<>();
             List<String> empEmails=new ArrayList<>();
             empName.add(empDetails.get(0).getName());
             empEmails.add(empDetails.get(0).getEmail());
                 NotificationByWebsocket employeeNotification=new NotificationByWebsocket();
              employeeNotification.setPriority("MODERATE");
              employeeNotification.setType("INFO");
              employeeNotification.setPushNotify(0);
    	      employeeNotification.setMailNotify(0);
    	      employeeNotification.setModuleName("Payroll");
    	      employeeNotification.setOrganizationId(empDetails.get(0).getOrganizationId());
    	      employeeNotification.setSenderId(0);
    	      employeeNotification.setSenderName("");
    	      employeeNotification.setTitle("Investment Declaration Form Submitted");
    	      employeeNotification.setBody("Your investment declaration form has been successfully submitted.");
    	      employeeNotification.setRecipientEmails(empEmails);
    	      employeeNotification.setRecipientNames(empName);
              notificationService.sendNotification(employeeNotification);
            }).start();

            resultMap.clear();
            resultMap.put("status", "success");

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in InvestmentDeclarationServiceImpl -> save() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map saveForAcc(Map map) {
        Map resultMap = new HashMap<>();
        logger.info("Account Inverstment Declaration form data show at save time  -> :: "+map);
        try {
            if (map.containsKey("section80C_key") && map.containsKey("section80D_key") && map.containsKey("rent_key") && map.containsKey("declarationId") && map.get("declarationId") != null) {
                Long declarationId = Long.parseLong(map.get("declarationId").toString());
                Optional<InvestmentDeclaration> inversObj = inverstmentdeclarationrepo.findById(declarationId);
                if (inversObj.isPresent()) {
                    InvestmentDeclaration invers = inversObj.get();
                    invers.setApprovedByAcc(true);
                    resultMap.put("status", this.saveInvestmentDeclarationForAccountant(map, invers));
                    resultMap.put("msg", "Save & Approved successfully");
                } else {
                    resultMap.put("status", "error");
                    resultMap.put("msg", "No investment declare with this id");
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid key and value");
            }
        } catch (Exception ex) {
            logger.error("Problem to save declare investment for accountant" + ex);
        }
        return resultMap;
    }

    private String saveInvestmentDeclarationForAccountant(Map map, InvestmentDeclaration invers) {
        try {
            if (map.containsKey("section80C_key") && map.containsKey("section80D_key") && map.containsKey("rent_key")) {
                Section80cApproved section80c = mapper.convertValue(map.get("section80C_key"), Section80cApproved.class);
                if (section80c != null) {
                    section80c.setDeclaration_id(invers.getDeclaration_id());
                    double total = (section80c.getProvident_fund_contribution()
                            + section80c.getLife_insurance_premium()
                            + section80c.getPublic_provident_fund()
                            + section80c.getVoluntary_provident_fund()
                            + section80c.getPension_fund_contribution()
                            + section80c.getNational_savings_certificate()
                            + section80c.getInterest_accrued_on_nsc()
                            + section80c.getUnit_linked_insurance_policy()
                            + section80c.getMutual_funds()
                            + section80c.getPayment_of_tuition_fees_for_children()
                            + section80c.getPrincipal_repayment_of_housing_loan()
                            + section80c.getRegistration_charges_incurred_for_buying_house()
                            + section80c.getSukanya_samriddhi_yojana()
                            + section80c.getInfrastructure_bonds()
                            + section80c.getBank_fixed_deposit()
                            + section80c.getPost_office_term_deposit())
                            + section80c.getSec80ccc();
                    invers.setTotal_allowances(total);
                    logger.info("Account Inverstment Declaration form Section 80c total  -> :: "+total);
                    section80cApprovedRepo.save(section80c);
                }
                OtherSectionApproved otherSection = mapper.convertValue(map.get("section80D_key"), OtherSectionApproved.class);
                System.out.println("otherSection===="+otherSection);
                if (otherSection != null) {

                    OtherSectionApproved otherSectionUpdate= otherSectionApprovedRepo.getOtherSectionApprovedDetails(invers.getDeclaration_id());
                    if(otherSectionUpdate !=null){

                        otherSectionUpdate.setTds(otherSection.getTds());
                        otherSectionUpdate.setSec80g(otherSection.getSec80g());
                        otherSectionUpdate.setSec80e(otherSection.getSec80e());
                        otherSectionUpdate.setIncome_fromPrevious_Employer(
                            (otherSection.getIncome_fromPrevious_Employer() > 0)
                            ? otherSection.getIncome_fromPrevious_Employer()
                            : otherSection.getIncome_from_previous_employer());
                        otherSectionUpdate.setStatus(otherSection.getStatus());
                        otherSectionUpdate.setNational_pension_scheme(otherSection.getNational_pension_scheme());
                        otherSectionUpdate.setInterest_income_fromsaving(otherSection.getInterest_income_fromsaving());
                        otherSectionUpdate.setProfessional_tax(otherSection.getProfessional_tax());
                        otherSectionUpdate.setSec80d(otherSection.getSec80d());
                        otherSectionUpdate.setInterest_on_housing_loan_before(otherSection.getInterest_on_housing_loan_before());
                        otherSectionUpdate.setSec80u(otherSection.getSec80u());
                        otherSectionUpdate.setPf(otherSection.getPf());
                        otherSectionUpdate.setSec80dd(otherSection.getSec80dd());
                        otherSectionUpdate.setSec80d_type(otherSection.getSec80d_type());
                        otherSectionApprovedRepo.save(otherSectionUpdate);
                         OtherSection other=otherSectionrepo.getOtherSectionDetails(invers.getDeclaration_id());
                         if(other !=null){
                         other.setStatus(otherSectionUpdate.getStatus());
                         otherSectionrepo.save(other);
                         }

                    }
                    else{
                            otherSection.setIncome_fromPrevious_Employer(
                            (otherSection.getIncome_fromPrevious_Employer() > 0)
                            ? otherSection.getIncome_fromPrevious_Employer()
                            : otherSection.getIncome_from_previous_employer());
                            otherSection.setDeclaration_id(invers.getDeclaration_id());
                    otherSectionApprovedRepo.save(otherSection);
                    }
                }
                List<RentAmountApproved> rent = mapper.convertValue(map.get("rent_key"), new TypeReference<List<RentAmountApproved>>() {
                });
                if (rent != null) {
                    double totals = 0.0;
                    for (RentAmountApproved amount : rent) {
                        amount.setDeclaration_id(invers.getDeclaration_id());
                        if (amount.getAmount() != null) {
                            totals += (amount.getAmount());
                        }
                    }
                    invers.setTotal_rent(totals);
                    logger.info("Account Inverstment Declaration form 12 month Rent total  -> :: "+totals);
                    rentamountApprovedRepo.saveAll(rent);
                }
            }
            if(map.get("taxSlabType")!=null){
                invers.setTaxSlabTpye(map.get("taxSlabType").toString());
            }
            if (invers.getDeclaration_id() != null || invers.getDeclaration_id() != 0) {
                inverstmentdeclarationrepo.save(invers);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Problem to save declare investment for accountant" + ex);
            return "error";
        }
        return "success";
    }

    @Override
    public Map getDeclarationByEmployeeId(String data) {
        Map resultMap = new HashMap();
         try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            LinkedCaseInsensitiveMap inverstmentdeclaration = inverstmentdeclarationrepo.getchDeclarationDataByIdChanges(Long.parseLong(map.get("employeeId").toString()), Integer.parseInt(map.get("fy_yearKey").toString()));
            if(inverstmentdeclaration == null){
                resultMap.put("status", "error");
                resultMap.put("accStatus", false);
                return resultMap;
            }

            Long declarationId = Long.parseLong(inverstmentdeclaration.get("declaration_id").toString());
            String slabType=  inverstmentdeclaration.get("tax_slab_tpye")!=null?inverstmentdeclaration.get("tax_slab_tpye").toString():"NewTaxSlabKey";
            List<LinkedCaseInsensitiveMap> rent = inverstmentdeclarationrepo.getRentDataById(declarationId);
            LinkedCaseInsensitiveMap sectionC = inverstmentdeclarationrepo.getSectionCdataById(declarationId);
            LinkedCaseInsensitiveMap otherSection = inverstmentdeclarationrepo.getOtherSectiondataById(declarationId);
            List<LinkedCaseInsensitiveMap> rentForAcc = inverstmentdeclarationrepo.getRentDataByIdForAcc(declarationId);
            LinkedCaseInsensitiveMap sectionCForAcc = inverstmentdeclarationrepo.getSectionCdataByIdForAcc(declarationId);
            LinkedCaseInsensitiveMap otherSectionForAcc = inverstmentdeclarationrepo.getOtherSectiondataByIdForAcc(declarationId);
            resultMap.put("list1", declarationId);
            resultMap.put("slabType",slabType);
            resultMap.put("list2", rent);
            resultMap.put("list3", sectionC);
            resultMap.put("list4", otherSection);
            resultMap.put("rentForAcc", rentForAcc);
            resultMap.put("sectionCForAcc", sectionCForAcc);
            resultMap.put("otherSectionForAcc", otherSectionForAcc);
            resultMap.put("otherSectionType",otherSectionForAcc.get("sec80d_type")!=null?otherSectionForAcc.get("sec80d_type"):null);
            resultMap.put("updateEmp", true);
            resultMap.put("accStatus", inverstmentdeclaration.get("approved_by_acc")!=null ? inverstmentdeclaration.get("approved_by_acc"):false);
            resultMap.put("status", "success");
            logger.info("Inverstment Declaration form data get Api call here  -> :: "+resultMap);
        } catch (Exception ex) {

            ex.printStackTrace();

            resultMap.clear();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    @Override
    public Map getCurrentFYYear() {
        Map resultMap = new HashMap();
        try {
            int month = 0;
            int year = 0;
            int startYear = 0;
            int endYear = 0;

            LocalDate date = LocalDate.now();
            month = date.getMonth().getValue();
            year = date.getYear();
            logger.info("Inverstment Declaration form Fy month -> :: "+month);
            logger.info("Inverstment Declaration form Fy year -> :: "+year);
            if (month >= 1 && month < 4) {
                startYear = year - 1;
                endYear = year;

            } else if (month >=4 && month <= 12) {
                startYear = year;
                endYear = year + 1;

            }

            resultMap.put("startYear", startYear);
            resultMap.put("lastYear", endYear);
            resultMap.put("status", "success");
            logger.info("Inverstment Declaration form Start Year  -> :: "+startYear);
            logger.info("Inverstment Declaration form End Year  -> :: "+endYear);
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "Exception");
        }

        return resultMap;
    }

    @Override
    public Map getTaxSlabKeyByEmpIdOrFy(String data) {
        Map resultMap = new HashMap();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            logger.info("getTaxSlabKeyByEmpIdOrFy method paylod print here  -> :: "+map);

            LinkedCaseInsensitiveMap inverstmentdeclaration = inverstmentdeclarationrepo.getSlabkey(Long.parseLong(map.get("employee_id").toString()),Long.parseLong(map.get("year").toString()));

            resultMap.put("key",inverstmentdeclaration );
            resultMap.put("status", "success");
            logger.info("getTaxSlabKeyByEmpIdOrFy method response print here at fech data from Inversment Declaration Table  -> :: "+resultMap);

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }


}
