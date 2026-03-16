INSERT INTO assets (id,name) VALUES
(1,'Gold'),
(2,'Diamonds'),
(3,'Loyalty')
ON CONFLICT (id) DO NOTHING;

INSERT INTO system_accounts (id,account_type,amount) VALUES
(1,'Revenue',0),
(2,'Treasury',1000)
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id,name) VALUES
(1,'RAM'),
(2,'SHYAM'),
(3,'MOHAN'),
(4,'GOPAL'),
(5,'ARJUN'),
(6,'VIKRAM'),
(7,'SURESH'),
(8,'RAJESH'),
(9,'DINESH'),
(10,'MAHESH')
ON CONFLICT (id) DO NOTHING;


INSERT INTO user_accounts (id,user_id,assert_id,amount) VALUES
(1,1,1,0),
(2,1,2,0),
(3,1,3,0),
(4,2,1,0),
(5,2,2,0),
(6,2,3,0),
(7,3,1,0),
(8,3,2,0),
(9,3,3,0),
(10,4,1,0),
(11,4,2,0),
(12,4,3,0),
(13,5,1,0),
(14,5,2,0),
(15,5,3,0),
(16,6,1,0),
(17,6,2,0),
(18,6,3,0),
(19,7,1,0),
(20,7,2,0),
(21,7,3,0),
(22,8,1,0),
(23,8,2,0),
(24,8,3,0),
(25,9,1,0),
(26,9,2,0),
(27,9,3,0),
(28,10,1,0),
(29,10,2,0),
(30,10,3,0)
ON CONFLICT (id) DO NOTHING;