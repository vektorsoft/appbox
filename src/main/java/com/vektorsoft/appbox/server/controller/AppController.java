/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vektorsoft.appbox.server.controller;

import com.vektorsoft.appbox.server.model.Application;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@RestController
@RequestMapping(value = "/applications")
public class AppController {

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody List<Application> listApplications() {
	Application app1 = new Application();
	app1.setId("app1");
	app1.setName("My App");
	app1.setVersion("1.0.0");
	app1.setIconUrl("http://myicn.com/icon.png");
	
	Application app2 = new Application();
	app2.setId("app2");
	app2.setName("Another App");
	app2.setVersion("1.0.2");
	app2.setDescription("My great app");
	
	return List.of(app1, app2);
    }
}
