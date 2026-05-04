package com.realcoderz.model;

import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Astha
 */
@Entity
@Table(name = "employee_perks_perquisite")
@Getter
@Setter
@ToString
@NoArgsConstructor
 @EqualsAndHashCode
public class PerksandPerquisite extends Auditable<String> {
    
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perk_id", length = 7)
    private Long perk_id;
     
    @OneToMany(mappedBy="perksandPerquisite")
    private List<PerquisiteData> perquisiteData;
 
//    @Column(name = "perquisite_name", length = 100)
//    private String perquisite_name;
    
//    @Column(name = "perquisite_value", length = 100)
//    private Double perquisite_value;
    
    @Column(name = "total_perquisite_value", length = 100)
    private Double total_perquisite_value;
    
//    @Column(name = "amount_recoverd", length = 100)
//    private Double amount_recoverd;
    
    @Column(name = "total_amount_recoverd", length = 100)
    private Double total_amount_recoverd;
    
//    @Column(name = "perquisite_amount", length = 100)
//    private Double perquisite_amount;
    
    @Column(name = "total_perquisite_amount", length = 100)
    private Double total_perquisite_amount;

    @Column(name = "organization_id", length = 5)
    private Long organization_id;
    
    @Column(name = "employee_id", length = 5)
    private Long employee_id;

    public PerksandPerquisite(Long perk_id, List<PerquisiteData> perquisiteData, String perquisite_name, Double perquisite_value, Double total_perquisite_value, Double amount_recoverd, Double total_amount_recoverd, Double perquisite_amount, Double total_perquisite_amount, Long organization_id, Long employee_id) {
        this.perk_id = perk_id;
        this.perquisiteData = perquisiteData;
//        this.perquisite_name = perquisite_name;
//        this.perquisite_value = perquisite_value;
        this.total_perquisite_value = total_perquisite_value;
//        this.amount_recoverd = amount_recoverd;
        this.total_amount_recoverd = total_amount_recoverd;
//        this.perquisite_amount = perquisite_amount;
        this.total_perquisite_amount = total_perquisite_amount;
        this.organization_id = organization_id;
        this.employee_id = employee_id;
    }
    
    }