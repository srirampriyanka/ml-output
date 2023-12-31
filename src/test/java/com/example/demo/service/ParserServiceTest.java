package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import com.example.demo.exceptionhandling.CorruptedJsonException;
import com.example.demo.exceptionhandling.IncorrectInputException;
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
		actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void disabledTransforms() {

		List<Transforms> t = new ArrayList<Transforms>();
		t.add(new Transforms("device_os", false, true, ".device.osType"));
		t.add(new Transforms("device_description", true, true, ".device.osType + \" \" + .device.model"));
		Input input = getOrOverrideDefaultInput(t, DEFAULT_FEATURE, DEFAULT_INPUTJSON, true);

		String expectedString = "{\"eventId\":\"878237843\",\"device_description\":\"Linux Laptop\"}";
		String actualString = null;
		actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Disabled
	@Test
	public void inputJsonHasNoMatchingJsltExpressionObjects() {

		String inputJson = "{ \"ip\" : \"10.45.2.30\",\"sessionId\": \"ads79uoijd098098\" }";
		Input input = getOrOverrideDefaultInput(DEFAULT_TRANSFORMS_LIST, DEFAULT_FEATURE, inputJson, true);

		String expectedString = "{\"device_os\":\"\",\"device_description\":\"\"}";
		String actualString = null;
		actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void corruptedJstlExpression() {

		List<Transforms> t = new ArrayList<Transforms>();
		t.add(new Transforms("device_os", true, true, ".dev"));
		t.add(new Transforms("device_description", true, true, ".device.osType + \" \" + .device.model"));
		Input input = getOrOverrideDefaultInput(t, DEFAULT_FEATURE, DEFAULT_INPUTJSON, true);

		String expectedString = "{\"eventId\":\"878237843\",\"device_os\":\"\",\"device_description\":\"Linux Laptop\"}";
		String actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void incorrectTransformName() {

		List<Transforms> t = new ArrayList<Transforms>();
		t.add(new Transforms("", true, true, ".dev"));
		t.add(new Transforms("device_description", true, true, ".device.osType + \" \" + .device.model"));
		Input input = getOrOverrideDefaultInput(t, DEFAULT_FEATURE, DEFAULT_INPUTJSON, true);

		String expectedString = "{\"eventId\":\"878237843\",\"device_description\":\"Linux Laptop\"}";
		String actualString = parserService.parse(input);

		Assert.isTrue(expectedString.equals(actualString));
	}

	@Test
	public void nullInput() {
		IncorrectInputException thrown = Assertions.assertThrows(IncorrectInputException.class, () -> {
			parserService.parse(null);
		}, parserService.INPUT_ERROR);
	}

	@Test
	public void emptyFeature() {
		Input input = getOrOverrideDefaultInput(DEFAULT_TRANSFORMS_LIST, null, DEFAULT_INPUTJSON, true);
		IncorrectInputException thrown = Assertions.assertThrows(IncorrectInputException.class, () -> {
			parserService.parse(input);
		}, parserService.FEATURE_ERROR);
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
		IncorrectInputException thrown = Assertions.assertThrows(IncorrectInputException.class, () -> {
			parserService.parse(input);
		}, parserService.INPUT_JSON_ERROR);
	}

	@Test
	public void emptyTransformsAndEventId() {
		Input input = getOrOverrideDefaultInput(null, DEFAULT_FEATURE, null, true);
		IncorrectInputException thrown = Assertions.assertThrows(IncorrectInputException.class, () -> {
			parserService.parse(input);
		}, parserService.INPUT_JSON_ERROR);
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
		CorruptedJsonException thrown = Assertions.assertThrows(CorruptedJsonException.class, () -> {
			parserService.parse(input);
		}, parserService.INPUT_JSON_CORRUPTED);
	}

	private Input getOrOverrideDefaultInput(List<Transforms> inputTransforms, Feature inputFeature, String inputJson,
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
