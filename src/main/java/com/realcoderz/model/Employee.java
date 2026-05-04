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
import lombok.ToString;

/**
 *
 * @author Astha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Employee extends Auditable<String>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 5)
    private Long id;
    
    @Column(name = "employee_id", length = 5)
    private Long employee_id;
     
    @Column(name = "organization_id", length = 5)
    private Long organization_id;
    
    @Column(name = "gross_salary", length = 20)
    private double gross_salary;
    
     @Column(name = "effective_date")
    private String effective_date;
    
}