package com.example.webcache.controller;

import com.example.webcache.WebCacheApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebCacheApplication.class)
@AutoConfigureMockMvc
public class WebCacheControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testNoSuchKey() throws Exception {
		mockMvc.perform(get("/cache/entries?key=test"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testStats() throws Exception {
		mockMvc.perform(get("/cache/stats"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

	@Test
	public void testScalarValue() throws Exception {
		// WHEN
		ObjectMapper objectMapper = new ObjectMapper();

		HashMap<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("key", "TEST");
		jsonMap.put("value", "VALUE");

		String json = objectMapper.writeValueAsString(jsonMap);

		mockMvc.perform(put("/cache/entries")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json))
				.andExpect(status().isOk());

		// THEN
		mockMvc.perform(get("/cache/entries?key=TEST"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

	@Test
	public void testKeyValuedValue() throws Exception {
		// WHEN
		ObjectMapper objectMapper = new ObjectMapper();

		HashMap<String, String> value = new HashMap<>();
		value.put("test1", "value1");
		value.put("test2", "value2");

		HashMap<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("key", "TEST");
		jsonMap.put("value", value);

		String json = objectMapper.writeValueAsString(jsonMap);

		mockMvc.perform(put("/cache/entries")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json))
				.andExpect(status().isOk());

		// THEN
		mockMvc.perform(get("/cache/entries?key=TEST"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

	@Test
	public void testListValue() throws Exception {
		// WHEN
		ObjectMapper objectMapper = new ObjectMapper();

		HashMap<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("key", "TEST");
		jsonMap.put("value", Arrays.asList(1, 2, 3, 4));

		String json = objectMapper.writeValueAsString(jsonMap);

		mockMvc.perform(put("/cache/entries")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json))
				.andExpect(status().isOk());

		// THEN
		mockMvc.perform(get("/cache/entries?key=TEST"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

	@Test
	public void testEntryDeletion() throws Exception {
		// WHEN
		ObjectMapper objectMapper = new ObjectMapper();

		HashMap<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("key", "TEST");
		jsonMap.put("value", "VALUE");

		String json = objectMapper.writeValueAsString(jsonMap);

		mockMvc.perform(put("/cache/entries")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json))
				.andExpect(status().isOk());

		// delete the entry
		jsonMap = new HashMap<>();
		jsonMap.put("key", "TEST");
		jsonMap.put("value", null);

		json = objectMapper.writeValueAsString(jsonMap);

		mockMvc.perform(put("/cache/entries")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(json))
				.andExpect(status().isOk());

		// THEN
		mockMvc.perform(get("/cache/entries?key=TEST"))
				.andExpect(status().isNotFound());
	}

}
