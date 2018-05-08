package com.apigate.swarmui.service;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;

public class JMailService {
	@Autowired  
    JavaMailSender mailSender;  
      
    @RequestMapping("sendemail")  
    public boolean sendEmail(String from, String to, String subject, String conext)  
    {
        try  
        {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();  
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);  
            message.setFrom(from);  
            message.setTo(to);  
            message.setSubject(subject);  
            message.setText(conext);  
            this.mailSender.send(mimeMessage);
            
            return true;
        }
        catch(Exception ex)
        {
            return false;
        }
    }  
}
