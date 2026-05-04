package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.config.GcpConfig;
import com.realcoderz.service.DetailsTaxSlipService;
import com.realcoderz.serviceImpl.SalaryBreakupServiceImpl;
import com.realcoderz.util.EncryptDecryptUtils;
import java.io.ByteArrayOutputStream;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import java.util.Scanner;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 *
 *
 *
 * @author adarsh
 *
 */

@RestController
@RequestMapping("/slip")
public class HtmlTemplateToPdfController {

    @Autowired

    public SalaryBreakupServiceImpl salaryBreakupServiceImpl;
    
     @Autowired
    public GcpConfig gcpConfig;
    
    @Value("${recruitBucketName}")
    private String recruitBucketName;
    
    @Value("${Organization_ids}")
    private Long Organization_ids;
    
    @Autowired
    private DetailsTaxSlipService detailService;

    @PostMapping(
            value = "/generate-report",
            produces = MediaType.APPLICATION_PDF_VALUE
    )

    public @ResponseBody

    byte[] generateReport(@RequestBody String payload,HttpServletRequest request){
     
        String uuid = UUID.randomUUID().toString();

        try {
           

            /**
             *
             * read the template and fill the data.
             *
             */          
            Map salaryData = this.fetchSalarySlipData(payload);
            if(salaryData.get("status").equals("error")){
                byte a[] = new byte[0];
                return a;
             
            }
            List<Map> list = (List<Map>) salaryData.get("list");

            List<Map> ytdAllowanceList = (List<Map>) salaryData.get("ytdAllowanceCalculationPriviousAndCurrentMonth");

            List<Map> ytdDeductionList = (List<Map>) salaryData.get("ytdDeductionCalculationPriviousAndCurrentMonth");
            
//            System.out.println("salaryData "+salaryData);
            Map userDetails = (Map) salaryData.get("userdetails");
//            System.out.println(userDetails);
            Map attendanceDetails = (Map) salaryData.get("attendanceDetails");
//            System.out.println(attendanceDetails);
            Map companyDetails = (Map) salaryData.get("companyDetails");
//            System.out.println(companyDetails);
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(payload), LinkedCaseInsensitiveMap.class);
            
            /**
             *  company Details
             *
             */
            
              String monthAndYear = Optional.ofNullable(companyDetails.getOrDefault("monthAndYear", "N/A"))
                    .orElse("N/A")
                    .toString();
            
            Map companyAddressTemplate = (Map) companyDetails.get("companyAddress");
            String template = Optional.ofNullable(companyAddressTemplate.getOrDefault("template", "N/A"))
                    .orElse("N/A")
                    .toString();
            
            String companyName = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_name", ""))
                    .orElse("")
                    .toString();
            String addressOne = Optional.ofNullable(companyAddressTemplate.getOrDefault("organization_address", ""))
                    .orElse("")
                    .toString();
            String addressTwo = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_address_line_two", ""))
                    .orElse("")
                    .toString();
            String pinCode = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_pincode", ""))
                    .orElse("")
                    .toString();
            String city = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_city", ""))
                    .orElse("")
                    .toString();
            String state = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_state", ""))
                    .orElse("")
                    .toString();
            
            String addressThree=pinCode+", "+city+", "+state;
            
              /**
               getting company logo from Recruit module
               **/            
            
              String companyLogo="";
              Map url=salaryBreakupServiceImpl.fetchingSingedUrlFromRecruit(request, Long.parseLong(map.get("organization_id").toString()));
              if(!url.isEmpty()){
                
                  if(url.get("status") !=null && url.get("status").toString().equalsIgnoreCase("success")){
                      companyLogo=url.get("orgImage") !=null && !url.get("orgImage").equals("") ?url.get("orgImage").toString():"";
                  }
                  
              }
                         
            if (template.equalsIgnoreCase("genconnect")) {
                template = "t3.html";
            } else {
                if(Objects.equals(Organization_ids, Long.parseLong(salaryData.get("org_id").toString()))){
                    template = "t4.html";
                }
                else{
                    template = "t2.html";
                }
                
            }

            String htmlContent = new Scanner(getClass().getClassLoader().getResourceAsStream(template), "UTF-8")
                    .useDelimiter("\\A")
                    .next();

            /**
             * User Details
                    *
             */
            String name = Optional.ofNullable(userDetails.getOrDefault("name", "-"))
                    .orElse("-")
                    .toString();
            String employee_code = Optional.ofNullable(userDetails.getOrDefault("employee_code", "-"))
                    .orElse("-")
                    .toString();

            String department_name = Optional.ofNullable(userDetails.getOrDefault("department_name", "-"))
                    .orElse("-")
                    .toString();
            String desingnation = Optional.ofNullable(userDetails.getOrDefault("emp_desingnation", "-"))
                    .orElse("-")
                    .toString();
            String joining_date = Optional.ofNullable(userDetails.getOrDefault("joining_date", "-"))
                    .orElse("-")
                    .toString();
            String esic = Optional.ofNullable(userDetails.getOrDefault("esic", "-"))
                    .orElse("-")
                    .toString();
            String uan = Optional.ofNullable(userDetails.getOrDefault("uan", "-"))
                    .orElse("-")
                    .toString();
            String pan_number = Optional.ofNullable(userDetails.getOrDefault("pan_number", "-"))
                    .orElse("-")
                    .toString();
            String aadhar_number = Optional.ofNullable(userDetails.getOrDefault("aadhar_number", "-"))
                    .orElse("-")
                    .toString();
            String bankname = Optional.ofNullable(userDetails.getOrDefault("bankname", "-"))
                    .orElse("-")
                    .toString();
            String bankaccount = Optional.ofNullable(userDetails.getOrDefault("bankaccount", "-"))
                    .orElse("-")
                    .toString();
            String pf = Optional.ofNullable(userDetails.getOrDefault("pf", "-"))
                    .orElse("-")
                    .toString();
             String location = Optional.ofNullable(userDetails.getOrDefault("location", "-"))
                    .orElse("-")
                    .toString();
              String grade = Optional.ofNullable(userDetails.getOrDefault("grade", "-"))
                    .orElse("-")
                    .toString();

            /**
             * Attendance Details
                    *
             */
            String actualDay = Optional.ofNullable(attendanceDetails.getOrDefault("actualDay", "N/A"))
                    .orElse("N/A")
                    .toString();
            String workingDay = Optional.ofNullable(attendanceDetails.getOrDefault("workingDay", "N/A"))
                    .orElse("N/A")
                    .toString();
            String Lwp = Optional.ofNullable(attendanceDetails.getOrDefault("Lwp", "N/A"))
                    .orElse("N/A")
                    .toString();
            String approvedLeave = Optional.ofNullable(attendanceDetails.getOrDefault("approvedLeave", "N/A"))
                    .orElse("N/A")
                    .toString();
            String presentDay = Optional.ofNullable(attendanceDetails.getOrDefault("presentDay", "N/A"))
                    .orElse("N/A")
                    .toString();
            
            String holiday = Optional.ofNullable(attendanceDetails.getOrDefault("holidays", "N/A"))
                    .orElse("N/A")
                    .toString();
            
            String weekOff = Optional.ofNullable(attendanceDetails.getOrDefault("weekOff", "N/A"))
                    .orElse("N/A")
                    .toString();
            
             String arrearAddedd = Optional.ofNullable(attendanceDetails.getOrDefault("arrearAddedd", 0))
                    .orElse(0)
                    .toString();
             
              String arrearDeduction = Optional.ofNullable(attendanceDetails.getOrDefault("arrearDeduction", 0))
                    .orElse(0)
                    .toString();

          

            StringBuilder tableRows1 = new StringBuilder();

            StringBuilder tableRows2 = new StringBuilder();

            StringBuilder tableRows3 = new StringBuilder();

            int secondLast = list.size() - 2;

            int last = list.size() - 1;

            int index = 0;

            for (Map data : list) {

                if ((index == secondLast) || (index == last)) {

                    break;

                }

                if (template.equals("t2.html")) {

                    tableRows1.append("<tr>\n");

                    /**
                     *
                     * left section. *
                     *
                     */
                    tableRows1.append("    <td class=\"earning-deduction\">");

                    tableRows1.append(" <table style=\"width:100%\">\n");

                    tableRows1.append(" <tbody>\n");

                    tableRows1.append("<tr>\n");

                    tableRows1.append("    <td class=\"title\">").append(data.getOrDefault("allowance_name", "")).append("</td>\n");

                    tableRows1.append("    <td class=\"description\" align=\"right\">").append(data.getOrDefault("allowance_amount", "")).append("</td>\n");

                    tableRows1.append("</tr>\n");

                    tableRows1.append("</tbody>\n");

                    tableRows1.append("</table>\n");

                    tableRows1.append("</td>\n");

                    /**
                     *
                     * middle section. *
                     *
                     */
                    tableRows1.append("    <td class=\"earning-deduction\">");

                    tableRows1.append(" <table style=\"width:100%\">\n");

                    tableRows1.append(" <tbody>\n");

                    tableRows1.append("<tr>\n");

                    tableRows1.append("    <td class=\"title\">").append(data.getOrDefault("allowance_name", "")).append("</td>\n");

                    tableRows1.append("    <td class=\"description\" align=\"right\">").append(data.getOrDefault("allowance_payable_amount", "")).append("</td>\n");

                    tableRows1.append("</tr>\n");

                    tableRows1.append("</tbody>\n");

                    tableRows1.append("</table>\n");

                    tableRows1.append("</td>\n");

                    /**
                     *
                     * right section. *
                     *
                     */
                    tableRows1.append("    <td class=\"earning-deduction\">");

                    tableRows1.append(" <table style=\"width:100%\">\n");

                    tableRows1.append(" <tbody>\n");

                    tableRows1.append("<tr>\n");

                    tableRows1.append("    <td class=\"title\">").append(data.getOrDefault("deduction_name", "")).append("</td>\n");

                    tableRows1.append("    <td class=\"description\" align=\"right\">").append(data.getOrDefault("deduction_payable_amount", "")).append("</td>\n");

                    tableRows1.append("</tr>\n");

                    tableRows1.append("</tbody>\n");

                    tableRows1.append("</table>\n");

                    tableRows1.append("</td>\n");

                    tableRows1.append("</tr>\n");

                } 
                else if (template.equals("t4.html")) {

                    tableRows1.append("<tr>\n");

                    /**
                     *
                     * left section. *
                     *
                     */
                    tableRows1.append("    <td class=\"earning-deduction\">");

                    tableRows1.append(" <table style=\"width:100%\">\n");

                    tableRows1.append(" <tbody>\n");

                    tableRows1.append("<tr>\n");

                    tableRows1.append("    <td class=\"title\">").append(data.getOrDefault("allowance_name", "")).append("</td>\n");

                    tableRows1.append("    <td class=\"description\" align=\"right\">").append(data.getOrDefault("allowance_amount", "")).append("</td>\n");

                    tableRows1.append("</tr>\n");

                    tableRows1.append("</tbody>\n");

                    tableRows1.append("</table>\n");

                    tableRows1.append("</td>\n");

                    /**
                     *
                     * middle section. *
                     *
                     */
                    tableRows1.append("    <td class=\"earning-deduction\">");

                    tableRows1.append(" <table style=\"width:100%\">\n");

                    tableRows1.append(" <tbody>\n");

                    tableRows1.append("<tr>\n");

                    tableRows1.append("    <td class=\"title\">").append(data.getOrDefault("allowance_name", "")).append("</td>\n");

                    tableRows1.append("    <td class=\"description\" align=\"right\">").append(data.getOrDefault("allowance_payable_amount", "")).append("</td>\n");

                    tableRows1.append("</tr>\n");

                    tableRows1.append("</tbody>\n");

                    tableRows1.append("</table>\n");

                    tableRows1.append("</td>\n");

                    /**
                     *
                     * right section. *
                     *
                     */
                    tableRows1.append("    <td class=\"earning-deduction\">");

                    tableRows1.append(" <table style=\"width:100%\">\n");

                    tableRows1.append(" <tbody>\n");

                    tableRows1.append("<tr>\n");

                    tableRows1.append("    <td class=\"title\">").append(data.getOrDefault("deduction_name", "")).append("</td>\n");

                    tableRows1.append("    <td class=\"description\" align=\"right\">").append(data.getOrDefault("deduction_payable_amount", "")).append("</td>\n");

                    tableRows1.append("</tr>\n");

                    tableRows1.append("</tbody>\n");

                    tableRows1.append("</table>\n");

                    tableRows1.append("</td>\n");

                    tableRows1.append("</tr>\n");

                }
                
                else if (template.equals("t3.html")) {

                    /**
                     *
                     * Earnings. *
                     *
                     */
                    tableRows2.append("<tr>\n");

                    if (index < ytdAllowanceList.size() - 1) {

                        tableRows2.append("    <td class=\"my-td description\">").append(index + 1).append("</td>\n");

                        tableRows2.append("    <td class=\"my-td title\">").append(data.getOrDefault("allowance_name", "")).append("</td>\n");

                        tableRows2.append("    <td class=\"my-td title\" align=\"right\">").append(data.getOrDefault("allowance_payable_amount", "")).append("</td>\n");

                        tableRows2.append("    <td class=\"my-td title\" align=\"right\">").append(ytdAllowanceList.get(index).getOrDefault("allowance_payable_amount", 0)).append("</td>\n");

                    } else if (index == ytdAllowanceList.size() - 1) {

                        tableRows2.append("    <td class=\"my-td description\">").append("</td>\n");

                        tableRows2.append("    <td class=\"my-td title\">").append("<b>").append(data.getOrDefault("allowance_name", "")).append("</b>").append("</td>\n");

                        tableRows2.append("    <td class=\"my-td title\" align=\"right\">").append("<b>").append(data.getOrDefault("allowance_payable_amount", "")).append("</b>").append("</td>\n");

                        tableRows2.append("    <td class=\"my-td title\" align=\"right\">").append("<b>").append(ytdAllowanceList.get(index).getOrDefault("sumOfYTDAllowance", 0)).append("</b>").append("</td>\n");

                    }

//                    
                    tableRows2.append("</tr>\n");

                    /**
                     *
                     * Deductions. *
                     *
                     */
                    tableRows3.append("<tr>\n");

                    if (index < ytdDeductionList.size() - 1) {

                        tableRows3.append("    <td class=\"my-td description\">").append(index + 1).append("</td>\n");

                        tableRows3.append("    <td class=\"my-td title\">").append(data.getOrDefault("deduction_name", "")).append("</td>\n");

                        tableRows3.append("    <td class=\"my-td title\" align=\"right\">").append(data.getOrDefault("deduction_payable_amount", "")).append("</td>\n");

                        tableRows3.append("    <td class=\"my-td title\" align=\"right\">").append(ytdDeductionList.get(index).getOrDefault("deduction_payable_amount", 0)).append("</td>\n");

                    } else if (index == ytdDeductionList.size() - 1) {

                        tableRows3.append("    <td class=\"my-td description\">").append("</td>\n");

                        tableRows3.append("    <td class=\"my-td title\">").append("<b>").append(list.get(secondLast - 1).getOrDefault("deduction_name", "")).append("</b>").append("</td>\n");

                        tableRows3.append("    <td class=\"my-td title\" align=\"right\">").append("<b>").append(list.get(secondLast - 1).getOrDefault("deduction_payable_amount", "")).append("</b>").append("</td>\n");

                        tableRows3.append("    <td class=\"my-td title\" align=\"right\">").append("<b>").append(ytdDeductionList.get(index).getOrDefault("sumOfYTDDeduction", 0)).append("</b>").append("</td>\n");

                    }

                    tableRows3.append("</tr>\n");

                }

                index++;

            }

            /**
             *
             * Perform replacements in the HTML content.
             *
             */
            htmlContent = htmlContent.replace("$netAmountName", list.get(secondLast).getOrDefault("deduction_name", "").toString())
                    .replace("$netAmount", list.get(secondLast).getOrDefault("deduction_payable_amount", "").toString())
                    .replace("$amountInWord", list.get(last).getOrDefault("allowance_name", "").toString());

            /**
             * Replacing User Details.
             *
             *
             */
            htmlContent = htmlContent.replace("$name", name)
                    .replace("$employee_code", employee_code)
                    .replace("$department_name", department_name)
                    .replace("$desingnation", desingnation)
                    .replace("$joining_date", joining_date)
                    .replace("$esic", esic)
                    .replace("$uan", uan)
                    .replace("$pan_number", pan_number)
                    .replace("$aadhar_number", aadhar_number)
                    .replace("$bankname", bankname)
                    .replace("$bankaccount", bankaccount)
                    .replace("$pf", pf)
                    .replace("$location", location)
                    .replace("$grade", grade);

            /**
             * Replacing Attendance Details.
             *
             *
             */
            htmlContent = htmlContent.replace("$actualDay", actualDay)
                    .replace("$workingDay", workingDay)
                    .replace("$Lwp", Lwp)
                    .replace("$approvedLeave", approvedLeave)
                    .replace("$presentDay", presentDay)
                    .replace("$holiday", holiday)
                    .replace("$weekOff", weekOff)
                    .replace("$arrearAddedd", arrearAddedd)
                    .replace("$arrearDeduction", arrearDeduction)
                    ;

            /**
             * Replacing Company Details.
             *
             *
             */
            
            htmlContent = htmlContent.replace("$companyLogo", companyLogo);
            
            htmlContent = htmlContent.replace("$name", name)
                          .replace("$companyName", companyName)
                          .replace("$addressOne", addressOne)
                          .replace("$addressTwo", addressTwo)
                          .replace("$addressThree", addressThree)
                          .replace("$monthAndYear", monthAndYear);

            if (template.equals("t2.html")) {

                htmlContent = htmlContent.replace("$rows", tableRows1.toString());

            } 
            else if (template.equals("t4.html")) {

                htmlContent = htmlContent.replace("$rows", tableRows1.toString());

            }
            else if (template.equals("t3.html")) {

                htmlContent = htmlContent.replace("$earningRows", tableRows2.toString());

                htmlContent = htmlContent.replace("$deductionsRows", tableRows3.toString());

            }
/**
 
 flying sourcer library not Support & 
 **/ 

htmlContent = htmlContent.replace("&", "&amp;");


//htmlContent = htmlContent.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") .replace("\"", "&quot;").replace("'", "&apos;");


             try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
//            System.out.println(outputStream.toByteArray()+" output");
            
//                   String base64Encoded = Base64.getEncoder().encodeToString(outputStream.toByteArray());
//        System.out.println("Base64 Encoded: " + base64Encoded);
            return outputStream.toByteArray();
        }


        } catch (Exception e) {

//            System.out.println("catch  block ");

            byte a[] = new byte[0];
            e.printStackTrace();
            return a;
        } finally {

        }

    }

    public Map fetchSalarySlipData(String data) {

        Map map=salaryBreakupServiceImpl.getPdfData(data);
//        System.out.println(" "+map);

        return map;

    }
    
     
    @PostMapping( value = "/generateTaxReport",produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] generateTaxReport(HttpServletRequest request,@RequestBody String body) {
      
      try{
          
         
            
//                   String base64Encoded = Base64.getEncoder().encodeToString(outputStream.toByteArray());
//        System.out.println("Base64 Encoded: " + base64Encoded);
          return detailService.getDetailSalarySlip(request,body);
        
          
      }catch(Exception e){
           e.printStackTrace();
            byte a[] = new byte[0];
           
           return a;
           
            
      }
   
     
        


    }
    
    
    @PostMapping( value = "/generateGateSalaryReport",produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] generateGateSalaryReport(HttpServletRequest request,@RequestBody String body) {
      
      try{
          return detailService.generateGateSalaryReport(request,body);
      }catch(Exception e){
           e.printStackTrace();
            byte a[] = new byte[0];
            return a;
      }
    }
    
    @PostMapping( value = "/generateMonthlySlip",produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] generateMonthlySlip(HttpServletRequest request,@RequestBody String body) {
      
      try{
          return detailService.generateMonthlySlip(request,body);
      }catch(Exception e){
           e.printStackTrace();
            byte a[] = new byte[0];
            return a;
      }
    }
    
    @PostMapping( value = "/generateGateSalarySaralReport",produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] generateGateSalarySaralReport(HttpServletRequest request,@RequestBody String body) {
      
      try{
          return detailService.generateGateSalarySaralReport(request,body);
      }catch(Exception e){
           e.printStackTrace();
            byte a[] = new byte[0];
            return a;
      }
    }
}
