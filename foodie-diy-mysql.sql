


##select `username`,`salt` from `user`  where phone = "13052345227";
##CREATE DATABASE foodie_sql;
##USE foodie_sql;


CREATE TABLE `user`(
  `id` 		VARCHAR(50) PRIMARY KEY, 	##   AUTO_INCREMENT
  `username` 	VARCHAR(50) ,		##		
  `phone` 	VARCHAR(50),
  `nickname`	VARCHAR(50),
  `password` 	VARCHAR(50),##用户密码
  `avator`	VARCHAR(50),
  `createTime`	VARCHAR(50),
  `salt`	VARCHAR(50),
  `age`		INT,
  `sex` 	VARCHAR(4) DEFAULT '男',
  `city`	VARCHAR(50),
  `score`	INT
); 
INSERT INTO `user`(`id`,`username`,`phone`,`nickname`,`password`,`avator`,`createTime`,`salt`,`age`,`city`,`score`) 
VALUES("1",'张三1',"13052345228","nickname待填写","123456","avator待填写","2016","salt待填写",18,"西安",100);


CREATE TABLE `fans`(
  `id` 		VARCHAR(50) PRIMARY KEY ,	##
  `userId`  	VARCHAR(50),			## 外键	
  `fansId`	VARCHAR(50),			##待定是否是 外键
  `followTime`	VARCHAR(50),
   CONSTRAINT `fk_fans_user` FOREIGN KEY (`userId`) REFERENCES `user`(`id`)   #通过外键创建链接
); 
INSERT INTO `fans`(`id`,`userId`,`fansId`,`followTime`) 
VALUES("1",'1',"99","2016");

CREATE TABLE `works`(
  `id` 			VARCHAR(50) PRIMARY KEY ,##
  `dishName`		VARCHAR(50) NOT NULL,##
  `introduction`	VARCHAR(200),
  `userId`		VARCHAR(50),		 ## 外键		
  `createTime`		VARCHAR(50),	
  `themeId`		VARCHAR(50),		##待定是否是 外键
  `thumbnail`		VARCHAR(200),	##图片
  `viewCount`		INT,
  `commentCount`	INT,
  `likeCount`		INT,
  CONSTRAINT `fk_works_user` FOREIGN KEY (`userId`) REFERENCES `user`(`id`)   #通过外键创建链接
);
INSERT INTO `works`(`id`,`dishName`,`introduction`,`userId`,`createTime`,`themeId`,`thumbnail`,`viewCount`,`commentCount`,`likeCount`) 
VALUES("1",'dishName很可能是通过外键',"introduction哈哈","1","2016","99","",66,66,66);


CREATE TABLE `like`(
  `id` 		VARCHAR(50) PRIMARY KEY ,##
  `userId`	VARCHAR(50),		 ## 外键  
  `worksId`	VARCHAR(50), 		 ## 外键  
  `createTime`	VARCHAR(50),
  CONSTRAINT `fk_like_user`  FOREIGN KEY (`userId`)  REFERENCES `user`(`id`),
  CONSTRAINT `fk_like_works` FOREIGN KEY (`worksId`) REFERENCES `works`(`id`) 
); 
INSERT INTO `like`(`id`,`userId`,`worksId`,`createTime`) 
VALUES("1",'1',"1","2016");

## 这个表 没有进行xml映射 ！！！
CREATE TABLE `comments`(
  `id` 		VARCHAR(50) PRIMARY KEY ,##
  `content`	VARCHAR(200),
  `userId`	VARCHAR(50),		 ## 外键  
  `worksId`	VARCHAR(50), 		 ## 外键   
  `time`	VARCHAR(50),  
  `parent_id`	VARCHAR(50),  		 ##待定是否是 外键
  `comment_time` VARCHAR(50),
  CONSTRAINT `fk_comments_user`  FOREIGN KEY (`userId`)  REFERENCES `user`(`id`),
  CONSTRAINT `fk_comments_works` FOREIGN KEY (`worksId`) REFERENCES `works`(`id`) 
); 
INSERT INTO `comments`(`id`,`content`,`userId`,`worksId`,`time`,`parent_id`,`comment_time`) 
VALUES("1",'content哈哈',"1","1","2016","99","2019");

