/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.AllowanceTemplatePayPlan;
import com.realcoderz.model.AllowanceTemplatePayPlanLogs;
import com.realcoderz.model.CustomAllowance;
import com.realcoderz.model.CustomDeduction;
import com.realcoderz.model.DeductionDependsOnAllowance;
import com.realcoderz.model.DeductionTemplatePayPlan;
import com.realcoderz.model.DeductionTemplatePayPlanLogs;
import com.realcoderz.model.PayPlan;
import com.realcoderz.model.PayPlanLogs;
import com.realcoderz.model.SalaryBreakUp;
import com.realcoderz.repository.AllowanceTemplatePayPlanLogsRepository;
import com.realcoderz.repository.CustomAllowanceRepository;
import com.realcoderz.repository.CustomDeductionRepository;
import com.realcoderz.repository.DeductionTemplatePayPlanLogsRepository;
import com.realcoderz.repository.PayPlanLogsRepository;
import com.realcoderz.repository.PayPlanRepository;
import com.realcoderz.repository.SalaryBreakuprepo;
import com.realcoderz.service.PayPlanService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Admin
 */
@Service
public class PayPlanServiceImpl implements PayPlanService {

    @Autowired
    private PayPlanRepository payPlanRepository;

    @Autowired
    private CustomAllowanceRepository customAllowanceRepository;

    @Autowired
    private CustomDeductionRepository customDeductionRepository;

    @Autowired
    private SalaryBreakuprepo salaryBreakuprepo;

    @Autowired
    private PayPlanLogsRepository payPlanLogsRepository;

    @Autowired
    private AllowanceTemplatePayPlanLogsRepository allowanceTemplatePayPlanLogsRepository;

