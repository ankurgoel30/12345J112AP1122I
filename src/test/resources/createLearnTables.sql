DROP TABLE IF EXISTS `mdl_package_master`;

CREATE TABLE `mdl_package_master` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `is_premium` smallint(2) DEFAULT '0',
  `description` text,
  `img_thumb` varchar(255) DEFAULT NULL,
  `img_large` varchar(255) DEFAULT NULL,
  `is_active` int(2) NOT NULL DEFAULT '0',
  `price_user_year` decimal(10,2) NOT NULL DEFAULT '0.00',
  `display_order` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

/*Data for the table `mdl_package_master` */

INSERT INTO `mdl_package_master` (`id`,`category_id`,`name`,`is_premium`,`description`,`img_thumb`,`img_large`,`is_active`,`price_user_year`,`display_order`) VALUES (2,NULL,'World',0,NULL,NULL,NULL,0,'0.00',NULL),(3,NULL,'Vado',0,NULL,NULL,NULL,0,'0.00',NULL),(5,NULL,'Vado Courses',0,NULL,NULL,NULL,0,'0.00',NULL),(9,NULL,'Campus ',0,NULL,NULL,NULL,0,'0.00',NULL),(12,NULL,'ThinkHR Safety',0,NULL,NULL,NULL,0,'0.00',NULL),(13,NULL,'AB 1825 No Restrictions',0,NULL,NULL,NULL,0,'0.00',NULL),(14,NULL,'Course Testing',0,NULL,NULL,NULL,0,'0.00',NULL),(15,NULL,'Safety Test',0,NULL,NULL,NULL,0,'0.00',NULL),(19,NULL,'ThinkHR Private',0,NULL,NULL,NULL,0,'0.00',NULL),(20,NULL,'Arindam PR',0,NULL,NULL,NULL,0,'0.00',NULL),(22,NULL,'Bullying',0,NULL,NULL,NULL,0,'0.00',NULL),(24,NULL,'OSHA 10 Hour',0,NULL,NULL,NULL,0,'0.00',NULL),(26,NULL,'Ubique Testin',0,NULL,NULL,NULL,0,'0.00',NULL),(27,NULL,'DZS',0,NULL,NULL,NULL,0,'0.00',NULL),(28,NULL,'DZS',0,NULL,NULL,NULL,0,'0.00',NULL),(29,NULL,'package',0,NULL,NULL,NULL,0,'0.00',NULL),(30,NULL,'Hospitality',0,NULL,NULL,NULL,0,'0.00',NULL),(31,NULL,'Hospitality',0,NULL,NULL,NULL,0,'0.00',NULL),(32,NULL,'All',0,NULL,NULL,NULL,0,'0.00',NULL),(33,NULL,'Ab 1825 part3',0,NULL,NULL,NULL,0,'0.00',NULL),(34,NULL,'inmarsat',0,NULL,NULL,NULL,0,'0.00',NULL),(35,NULL,'cash tyme',0,NULL,NULL,NULL,0,'0.00',NULL),(37,NULL,'SHS Quiz',0,NULL,NULL,NULL,0,'0.00',NULL),(38,NULL,'ELS',0,NULL,NULL,NULL,0,'0.00',NULL),(39,NULL,'ww flight',0,NULL,NULL,NULL,0,'0.00',NULL),(40,NULL,'BLR Courses',0,NULL,NULL,NULL,0,'0.00',NULL),(41,NULL,'SpanishAB25',0,NULL,NULL,NULL,0,'0.00',NULL),(42,NULL,'Healthcare',0,NULL,NULL,NULL,0,'0.00',NULL),(43,NULL,'Hospitality',0,NULL,NULL,NULL,0,'0.00',NULL),(44,NULL,'NFL Players',0,NULL,NULL,NULL,0,'0.00',NULL),(45,NULL,'perf & skills',0,NULL,NULL,NULL,0,'0.00',NULL),(46,NULL,'perf & skills',0,NULL,NULL,NULL,0,'0.00',NULL),(47,NULL,'Sales',0,NULL,NULL,NULL,0,'0.00',NULL),(49,NULL,'Safe Driving',0,NULL,NULL,NULL,0,'0.00',NULL),(50,NULL,'Control Test',0,NULL,NULL,NULL,0,'0.00',NULL),(51,NULL,'qumu',0,NULL,NULL,NULL,0,'0.00',NULL),(52,NULL,'T and M ',0,NULL,NULL,NULL,0,'0.00',NULL),(53,NULL,'eig',0,NULL,NULL,NULL,0,'0.00',NULL),(54,NULL,'kirbyville',0,NULL,NULL,NULL,0,'0.00',NULL),(55,NULL,'Exceltox',0,NULL,NULL,NULL,0,'0.00',NULL),(56,NULL,'Sinclair',0,NULL,NULL,NULL,0,'0.00',NULL),(57,NULL,'FCPA',0,NULL,NULL,NULL,0,'0.00',NULL),(58,NULL,'GPI',0,NULL,NULL,NULL,0,'0.00',NULL),(59,NULL,'Harassment',0,NULL,NULL,NULL,0,'0.00',NULL),(60,NULL,'Berends',0,NULL,NULL,NULL,0,'0.00',NULL),(61,NULL,'360i',0,NULL,NULL,NULL,0,'0.00',NULL),(62,NULL,'SS - Arindam',0,NULL,NULL,NULL,0,'0.00',NULL),(64,NULL,'',0,NULL,NULL,NULL,0,'0.00',NULL),(65,NULL,'',0,NULL,NULL,NULL,0,'0.00',NULL),(66,NULL,'',0,NULL,NULL,NULL,0,'0.00',NULL),(67,NULL,'Skillsoft',0,NULL,NULL,NULL,0,'0.00',NULL),(70,NULL,'5 Tips',0,NULL,NULL,NULL,0,'0.00',NULL),(71,NULL,'5 Tips',0,NULL,NULL,NULL,0,'0.00',NULL),(72,NULL,'Essentials',1,NULL,NULL,NULL,0,'0.00',NULL),(73,NULL,'Expert',1,NULL,NULL,NULL,0,'0.00',NULL),(74,NULL,'Test Package',0,NULL,NULL,NULL,0,'0.00',NULL),(75,195,'Customer Satisfaction',1,'Train on good service principles and building rapport with customers, both in person and over the phone.','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-CustomerService.png',NULL,0,'5.00',1),(76,175,'Active Shooter',1,'Prepare your entire company with these expert trainings designed to explain warning signs and survival tactics.','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-ActiveShooter.png',NULL,1,'10.00',2),(77,171,'Cybersecurity',1,'Get practical steps to avoid the growing danger of cyber attacks, ransomware, viruses, malware and more.','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-CyberSecurity.png',NULL,1,'30.00',3),(78,177,'People Operations',1,'Stay on top of benefits issues, leaves of absence, wages and compliance concerns. ','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-PeopleOperations.png',NULL,1,'20.00',4),(79,192,'Talent Management',1,'Train on employee onboarding and engagement to diversity and creating conflict-free environments.','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-HRandTalentManagement.png',NULL,1,'10.00',5),(80,180,'Business Risk Management',1,'Everything your employees need to know about business law and ethics, codes of conduct and risk management. \n','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-BusinessRiskManagement.png',NULL,1,'10.00',6),(81,173,'Project Management',1,'Acquire one of the most sought-after skills in the business world—successfully managing a project to completion.','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-ProjectManagement.png',NULL,1,'15.00',7),(82,162,'Leadership Development',1,'Learn everything from managing new managers and motivating teams to implementing change and building coaching skills.','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-LeadershipDevelopment.png',NULL,1,'20.00',8),(83,181,'Microsoft Office',1,'Make your team experts on the essential office tools including Excel, Word, Outlook and PowerPoint.','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-MicrosoftOffice.png',NULL,1,'20.00',9),(84,167,'Desktop Publishing',1,'Get the foundational design training on Adobe Illustrator, Adobe InDesign and Adobe Photoshop.','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-DesktopPublishing.png',NULL,1,'15.00',10),(85,166,'Professional Effectiveness',1,'Master soft skills such as building relationships, managing time and communicating effectively. ','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-ProfessionalEffectiveness.png',NULL,1,'10.00',11),(86,201,'Communication Methodology',1,'Train your team on communication across cultures, negotiating successfully, and building trust.','https://s3-us-west-2.amazonaws.com/com.thinkhr/public/packages/thumbs/Bundle-Communcation.png',NULL,1,'15.00',12),(87,NULL,'Enterprise',1,NULL,NULL,NULL,0,'0.00',NULL),(88,NULL,'Skill3',0,NULL,NULL,NULL,0,'0.00',NULL),(89,NULL,'Test Course',0,NULL,NULL,NULL,0,'0.00',NULL),(90,NULL,'',0,NULL,NULL,NULL,0,'0.00',NULL),(91,NULL,'CompliGo',0,NULL,NULL,NULL,0,'0.00',NULL),(92,NULL,'Four Horsemen',0,NULL,NULL,NULL,0,'0.00',NULL),(93,NULL,'',0,NULL,NULL,NULL,0,'0.00',NULL),(94,NULL,'NCHRA Base',0,NULL,NULL,NULL,1,'0.00',NULL),(95,NULL,'PrimePay Base',0,NULL,NULL,NULL,0,'0.00',NULL);

