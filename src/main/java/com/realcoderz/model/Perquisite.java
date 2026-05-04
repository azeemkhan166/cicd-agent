package com.realcoderz.model;

import javax.persistence.Column;
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
 * @author Astha
 */
@Entity
@Table(name = "perquisite")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Perquisite extends Auditable<String> {
   
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perquisite_id", length = 7)
    private Long perquisite_id;

    @Column(name = "perquisite_name", length = 100)
    private String perquisite_name;

    @Column(name = "organization_id", length = 5)
    private Long organization_id;

}