    @Autowired
    private DeductionTemplatePayPlanLogsRepository deductionTemplatePayPlanLogsRepository;

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map save(Map map) {

        Map resultMap = new HashMap<>();

        try {

            PayPlan PayPlanData = mapper.convertValue(map, PayPlan.class);

            System.out.println("PayPlanData");
            System.out.println(PayPlanData);

            PayPlan PayPlanDatalog = payPlanRepository.save(PayPlanData);

            // below code is for Logs
            List<AllowanceTemplatePayPlanLogs> allowanceLogs = new ArrayList<>();
            List<DeductionTemplatePayPlanLogs> deductionLogs = new ArrayList<>();

            PayPlanLogs logs = new PayPlanLogs();

            logs.setOrganizationId(PayPlanDatalog.getOrganizationId());
            logs.setEmployeeType(PayPlanDatalog.getEmployeeType());
            logs.setSiteId(PayPlanDatalog.getSiteId());
            logs.setPayMode(PayPlanDatalog.getPayMode());
            logs.setPlanName(PayPlanDatalog.getPlanName());
            logs.setDescription(PayPlanDatalog.getDescription());
            logs.setSkilledLevelType(PayPlanDatalog.getSkilledLevelType());
            logs.setOvertime(PayPlanDatalog.getOvertime());
            logs.setPtamount(PayPlanDatalog.getPtamount());
            logs.setTimes(PayPlanDatalog.getTimes());
            logs.setRate(PayPlanDatalog.getRate());
            logs.setArrear(PayPlanDatalog.getArrear());
            logs.setReimb(PayPlanDatalog.getReimb());
            logs.setIncentive(PayPlanDatalog.getIncentive());
            logs.setDays(PayPlanDatalog.getDays());
            logs.setPtNumber(PayPlanDatalog.getPtNumber());
            logs.setStateName(PayPlanDatalog.getStateName());
            logs.setPayPlanId(PayPlanDatalog.getId());
            logs.setModifyBy(PayPlanDatalog.getModifyBy());
            logs.setWeekoffflag(PayPlanDatalog.getWeekoffflag());
            logs.setSecondsalaryflag(PayPlanDatalog.getSecondsalaryflag());
            logs.setBonus(PayPlanDatalog.getBonus());
            logs.setPercentage(PayPlanDatalog.getPercentage());
            logs.setMaximumvalue(PayPlanDatalog.getMaximumvalue());
            logs.setBasicRate(PayPlanDatalog.getBasicRate());

            // Date currentDateTime = new Date();
            logs.setLoginTime(Instant.now());

            PayPlanLogs PayPlanLogsValue = payPlanLogsRepository.save(logs);

            PayPlanDatalog.getAllowanceTemplatePayPlan().stream().forEach(action -> {

                AllowanceTemplatePayPlanLogs allowanceLog = new AllowanceTemplatePayPlanLogs();
                allowanceLog.setAllowanceId(action.getAllowanceId());
                allowanceLog.setAllowanceType(action.getAllowanceType());
                allowanceLog.setAmount(action.getAmount());
                allowanceLog.setPayPlanId(PayPlanDatalog.getId());
                allowanceLog.setPayPlanLogsId(PayPlanLogsValue.getId());

                allowanceLogs.add(allowanceLog);
            });

            PayPlanDatalog.getDeductionTemplatePayPlan().stream().forEach(d -> {

                DeductionTemplatePayPlanLogs deductionlogs = new DeductionTemplatePayPlanLogs();

                deductionlogs.setDeductionId(d.getDeductionId());
                deductionlogs.setDeductionType(d.getDeductionType());
                deductionlogs.setAmount(d.getAmount());
                deductionlogs.setPayPlanId(PayPlanDatalog.getId());
                deductionlogs.setPayPlanLogsId(PayPlanLogsValue.getId());

                deductionLogs.add(deductionlogs);

            });

            allowanceTemplatePayPlanLogsRepository.saveAll(allowanceLogs);
            deductionTemplatePayPlanLogsRepository.saveAll(deductionLogs);

            resultMap.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    @Override
    public Map getAllPayPlan(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("organizationId").toString());
            List<LinkedCaseInsensitiveMap> data = payPlanRepository.findPayPlanById(ids);
            resultMap.put("status", "success");
            resultMap.put("value", data);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    @Override
    public Map findById(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("id").toString());
            Optional<PayPlan> data = payPlanRepository.findById(ids);
            resultMap.put("status", "success");
            if (data.isPresent()) {
                resultMap.put("value", data.get());
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    @Override
    public Map calculationAmount(Map map) {

        Map resultMap = new HashMap<>();

        try {

            PayPlan PayPlanData = mapper.convertValue(map, PayPlan.class);

            Double grossSalary = PayPlanData.getGrossSalary();
            Double basicRate = PayPlanData.getBasicRate();
            String empType = PayPlanData.getEmployeeType();
            // double days = PayPlanData.getDays();
            double days = 26;
            Long basicSalaryId[] = new Long[1];
            basicSalaryId[0] = 0l;
            Long grossSalaryId[] = new Long[1];
            grossSalaryId[0] = 0l;
            Long otherAllowanceId[] = new Long[1];
            otherAllowanceId[0] = 0l;
            Long HraAllowanceId[] = new Long[1];
            HraAllowanceId[0] = 0l;
            Double gross[] = new Double[1];
            gross[0] = grossSalary;

            if (empType.equalsIgnoreCase("worker")) {
                gross[0] = gross[0] * days;
            }

            if (gross == null) {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please enter Gross Salary");
                return resultMap;
            }
            List<AllowanceTemplatePayPlan> allowanceTemplatePayPlan = PayPlanData.getAllowanceTemplatePayPlan();
            List<CustomAllowance> customAllowance = customAllowanceRepository
                    .findAllowanceByIdAndType(PayPlanData.getOrganizationId(), empType);

            System.out.println("customAllowance hai ");
            System.out.println(customAllowance);

            System.out.println(allowanceTemplatePayPlan);

            // basic Salary Allowance Calculation
            for (CustomAllowance a : customAllowance) {

                String allowanceName = a.getAllowanceName();
                Long ids = a.getId();

                if (allowanceName.trim().equalsIgnoreCase("Basic Salary")) {

                    allowanceTemplatePayPlan.stream().forEach(action -> {

                        Long aId = action.getAllowanceId();

                        if (Objects.equals(aId, ids)) {
                            String allowanceType = action.getAllowanceType();
                            basicSalaryId[0] = aId;
                            if (allowanceType.equalsIgnoreCase("fixed")) {
                                // double amount = action.getAmount();
                                // if (empType.equalsIgnoreCase("worker")) {
                                double amount = basicRate * days;
                                action.setAmount(amount);

                                action.setCalculatedAmount(amount);
                            } else if (allowanceType.equalsIgnoreCase("variable")) {
                                action.setCalculatedAmount(
                                        Math.round(Math.round((gross[0] / 100) * action.getAmount()) * 100.00)
                                                / 100.00);
                            }
                        }
                    });

                }

                if (allowanceName.trim().equalsIgnoreCase("gross")) {
                    grossSalaryId[0] = ids;
                }
                if (allowanceName.trim().equalsIgnoreCase("other allowance")
                        || allowanceName.trim().equalsIgnoreCase("other allowances")) {
                    otherAllowanceId[0] = ids;
                }
                if (allowanceName.trim().equalsIgnoreCase("hra")) {
                    HraAllowanceId[0] = ids;
                }

            }

            // rest Allowance Calculation
            for (AllowanceTemplatePayPlan a : allowanceTemplatePayPlan) {
                Long aId = a.getAllowanceId();
                // checking for basic salary
                if (!Objects.equals(aId, basicSalaryId[0])) {

                    Long dependentAllowanceId = a.getAllowanceDependOn();

                    if (a.getAllowanceType().equalsIgnoreCase("fixed")) {

                        double amount = a.getAmount();
                        if (empType.equalsIgnoreCase("worker")) {
                            amount = amount * days;
                        }
                        a.setCalculatedAmount(amount);
                    } else if (a.getAllowanceType().equalsIgnoreCase("variable")) {

                        // checkinh for gross
                        if (Objects.equals(dependentAllowanceId, grossSalaryId[0])) {
                            a.setCalculatedAmount(
                                    Math.round(Math.round((gross[0] / 100) * a.getAmount()) * 100.00) / 100.00);
                        } else {
                            allowanceTemplatePayPlan.stream().forEach(action -> {

                                Long allowanceIds = action.getAllowanceId();

                                if (Objects.equals(dependentAllowanceId, allowanceIds)) {
                                    a.setCalculatedAmount(Math.round(
                                            Math.round((action.getCalculatedAmount() / 100) * a.getAmount()) * 100.00)
                                            / 100.00);

                                }
                            });

                        }

                    }
                }

            }

            // allowances Sum

            double allowanceSum = allowanceTemplatePayPlan.stream()
                    .map(AllowanceTemplatePayPlan::getCalculatedAmount)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum();
            System.out.println("otherAllowanceId[0] " + otherAllowanceId[0]);

            // check condition if Other Allowance is negative then less from hra

            double otherAllowanceAmount = gross[0] - allowanceSum;

            if (otherAllowanceAmount < 0) {

                allowanceTemplatePayPlan.stream().forEach(action -> {

                    Long allowanceIds = action.getAllowanceId();

                    if (Objects.equals(HraAllowanceId[0], allowanceIds)) {
                        double currentAmount = action.getCalculatedAmount() != null ? action.getCalculatedAmount()
                                : 0.0;
                        action.setCalculatedAmount(currentAmount + otherAllowanceAmount);
                    }
                });

            }

            allowanceSum = allowanceTemplatePayPlan.stream()
                    .map(AllowanceTemplatePayPlan::getCalculatedAmount)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum();
            double otherAllowanceAmount1 = gross[0] - allowanceSum;
            // other allowance calculation
            allowanceTemplatePayPlan.stream().forEach(action -> {

                if (Objects.equals(otherAllowanceId[0], action.getAllowanceId())) {
                    action.setCalculatedAmount(otherAllowanceAmount1);
                }

            });

            List<DeductionTemplatePayPlan> deductionTemplatePayPlan = PayPlanData.getDeductionTemplatePayPlan();

            // Deduction Calculation
            for (DeductionTemplatePayPlan d : deductionTemplatePayPlan) {

                if (d.getDeductionType().equalsIgnoreCase("fixed")) {
                    d.setCalculatedAmount(d.getAmount());
                } else if (d.getDeductionType().equalsIgnoreCase("variable")) {
                    List<DeductionDependsOnAllowance> deductionDependsOnAllowance = d.getDeductionDependOn();

                    List<Long> dependAllowanceId = deductionDependsOnAllowance.stream()
                            .map(DeductionDependsOnAllowance::getId)
                            .collect(Collectors.toList());

                    double totalAmount = 0.0;
                    if (allowanceTemplatePayPlan != null) {
                        totalAmount = allowanceTemplatePayPlan.stream()
                                .filter(plan -> plan != null && plan.getAllowanceId() != null
                                        && dependAllowanceId.contains(plan.getAllowanceId()))
                                .mapToDouble(
                                        plan -> plan.getCalculatedAmount() != null ? plan.getCalculatedAmount() : 0.0)
                                .sum();
                    }

                    if (dependAllowanceId.contains(grossSalaryId[0])) {

                        totalAmount = totalAmount + gross[0];
                    }

                    d.setCalculatedAmount(
                            Math.round(Math.round((totalAmount / 100) * d.getAmount()) * 100.00) / 100.00);

                }

            }

            double deductionSum = deductionTemplatePayPlan.stream()
                    .map(DeductionTemplatePayPlan::getCalculatedAmount)
                    .filter(Objects::nonNull) // remove nulls
                    .mapToDouble(Double::doubleValue)
                    .sum();

            // all allowance sum for net Payable

            double allowanceSumForNetPayable = allowanceTemplatePayPlan.stream()
                    .map(AllowanceTemplatePayPlan::getCalculatedAmount)
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum();

            PayPlanData.setAllowanceTemplatePayPlan(allowanceTemplatePayPlan);
            PayPlanData.setDeductionTemplatePayPlan(deductionTemplatePayPlan);
            PayPlanData.setNetPayable(allowanceSumForNetPayable - deductionSum);
            resultMap.put("status", "success");
            resultMap.put("value", PayPlanData);

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    @Override
    public Map calculateStandard(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Long basicSalaryId[] = new Long[1];
            basicSalaryId[0] = 0l;
            Double basicSalary[] = new Double[1];
            basicSalary[0] = 0.0;
            Long grossSalaryId[] = new Long[1];
            grossSalaryId[0] = 0l;
            Long otherAllowanceId[] = new Long[1];
            otherAllowanceId[0] = 0l;
            Long hraAllowanceId[] = new Long[1];
            hraAllowanceId[0] = 0l;
            Double gross[] = new Double[1];
            gross[0] = 0.0;

            gross[0] = Double.parseDouble(map.get("grossSalary").toString());
            Long empIds = Long.parseLong(map.get("emp_Id").toString());
            String empType = map.get("employee_Type").toString();

            // for view

            if (map.get("view") != null && Boolean.parseBoolean(map.get("view").toString())) {

                SalaryBreakUp SalaryBreakUp = salaryBreakuprepo.getDataFromSalaryBreakup(empIds, gross[0]);

                List<LinkedCaseInsensitiveMap> allowanceData = customAllowanceRepository
                        .getAllowanceData(SalaryBreakUp.getSid());
                List<LinkedCaseInsensitiveMap> deductionData = customDeductionRepository
                        .getDeductionData(SalaryBreakUp.getSid());

                resultMap.put("status", "success");
                resultMap.put("finalAllowance", allowanceData);
                resultMap.put("finalDeduction", deductionData);
                resultMap.put("totalEarning", SalaryBreakUp.getTotal_earning());
                resultMap.put("totalDeduction", SalaryBreakUp.getTotal_deduction());
                resultMap.put("netAmount", SalaryBreakUp.getNet_amount());
                resultMap.put("payPlanId", SalaryBreakUp.getPayPlanId());
                resultMap.put("siteId", SalaryBreakUp.getSiteId());
                resultMap.put("effectiveDate", SalaryBreakUp.getEffective_date());

                return resultMap;

            }

            Long payPlanId = Long.parseLong(map.get("payPlanId").toString());

            Optional<PayPlan> data = payPlanRepository.findById(payPlanId);

            List<LinkedCaseInsensitiveMap> allowanceList = new ArrayList<>();
            List<LinkedCaseInsensitiveMap> deductionList = new ArrayList<>();

            if (data.isPresent()) {

                PayPlan PayPlanData = data.get();
                // double days = PayPlanData.getDays();
                double days = 26;
                double basicRate = PayPlanData.getBasicRate();

                if (empType.equalsIgnoreCase("worker")) {
                    gross[0] = gross[0] * days;
                }

                List<AllowanceTemplatePayPlan> allowanceTemplatePayPlan = PayPlanData.getAllowanceTemplatePayPlan();
                List<CustomAllowance> customAllowance = customAllowanceRepository
                        .findAllowanceByIdAndType(PayPlanData.getOrganizationId(), empType);
                List<CustomDeduction> customDeduction = customDeductionRepository
                        .findDeductionById(PayPlanData.getOrganizationId());
                // basic Salary Allowance Calculation
                for (CustomAllowance a : customAllowance) {

                    LinkedCaseInsensitiveMap json = new LinkedCaseInsensitiveMap();
                    String allowanceName = a.getAllowanceName();
                    Long ids = a.getId();

                    if (allowanceName.trim().equalsIgnoreCase("Basic Salary")) {

                        allowanceTemplatePayPlan.stream().forEach(action -> {

                            Long aId = action.getAllowanceId();

                            if (Objects.equals(aId, ids)) {
                                String allowanceType = action.getAllowanceType();
                                basicSalaryId[0] = aId;
                                if (allowanceType.equalsIgnoreCase("fixed")) {
                                    double amount = basicRate * days;
                                    action.setAmount(amount);
                                    // double amount = action.getAmount();
                                    // if (empType.equalsIgnoreCase("worker")) {
                                    // amount = amount * days;
                                    // }

                                    action.setCalculatedAmount(amount);
                                    json.put("allowance_amount", amount);
                                    json.put("allowance_id", aId);
                                    json.put("type_of_allowance", allowanceType);
                                    json.put("allowance_name", allowanceName);
                                    json.put("percentage", amount);
                                    allowanceList.add(json);
                                    basicSalary[0] = amount;
                                } else if (allowanceType.equalsIgnoreCase("variable")) {
                                    double amount = Math
                                            .round(Math.round((gross[0] / 100) * action.getAmount()) * 100.00) / 100.00;
                                    // if(empType.equalsIgnoreCase("worker")){
                                    // amount=amount*days;
                                    // }

                                    json.put("allowance_amount", amount);
                                    json.put("allowance_id", aId);
                                    json.put("type_of_allowance", allowanceType);
                                    json.put("allowance_name", allowanceName);
                                    json.put("percentage", action.getAmount());
                                    allowanceList.add(json);
                                    action.setCalculatedAmount(amount);
                                    basicSalary[0] = amount;
                                }
                            }
                        });

                    }

                    if (allowanceName.trim().equalsIgnoreCase("gross")) {
                        grossSalaryId[0] = ids;
                    }
                    if (allowanceName.trim().equalsIgnoreCase("other allowance")
                            || allowanceName.trim().equalsIgnoreCase("other allowances")) {
                        otherAllowanceId[0] = ids;
                    }
                    if (allowanceName.trim().equalsIgnoreCase("hra")) {
                        hraAllowanceId[0] = ids;
                    }

                }

                // rest Allowance Calculation
                for (AllowanceTemplatePayPlan a : allowanceTemplatePayPlan) {
                    LinkedCaseInsensitiveMap json = new LinkedCaseInsensitiveMap();
                    // checking for basic salary
                    if (!Objects.equals(a.getAllowanceId(), basicSalaryId[0])) {

                        Long aId = a.getAllowanceId();
                        Long dependentAllowanceId = a.getAllowanceDependOn();

                        String allowanceName = customAllowance.stream()
                                .filter(ca -> ca.getId().equals(aId))
                                .map(CustomAllowance::getAllowanceName)
                                .findFirst()
                                .orElse(null);

                        if (a.getAllowanceType().equalsIgnoreCase("fixed")) {

                            double amount = a.getAmount();

                            if (empType.equalsIgnoreCase("worker")) {
                                amount = amount * days;
                            }

                            a.setCalculatedAmount(amount);
                            json.put("allowance_amount", amount);
                            json.put("allowance_id", aId);
                            json.put("type_of_allowance", a.getAllowanceType());
                            json.put("allowance_name", allowanceName);
                            json.put("percentage", a.getAmount());
                            allowanceList.add(json);
                        } else if (a.getAllowanceType().equalsIgnoreCase("variable")) {

                            // checkinh for gross
                            if (Objects.equals(dependentAllowanceId, grossSalaryId[0])) {

                                double amount = Math.round(Math.round((gross[0] / 100) * a.getAmount()) * 100.00)
                                        / 100.00;

                                // if(empType.equalsIgnoreCase("worker")){
                                // amount=amount*days;
                                // }

                                a.setCalculatedAmount(amount);

                                json.put("allowance_amount", amount);
                                json.put("allowance_id", aId);
                                json.put("type_of_allowance", a.getAllowanceType());
                                json.put("allowance_name", allowanceName);
                                json.put("percentage", a.getAmount());
                                allowanceList.add(json);

                            } else {
                                allowanceTemplatePayPlan.stream().forEach(action -> {

                                    Long allowanceIds = action.getAllowanceId();

                                    if (Objects.equals(dependentAllowanceId, allowanceIds)) {

                                        double amount = Math
                                                .round(Math.round((action.getCalculatedAmount() / 100) * a.getAmount())
                                                        * 100.00)
                                                / 100.00;
                                        // if(empType.equalsIgnoreCase("worker")){
                                        // amount=amount*days;
                                        // }

                                        a.setCalculatedAmount(amount);

                                        json.put("allowance_amount", amount);
                                        json.put("allowance_id", aId);
                                        json.put("type_of_allowance", a.getAllowanceType());
                                        json.put("allowance_name", allowanceName);
                                        json.put("percentage", a.getAmount());
                                        allowanceList.add(json);
                                    }
                                });

                            }

                        } else if (a.getAllowanceType().equalsIgnoreCase("other")) {

                            a.setCalculatedAmount(a.getAmount() * days);
                            json.put("allowance_amount", 0);
                            json.put("allowance_id", aId);
                            json.put("type_of_allowance", a.getAllowanceType());
                            json.put("allowance_name", allowanceName);
                            json.put("percentage", a.getAmount() * days);
                            allowanceList.add(json);
                        }
                    }

                }
                // all allowance sum

                BigDecimal totalAmount = BigDecimal.ZERO;

                for (LinkedCaseInsensitiveMap mapp : allowanceList) {
                    Object amountObj = mapp.get("allowance_amount");

                    if (amountObj != null) {
                        try {
                            BigDecimal amount = new BigDecimal(amountObj.toString());
                            totalAmount = totalAmount.add(amount);
                        } catch (NumberFormatException e) {
                            // Optionally handle invalid number formats
                            System.err.println("Invalid allowance_amount: " + amountObj);
                        }
                    }
                }

                // check condition if Other Allowance is negative

                double otherAllowanceAmount = gross[0] - totalAmount.doubleValue();

                if (otherAllowanceAmount < 0) {

                    for (LinkedCaseInsensitiveMap mapp : allowanceList) {
                        Object amountObj = mapp.get("allowance_id");

                        if (amountObj != null) {
                            try {
                                // BigDecimal amount = new BigDecimal(amountObj.toString());
                                if (Objects.equals(hraAllowanceId[0], Long.parseLong(amountObj.toString()))) {
                                    // totalAmount = totalAmount.add(amount);
                                    double currentAmount = mapp.get("allowance_amount") != null
                                            ? Double.parseDouble(mapp.get("allowance_amount").toString())
                                            : 0.0;
                                    mapp.put("allowance_amount", (currentAmount + otherAllowanceAmount));
                                }
                            } catch (NumberFormatException e) {
                                // Optionally handle invalid number formats
                                System.err.println("Invalid allowance_amount: " + amountObj);
                            }
                        }
                    }

                }

                BigDecimal totalAmountt = BigDecimal.ZERO;

                for (LinkedCaseInsensitiveMap mapp : allowanceList) {
                    Object amountObj = mapp.get("allowance_amount");

                    if (amountObj != null) {
                        try {
                            BigDecimal amount = new BigDecimal(amountObj.toString());
                            totalAmountt = totalAmountt.add(amount);
                        } catch (NumberFormatException e) {
                            // Optionally handle invalid number formats
                            System.err.println("Invalid allowance_amount: " + amountObj);
                        }
                    }
                }

                // other allowance calculation

                for (LinkedCaseInsensitiveMap mapp : allowanceList) {
                    Object amountObj = mapp.get("allowance_id");

                    if (amountObj != null) {
                        try {
                            // BigDecimal amount = new BigDecimal(amountObj.toString());
                            if (Objects.equals(otherAllowanceId[0], Long.parseLong(amountObj.toString()))) {
                                // totalAmount = totalAmount.add(amount);
                                mapp.put("allowance_amount", (gross[0] - totalAmountt.doubleValue()));
                            }
                        } catch (NumberFormatException e) {
                            // Optionally handle invalid number formats
                            System.err.println("Invalid allowance_amount: " + amountObj);
                        }
                    }
                }
                resultMap.put("finalAllowance", allowanceList);

                // Deduction calculation

                List<DeductionTemplatePayPlan> deductionTemplatePayPlan = PayPlanData.getDeductionTemplatePayPlan();

                for (DeductionTemplatePayPlan d : deductionTemplatePayPlan) {

                    LinkedCaseInsensitiveMap json = new LinkedCaseInsensitiveMap();

                    String deductionName = customDeduction.stream()
                            .filter(ca -> ca.getId().equals(d.getDeductionId()))
                            .map(CustomDeduction::getDeductionName)
                            .findFirst()
                            .orElse(null);

                    if (d.getDeductionType().equalsIgnoreCase("fixed")) {
                        d.setCalculatedAmount(d.getAmount());
                        json.put("deduction_amount", d.getAmount());
                        json.put("deduction_id", d.getDeductionId());
                        json.put("deduction_name", deductionName);
                        deductionList.add(json);
                    } else if (d.getDeductionType().equalsIgnoreCase("variable")) {
                        List<DeductionDependsOnAllowance> deductionDependsOnAllowance = d.getDeductionDependOn();

                        List<Long> dependAllowanceId = deductionDependsOnAllowance.stream()
                                .map(DeductionDependsOnAllowance::getId)
                                .collect(Collectors.toList());

                        double totalAmount1 = allowanceTemplatePayPlan.stream()
                                .filter(plan -> dependAllowanceId.contains(plan.getAllowanceId()))
                                .mapToDouble(AllowanceTemplatePayPlan::getCalculatedAmount)
                                .sum();

                        if (dependAllowanceId.contains(grossSalaryId[0])) {

                            totalAmount1 = totalAmount1 + gross[0];
                        }

                        if (deductionName.equalsIgnoreCase("epf")) {

                            if (totalAmount1 >= 15000) {
                                totalAmount1 = 15000;
                            }
                        }

                        if (deductionName.equalsIgnoreCase("esic")) {

                            if (totalAmount1 > 21000) {
                                totalAmount1 = 0;
                            }
                        }

                        d.setCalculatedAmount(
                                Math.round(Math.round((totalAmount1 / 100) * d.getAmount()) * 100.00) / 100.00);
                        json.put("deduction_amount",
                                Math.round(Math.round((totalAmount1 / 100) * d.getAmount()) * 100.00) / 100.00);
                        json.put("deduction_id", d.getDeductionId());
                        json.put("deduction_name", deductionName);
                        deductionList.add(json);
                    } else if (d.getDeductionType().equalsIgnoreCase("other")) {

                        json.put("deduction_amount", 0);
                        json.put("deduction_id", d.getDeductionId());
                        json.put("deduction_name", deductionName);
                        deductionList.add(json);
                    }

                }

                double deductionSum = deductionTemplatePayPlan.stream()
                        .map(DeductionTemplatePayPlan::getCalculatedAmount)
                        .filter(Objects::nonNull) // remove nulls
                        .mapToDouble(Double::doubleValue)
                        .sum();

                double rate = 0.0;
                if (empType.equalsIgnoreCase("full time")) {
                    rate = (basicSalary[0] / 30) / 8;
                } else {
                    rate = basicSalary[0] / 8;
                }

                resultMap.put("status", "success");
                resultMap.put("finalDeduction", deductionList);
                resultMap.put("totalEarning", gross[0]);
                resultMap.put("totalDeduction", deductionSum);
                resultMap.put("netAmount", gross[0] - deductionSum);
                resultMap.put("payPlanId", payPlanId);
                resultMap.put("siteId", PayPlanData.getSiteId());
                resultMap.put("rate", rate);

            } else {
                resultMap.put("status", "error");
                resultMap.put("value", "No Plan found");

            }

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;

    }

    @Override
    public Map findLogsById(Map map) {

        Map resultMap = new HashMap<>();

        try {

            Long ids = Long.parseLong(map.get("id").toString());
            Map map1 = new HashMap<>();
            List<LinkedCaseInsensitiveMap> payPlanLogs = payPlanLogsRepository.findPayPlanLogsById(ids);
            List<LinkedCaseInsensitiveMap> allowanceLogs = allowanceTemplatePayPlanLogsRepository
                    .findAllowanceLogsById(ids);
            List<LinkedCaseInsensitiveMap> deductionLogs = deductionTemplatePayPlanLogsRepository
                    .findAllowanceLogsById(ids);

            map1.put("planLogs", payPlanLogs);
            map1.put("allowanceLogs", allowanceLogs);
            map1.put("deductionLogs", deductionLogs);
            resultMap.put("value", map1);
            resultMap.put("status", "success");

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "exception");
        }

        return resultMap;

    }
}
