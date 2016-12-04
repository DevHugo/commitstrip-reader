package com.commitstrip.commitstripreader.integrationtest;

import com.commitstrip.commitstripreader.backend.Application;
import com.commitstrip.commitstripreader.backend.config.SampleConfig;
import com.commitstrip.commitstripreader.backend.repository.DatabaseRepository;
import com.commitstrip.commitstripreader.integrationtest.util.SampleData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class}, properties = "application.properties")
@WebAppConfiguration
@ActiveProfiles("test")
public class FindAllStripTest {
    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");

    @Autowired
    private WebApplicationContext context;

    private RestDocumentationResultHandler document;
    private MockMvc mockMvc;

    @Autowired
    private DatabaseRepository repository;

    @MockBean
    private SampleConfig sampleConfig;

    @Before
    public void setup() throws Exception {
        this.document = document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(this.document)
                .build();

        this.document.document(
                responseFields(
                        fieldWithPath("content[].title").description("The strips' title"),
                        fieldWithPath("content[].date").description("The timestamp of the published date of the strips'."),
                        fieldWithPath("content[].thumbnail").description("The strips' url where the thumbnail is located"),
                        fieldWithPath("content[].content").description("The strips' url where the image is located"),
                        fieldWithPath("content[].url").description("The page strips' url on CommitStrip website"),
                        fieldWithPath("content[].previous").description("Id of the previous strip"),
                        fieldWithPath("content[].next").description("Id of the next strip")
                )
        );

        repository.save(SampleData.addSampleStrips(3));
    }

    @Test
    public void findAllStrips() throws Exception {

        mockMvc.perform(get("/strip/"))
                .andExpect(status().isOk())
                .andDo(document)
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    public void findAllStripsSorted() throws Exception {

        mockMvc.perform(get("/strip/?sort=title&date.dir=desc"))
                .andExpect(status().isOk())
                .andDo(document)
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].title", is("La guerre invisible")))
                .andExpect(jsonPath("$.content[1].title", is("Mon royaume pour un commit")))
                .andExpect(jsonPath("$.content[2].title", is("Pendant ce temps, sur Mars – #10")));
    }

    @Test
    public void findOneStrip() throws Exception {

       mockMvc.perform(get("/strip/1"))
                .andDo(document)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Mon royaume pour un commit")));
    }

    @Test
    public void findMostRecentStrip() throws Exception {

        mockMvc.perform(get("/strip/recent"))
                .andDo(document)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Mon royaume pour un commit")));
    }

}
