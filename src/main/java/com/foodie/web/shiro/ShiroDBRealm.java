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
     * 为当前登录的Subject授予角色和权限 
     * @see 经测试:本例中该方法的调用时机为需授权资源被访问时 
     * @see 经测试:并且每次访问需授权资源时都会执行该方法中的逻辑,这表明本例中默认并未启用AuthorizationCache 
     * @see 个人感觉若使用了Spring3.1开始提供的ConcurrentMapCache支持,则可灵活决定是否启用AuthorizationCache 
     * @see 比如说这里从数据库获取权限信息时,先去访问Spring3.1提供的缓存,而不使用Shior提供的AuthorizationCache 
     */  
    @Override  
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals){
    	logger.debug("开始授权");
    	String account = (String) super.getAvailablePrincipal(principals);
    	logger.debug("account:{}", account);
    	SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        logger.debug("结束授权");
        return info;  
    }  
      
    /** 
     * 验证当前登录的Subject 
     * @see 经测试:本例中该方法的调用时机为LoginController.login()方法中执行Subject.login()时 
     */  
    @Override  											  		
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
    	logger.debug("开始认证");
    	//获取基于用户名和密码的令牌  
        //实际上这个authcToken是从LoginController里面currentUser.login(token)传过来的  
        //两个token的引用都是一样的,本例中是org.apache.shiro.authc.UsernamePasswordToken@33799a1e  
        UsernamePasswordToken token = (UsernamePasswordToken)authcToken;
        
        System.out.println("token.getUsername()--[4]--: "+token.getUsername());					// 18991921951	 用户键入的
        System.out.println("token.getPassword()--[5]--: "+new String(token.getPassword()) );	// 111111		用户键入的
        
        String userName = token.getUsername();				// 18991921951
        String password = new String(token.getPassword());	// 111111
        User user=null;
        Pattern patternPhone = Pattern.compile("^1[34589]\\d{9}$");
		Matcher matcherPhone = patternPhone.matcher(userName);
		if(matcherPhone.matches()){
			user = this.userservice.selectByPhone(userName);
			System.out.println("token.getUsername()--[6]--: "+user.getUsername());	// 18991921951(注册的名字)
			System.out.println("token.getPassword()--[7]--: "+user.getPassword());	// 5b63f66e64695b9415b98de7c9c031f4(注册的时候 自动给密码加上随机的"salt"生成的新的密码)
			System.out.println("token.getSalt()--[8]--: "+user.getSalt());			 //2f9933681ff34c579c62d62fea944100
			System.out.println("token.getSalt()--[9]--: "+user.getSalt().toString());//2f9933681ff34c579c62d62fea944100
			
			System.out.println("this.getName()--[10]--: "+ this.getName().toString());	//com.foodie.web.shiro.ShiroDBRealm_0
			
		}
		if( user== null){
			System.out.println("这里代表 user==null !!! ");	//
        	throw new UnknownAccountException();
        }
		
		//										               用户键入的密码111111
		if(user.getPassword().equals(MD5Utils.encrypt(password       + user.getSalt()))){ //此方法 禁止了 从数据库直接插入数据 登陆app的渠道
		// user.getPassword(): 5b63f66e64695b9415b98de7c9c031f4(注册的时候 自动给密码加上随机的"salt"生成的新的密码)
			
			//		 							
        	AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(userName, password, this.getName());
            this.setSession("userId", user.getId());
            return authcInfo;
        }
		return null;
    }  
      
      
    /** 
     * 将一些数据放到ShiroSession中,以便于其它地方使用 
     * @see 比如Controller,使用时直接用HttpSession.getAttribute(key)就可以取到 
     */  
    private void setSession(Object key, Object value){
        Subject currentUser = SecurityUtils.getSubject();  
        if(null != currentUser){  
            Session session = currentUser.getSession();
            if(session != null){
                logger.debug("Session默认超时时间为[" + session.getTimeout() + "]毫秒");  
            }
            if(null != session){ 
                session.setAttribute(key, value);  
            }  
        }  
    }  
    /**
     * 获取session
     */
    private Session getSession(){
    	Subject currentUser = SecurityUtils.getSubject();
    	return currentUser.getSession();
    }
}
