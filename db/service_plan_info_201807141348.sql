INSERT INTO psdb.service_plan_info (id,plan_type,price,country_id,rules,price_update_completed) VALUES 
(1,'basic',4,3,NULL,0),
(2,'normal',5,3,'new_customer_rule',0),
(3,'premium',6,3,'new_customer_rule',0),

(4,'basic',2.99,1,NULL,0),
(5,'normal',4.99,1,'new_customer_rule',0),
(6,'premium',6.99,1,'new_customer_rule,loyal_US_customer',0),

(7,'basic',200,2,'new_customer_rule',0),
(8,'normal',499,2,'new_customer_rule',0),
(9,'premium',699,2,'new_customer_rule',0)
;


INSERT INTO psdb.service_plan_info (id,plan_type,price,country_id,rules,price_update_completed) VALUES 
(10,'basic',100,5,NULL,0),
(11,'normal',200,5,NULL,0),
(12,'premium',300,5,NULL,0),

(13,'basic',4.99,4,NULL,0),
(14,'normal',8.99,4,NULL,0),
(15,'premium',12.99,4,NULL,0),

(16,'basic',4,6,NULL,0),
(17,'normal',5,6,NULL,0),
(18,'premium',6,6,NULL,0)
;