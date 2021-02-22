package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.MappingStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.*;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingSuggestionDto;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class})
public class EntityControllerTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    private Project project;

    private SourceDto sourceDto;

    @Override
    public void setup() throws Exception {
        super.setup();
        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});
        ProjectDto projectDto = super.createProject("New Project", "token1", datasources, ontologies, "efo");
        user1 = userService.findByEmail(user1.getEmail());
        project = projectService.retrieveProject(projectDto.getId(), user1);
        sourceDto = super.createSource(project.getId());

        super.createEntityTestData(sourceDto.getId(), project.getId(), user1);
    }

    /**
     * GET /v1/projects/{projectId}/entities
     */
    @Test
    public void shouldGetEntities() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_ENTITIES;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RestResponsePage<EntityDto> entitiesPage = mapper.readValue(response, new TypeReference<RestResponsePage<EntityDto>>() {
        });
        assertEquals(1, entitiesPage.getTotalElements());

        EntityDto actual = entitiesPage.getContent().get(0);
        assertEquals("Achondroplasia", actual.getName());
        assertEquals(EntityStatus.AUTO_MAPPED.name(), actual.getMappingStatus());

        assertEquals(1, actual.getMappings().size());
        assertEquals("Orphanet:15", actual.getMappings().get(0).getOntologyTerm().getCurie());
        assertEquals(MappingStatus.AWAITING_REVIEW.name(), actual.getMappings().get(0).getStatus());

        assertEquals(2, actual.getMappingSuggestions().size());
        int foundCuries = 0;
        for (MappingSuggestionDto mappingSuggestion : actual.getMappingSuggestions()) {
            if (mappingSuggestion.getOntologyTerm().getCurie().equalsIgnoreCase("Orphanet:15")) {
                foundCuries++;
            }
            if (mappingSuggestion.getOntologyTerm().getCurie().equalsIgnoreCase("MONDO:0007037")) {
                foundCuries++;
            }
        }

        assertEquals(2, foundCuries);
        assertEquals(sourceDto.getId(), actual.getSource().getId());
    }

    /**
     * GET /v1/projects/{projectId}/entities/{entityId}
     */
    @Test
    public void shouldGetEntity() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ENTITIES + "/" + entity.getId();
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EntityDto actual = mapper.readValue(response, new TypeReference<EntityDto>() {
        });

        assertEquals("Achondroplasia", actual.getName());
        assertEquals(EntityStatus.AUTO_MAPPED.name(), actual.getMappingStatus());

        assertEquals(1, actual.getMappings().size());
        assertEquals("Orphanet:15", actual.getMappings().get(0).getOntologyTerm().getCurie());
        assertEquals(MappingStatus.AWAITING_REVIEW.name(), actual.getMappings().get(0).getStatus());

        assertEquals(2, actual.getMappingSuggestions().size());
        int foundCuries = 0;
        for (MappingSuggestionDto mappingSuggestion : actual.getMappingSuggestions()) {
            if (mappingSuggestion.getOntologyTerm().getCurie().equalsIgnoreCase("Orphanet:15")) {
                foundCuries++;
            }
            if (mappingSuggestion.getOntologyTerm().getCurie().equalsIgnoreCase("MONDO:0007037")) {
                foundCuries++;
            }
        }

        assertEquals(2, foundCuries);
        assertEquals(sourceDto.getId(), actual.getSource().getId());
    }
}
