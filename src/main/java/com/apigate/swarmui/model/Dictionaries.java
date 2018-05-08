package com.apigate.swarmui.model;

public class Dictionaries {
	public static class  Dictionary{
		String key;
		String value;
		String text;
		public Dictionary(){
			
		}
		public Dictionary(String key, String value, String text){
			this.key = key;
			this.value = value;
			this.text = text;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	}
	
	public static Dictionary user_role_system = new Dictionary("system","system","系统管理员");
	public static Dictionary user_role_admin = new Dictionary("admin","admin","集群管理员");
	public static Dictionary user_role_service = new Dictionary("service","service","客服员");
	public static Dictionary user_role_user = new Dictionary("user","user","业务用户");	
	public static Dictionary user_role_default = user_role_user;
	
	public static Dictionary user_role[] = {
			user_role_system,
			user_role_admin,
			user_role_service,
			user_role_user
	};
	
	public static Dictionary user_status_normal = new Dictionary("normal","normal","正常");
	public static Dictionary user_status_suspend = new Dictionary("suspend","suspend","暂停");
	public static Dictionary user_status_leave = new Dictionary("leave","leave","离职");
	public static Dictionary user_status_default = user_status_normal;
	
	public static Dictionary user_status[] = {
			user_status_normal,
			user_status_suspend,
			user_status_leave
	};	
	
	public static Dictionary boolean_type_yes = new Dictionary("yes","yes","是");
	public static Dictionary boolean_type_no = new Dictionary("no","no","否");
	
	public static Dictionary boolean_type[] = {
			boolean_type_yes,
			boolean_type_no
	};
	
	public static Dictionary right_type_r = new Dictionary("r","read","读");
	public static Dictionary right_type_w = new Dictionary("w","write","写");
	public static Dictionary right_type_d = new Dictionary("d","delete","删");
	
	public static Dictionary right_type[] = {
			right_type_r,
			right_type_w,
			right_type_d
	};
	
	public static boolean keyIsIn(String key, Dictionary[] dictionary){
		if(key == null){
			return false;
		}
		for(Dictionary d : dictionary){
			if(d.getKey().equals(key)){
				return true;
			}
		}
		return false;
	}
	
	
	

}