DROP TABLE IF EXISTS `mdl_company`;

CREATE TABLE `mdl_company` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `company_name` varchar(250) NOT NULL DEFAULT '',
  `thrclientid` int(20) DEFAULT NULL,
  `company_key` varchar(50) NOT NULL DEFAULT '',
  `createdby` bigint(20) NOT NULL,
  `address` char(250) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `zip` varchar(100) DEFAULT NULL,
  `phone` char(12) DEFAULT NULL,
  `company_type` char(100) DEFAULT NULL,
  `employee_count` int(10) DEFAULT NULL,
  `timecreated` bigint(10) NOT NULL DEFAULT '0',
  `timemodified` bigint(10) NOT NULL DEFAULT '0',
  `license` bigint(20) DEFAULT '0' COMMENT 'number of license against a company',
  `enrollmentstart` bigint(10) DEFAULT NULL,
  `enrollmentend` bigint(10) DEFAULT NULL,
  `suspended` tinyint(2) DEFAULT '0',
  `partnerid` varchar(12) DEFAULT NULL COMMENT 'thr_partnerid',
  `logo` varchar(256) DEFAULT '',
  `upgraderequired` tinyint(4) DEFAULT '0' COMMENT 'Flag to indicate if this company needs learn uprade',
  `externalapicompany` int(2) NOT NULL DEFAULT '0' COMMENT '0 --> No, 1 --> SSO ENI API, 2 --> XXX',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `mdl_package_company`;

