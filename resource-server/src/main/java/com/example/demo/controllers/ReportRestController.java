package com.example.demo.controllers;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.TollUsage;

/**
 * REST Controller that serves the report data at:
 *
 *     http://localhost:8081/services/tolldata
 */
@RestController
public class ReportRestController {
	
	protected final Log logger = LogFactory.getLog(getClass());

    @RequestMapping("/tolldata")
	@PreAuthorize("#oauth2.hasScope('toll_read')")
	public ArrayList<TollUsage> getTollData() {
		
		TollUsage instance1 = new TollUsage("200", "station150", "B65GT1W", "2016-09-30T06:31:22");
		TollUsage instance2 = new TollUsage("201", "station119", "AHY673B", "2016-09-30T06:32:50");
		TollUsage instance3 = new TollUsage("202", "station150", "ZN2GP0", "2016-09-30T06:37:01");
		
		ArrayList<TollUsage> tolls = new ArrayList<TollUsage>();
		tolls.add(instance1);
		tolls.add(instance2);
		tolls.add(instance3);
		
		return tolls;
	}
}
