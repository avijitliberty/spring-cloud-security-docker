drop table if exists `note-db`.`note`;

CREATE TABLE `note-db`.`note` (
	`note_id` 			integer not null auto_increment, 
	`created_by` 		varchar(255), 
	`created_date` 		datetime, 
	`last_modified_by` 	varchar(255), 
	`last_modified_date`datetime, 
	`body` 				varchar(255), 
	`subject`        	varchar(255), 
	`topic`          	varchar(255), 
	PRIMARY KEY (note_id)
)ENGINE=InnoDB DEFAULT CHARSET=latin1;