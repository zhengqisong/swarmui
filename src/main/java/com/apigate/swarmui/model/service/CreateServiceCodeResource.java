package com.apigate.swarmui.model.service;

import java.util.HashMap;
import java.util.Map;

import com.apigate.swarmui.model.Config;
import com.apigate.swarmui.model.Network;
import com.apigate.swarmui.model.Secret;
import com.apigate.swarmui.model.StorageVolume;

public class CreateServiceCodeResource {
	Map<String,Network> networkMap = new HashMap<String,Network>();
	Map<String,Config> configMap = new HashMap<String,Config>();
	Map<String,Secret> secretMap = new HashMap<String,Secret>();
	Map<String,StorageVolume> storageVolumeMap = new HashMap<String,StorageVolume>();
	
	public void addNetwork(Network network){
		networkMap.put(network.getCode(), network);
	}
	public void addConfig(Config config){
		configMap.put(config.getCode(), config);
	}
	public void addSecret(Secret secret){
		secretMap.put(secret.getCode(), secret);
	}
	public void addStorageVolume(StorageVolume storageVolume){
		storageVolumeMap.put(storageVolume.getCode(), storageVolume);
	}
	
	public Network getNetwork(String code){
		return networkMap.get(code);
	}
	public Config getConfig(String code){
		return configMap.get(code);
	}
	public Secret getSecret(String code){
		return secretMap.get(code);
	}
	public StorageVolume getStorageVolume(String code){
		return storageVolumeMap.get(code);
	} 
	
	public void clearNetwork(){
		networkMap.clear();
	}
	public void clearConfig(){
		configMap.clear();
	}
	public void clearSecret(){
		secretMap.clear();
	}
	public void clearStorageVolume(){
		storageVolumeMap.clear();
	}
	
	
}
