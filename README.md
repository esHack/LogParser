# LogParser
Parser to parse the log file and store the data in database


run the following command to build the project / jar

#### mvn clean install -U

Copy the jar file and access.log file in the same directory
run the following command to save the data to database initially

#### java -jar logparser-0.0.1 

run the following command to fetch the data from the based on start date, the duration and the threshold

#### java -jar logparser-0.0.1 --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100


mysql scripts
-------------------------------

#### select count(ip),ip from logdata where log_date between '2017-01-01 13:00:00' and '2017-01-01 14:00:00' group by ip having count(ip) > 100;

#### select * from logdata where ip='192.168.228.188';

