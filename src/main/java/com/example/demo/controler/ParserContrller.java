package com.example.demo.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Input;
import com.example.demo.service.ParserService;

@RestController
public class ParserContrller {

	@Autowired
	ParserService parserService;

	/**
	 * <p>
	 * This API will return
	 * 
	 * <P>
	 * Errors
	 * @return - empty "{}" json body if it was unable to parse the
	 *         feature/inputjson into any valid output json 
	 *         {"error":"Feature Field is empty, nothing to parse"} 
	 *         - if Feature object is empty
	 *         {"error":"InputJson Field is empty, nothing to parse"} 
	 *         - if Input object is empty 
	 *         {"error":"InputJson Field is Corrupted, please check the json syntax"} 
	 *         - if Input Json is corrupted -
	 *         {"error":"Input is empty, nothing to parse"} 
	 *         - If Input is empty
	 *   
	 * <P>
	 * Sample Successful Output
	 * @return
	 * Input FeatureConfig Object { "id": 1, "name": "DeviceFeatures" "transforms":
	 * [ { "name": "device_os", "useInML" : true, "enabled" : true,
	 * "jsltExpression": ".device.osType" }, { "name": "device_description",
	 * "useInML" : true, "enabled" : true, "jsltExpression":
	 * ".device.osType + \" \" + .device.model" } ] }
	 * 
	 * Input Json: { "eventId": "878237843" "device": { "osType": "Linux", "model":
	 * "Laptop", }, "ip" : "10.45.2.30", "sessionId": "ads79uoijd098098" }
	 *
	 * Output: { "eventId": "878237843", "device_os": "Linux" , "device_description"
	 * : "Linux Laptop" }
	 * 
	 */
	@PostMapping("/api/parse")
	public String parseInput(@RequestBody Input input) {
		return parserService.parse(input);
	}
}
