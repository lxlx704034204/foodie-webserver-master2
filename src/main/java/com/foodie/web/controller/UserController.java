package com.foodie.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.tags.UserTag;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.foodie.core.entity.ImageResult;
import com.foodie.core.entity.Result;
import com.foodie.core.util.MD5Utils;
import com.foodie.core.util.STDateUtils;
import com.foodie.core.util.SessionUtil;
import com.foodie.core.util.UUIDUtils;
import com.foodie.web.model.Collection;
import com.foodie.web.model.Fans;
import com.foodie.web.model.User;
import com.foodie.web.service.IFansService;
import com.foodie.web.service.IFileService;
import com.foodie.web.service.IUserService;
import com.foodie.web.service.IWorksService;
import com.foodie.web.service.impl.WorksService;

/**
 * �û�������
 * 
 * @author Xiongbo
 * @since 2016��7��3�� ����3:54:00
 * 
 * 
 * 	D:\Users\lx\Workspaces\MyEclipse 10\.metadata\.me_tcat\webapps
 **/
@Controller
@RequestMapping(value = "/user")
public class UserController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private IUserService userService;
    @Autowired
    private IFansService fansService;
    @Autowired
    private IWorksService worksService;
    @Autowired
    private IFileService fileService;
    /**
	 * �ύע��ʱ���
	 * 
	 * @param request
	 * @param session
	 * @param model
	 * @return
	 * 
	 * {[/user/],methods=[POST],params=[],headers=[],consumes=[],produces=[],custom=[]}" 
	 * onto public com.foodie.core.entity.Result com.foodie.web.controller.UserController.register(javax.servlet.http.HttpServletRequest)
	 * 
	 */
	@RequestMapping(value="/",method=RequestMethod.POST)
	@ResponseBody
	public Result register(HttpServletRequest request) {
		Result result = new Result();

		String nickname = request.getParameter("nickname");
		String phone = request.getParameter("phone");
		String password = request.getParameter("password");
		String passwordAgain = request.getParameter("passwordAgain");
		
		System.out.println( "register--[1]--nickname: "+nickname+"/n"+
							"register--[2]--phone: "+phone+"/n"+
							"register--[3]--password: "+password+"/n"+
							"register--[4]--passwordAgain: "+passwordAgain+"/n"
				);//
		
		
		if (nickname==null||phone == null || password == null
				|| passwordAgain == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("lackRegisterInfo");
			result.setTipMsg("ע����Ϣ������");
			return result;
		}
		if (!this.phoneNoCheck(phone)) {
			result.setStatus(Result.FAILED);
			result.setTipCode("mobileNoIllegal");
			result.setTipMsg("�ֻ��Ų���Ҫ��");
			return result;
		}
		if (!this.phoneNoReg(phone)) {
			result.setStatus(Result.FAILED);
			result.setTipCode("mobileNoHasBeenRegistered");
			result.setTipMsg("�ֻ�����ע��");
			return result;
		}
		if (!this.passwordCheck(password, passwordAgain)) {
			result.setStatus(Result.FAILED);
			result.setTipCode("passwordNotEqual");
			result.setTipMsg("�����������벻һ��");
			return result;
		}

		User user = new User();
		String id = UUIDUtils.getUUID();
		String salt = UUIDUtils.getUUID();
		String createTime = STDateUtils.getCurrentTime();
		
		user.setId(id);
		user.setUsername(phone);
		user.setPhone(phone);
		user.setNickname(nickname);
		user.setPassword(MD5Utils.encrypt(password + salt));
		user.setCreateTime(createTime);
		user.setSalt(salt);
		user.setAvator("");// "http://bjtu-foodie.oss-cn-shanghai.aliyuncs.com/head/head0.jpeg"
		this.userService.insert(user);
		shiroLogin(phone, password);
		result.setData(user);
		return result;
	}
    /**
	 * ��¼(�û������ֻ��Ż�����)
	 * 
	 * @param request
	 * @param username
	 * @param password
	 * @return
	 * 
	 * {[/user/login],methods=[POST],params=[],headers=[],consumes=[],produces=[],custom=[]}" 
	 * onto public com.foodie.core.entity.Result com.foodie.web.controller.UserController.login
	 * (javax.servlet.http.HttpServletRequest,java.lang.String,java.lang.String)
	 * 
	 */
	@RequestMapping(value = "/login",method=RequestMethod.POST)
	@ResponseBody
	public Result login(HttpServletRequest request,
			@RequestParam("phone") String phone,
			@RequestParam("password") String password
			) {
		logger.debug("����login����");
		Result result = new Result();
		//ȥ���û���������ǰ��Ŀո�
		phone = phone.trim();
		password = password.trim();
		
		System.out.println("�û������phone--[1]--: "+phone+"<====>"+"�û������password: "+password);//
		
		UsernamePasswordToken token = new UsernamePasswordToken(phone, password); // phone, password		"13052345227", "123456"
		 //5.���ü�ס��  
        token.setRememberMe(true); 
		
		System.out.println(".toString()2--[2]--: "+token.toString());
		
		logger.debug("Ϊ����֤��¼�û�����װ��tokenΪ:" + JSON.toJSONString(token));
		// ��ȡ��ǰ��Subject
		Subject currentUser = SecurityUtils.getSubject();
		
		System.out.println("currentUser.toString()--[3]--: "+currentUser.toString());
		
		
		try {
			 
			
			// �ڵ�����login������,SecurityManager���յ�AuthenticationToken,�����䷢�͸������õ�Realmִ�б������֤���
			// ÿ��Realm�����ڱ�Ҫʱ���ύ��AuthenticationTokens������Ӧ
			// ������һ���ڵ���login(token)����ʱ,�����ߵ�MyRealm.doGetAuthenticationInfo()������,������֤��ʽ����˷���
			logger.debug("���û�[{}]���е�¼��֤..��֤��ʼ", phone);
			currentUser.login(token);	//���� �˷������� ShiroDBRealm�е� doGetAuthenticationInfo(AuthenticationToken authcToken)����
			logger.debug("���û�[{}]���е�¼��֤..��֤ͨ��", phone);
		} catch (UnknownAccountException uae) {
			logger.warn("���û�[{}]���е�¼��֤..��֤δͨ��,δ֪�˺�", phone);
			logger.warn("δ֪�˻�", uae);
			result.setStatus(Result.FAILED);
			result.setTipMsg("�˻�δע��");
		} catch (IncorrectCredentialsException ice) {
			logger.warn("���û�[{}]���е�¼��֤..��֤δͨ��,�����ƾ֤", phone);
			logger.warn("�������", ice);
			result.setStatus(Result.FAILED);
			result.setTipMsg("�û������������");
		} catch (LockedAccountException lae) {
			logger.warn("���û�[{}]���е�¼��֤..��֤δͨ��,�˺�������", phone);
			logger.warn("�˺�������", lae);
			result.setStatus(Result.FAILED);
			result.setTipMsg("�˺�������");
		} catch (ExcessiveAttemptsException eae) {
			logger.warn("���û�[{}]���е�¼��֤..��֤δͨ��,�����������", phone);
			logger.warn("�û���������������̫��", eae);
			result.setStatus(Result.FAILED);
			result.setTipMsg("��������ȷ��ͼƬ��֤��");
			result.setTipCode("showPictureCode");
		} catch (AuthenticationException ae) {
			// ͨ������Shiro������ʱAuthenticationException�Ϳ��Կ����û���¼ʧ�ܻ��������ʱ���龰
			logger.warn("���û�[{}]���е�¼��֤..��֤δͨ��,��ջ�켣����", phone);
			logger.warn("�û��������벻��ȷ", ae);
			result.setStatus(Result.FAILED);
			result.setTipMsg("�û��������벻��ȷ");
		}
		//��֤�Ƿ��¼�ɹ�
		if (currentUser.isAuthenticated()) {
			logger.info("�û�[{}]��¼��֤ͨ��(������Խ���һЩ��֤ͨ�����һЩϵͳ������ʼ������)", phone);
			User user=userService.selectByPhone(currentUser.getPrincipal().toString());
			result.setData(user);
		} else {
			token.clear();
		}
		return result;
	}

	/**
	 * �˳���¼
	 * 
	 * @return
	 */
	@RequestMapping(value="/logOut",method=RequestMethod.GET)
	@ResponseBody
	public Result logOut() {
		SecurityUtils.getSubject().logout();
		logger.info("�˳���¼");
		Result result = new Result();
		return result;
	}
	
	@RequestMapping(value="/me",method=RequestMethod.GET)
	@ResponseBody
	public Result showUserInfo(HttpSession session){
		Result result = new Result();
		String userId = SessionUtil.getUserId(session);
		if (userId == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("userNotLogin");
			result.setTipMsg("�û�δ��½");
			return result;
		}
		User user = this.userService.selectByPrimaryKey(userId);
		if (user == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("visitorNotExist");
			result.setTipMsg("�����ڸ��û�");
			return result;
		}
		result.setData(user);
		return result;
	}
	/**
	 * �鿴ָ���û���Ϣ
	 * @param userId
	 * @return
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Result selectByPrimaryKey(@PathVariable("id") String userId){
		Result result = new Result();
		if (userId == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("userNotLogin");
			result.setTipMsg("�û�������");
			return result;
		}
		User user = this.userService.selectByPrimaryKey(userId);
		if (user == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("userNotExist");
			result.setTipMsg("�����ڸ��û�");
			return result;
		}
		result.setData(user);
		return result;
	}
	/**
	 * �޸��ҵ�����
	 * @return
	 */
	@RequestMapping(value="/me/modify",method=RequestMethod.POST)
	@ResponseBody
	public Result modifyUserInfo(HttpServletRequest request,HttpSession session,@RequestParam(value = "file") MultipartFile fileUpload){
		Result result=new Result();
		String userId = SessionUtil.getUserId(session);
		if (userId == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("NotLogin");
			result.setTipMsg("�û�δ��½");
			return result;
		}
		String nickname = request.getParameter("nickname");
		String password = request.getParameter("password");
		String passwordAgain = request.getParameter("passwordAgain");
		//ͼƬ�ϴ�
    	ImageResult pictureResult;
		try {
			logger.info("��ʼ�ύͼƬ");
			 pictureResult =fileService.uploadPic(fileUpload,"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setStatus(Result.FAILED);
			result.setTipCode("error");
			result.setTipMsg("ͼƬ�ϴ�����");
			return result;
		}
		logger.info("�ύͼƬ�ɹ�"+pictureResult.getData());
		if(!pictureResult.getStatus().equals(Result.SUCCESS)){
			logger.info("û��ͼƬ");
			result.setStatus(Result.FAILED);
			result.setTipCode("error");
			result.setTipMsg("ͼƬ�ϴ�ʧ�ܣ��������ϴ�");
			return result;
		}
		User user=userService.selectByPrimaryKey(userId);
		
		user.setAvator(pictureResult.getData());
		if(nickname!=null&&!nickname.equals("")){
			user.setNickname(nickname);
		}
		if(password!=null&&!password.equals("")){
			user.setPassword(password);
			
		}
		if(passwordAgain!=null&&!passwordAgain.equals("")){
			if(!passwordCheck(password, passwordAgain)){
				result.setStatus(Result.FAILED);
				result.setTipCode("passwordDiffirent");
				result.setTipMsg("�û�δ���벻һ��");
				return result;
			}
		}
		if(userService.updateByPrimaryKeySelective(user)!=1){
			result.setStatus(Result.FAILED);
			result.setTipCode("updateError");
			result.setTipMsg("�޸�ʧ��");
			return result;
		}
		result.setData(user);
		return result;
	}
	/**
	 * ��ע�û�,ȡ����ע
	 * @param userId
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/{id}/togglefollow/",method=RequestMethod.POST)
	@ResponseBody
	public Result follow(@PathVariable("id") String userId,HttpSession session){
		Result result=new Result();
		String myUserId = SessionUtil.getUserId(session);
    	if(myUserId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("δ��¼");
			return result;
    	}
    	if(userService.selectByPrimaryKey(userId)==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("userNotExist");
			result.setTipMsg("�û�������");
			return result;
    	}
    	if(fansService.selectByTwoId(userId,myUserId)!=null){
    		Fans fans=fansService.selectByTwoId(userId, myUserId);
    		if(fansService.delete(fans)!=1){
    			result.setStatus(Result.FAILED);
    			result.setTipCode("unFollowFail");
    			result.setTipMsg("ȡ����עʧ��");
    			return result;
    		}else{
    			result.setStatus(Result.SUCCESS);
    			result.setTipCode("unFollow");
    			result.setTipMsg("ȡ����ע�ɹ�");
    			result.setData(fans);
    			return result;
    		}
    	}else{
    		Fans fans=new Fans();
        	String id=UUIDUtils.getUUID();
        	String followTime=STDateUtils.getCurrentTime();
        	fans.setId(id);
        	fans.setFansId(myUserId);
        	fans.setUserId(userId);
        	fans.setFollowTime(followTime);
        	if(fansService.insert(fans)!=1){
        		result.setStatus(Result.FAILED);
    			result.setTipCode("followFail");
    			result.setTipMsg("��עʧ��");
    			return result;
        	}else{
        		result.setStatus(Result.SUCCESS);
    			result.setTipCode("follow");
    			result.setTipMsg("��ע�ɹ�");
    			result.setData(fans);
    			return result;
        	}
    	}
	}
	
	@RequestMapping(value="/{id}/isfollow/",method=RequestMethod.GET)
	@ResponseBody
	public Result hasFollow(@PathVariable("id") String userId,HttpSession session){
		Result result=new Result();
    	String fansId = SessionUtil.getUserId(session);
    	if(fansId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("δ��¼");
			return result;
    	}
    	if(userService.selectByPrimaryKey(userId)==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notExist");
			result.setTipMsg("���ע���û�������");
			return result;
    	}
    	
    	Fans fans=fansService.selectByTwoId(userId, fansId);
    	if(fans==null){
    		result.setStatus(Result.SUCCESS);
			result.setTipCode("notFollow");
			result.setTipMsg("δ��ע");
			return result;
    	}
    	result.setTipCode("follow");
    	result.setData(fans);
		return result;
	}
	@RequestMapping(value="/fanscount/",method=RequestMethod.GET)
	@ResponseBody
	public Result getFansCount(HttpSession session){
		Result result=new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("δ��¼");
			return result;
    	}
    	List<Fans> fanses=fansService.selectByUserId(userId);
    	int count=fanses.size();
		result.setData(count);
		return result;
		
	}
	@RequestMapping(value="/followcount/",method=RequestMethod.GET)
	@ResponseBody
	public Result getFollowCount(HttpSession session){
		Result result=new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("δ��¼");
			return result;
    	}
    	List<Fans> fanses=fansService.selectByFansId(userId);
    	int count=fanses.size();
		result.setData(count);
		return result;
		
	}
	@RequestMapping(value="/allfans/",method=RequestMethod.GET)
	@ResponseBody
	public Result getAllFans(HttpSession session){
		Result result=new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("δ��¼");
			return result;
    	}
    	List<Fans> fanses=fansService.selectByUserId(userId);
    	if(fanses==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("null");
			result.setTipMsg("��û���κη�˿");
			return result;
    	}else{
    		List<User> users=new ArrayList<>();
    		for(Fans fans:fanses){
    			User user=userService.selectByPrimaryKey(fans.getFansId());
    			users.add(user);
    		}
    		result.setData(users);
    		return result;
    	}	
	}
	@RequestMapping(value="/allfollow/",method=RequestMethod.GET)
	@ResponseBody
	public Result getAllFollow(HttpSession session){
		Result result=new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("δ��¼");
			return result;
    	}
    	List<Fans> fanses=fansService.selectByFansId(userId);
    	if(fanses==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("null");
			result.setTipMsg("��û�й�ע�κ��û�");
			return result;
    	}else{
    		List<User> users=new ArrayList<>();
    		for(Fans fans:fanses){
    			User user=userService.selectByPrimaryKey(fans.getUserId());
    			users.add(user);
    		}
    		result.setData(users);
    		return result;
    	}	
		
	}
	private boolean nameCheck(String name) {
		Pattern patternName = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_]{2,20}$");
		Matcher matcherName = patternName.matcher(name);
		return matcherName.matches();
	}
	
	private boolean phoneNoCheck(String mobileNo) {
		Pattern patternMobileNo = Pattern.compile("^1[34578]\\d{9}$");
		Matcher matcherMobileNo = patternMobileNo.matcher(mobileNo);
		return matcherMobileNo.matches();
	}

	private boolean passwordCheck(String password, String passwordAgain) {
		return (password.length() >= 6 && password.length() <= 20 && password
				.equals(passwordAgain));
	}
	private boolean nameReg(String username) {
		User user = this.userService.selectByUsername(username);
		return (user == null);
	}
	private boolean phoneNoReg(String phone) {
		User user = this.userService.selectByPhone(phone);
		return (user == null);
	}
	/**
	 * shiro��¼
	 * @param name
	 * @param password
	 */
	private void shiroLogin(String name, String password){
		UsernamePasswordToken token = new UsernamePasswordToken(name, password);
		Subject currentUser = SecurityUtils.getSubject();
		currentUser.login(token);
	}
}