CREATE TABLE `worksComment`(
  `id` 		VARCHAR(50) PRIMARY KEY ,##
  `worksId`	VARCHAR(50), 		 ## 外键   
  `content`	VARCHAR(200),
  `createTime`	VARCHAR(50),
  `userId`	VARCHAR(50),		 ## 外键  
  `parentId`	VARCHAR(50),		##待定是否是 外键
  CONSTRAINT `fk_worksComment_user`  FOREIGN KEY (`userId`)  REFERENCES `user`(`id`),
  CONSTRAINT `fk_worksComment_works` FOREIGN KEY (`worksId`) REFERENCES `works`(`id`) 
  
); 

  INSERT INTO `workscomment`(`id`,`worksId`,`content`,`createTime`,`userId`,`parentId`) VALUES ('2','1','content哈哈','2017','1','99');
  


CREATE TABLE `recipe`(
  `id` 			VARCHAR(50) PRIMARY KEY ,##
  `name`  		VARCHAR(50),
  `introduction`	VARCHAR(200),
  `userId`		VARCHAR(50),		 ## 外键 
  `createTime`		VARCHAR(50),	
  `tips`		VARCHAR(50),
  `story`		VARCHAR(50),
  `time` 		VARCHAR(50),
  `difficult`		VARCHAR(50),
  `viewCount`		INT,
  `thumbnail`		VARCHAR(200),
  `score`		VARCHAR(50),
  `collectionCount`	INT,		
  CONSTRAINT `fk_recipe_user`  FOREIGN KEY (`userId`)  REFERENCES `user`(`id`)
); 
INSERT INTO `recipe`(`id`,`name`,`introduction`,`userId`,`createTime`,`tips`,`time`,`difficult`,`viewCount`,`thumbnail`,`score`,`collectionCount`) 
VALUES("1",'recipe表的name待写哈哈',"introduction待写哈哈","1","2016","tips待写","2016","difficult待写哈哈",66,"","99",66);



## ----------------------2



CREATE TABLE `ingredient`(
  `id` 			VARCHAR(50) PRIMARY KEY ,##
  `name`  		VARCHAR(50),
  `introduction`	VARCHAR(200)	
); 
INSERT INTO `ingredient`(`id`,`name`,`introduction` ) 
VALUES("1",'ingredient-name待写哈哈',"ingredient-introduction待写哈哈");
CREATE TABLE `amount`(
  `id` 		 VARCHAR(50) PRIMARY KEY ,##
  `recipeId`  	 VARCHAR(50),		 ## 外键 
  `ingredientId` VARCHAR(50),		 ## 外键 	   	 
  `amount`	 VARCHAR(50),
  CONSTRAINT `fk_amount_recipe`     FOREIGN KEY (`recipeId`)  	 REFERENCES `recipe`(`id`),
  CONSTRAINT `fk_amount_ingredient` FOREIGN KEY (`ingredientId`) REFERENCES `ingredient`(`id`) 	
); 
INSERT INTO `amount`(`id`,`recipeId`,`ingredientId`,`amount` ) 
VALUES("1",'1',"1","amount待写");

CREATE TABLE `step`(
  `id` 		 VARCHAR(50) PRIMARY KEY ,##
  `recipeId`  	 VARCHAR(50),		 ## 外键   
  `imageUrl`	 VARCHAR(200),
  `introduction` VARCHAR(200),
  `order`  	 VARCHAR(50),
  CONSTRAINT `fk_step_recipe` FOREIGN KEY (`recipeId`) REFERENCES `recipe`(`id`)	
); 
INSERT INTO `step`(`id`,`recipeId`,`imageUrl`,`introduction`,`order` ) 
VALUES("1",'1',"imageUrl待写","step-introduction待写","step-order待写");

