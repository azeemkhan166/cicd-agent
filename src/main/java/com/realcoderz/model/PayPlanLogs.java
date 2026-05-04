/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author Admin
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PayPlanLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long organizationId;
    private String employeeType;
    private Long siteId;
    private String payMode;
    private String planName;
    private String description;
    private String skilledLevelType;
    private String overtime;
    private Double times;
    private Double rate;
    private String arrear;
    private String reimb;
    private String incentive;
    private Long days;
    private String ptNumber;
    private String stateName;
    private Long payPlanId;
    private String modifyBy;
    private String weekoffflag;
    private String secondsalaryflag;
    private String bonus;
    private Double percentage;
    private Double maximumvalue;
    private String ptamount;
    private Double basicRate;
    // @Column(name = "login_time", columnDefinition = "TIMESTAMP DEFAULT
    // CURRENT_TIMESTAMP")
    // @Temporal(TemporalType.TIMESTAMP)
    // private Date loginTime;
    @Column(name = "login_time", nullable = false)
    private Instant loginTime;

}
