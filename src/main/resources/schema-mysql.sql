create table if not exists broken
(
	id BIGINT(20) NOT NULL AUTO_INCREMENT,
	line_text VARCHAR(300) NULL,
	ref_id INT(20) NOT NULL,
	rec_line BIGINT NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE = MyISAM DEFAULT CHARSET = utf8 DEFAULT COLLATE = utf8_general_ci;

create table deals
(
	id BIGINT(20) NOT NULL AUTO_INCREMENT,
	deal_id varchar(200) not null,
	from_currency_iso varchar(3) not null,
	to_currency_iso varchar(3) not null,
	deal_timestamp timestamp default CURRENT_TIMESTAMP not null,
	ordering_amount double null,
	ref_id int not null,
	PRIMARY KEY (`id`),
	UNIQUE KEY `deal_id` (`deal_id`,`ref_id`)
) ENGINE = MyISAM DEFAULT CHARSET = utf8 DEFAULT COLLATE = utf8_general_ci;

create table fileinfo
(
	ref_id int auto_increment
		primary key,
	file_name varchar(255) not null,
	check_sum varchar(255) null,
	constraint file_refs_ref_id_uindex
	unique (ref_id),
	constraint file_refs_file_name_uindex
	unique (file_name)
)
;
create table statistics
(
	iso_code varchar(3) not null
		primary key,
	count bigint default '0' not null
)
;
ALTER TABLE broken ENGINE = MyISAM;
ALTER TABLE deals ENGINE = MyISAM;
ALTER TABLE fileinfo ENGINE = MyISAM;
ALTER TABLE statistics ENGINE = MyISAM;

SET GLOBAL max_allowed_packet = 9377953;
SET bulk_insert_buffer_size= 1024 * 1024 * 256;
SET GLOBAL storage_engine=MYISAM;