CREATE TABLE `mdl_package_company` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `packageid` bigint(10) NOT NULL,
  `companyid` bigint(10) NOT NULL,
  PRIMARY KEY (`id`)
);


DROP TABLE IF EXISTS `mdl_user`;

CREATE TABLE `mdl_user` (                                                                                                
            `id` bigint(10) NOT NULL AUTO_INCREMENT,                                                                               
            `thrcontactid` int(20) DEFAULT NULL,                                                                                   
            `deleted` tinyint(1) NOT NULL DEFAULT '0',                                                                             
            `username` varchar(100) NOT NULL DEFAULT '',                                                   
            `password` varchar(255) NOT NULL DEFAULT '',                                                   
            `firstname` varchar(100) NOT NULL DEFAULT '',                                                  
            `lastname` varchar(100) NOT NULL DEFAULT '',                                                   
            `email` varchar(100) NOT NULL DEFAULT '',                                                      
            `phone1` varchar(20) NOT NULL DEFAULT '',                                                      
            `companyid` bigint(20) DEFAULT NULL,                                                                                   
            `jobtitle` varchar(200) DEFAULT NULL,                                                          
            `bounced` int(11) DEFAULT '0',                                                                                         
            `blockedaccount` int(10) NOT NULL DEFAULT '0',                                                                         
            PRIMARY KEY (`id`)                                                                                      
);

DROP TABLE IF EXISTS `mdl_role_assignments`;

CREATE TABLE `mdl_role_assignments` (                                                                                              
                        `id` bigint(10) NOT NULL AUTO_INCREMENT,                                                                                         
                        `roleid` bigint(10) NOT NULL DEFAULT '0',                                                                                        
                        `contextid` bigint(10) NOT NULL DEFAULT '0',                                                                                     
                        `userid` bigint(10) NOT NULL DEFAULT '0',                                                                                        
                        `timemodified` bigint(10) NOT NULL DEFAULT '0',                                                                                  
                        `modifierid` bigint(10) NOT NULL DEFAULT '0',                                                                                    
                        `component` varchar(100) NOT NULL DEFAULT '',                                                            
                        `itemid` bigint(10) NOT NULL DEFAULT '0',                                                                                        
                        `sortorder` bigint(10) NOT NULL DEFAULT '0',                                                                                     
                        PRIMARY KEY (`id`)                                                                                                              
 );