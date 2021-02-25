package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.ac.ebi.spot.ontotools.curation.domain.config.ProjectMappingConfig;

import java.util.List;

@Document(collection = "projects")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Project {

    @Id
    private String id;

    private String name;

    private String description;

    /**
     * List of datasources used to query Zooma to retrieve suggestions.
     * Zooma service uses this list to filter results pertaining only to these datasources.
     * <p>
     * `ProjectMappingConfig` enables a field-based mapping preference setting.
     * The default value for `field` when fields are not used is ALL.
     */
    private List<ProjectMappingConfig> datasources;

    /**
     * List of datasources used to query Zooma to retrieve suggestions.
     * Zooma service uses this list to filter results pertaining only to these ontologies.
     * <p>
     * `ProjectMappingConfig` enables a field-based mapping preference setting.
     * The default value for `field` when fields are not used is ALL.
     * <p>
     * NB: Orphanet prefix used by Zooma is `ordo`
     */
    private List<ProjectMappingConfig> ontologies;

    /**
     * Ontology ID used when creating ontology terms locally to query OLS for a mapping
     */
    private List<String> preferredMappingOntologies;

    private Integer numberOfReviewsRequired;

    private Provenance created;
}