CREATE TABLE `recipeComment`(
  `id` 		VARCHAR(50) PRIMARY KEY ,##
  `recipeId`	VARCHAR(50),		 ## 外键   
  `content`	VARCHAR(200),
  `createTime`	VARCHAR(50),	  
  `userId`	VARCHAR(50),		 ## 外键   	 	
  `parentId`	VARCHAR(50),		##待定是否是 外键
  CONSTRAINT `fk_recipeComment_recipe` FOREIGN KEY (`recipeId`)  REFERENCES `recipe`(`id`),
  CONSTRAINT `fk_recipeComment_user`   FOREIGN KEY (`userId`)    REFERENCES `user`(`id`)
); 
INSERT INTO `recipeComment`(`id`,`recipeId`,`content`,`createTime`,`userId`,`parentId` ) 
VALUES("1",'1',"recipeComment-content待写","2015","1","99");

## ----------------------3

CREATE TABLE `restaurant`(
  `id` 			VARCHAR(50) PRIMARY KEY ,##
  `accountName`  	VARCHAR(50),
  `accountPassword`	VARCHAR(50),
  `restaurantName`	VARCHAR(200),
  `address`  		VARCHAR(200),	
  `introduction`	VARCHAR(200),
  `restaurantType`	VARCHAR(50),
  `pictureLarge`	VARCHAR(200),
  `pictureSmall`	VARCHAR(200),
  `status`		VARCHAR(50),
  `score`		VARCHAR(50),
  `phone`		VARCHAR(50),
  `serveTime`		VARCHAR(50),
  `averagePrice`	VARCHAR(50),	
  `registerTime`	VARCHAR(50),
  `keyWord`		VARCHAR(50),
  `other`		VARCHAR(200)
); 
INSERT INTO `restaurant`(`id`,`accountName`,`accountPassword`,`restaurantName`,`address`,`introduction`,`restaurantType`,`pictureLarge`,`pictureSmall`,`score`,`phone`,`serveTime`,`averagePrice`,`registerTime`,`keyWord`,`other` ) 
VALUES
("1",'accountName待写',"accountPassword待写","木易餐馆","中国上海市徐汇区田州路127号","很赞很赞呢","restaurantType","","","99","13052345227","09:00-18:00","66.60","2000","keyWord待写","other待写");



 
##SELECT id, restaurantId, categoryId, dishName, introduction, picture, price, score, other, createTime, taste FROM `dish`  ORDER BY createTime DESC;
##SELECT*from `dish`;

CREATE TABLE `dish`(
  `id` 			VARCHAR(50) PRIMARY KEY ,##
  `restaurantId`	VARCHAR(50),		 ## 外键  
  `categoryId`	 	VARCHAR(50),		 ##待定是否是 外键
  `dishName`		VARCHAR(50),
  `indroduction`		VARCHAR(200),
  `picture`		VARCHAR(200),
  `price`		VARCHAR(50),
  `score`		VARCHAR(50),	
  `other`		VARCHAR(100),
  `createTime`		VARCHAR(50),
  `taste`		VARCHAR(50),
  CONSTRAINT `fk_dish_restaurant`   FOREIGN KEY (`restaurantId`) REFERENCES `restaurant`(`id`)
); 
INSERT INTO `dish`(`id`,`restaurantId`,`categoryId`,`dishName`,`indroduction`,`picture`,`price`,`score`,`other`,`createTime`,`taste`) 
VALUES
("1",'1',"99","凉皮炒蛋","味道鲜美","D:\Program Files (x86)\项目用 图片\pucca-pseudoroom\ic_12.png","12.00","100","other-null","2000","口味：taste待写");
##("2",'1',"99","黄蚬子","说说这道东北菜\"不讲究\"，确实不用考究味道鲜美，随性而成，混的素的，菜的肉的，一锅端出，大火炖煮。等到揭开盖子，保证香味扑鼻，入味好吃。",
##	"ic_12.png","12.00","4.0","other-null","2000","taste待写");



