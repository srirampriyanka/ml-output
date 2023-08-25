package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.exceptionhandling.CorruptedJsonException;
import com.example.demo.exceptionhandling.IncorrectInputException;
import com.example.demo.model.Feature;
import com.example.demo.model.Input;
import com.example.demo.model.Transforms;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.JsltException;
import com.schibsted.spt.data.jslt.Parser;

import io.micrometer.common.util.StringUtils;

@Service
public class ParserService {

	public static String FEATURE_ERROR = "Feature Field is empty, nothing to parse";
	public static String INPUT_JSON_ERROR = "InputJson Field is empty, nothing to parse";
	public static String INPUT_JSON_CORRUPTED = "Input Json Field is Corrupted, please check the json syntax";
	public static String INPUT_ERROR = "Input is empty, nothing to parse";

	public String parse(Input input) {
		if (null == input) {
			throw new IncorrectInputException(INPUT_ERROR);
			// return "{\"error\":\"" + INPUT_ERROR + "\"}";
		}

		Feature feature = input.getFeature();
		if (null == feature) {
			throw new IncorrectInputException(FEATURE_ERROR);
			// return "{\"error\":\"" + FEATURE_ERROR + "\"}";
		}

		String inputJson = input.getJson();
		if (null == inputJson || inputJson.length() == 0) {
			throw new IncorrectInputException(INPUT_JSON_ERROR);
			// return "{\"error\":\"" + INPUT_JSON_ERROR + "\"}";
		}
		// convert input String to JsonNode
		JsonNode inputJsonNode;
		try {
			inputJsonNode = convertToJsonNode(inputJson);
		} catch (JsonProcessingException e) {
			// instead of empty string, we can return an error
			// return "{\"error\":\"" + INPUT_JSON_CORRUPTED + "\"}";
			throw new CorruptedJsonException(INPUT_JSON_CORRUPTED);
		}

		StringBuilder stb = new StringBuilder();
		stb.append("{");

		boolean addComma = false;
		// append event id if exists in the inputJson
		if (null != inputJsonNode.get("eventId") && inputJsonNode.get("eventId").isTextual()) {
			stb.append("\"eventId\":").append(inputJsonNode.get("eventId"));
			addComma = true;
		}

		// the below condition can be tied to eventId. If eventId is empty we can skip
		// showing transforms altogether.
		// currently assuming we have to show transforms irrespective of eventId
		if (null != feature.getTransforms()) {
			// for each transform, extract the data from inputJson using jsltExpression
			// present inside Transform and append to the final string
			for (Transforms transform : feature.getTransforms()) {
				if (!transform.isEnabled()) {
					continue;
				}
				if (StringUtils.isBlank(transform.getName())) {
					continue;
				}

				if (addComma) {
					stb.append(",");
				}
				stb.append("\"").append(transform.getName()).append("\":\"");
				String jsltExpression = transform.getJsltExpression();
				try {
					Expression jslt = Parser.compileString(jsltExpression);

					JsonNode output = jslt.apply(inputJsonNode);
					if (output.isNull()) {
						stb.append("\"");
					} else {
						stb.append(output.asText()).append("\"");
					}
					addComma = true;
				} catch (JsltException e) {
					// ignoring any parser issues and putting value as ""
					stb.append("\"");
					addComma = true;
				}
			}
		}

		// close the json string
		stb.append("}");

		return stb.toString();
	}

	private JsonNode convertToJsonNode(String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode newJsonNode = mapper.readTree(json);
		return newJsonNode;
	}
}

/*
 * sample Input
 */
/*
 * Input FeatureConfig Object { "id": 1, "name": "DeviceFeatures" "transforms":
 * [ { "name": "device_os", "useInML" : true, "enabled" : true,
 * "jsltExpression": ".device.osType" }, { "name": "device_description",
 * "useInML" : true, "enabled" : true, "jsltExpression":
 * ".device.osType + \" \" + .device.model" } ] }
 * 
 * Input Json: { "eventId": "878237843" "device": { "osType": "Linux", "model":
 * "Laptop", }, "ip" : "10.45.2.30", "sessionId": "ads79uoijd098098" }
 * 
 * 
 * 
 * Output: { "eventId": "878237843", "device_os": "Linux" , "device_description"
 * : "Linux Laptop" }
 * 
 */
