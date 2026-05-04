package com.realcoderz.model;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "perks_perquisite")
@Getter
@Setter
@ToString
@NoArgsConstructor
 @EqualsAndHashCode
public class PerquisiteData extends Auditable<String>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 7)
    private Long id;
    
    @ManyToOne()
    @JoinColumn(name = "perk_id",nullable = false)
//    @Column(name = "perk_id", length = 7)
//    private Long perk_id;
    private PerksandPerquisite perksandPerquisite;
    
    @Column(name = "perquisite_name", length = 100)
    private String perquisite_name;
     
     @Column(name = "perquisite_value", length = 100)
    private Double perquisite_value;
      
    @Column(name = "amount_recoverd", length = 100)
    private Double amount_recoverd;
      
    @Column(name = "perquisite_amount", length = 100)
    private Double perquisite_amount;

     @Column(name = "organization_id", length = 5)
    private Long organization_id;
     
     @Column(name = "employee_id", length = 5)
    private Long employee_id;

    public PerquisiteData(Long id, Set<PerksandPerquisite>perksandPerquisite, String perquisite_name, Double perquisite_value, Double amount_recoverd, Double perquisite_amount, Long organization_id,Long employee_id) {
        this.id = id;
        this.perksandPerquisite = (PerksandPerquisite) perksandPerquisite;
        this.perquisite_name = perquisite_name;
        this.perquisite_value = perquisite_value;
        this.amount_recoverd = amount_recoverd;
        this.perquisite_amount = perquisite_amount;
        this.organization_id = organization_id;
         this.employee_id = employee_id;
    }
     
     
}