CREATE TABLE `collection`(
  `id` 		VARCHAR(50) PRIMARY KEY ,##
  `userId`  	VARCHAR(50),		 ## 外键  
  `dishId`	VARCHAR(50),		 ## 外键   
  `createTime`	VARCHAR(50),
   CONSTRAINT `fk_collection_user` FOREIGN KEY (`userId`)  REFERENCES `user`(`id`),
   CONSTRAINT `fk_collection_dish` FOREIGN KEY (`dishId`)  REFERENCES `dish`(`id`)
);
INSERT INTO `collection`(`id`,`userId`,`dishId`,`createTime`) 
VALUES("1",'1',"1","1999");


## ----------------------4 以下是 独立的表：=----------------------------------------------------------------

CREATE TABLE `merchant`(
  `id` 			VARCHAR(50) PRIMARY KEY ,##
  `loginName`		VARCHAR(50),
  `loginPassword`	VARCHAR(50),
  `address`		VARCHAR(100),
  `desc`		VARCHAR(200),
  `picture`		VARCHAR(200),
  `registerTime`	VARCHAR(50),
  `legalName`		VARCHAR(50),
  `status` 		INT,
  `score`		VARCHAR(50),
  `type`		VARCHAR(50)
    
); 
INSERT INTO `merchant`(`id`,`loginName`,`loginPassword`,`address`,`desc`,`picture`,`registerTime`,`status`,`score`,`type`) 
VALUES("1",'萝卜斯',"123456","科技路0000号","暂时没有描述哦","","2005", 1,"99","type待写");







DROP TABLE `fans`;
DROP TABLE `like`;
DROP TABLE `comments`;
DROP TABLE `worksComment`;
DROP TABLE `amount`;
DROP TABLE `step`;
DROP TABLE `recipeComment`;
DROP TABLE `collection`;
DROP TABLE `merchant`; 
DROP TABLE `dish`;
DROP TABLE `ingredient`;
DROP TABLE `recipe`;
DROP TABLE `restaurant`;
DROP TABLE `works`;
DROP TABLE `user`;


SELECT * FROM `user`;
SELECT * FROM `fans`;
SELECT * FROM `works`;
SELECT * FROM `like`;
SELECT * FROM `comments`;
SELECT * FROM `worksComment`;
SELECT * FROM `recipe`;
SELECT * FROM `ingredient`;
SELECT * FROM `amount`;
SELECT * FROM `step`;
SELECT * FROM `recipeComment`;
SELECT * FROM `restaurant`;
SELECT * FROM `dish`;
SELECT * FROM `collection`;
SELECT * FROM `merchant`; 





/*
  ALTER TABLE `fans` DROP CONSTRAINT `fk_fans_user`;
  ALTER TABLE `works` DROP CONSTRAINT `fk_works_user`;
  ALTER TABLE `like` DROP CONSTRAINT `fk_like_user`;
  ALTER TABLE `like` DROP CONSTRAINT `fk_like_works`;
  ALTER TABLE `comments` DROP CONSTRAINT `fk_comments_user`;
  ALTER TABLE `comments` DROP CONSTRAINT `fk_comments_works`;
  ALTER TABLE `worksComment` DROP CONSTRAINT `fk_worksComment_user`;
  ALTER TABLE `worksComment` DROP CONSTRAINT `fk_worksComment_works`;
  ALTER TABLE `recipe` DROP CONSTRAINT `fk_recipe_user`;
  ALTER TABLE `amount` DROP CONSTRAINT `fk_amount_recipe`;
  ALTER TABLE `amount` DROP CONSTRAINT `fk_amount_ingredient`;
  ALTER TABLE `step` DROP CONSTRAINT `fk_step_recipe`;
  ALTER TABLE `recipeComment` DROP CONSTRAINT `fk_recipeComment_recipe`;
  ALTER TABLE `recipeComment` DROP CONSTRAINT `fk_recipeComment_user`;
  ALTER TABLE `dish` DROP CONSTRAINT `fk_dish_restaurant`;
  ALTER TABLE `collection` DROP CONSTRAINT `fk_collection_user`;
  ALTER TABLE `collection` DROP CONSTRAINT `fk_collection_dish`;
*/
