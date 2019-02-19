package com.example.demo;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class NoteServerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldCreateNote() throws Exception {
		mockMvc.perform(
                post("/notes")
                        .contentType(APPLICATION_JSON)
                        .content("{" +
                                "\"topic\": \"Note Topic\"," +
                                "\"subject\": \"Note Subject\"," +
                                "\"body\": \"First Note Body\"" +
                                "}"
                        ))
                .andDo(print())
                .andExpect(status().isCreated());
	}

}
