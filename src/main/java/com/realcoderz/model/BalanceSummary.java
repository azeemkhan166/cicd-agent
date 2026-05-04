
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
public class BalanceSummary extends Auditable<String>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceSummaryId;
    private int year;
    private int month;
    private Double openingBalance;
    private Double currentMonthSalary;
    private Double payment;
    private Double netBalance;
    private Long employeeId;
    private Long organizationId;

}
