#Local(default local profile)
- mvn spring-boot:run
---
#Docker(dev profile)
- mvn clean install -DskipTests -Pdev
- docker-compose up -d --build
- docker-compose down
---
#In-Progress
- Transaction support for mongo in docker
- Security
- Statistic api
- versioning
- logs
- javadocs