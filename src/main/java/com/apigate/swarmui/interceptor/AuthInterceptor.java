package com.apigate.swarmui.interceptor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.anntotation.RightMapping;
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.model.UserInfo;
import com.apigate.swarmui.service.CurrentUserSvc;

public class AuthInterceptor implements HandlerInterceptor {
	
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		currentUserSvc.removeCurrentSessionUser();
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
			throws Exception {
	}
	
	private void returnJson(HttpServletResponse response, Object obj) throws Exception{
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss"));

        } catch (IOException e) {
        } finally {
            if (writer != null)
                writer.close();
        }
    }

	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(currentUserSvc == null){
			BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext()); 
			currentUserSvc = (CurrentUserSvc) factory.getBean("currentUserSvc");
		}
		currentUserSvc.initCurrentSessionUser(request);
		
		UserInfo userinfo = currentUserSvc.getCurrentSessionUser();
		
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod)handler;		
			if(handlerMethod.hasMethodAnnotation(RightMapping.class)){
				//no login
				if(userinfo == null){
					Result result = new Result();
					result.setStatus(-204);
					result.setError(MessageManager.getMsg("auth.no_auth"));
					returnJson(response, result);
					return false;
				}
				RightMapping rightMapping = handlerMethod.getMethodAnnotation(RightMapping.class);
				String[] roles = rightMapping.role();
				if(roles != null && roles.length > 0){
					String userRole = userinfo.getRole();
					boolean include = false;
					for(String role : roles){
						if(role.equals(userRole)){
							include = true;
							break;
						}
					}
					if(include == false){
						Result result = new Result();
						result.setStatus(-1);
						result.setError(MessageManager.getMsg("auth.no_right"));
						returnJson(response, result);
						return false;
					}
				}
			}
		}
		
		return true;
	}

}
