package com.realcoderz.service;

import com.realcoderz.model.ProfessionalTaxSlab;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 *
 * @author Pooja Gupta
 */

@Service
public interface ProfessionalTaxService {

    public Map getProfessionalTaxSlabs();

    public Map saveProfessionTaxSlabs(Map map);

    public ProfessionalTaxSlab fetchProfessionalTaxSlabById(Long slabId);

    public void removeProfessionalTaxSlabById(Long slabId);
}
