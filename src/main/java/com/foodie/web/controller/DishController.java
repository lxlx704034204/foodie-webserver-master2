package com.foodie.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.foodie.core.entity.ImageResult;
import com.foodie.core.entity.Result;
import com.foodie.core.util.STDateUtils;
import com.foodie.core.util.SessionUtil;
import com.foodie.core.util.UUIDUtils;
import com.foodie.web.model.Collection;
import com.foodie.web.model.Dish;
import com.foodie.web.model.Fans;
import com.foodie.web.model.Restaurant;
import com.foodie.web.model.User;
import com.foodie.web.model.Works;
import com.foodie.web.service.ICollectionService;
import com.foodie.web.service.IDishService;
import com.foodie.web.service.IFileService;
import com.foodie.web.service.IRestaurantService;
import com.foodie.web.service.impl.RestaurantService;

@Controller
@RequestMapping(value="/dish")
public class DishController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private IDishService dishService;
    @Autowired
    private IRestaurantService restaurantService;
    @Autowired
    private IFileService fileService;
    @Autowired
    private ICollectionService collectionService;
    /**
     * ��ȡ���в���
     * @return
     */
    @RequestMapping(value="/",method=RequestMethod.GET)
    @ResponseBody
    public Result getAll(){
    	Result result=new Result();
		List<Dish> dishes=dishService.getAll();
		result.setData(dishes);
		return result;
    }
    
    
    // http://192.168.1.87:8080/foodie/dish/1  // �����֤�ɹ��� ������
    @RequestMapping(value="/{id}",method=RequestMethod.GET)
    @ResponseBody
    public Result getOne(@PathVariable("id") String id){
    	Result result=new Result();
    	Dish dish=dishService.selectByPrimaryKey(id);
    	if(dish==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("NotExsit");
			result.setTipMsg("��Ʒ������");
    	}
		result.setData(dish);
		return result;
    }
    /**
     * ��ȡ���в����б����������ߵ���Ϣ
     * @return
     * 
     * {[/dish/list],methods=[GET],params=[],headers=[],consumes=[],produces=[],custom=[]}" 
     * onto public com.foodie.core.entity.Result com.foodie.web.controller.DishController.getDishInfo()
     * 
     */
    @RequestMapping(value="/list",method=RequestMethod.GET)
    @ResponseBody
    public Result getDishInfo(){
    	Result result=new Result();
    	List<Map<String, Object>> dishInfoList=new ArrayList<>();
		List<Dish> dishes=dishService.getAll();
		for(Dish dish:dishes){
			Map<String,Object> dishInfo=new HashMap<>();
			dishInfo.put("id", dish.getId());
			dishInfo.put("categoryId", dish.getCategoryId());
			dishInfo.put("createTime", dish.getCreateTime());
			dishInfo.put("dishName", dish.getDishName());
			dishInfo.put("introduction", dish.getIndroduction());
			dishInfo.put("picture", dish.getPicture());
			dishInfo.put("other", dish.getOther());
			dishInfo.put("price", dish.getPrice());
			dishInfo.put("restaurantId", dish.getRestaurantId());
			dishInfo.put("score", dish.getScore());
			dishInfo.put("taste", dish.getTaste());
			String resturantId=dish.getRestaurantId();
			Restaurant resturant=restaurantService.selectByPrimaryKey(resturantId);
			dishInfo.put("restaurantName", resturant.getRestaurantName());
			dishInfo.put("address", resturant.getAddress());
			dishInfo.put("pictureSmall", resturant.getPictureSmall());
			//dishInfo.put("", resturant.getIntroduction())
			dishInfoList.add(dishInfo);
		}
		result.setData(dishInfoList);
		return result;
    }
    /**
     * ��ȡ���в����б����������ߵ���Ϣ
     * @return
     */
    @RequestMapping(value="/list/me",method=RequestMethod.GET)
    @ResponseBody
    public Result getMyDishInfo(){
    	Result result=new Result();
    	List<Map<String, Object>> dishInfoList=new ArrayList<>();
		List<Dish> dishes=dishService.selectByRestaurantId("1");
		for(Dish dish:dishes){
			Map<String,Object> dishInfo=new HashMap<>();
			dishInfo.put("id", dish.getId());
			dishInfo.put("categoryId", dish.getCategoryId());
			dishInfo.put("createTime", dish.getCreateTime());
			dishInfo.put("dishName", dish.getDishName());
			dishInfo.put("introduction", dish.getIndroduction());
			dishInfo.put("picture", dish.getPicture());
			dishInfo.put("other", dish.getOther());
			dishInfo.put("price", dish.getPrice());
			dishInfo.put("restaurantId", dish.getRestaurantId());
			dishInfo.put("score", dish.getScore());
			dishInfo.put("taste", dish.getTaste());
			String resturantId=dish.getRestaurantId();
			Restaurant resturant=restaurantService.selectByPrimaryKey(resturantId);
			dishInfo.put("restaurantName", resturant.getRestaurantName());
			dishInfo.put("address", resturant.getAddress());
			dishInfo.put("pictureSmall", resturant.getPictureSmall());
			//dishInfo.put("", resturant.getIntroduction())
			dishInfoList.add(dishInfo);
		}
		result.setData(dishInfoList);
		return result;
    }
    /**
     * ��ȡ���������б����������ߵ���Ϣ
     * @return
     */
    @RequestMapping(value="/search/{queryString}",method=RequestMethod.GET)
    @ResponseBody
    public Result search(@PathVariable("queryString") String queryString){
    	Result result=new Result();
    	List<Map<String, Object>> dishInfoList=new ArrayList<>();
		List<Dish> dishes=dishService.search(queryString);
		for(Dish dish:dishes){
			Map<String,Object> dishInfo=new HashMap<>();
			dishInfo.put("id", dish.getId());
			dishInfo.put("categoryId", dish.getCategoryId());
			dishInfo.put("createTime", dish.getCreateTime());
			dishInfo.put("dishName", dish.getDishName());
			dishInfo.put("introduction", dish.getIndroduction());
			dishInfo.put("picture", dish.getPicture());
			dishInfo.put("other", dish.getOther());
			dishInfo.put("price", dish.getPrice());
			dishInfo.put("restaurantId", dish.getRestaurantId());
			dishInfo.put("score", dish.getScore());
			dishInfo.put("taste", dish.getTaste());
			String resturantId=dish.getRestaurantId();
			Restaurant resturant=restaurantService.selectByPrimaryKey(resturantId);
			dishInfo.put("restaurantName", resturant.getRestaurantName());
			dishInfo.put("address", resturant.getAddress());
			dishInfo.put("pictureSmall", resturant.getPictureSmall());
			//dishInfo.put("", resturant.getIntroduction())
			dishInfoList.add(dishInfo);
		}
		result.setData(dishInfoList);
		return result;
    }
    /**
     * ��ȡ�ղصĲ˱�
     * @return
     */
    @RequestMapping(value="/collection/list",method=RequestMethod.GET)
    @ResponseBody
    public Result getCollectionDishInfo(HttpSession session){
    	Result result=new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("δ��¼");
			return result;
    	}
    	List<Collection> collections=collectionService.selectByUserId(userId);
    	if(collections==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("null");
			result.setTipMsg("��û���ղ��κ���ʳ");
			return result;
    	}else{
    		List<Dish> dishes=new ArrayList<>();
    		for(Collection collection:collections){
    			Dish dish=dishService.selectByPrimaryKey(collection.getDishId());
    			dishes.add(dish);
    		}
    		List<Map<String, Object>> dishInfoList=new ArrayList<>();
    		for(Dish dish:dishes){
    			Map<String,Object> dishInfo=new HashMap<>();
    			dishInfo.put("id", dish.getId());
    			dishInfo.put("categoryId", dish.getCategoryId());
    			dishInfo.put("createTime", dish.getCreateTime());
    			dishInfo.put("dishName", dish.getDishName());
    			dishInfo.put("introduction", dish.getIndroduction());
    			dishInfo.put("picture", dish.getPicture());
    			dishInfo.put("other", dish.getOther());
    			dishInfo.put("price", dish.getPrice());
    			dishInfo.put("restaurantId", dish.getRestaurantId());
    			dishInfo.put("score", dish.getScore());
    			dishInfo.put("taste", dish.getTaste());
    			String resturantId=dish.getRestaurantId();
    			Restaurant resturant=restaurantService.selectByPrimaryKey(resturantId);
    			dishInfo.put("restaurantName", resturant.getRestaurantName());
    			dishInfo.put("address", resturant.getAddress());
    			dishInfo.put("pictureSmall", resturant.getPictureSmall());
    			//dishInfo.put("", resturant.getIntroduction())
    			dishInfoList.add(dishInfo);
    		}
    		result.setData(dishInfoList);
    		return result;
    	}
    }
    /**
     * �ϴ����ȣ���ʳ
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value="/",method=RequestMethod.POST)
	@ResponseBody
	public Result Insert(HttpServletRequest request,HttpSession session,@RequestParam(value = "file") MultipartFile fileUpload){
		Result result = new Result();
		logger.info("��ʼ�ύ��ʳ");
		//��ȡ�û���Ϣ
    	Object restaurantId = request.getSession().getAttribute("restaurantId");
//    	if(restaurantId==null){
//    		result.setStatus(Result.FAILED);
//			result.setTipCode("notLogin");
//			result.setTipMsg("�̻�δ��¼");
//			return result;
//    	}
    	//��ȡ�������
    	String dishName=request.getParameter("dishName");
    	String introduction=request.getParameter("introduction");
    	String taste=request.getParameter("taste");
    	String price=request.getParameter("price");

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
    	//String categaryId=request.getParameter("1");
    	String id = UUIDUtils.getUUID();
		String createTime = STDateUtils.getCurrentTime();
    	Dish dish=new Dish();
    	dish.setId(id);
    	dish.setDishName(dishName);
    	dish.setCategoryId("1");
    	dish.setCreateTime(createTime);
    	dish.setIndroduction(introduction);
    	dish.setPicture(pictureResult.getData());
    	dish.setTaste(taste);
    	dish.setPrice(price);
    	dish.setScore("4.0");
    	dish.setRestaurantId("1");

    	if(dishService.insert(dish)!=1){
    		logger.info("����ʧ��");
    		result.setStatus(Result.FAILED);
			result.setTipCode("error");
			result.setTipMsg("�������");
			return result;
    	}
    	result.setData(dish);
		return result;
	}
    /**
     * �ϴ����ȣ���ʳ
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value="/me",method=RequestMethod.POST)
	@ResponseBody
	public Result InsertOne(HttpServletRequest request,HttpSession session,@RequestParam(value = "file") MultipartFile fileUpload){
		Result result = new Result();
		logger.info("��ʼ�ύ��ʳ");
		//��ȡ�û���Ϣ
    	Object restaurantId = request.getSession().getAttribute("restaurantId");
    	if(restaurantId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("�̻�δ��¼");
			return result;
    	}
    	//��ȡ�������
    	String dishName=request.getParameter("dishName");
    	String introduction=request.getParameter("introduction");
    	String taste=request.getParameter("taste");
    	String price=request.getParameter("price");

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
    	//String categaryId=request.getParameter("1");
    	String id = UUIDUtils.getUUID();
		String createTime = STDateUtils.getCurrentTime();
    	Dish dish=new Dish();
    	dish.setId(id);
    	dish.setDishName(dishName);
    	dish.setCategoryId("1");
    	dish.setCreateTime(createTime);
    	dish.setIndroduction(introduction);
    	dish.setPicture(pictureResult.getData());
    	dish.setTaste(taste);
    	dish.setPrice(price);
    	dish.setScore("4.0");
    	dish.setRestaurantId("1");

    	if(dishService.insert(dish)!=1){
    		logger.info("����ʧ��");
    		result.setStatus(Result.FAILED);
			result.setTipCode("error");
			result.setTipMsg("�������");
			return result;
    	}
    	result.setData(dish);
		return result;
	}
    @RequestMapping(value="/{id}/iscollect/",method=RequestMethod.GET)
    @ResponseBody
    public Result isCurrentUserCollect(@PathVariable("id") String dishId,HttpSession session){
    	Result result=new Result();
    	String userId = SessionUtil.getUserId(session);
    	
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("δ��¼,��¼������ղء�");
			return result;
    	}
    	if(dishService.selectByPrimaryKey(dishId)==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notExist");
			result.setTipMsg("��Ʒ������");
			return result;
    	}
    	Collection collection=collectionService.selectByDishAndUser(dishId,userId);
    	if(collection==null){
    		result.setStatus(Result.SUCCESS);
			result.setTipCode("notCollect");
			result.setTipMsg("δ�ղ�");
			return result;
    	}
    	result.setTipCode("collect");
    	result.setData(collection);
		return result;
    	
    }
    /**
     * �ղػ�ȡ���ղ�
     * @param dishId
     * @param session
     * @return
     */
    @RequestMapping(value="/{id}/toggleCollect/",method=RequestMethod.POST)
    @ResponseBody
    public Result toggleCollect(@PathVariable("id") String dishId,HttpSession session){
    	Result result=new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("δ��¼,��¼������ղء�");
			return result;
    	}
    	if(dishService.selectByPrimaryKey(dishId)==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notExist");
			result.setTipMsg("��Ʒ������");
			return result;
    	}
    	Collection collection=collectionService.selectByDishAndUser(dishId,userId);
    	if(collection==null){
    		Collection newCollection=new Collection();
    		String id = UUIDUtils.getUUID();
    		String createTime = STDateUtils.getCurrentTime();
    		newCollection.setId(id);
    		newCollection.setCreateTime(createTime);
    		newCollection.setDishId(dishId);
    		newCollection.setUserId(userId);
    		if(collectionService.insert(newCollection)!=1){
    			result.setStatus(Result.FAILED);
    			result.setTipCode("CollectFail");
    			result.setTipMsg("�ղ�ʧ��");
    			return result;
    		}
    		result.setStatus(Result.SUCCESS);
			result.setTipCode("Collect");
			result.setTipMsg("�ղسɹ�");
			result.setData(newCollection);
			return result;
    	}else{
    		if(collectionService.delete(collection)!=1){
    			result.setStatus(Result.FAILED);
    			result.setTipCode("unCollectFail");
    			result.setTipMsg("ȡ��ʧ��");
    			return result;
    		}
    		result.setStatus(Result.SUCCESS);
			result.setTipCode("unCollect");
			result.setTipMsg("ȡ���ɹ�");
			result.setData(collection);
			return result;
    	}
    }
    @RequestMapping(value="/delete/{id}",method=RequestMethod.GET)
    @ResponseBody
    public Result delete(@PathVariable("id") String id){
    	Result result=new Result();
    	Dish dish=dishService.selectByPrimaryKey(id);
    	if(dish==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("NotExsits");
			result.setTipMsg("��Ʒ������");
			return result;
    	}
    	if(dishService.delete(dish)!=1){
    		result.setStatus(Result.FAILED);
			result.setTipCode("deleteFaile");
			result.setTipMsg("ɾ��ʧ��");
			return result;
    	}
    	result.setData(dish);
		return result;
    }
	
}
