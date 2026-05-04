package com.realcoderz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.realcoderz.enums.AuthProvider;
import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.LinkedCaseInsensitiveMap;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User  {

    @Id
    private Long id;

    private String name;

    private String lastName;

    private String email;

    @Column(columnDefinition = "text")
    private String imageUrl;

    private Boolean emailVerified = false;

    @JsonIgnore
    private String password;

    private Long mobile;

    private String mobileCode;

    @Transient
    private Long student_id;

    @Transient
    private Long user_id;

    @Transient
    private String sentCode;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    @Transient
    private Set<LinkedCaseInsensitiveMap> roles;

    @Transient
    private Set<LinkedCaseInsensitiveMap> rights;

    @Transient
    private Set<LinkedCaseInsensitiveMap> batches;

    @Transient
    private String profilePath;

    @Transient
    private String mobileProfilePath;

    @Transient
    private Long default_role;

    @Transient
    private Long jobPortalId;

    @Transient
    private String default_mobile_role;
    
    private Long organizationId;

    @Transient
    private Character userActive;

    @Transient
    private Character orgActive;

    @Transient
    private String organizationName;

    @Transient
    private String countryCode;

    public User(Long id, String name, String lastName, String email, String imageUrl, String password, Long student_id, String sentCode, AuthProvider provider, String providerId, Set<LinkedCaseInsensitiveMap> roles, Set<LinkedCaseInsensitiveMap> rights, Set<LinkedCaseInsensitiveMap> batches) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.password = password;
        this.student_id = student_id;
        this.sentCode = sentCode;
        this.provider = provider;
        this.providerId = providerId;
        this.roles = roles;
        this.rights = rights;
        this.batches = batches;
    }

}
