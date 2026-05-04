/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author tause
 */
@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class OrganizationIdDates extends Auditable<String>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long organizationDatesId;
    private Integer financialYear;
    private Long organizationId;
    private String startdate;
    private String endDate;
}
