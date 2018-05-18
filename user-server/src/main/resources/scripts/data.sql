INSERT INTO `oauth2-db`.`user` (`user_id`,`active`,`email`,`last_name`,`name`,`password`)
VALUES (1,1,'bob@gmail.com','s','bob','$2a$10$lbnd2NTRf0qqGYsRwq6wkulJ0.em54y4CXZ1P.mGtneRqsP.1y45y'),
       (2,1,'jim@gmail.com','s','jim','$2a$10$9BS87iDLAPgbLTEfHXyBsOBPtgtFqLa1GGIXr8OqSZpVikVY8hbWq'),
       (3,1,'ted@gmail.com','s','ted','$2a$10$9XbISGUyPqI1Yp8gYHn4zeSGyiJIua8PyVWCT0mM2X0uOlYSFiSzm');

INSERT INTO `oauth2-db`.`role` (`role_id`,`role`)
VALUES (1,'ADMIN'), (2, 'USER');
    
INSERT INTO `oauth2-db`.`user_role` (`user_id`,`role_id`)
VALUES (1,2),(2,1),(3,1),(3,2);