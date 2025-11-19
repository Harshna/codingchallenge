**Pre-requisite:** 
-JDK 17
-Maven

How to build and run the project 
1. Build the project with : mvn clean install

2. Run as a spring boot project 

3. The project is using in memory database : h2
      http://localhost:8080/h2-console
      jdbc-url : jdbc:h2:mem:test-db
       username : sa 
       password : blank

   Two database table would be created : 1. Depth_Chart 2. Players 
   Players table is pre-populated with sample players data at the start-up with commandline runner 
   
Sample players data : 
      UNIQUE_TEAM_NUMBER  	ID  	PLAYER_NAME  
      13	                1	    Evans Mike
      12	                2	    Tom Brady
      10	                3	    Scott Miller
      65	                4	    Alex Cappa
      9	                    5	    Richard Williams
   
New Depth charts are inserted/updated into Depth_Chart table

4. Open API and Swagger has been included with the project to refer the API documentation 
    - http://localhost:8080/swagger-ui/index.html

5. Sample JSON payload to create the depth chart(POST localhost:8080/api/v1/depth-charts):
         {
            "position":"QB",
            "player":
            {
                "playerName":"Alex Cappa",
                "uniqueTeamNumber":"65"
            },
            "positionDepth":"0"
         }
    - Sample request to get Full Depth Chart 
       (GET - localhost:8080/api/v1/depth-charts/full-depth-chart) 
   
    - Sample request to delete a player 
       (DELETE - localhost:8080/api/v1/depth-charts/QB/players?playerName=Tom Brady&playerUniqueNumber=12)
   
    - Sample payload to get backup for a given player 
        (GET - localhost:8080/api/v1/depth-charts/QB/players/back-ups?playerUniqueNumber=12&playerName=Tom Brady)
