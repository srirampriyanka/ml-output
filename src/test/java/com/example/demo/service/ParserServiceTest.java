package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import com.example.demo.model.Feature;
import com.example.demo.model.Input;
import com.example.demo.model.Transforms;

public class ParserServiceTest {

	ParserService parserService = new ParserService();

	Input DEFAULT_INPUT = null;
	List<Transforms> DEFAULT_TRANSFORMS_LIST = null;
	Feature DEFAULT_FEATURE = null;
	String DEFAULT_INPUTJSON = null;

	@BeforeEach
	public void setDefaults() {
		DEFAULT_TRANSFORMS_LIST = new ArrayList<Transforms>();
		DEFAULT_TRANSFORMS_LIST.add(new Transforms("device_os", true, true, ".device.osType"));
		DEFAULT_TRANSFORMS_LIST
				.add(new Transforms("device_description", true, true, ".device.osType + \" \" + .device.model"));

		DEFAULT_FEATURE = new Feature(1, "DeviceFeatures", DEFAULT_TRANSFORMS_LIST);

		DEFAULT_INPUTJSON = "{ \"eventId\": \"878237843\", \"device\": {\"osType\": \"Linux\",\"model\": \"Laptop\"},\"ip\" : \"10.45.2.30\",\"sessionId\": \"ads79uoijd098098\" }";

		DEFAULT_INPUT = new Input(DEFAULT_FEATURE, DEFAULT_INPUTJSON);
	}

	@Test
	public void successfulTest() {
		Input input = getOrOverrideDefaultInput(DEFAULT_TRANSFORMS_LIST, DEFAULT_FEATURE, DEFAULT_INPUTJSON, false);

		String expectedString = "{\"eventId\":\"878237843\",\"device_os\":\"Linux\",\"device_description\":\"Linux Laptop\"}";
		String actualString = null;
		try {
			actualString = parserService.parse(input);
		} catch (Exception e) {
			System.out.println("exception occured");
		}

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void nullInput() {
		String expectedString = "{\"error\":\"" + ParserService.INPUT_ERROR + "\"}";
		;
		String actualString = parserService.parse(null);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void emptyFeature() {
		Input input = getOrOverrideDefaultInput(DEFAULT_TRANSFORMS_LIST, null, DEFAULT_INPUTJSON, true);
		String expectedString = "{\"error\":\"" + ParserService.FEATURE_ERROR + "\"}";
		String actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void emptyTransforms() {
		Input input = getOrOverrideDefaultInput(null, DEFAULT_FEATURE, DEFAULT_INPUTJSON, true);
		String expectedString = "{\"eventId\":\"878237843\"}";
		String actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void emptyInputJson() {
		Input input = getOrOverrideDefaultInput(DEFAULT_TRANSFORMS_LIST, DEFAULT_FEATURE, null, true);
		String expectedString = "{\"error\":\"" + ParserService.INPUT_JSON_ERROR + "\"}";
		String actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void emptyTransformsAndEventId() {
		Input input = getOrOverrideDefaultInput(null, DEFAULT_FEATURE, null, true);
		String expectedString = "{\"error\":\"" + ParserService.INPUT_JSON_ERROR + "\"}";
		String actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void emptyEventIdOnly() {
		String json = "{\"device\": {\"osType\": \"Linux\",\"model\": \"Laptop\"},\"ip\" : \"10.45.2.30\",\"sessionId\": \"ads79uoijd098098\"}";

		Input input = getOrOverrideDefaultInput(DEFAULT_TRANSFORMS_LIST, DEFAULT_FEATURE, json, true);
		String expectedString = "{\"device_os\":\"Linux\",\"device_description\":\"Linux Laptop\"}";
		String actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void inputJsonMismatchTransformExpression() {
		String json = "{\"device\": {\"osType\": \"Linux\"},\"ip\" : \"10.45.2.30\",\"sessionId\": \"ads79uoijd098098\"}";

		Input input = getOrOverrideDefaultInput(DEFAULT_TRANSFORMS_LIST, DEFAULT_FEATURE, json, true);
		String expectedString = "{\"device_os\":\"Linux\",\"device_description\":\"Linux null\"}";
		String actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void corruptInputJson() {
		String json = "{\"";

		Input input = getOrOverrideDefaultInput(DEFAULT_TRANSFORMS_LIST, DEFAULT_FEATURE, json, true);
		String expectedString = "{\"error\":\"" + ParserService.INPUT_JSON_CORRUPTED + "\"}";
		String actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	public Input getOrOverrideDefaultInput(List<Transforms> inputTransforms, Feature inputFeature, String inputJson,
			boolean overrideDefaults) {

		List<Transforms> transforms;
		if (overrideDefaults) {
			transforms = inputTransforms;
		} else {
			transforms = DEFAULT_TRANSFORMS_LIST;
		}

		Feature feature;
		if (overrideDefaults) {
			feature = inputFeature;
			if (null != feature) {
				feature.setTransforms(transforms);
			}
		} else {
			feature = DEFAULT_FEATURE;
		}

		String json;
		if (overrideDefaults) {
			json = inputJson;
		} else {
			json = DEFAULT_INPUTJSON;
		}

		Input newInput = new Input(feature, json);

		return newInput;
	}
}
