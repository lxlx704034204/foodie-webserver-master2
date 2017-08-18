package com.foodie.web.shiro;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.foodie.core.util.MD5Utils;
import com.foodie.web.model.User;
import com.foodie.web.service.IUserService;

public class ShiroDBRealm extends AuthorizingRealm {
	private Logger logger = LoggerFactory.getLogger(ShiroDBRealm.class);
	
	@Autowired
	IUserService userservice;
	/** 
     * Ϊ��ǰ��¼��Subject�����ɫ��Ȩ�� 
     * @see ������:�����и÷����ĵ���ʱ��Ϊ����Ȩ��Դ������ʱ 
     * @see ������:����ÿ�η�������Ȩ��Դʱ����ִ�и÷����е��߼�,�����������Ĭ�ϲ�δ����AuthorizationCache 
     * @see ���˸о���ʹ����Spring3.1��ʼ�ṩ��ConcurrentMapCache֧��,����������Ƿ�����AuthorizationCache 
     * @see ����˵��������ݿ��ȡȨ����Ϣʱ,��ȥ����Spring3.1�ṩ�Ļ���,����ʹ��Shior�ṩ��AuthorizationCache 
     */  
    @Override  
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals){
    	logger.debug("��ʼ��Ȩ");
    	String account = (String) super.getAvailablePrincipal(principals);
    	logger.debug("account:{}", account);
    	SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        logger.debug("������Ȩ");
        return info;  
    }  
      
    /** 
     * ��֤��ǰ��¼��Subject 
     * @see ������:�����и÷����ĵ���ʱ��ΪLoginController.login()������ִ��Subject.login()ʱ 
     */  
    @Override  											  		
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
    	logger.debug("��ʼ��֤");
    	//��ȡ�����û��������������  
        //ʵ�������authcToken�Ǵ�LoginController����currentUser.login(token)��������  
        //����token�����ö���һ����,��������org.apache.shiro.authc.UsernamePasswordToken@33799a1e  
        UsernamePasswordToken token = (UsernamePasswordToken)authcToken;
        
        System.out.println("token.getUsername()--[4]--: "+token.getUsername());					// 18991921951	 �û������
        System.out.println("token.getPassword()--[5]--: "+new String(token.getPassword()) );	// 111111		�û������
        
        String userName = token.getUsername();				// 18991921951
        String password = new String(token.getPassword());	// 111111
        User user=null;
        Pattern patternPhone = Pattern.compile("^1[34589]\\d{9}$");
		Matcher matcherPhone = patternPhone.matcher(userName);
		if(matcherPhone.matches()){
			user = this.userservice.selectByPhone(userName);
			System.out.println("token.getUsername()--[6]--: "+user.getUsername());	// 18991921951(ע�������)
			System.out.println("token.getPassword()--[7]--: "+user.getPassword());	// 5b63f66e64695b9415b98de7c9c031f4(ע���ʱ�� �Զ���������������"salt"���ɵ��µ�����)
			System.out.println("token.getSalt()--[8]--: "+user.getSalt());			 //2f9933681ff34c579c62d62fea944100
			System.out.println("token.getSalt()--[9]--: "+user.getSalt().toString());//2f9933681ff34c579c62d62fea944100
			
			System.out.println("this.getName()--[10]--: "+ this.getName().toString());	//com.foodie.web.shiro.ShiroDBRealm_0
			
		}
		if( user== null){
			System.out.println("������� user==null !!! ");	//
        	throw new UnknownAccountException();
        }
		
		//										               �û����������111111
		if(user.getPassword().equals(MD5Utils.encrypt(password       + user.getSalt()))){ //�˷��� ��ֹ�� �����ݿ�ֱ�Ӳ������� ��½app������
		// user.getPassword(): 5b63f66e64695b9415b98de7c9c031f4(ע���ʱ�� �Զ���������������"salt"���ɵ��µ�����)
			
			//		 							
        	AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(userName, password, this.getName());
            this.setSession("userId", user.getId());
            return authcInfo;
        }
		return null;
    }  
      
      
    /** 
     * ��һЩ���ݷŵ�ShiroSession��,�Ա��������ط�ʹ�� 
     * @see ����Controller,ʹ��ʱֱ����HttpSession.getAttribute(key)�Ϳ���ȡ�� 
     */  
    private void setSession(Object key, Object value){
        Subject currentUser = SecurityUtils.getSubject();  
        if(null != currentUser){  
            Session session = currentUser.getSession();
            if(session != null){
                logger.debug("SessionĬ�ϳ�ʱʱ��Ϊ[" + session.getTimeout() + "]����");  
            }
            if(null != session){ 
                session.setAttribute(key, value);  
            }  
        }  
    }  
    /**
     * ��ȡsession
     */
    private Session getSession(){
    	Subject currentUser = SecurityUtils.getSubject();
    	return currentUser.getSession();
    }
}
