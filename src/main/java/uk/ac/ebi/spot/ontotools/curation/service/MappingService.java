package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.Mapping;

import java.util.List;
import java.util.Map;

public interface MappingService {

    void runAutoMapping(String sourceId);
}
