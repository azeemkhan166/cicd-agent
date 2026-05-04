package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.ProfessionalTaxSlab;
import com.realcoderz.repository.ProfessionalTaxSlabRepo;
import com.realcoderz.service.ProfessionalTaxService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Pooja Gupta
 */
@Service
public class ProfessionalTaxServiceImpl implements ProfessionalTaxService {

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(ProfessionalTaxServiceImpl.class);

    @Autowired
    private ProfessionalTaxSlabRepo taxSlabRepo;

    @Override
    public Map getProfessionalTaxSlabs() {
        Map resultMap = new HashMap<>();
        try {
            List<ProfessionalTaxSlab> taxSlab = taxSlabRepo.findAllByOrderByLastModifiedDateDesc();
            if (!taxSlab.isEmpty()) {
                resultMap.put("status", "success");
                resultMap.put("list", taxSlab);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "No professional tax slab available");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in ProfessionTaxServiceImpl -> getProfessionalTaxSlabs() :: ", ex);

        }

        return resultMap;
    }

    @Override
    public ProfessionalTaxSlab fetchProfessionalTaxSlabById(Long slabId) {
        ProfessionalTaxSlab taxSlab = taxSlabRepo.findBySlabId(slabId);
        return taxSlab;
    }

    @Override
    public void removeProfessionalTaxSlabById(Long slabId) {
        taxSlabRepo.deleteById(slabId);
    }

    @Override
    public Map saveProfessionTaxSlabs(Map map) {
        Map resultMap = new HashMap<>();
        try {
            if (map.containsKey("list")) {
                LinkedHashMap slabs = (LinkedHashMap) map.get("list");
                ProfessionalTaxSlab taxSlab = mapper.convertValue(slabs, ProfessionalTaxSlab.class);
                if (taxSlab.getSlab_id() != null) {
                    ProfessionalTaxSlab taxSlabFound = this.fetchProfessionalTaxSlabById(taxSlab.getSlab_id());
                    taxSlabFound.setMaxSalary(taxSlab.getMaxSalary());
                    taxSlabFound.setMinSalary(taxSlab.getMinSalary());
                    taxSlabFound.setTaxAmount(taxSlab.getTaxAmount());
                    taxSlabRepo.save(taxSlabFound);
                } else {
                    taxSlabRepo.save(taxSlab);
                }

                resultMap.put("status", "success");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in ProfessionTaxServiceImpl -> saveProfessionTaxSlabs() :: ", ex);

        }
        return resultMap;
    }

}
