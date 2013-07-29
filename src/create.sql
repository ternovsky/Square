delimiter $$
CREATE DATABASE IF NOT EXISTS  `test`$$ 
CREATE TABLE IF NOT EXISTS  `test`.`position` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `x` float NOT NULL,
  `y` float NOT NULL,
  `date` varchar(45) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1474 DEFAULT CHARSET=utf8 COLLATE=utf8_bin$$

