CREATE TABLE people (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  address varchar(255),
  age int
);

CREATE TABLE `venues` (
  `ven_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `price` int(11) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ven_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE `events` (
  `event_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `venue_id` int(11) unsigned NOT NULL,
  `event_name` varchar(255) NOT NULL,
  PRIMARY KEY (`event_id`),
  KEY `venue_id` (`venue_id`),
  CONSTRAINT `items_ibfk_1` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`ven_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
