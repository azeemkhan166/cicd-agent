package com.realcoderz.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Astha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class SalaryHistoryRecord extends Auditable<String> {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", length = 5)
    private Long record_id;

    @Column(name = "organization_id", length = 5)
    private Long organization_id;
   
    @Column(name = "employee_id", length = 5)
    private int employee_id;
    
    @Column(name = "gross_salary", length = 20)
    private double gross_salary;
    
    @Column(name = "appraisal_salary", length = 20)
    private double appraisal_salary;
    
    @Column(name = "effective_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date effective_date;
    
    @Column(name = "month", length = 2)
    private Integer month;

    @Column(name = "year", length = 4)
    private Integer year;
    
}