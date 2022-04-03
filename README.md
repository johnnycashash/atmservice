#Local
- mvn spring-boot:run
#Docker
- mvn clean install -DskipTests -Pdev
- docker-compose up -d --build
- docker-compose down




#In-Progress
- Security
- Test
- Transactional
- Statistic api
- versioning
- postman collection
- logs
- javadocs