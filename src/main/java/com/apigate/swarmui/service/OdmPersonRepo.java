package com.apigate.swarmui.service;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import com.apigate.swarmui.model.LdapPerson;
 
@Service
public class OdmPersonRepo {
 
 @Autowired
 private LdapTemplate ldapTemplate;
 
 public LdapPerson create(LdapPerson LdapPerson){
  ldapTemplate.create(LdapPerson);
  return LdapPerson;
 }
 
 public LdapPerson findByCn(String cn){
  return ldapTemplate.findOne(query().where("cn").is(cn),LdapPerson.class);
 }
 
 public LdapPerson modifyPerson(LdapPerson LdapPerson){
  ldapTemplate.update(LdapPerson);
  return LdapPerson;
 }
 
 public void deletePerson(LdapPerson LdapPerson){
  ldapTemplate.delete(LdapPerson);
 }
 
}