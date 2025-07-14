UPDATE xmall.t_order t
SET t.create_time = '2025-06-06 22:30:33'
WHERE t.id = 7;

UPDATE xmall.t_order t
SET t.create_time = '2025-06-06 14:22:08'
WHERE t.id = 5;

UPDATE xmall.t_order t
SET t.create_time = '2025-06-06 11:31:32'
WHERE t.id = 4;

UPDATE xmall.t_order t
SET t.create_time = '2025-01-21 22:52:25'
WHERE t.id = 8;

UPDATE xmall.t_order t
SET t.create_time = '2025-03-25 14:04:44'
WHERE t.id = 11;

UPDATE xmall.t_order t
SET t.create_time = '2025-02-16 21:03:37'
WHERE t.id = 9;

UPDATE xmall.t_order t
SET t.create_time = '2025-03-25 16:06:00'
WHERE t.id = 12;

UPDATE xmall.t_order t
SET t.create_time = '2025-06-02 11:55:30'
WHERE t.id = 1;

UPDATE xmall.t_order t
SET t.create_time = '2025-06-06 14:23:38'
WHERE t.id = 6;

UPDATE xmall.t_order t
SET t.create_time = '2025-02-16 21:04:47'
WHERE t.id = 10;

UPDATE xmall.t_message t
SET t.time = '2025-06-06 22:32:45'
WHERE t.id = 26;

UPDATE xmall.t_message t
SET t.time = '2025-05-24 17:13:45'
WHERE t.id = 23;

UPDATE xmall.t_message t
SET t.time = '2025-05-22 10:49:48'
WHERE t.id = 21;

UPDATE xmall.t_message t
SET t.time = '2025-05-24 17:13:58'
WHERE t.id = 24;

UPDATE xmall.t_message t
SET t.time = '2025-05-22 10:48:55'
WHERE t.id = 20;

UPDATE xmall.t_message t
SET t.time = '2025-06-06 12:41:30'
WHERE t.id = 25;


UPDATE xmall.t_log
SET time = CONCAT('2025', SUBSTRING(time, 5))
WHERE time LIKE '202_-%';

