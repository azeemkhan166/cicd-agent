
package com.realcoderz.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author tauseef
 */
@Entity
@Table(name = "relief_87a_newregime")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Relief87ANewRegime extends Auditable<String>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private Long relief87ANewRegimeId;
    private double income;
    private double rate;
    private Long year;
}
