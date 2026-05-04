
package com.realcoderz.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author tauseef
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BalanceSummaryHistory extends Auditable<String>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceSummaryHistoryId;
    private Long balanceSummaryId;
    private Double netBalance;
    private Double openingBalance;
    private Double paymentAmount;
    private Double currentMonthSalary;
    private Long employeeId;
    private Long organizationId;
  
    private String paymentDate;
    private int month;
    private int year;
}
