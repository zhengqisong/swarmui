package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apigate.swarmui.mapper.RegisterMapper;
import com.apigate.swarmui.model.Register;

@Service
public class RegisterSvc {
	
	@Autowired
	RegisterMapper registerMapper;
	
	public int deleteByPrimaryKey(Integer registerid){
		return this.registerMapper.deleteByPrimaryKey(registerid);
	}

	public int insert(Register register){
		return this.registerMapper.insert(register);
	}

	public int insertSelective(Register register){
		return this.registerMapper.insertSelective(register);
	}

    public Register selectByPrimaryKey(Integer registerid){
    	return this.registerMapper.selectByPrimaryKey(registerid);
    }

    public int updateByPrimaryKeySelective(Register register){
    	return this.registerMapper.updateByPrimaryKeySelective(register);
    }

    public int updateByPrimaryKey(Register register){
    	return this.registerMapper.updateByPrimaryKey(register);
    }
    
    public List<Register> selectAll(Integer clusterid){
    	return this.registerMapper.selectAll(clusterid);
    }
    
}
