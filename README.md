# LogParser
Parser to parse the log file and store the data in database. 
Uses Spring task executor to insert data in batches that help in insertions faster.


run the following command to build the project that creates logparser-0.0.1.jar

#### mvn clean install -U

Copy the logparser-0.0.1.jar file and access.log file in the same directory
run the following command to save the data to database initially

#### java -jar logparser-0.0.1.jar 

run the following command to fetch the data from the based on start date, the duration and the threshold

#### java -jar logparser-0.0.1.jar --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100


mysql scripts
-------------------------------

>DROP TABLE IF EXISTS `eshdb`.`logdata`;
CREATE TABLE  `eshdb`.`logdata` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) DEFAULT NULL,
  `log_date` datetime DEFAULT NULL,
  `request` varchar(45) DEFAULT NULL,
  `status` int(10) unsigned DEFAULT NULL,
  `user_agent` varchar(400) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=116337 DEFAULT CHARSET=latin1;


>DROP TABLE IF EXISTS `eshdb`.`errorlog`;
CREATE TABLE  `eshdb`.`errorlog` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) NOT NULL DEFAULT '',
  `remarks` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#### select count(ip),ip from logdata where log_date between '2017-01-01 13:00:00' and '2017-01-01 14:00:00' group by ip having count(ip) > 100;

#### select * from logdata where ip='192.168.228.188';

