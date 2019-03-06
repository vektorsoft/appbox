/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.apps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vektorsoft.appbox.server.content.ContentStorage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

/**
 * @author Vladimir Djurovic
 */
@RunWith(SpringRunner.class)
@WebMvcTest(AppController.class)
public class AppControllerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ApplicationService appService;
	@MockBean
	private ContentStorage contentStorage;

	private Application app;

	@Before
	public void setup() {
		app = new Application();
		app.setName("App name");
		app.setHeadline("app headline");
		app.setDescription("Description");

		var out = new Application();
		out.setId("appid");
		out.setName(app.getName());
		out.setHeadline(app.getHeadline());
		out.setDescription(app.getDescription());
		when(appService.createApplication(any())).thenReturn(out);
	}

	@Test
	public void createAppTest() throws Exception {
		String appJson = objectMapper.writeValueAsString(app.convertToDto());

		var result = mockMvc.perform(post("/applications")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(appJson))
				.andExpect(status().isCreated())
				.andReturn();

		var content = result.getResponse().getContentAsString();
		var dto = objectMapper.readValue(content, ApplicationDTO.class);

		assertNotNull(dto);
		assertNotNull(dto.getId());
		assertEquals(app.getName(), dto.getName());
		assertEquals(app.getHeadline(), dto.getHeadline());
		assertEquals(app.getDescription(), dto.getDescription());
	}

	@Test
	public void listApplictionsTest() throws Exception {
		var app1 = new Application();
		app1.setId("app1");
		app1.setName("App 1");

		var app2 = new Application();
		app2.setId("app2");
		app2.setName("App 2");
		Page<Application> page = new PageImpl<Application>(List.of(app1, app2));
		when(appService.getApplications(any(Pageable.class))).thenReturn(page);

		var result = mockMvc.perform(get("/applications"))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
		List<ApplicationDTO> appList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ApplicationDTO>>() {});
		assertEquals(2, appList.size());
	}
}
