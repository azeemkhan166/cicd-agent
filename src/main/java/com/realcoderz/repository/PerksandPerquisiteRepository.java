package com.realcoderz.repository;

import com.realcoderz.model.PerksandPerquisite;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Astha
 */
@Repository
public interface PerksandPerquisiteRepository extends JpaRepository<PerksandPerquisite, Long> {

	@Query(nativeQuery = true, value = "select distinct p.employee_id,p.perk_id,p.id,p.perquisite_name,p.perquisite_value,p.amount_recoverd,p.perquisite_amount from perks_perquisite p  where p.employee_id=:employee_id")
	public List<LinkedCaseInsensitiveMap> getPerksandPerquisite(@Param("employee_id") Long employee_id);

	@Query(nativeQuery = true, value = "select employee_id,perk_id ,total_perquisite_amount from employee_perks_perquisite where organization_id=?")
	public List<LinkedCaseInsensitiveMap> getDataById(@Param("organization_id") Long organization_id);

	@Query(nativeQuery = true, value = "select perquisite_id from perquisite")
	public List<LinkedCaseInsensitiveMap> getPerquisiteById(@Param("organization_id") Long organization_id);

	@Query(nativeQuery = true, value = "SELECT sum(perquisite_value) FROM perks_perquisite where employee_id =?1 and organization_id =?2")
	public String getPerquisiteSum(Long employeeId, Long organizationId);
        
        @Query(nativeQuery = true,value="select total_perquisite_amount from employee_perks_perquisite where employee_id=?1 limit 1")
        public LinkedCaseInsensitiveMap getTotalPerquisiteAmount(Long employee_id);
}