package com.apigate.swarmui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apigate.swarmui.model.LdapPerson;
import com.apigate.swarmui.service.OdmPersonRepo;

/**
 * @Author: geng_pool
 * @Description:
 * @Date: Created in 2017/12/27 10:50
 * @Modified by:
 */
@RestController
@RequestMapping("/api/ldap")
public class LdapController {
	@Autowired
	private OdmPersonRepo odmPersonRepo;

	@RequestMapping(value = "/findOne", method = RequestMethod.POST)
	public LdapPerson findByCn(@RequestParam(name = "cn", required = true) String cn) {
		
		return odmPersonRepo.findByCn(cn);
	}

	//@PostMapping(value = "/create")
	public LdapPerson create(@RequestParam(name = "cn") String cn, @RequestParam(name = "sn") String sn,
			@RequestParam(name = "userPassword") String userPassworld) {
		LdapPerson person = new LdapPerson();
		person.setCn(cn);
		person.setSn(sn);
		person.setUserPassword(userPassworld);
		return odmPersonRepo.create(person);
	}

	//@PostMapping(value = "/update")
	public LdapPerson update(@RequestParam(name = "cn") String cn, @RequestParam(name = "sn") String sn,
			@RequestParam(name = "userPassword") String userPassworld) {
		LdapPerson person = new LdapPerson();
		person.setCn(cn);
		person.setSn(sn);
		person.setUserPassword(userPassworld);
		return odmPersonRepo.modifyPerson(person);
	}

	//@PostMapping(value = "/delete")
	public void delete(@RequestParam(name = "cn") String cn) {
		LdapPerson person = new LdapPerson();
		person.setCn(cn);
		odmPersonRepo.deletePerson(person);
	}

}