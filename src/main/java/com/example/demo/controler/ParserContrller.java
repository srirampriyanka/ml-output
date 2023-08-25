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
	 * <P>
	 * <li>{ "statusCode": 400, "message": "Input Json Field is Corrupted, please
	 * check the json syntax", "timestamp": 1692980639077 }</li>
	 * <li>{ "statusCode": 400, "message": "Feature Field is empty, nothing to
	 * parse", "timestamp": 1692980720122 }</li>
	 * 
	 * <P>
	 * Sample Successful Output
	 * 
	 * @return Input FeatureConfig Object { "id": 1, "name": "DeviceFeatures"
	 *         "transforms": [ { "name": "device_os", "useInML" : true, "enabled" :
	 *         true, "jsltExpression": ".device.osType" }, { "name":
	 *         "device_description", "useInML" : true, "enabled" : true,
	 *         "jsltExpression": ".device.osType + \" \" + .device.model" } ] }
	 * 
	 *         Input Json: { "eventId": "878237843" "device": { "osType": "Linux",
	 *         "model": "Laptop", }, "ip" : "10.45.2.30", "sessionId":
	 *         "ads79uoijd098098" }
	 *
	 *         Output: { "eventId": "878237843", "device_os": "Linux" ,
	 *         "device_description" : "Linux Laptop" }
	 * 
	 */
	@PostMapping("/api/parse")
	public String parseInput(@RequestBody Input input) {
		return parserService.parse(input);
	}
}
