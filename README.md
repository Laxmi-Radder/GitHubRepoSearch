# GitHubRepoSearch
GitHub repository search for a given language 

Problem Statement: 

1. Write a REST service in Java which takes a language as input (e.g. rust, go, coffeescript, …) and provides a JSON output, listing all the projects that are using that language in GitHub
2. The output must contain only project id, name, url and the owner login
3. We expect working code to be provided with instructions on how to execute it – either email, upload to GitHub etc.
You may use open source libraries where available
The relevant GitHub API is: https://docs.github.com/en/rest/reference/search#search-repositories
Not expecting candidate to spend more than 3 hours on the test


How to run locally?

There are 2 ways either by running code directly or by running built jar.
1. You can run jar with command "java -jar SearchGitHubRepo-0.0.1-SNAPSHOT.jar"
2. Clone the Git project:

    ⦁   Github clone URL 
        https://github.com/Laxmi-Radder/GitHubRepoSearch.git
       
    ⦁   Open the project using IDE(Eclipse/Intellij Idea)
    
    ⦁   Run the build
    
    ⦁   Go to SearchRepoApplication.java file and start the application
         
    ⦁   Once application is up,  you can use the Swagger UI/Postman 
    
  Swager UI:
   
   	1.  Url http://localhost:8090/swagger-ui.html#!/
	2.  Click on link "search-repo-controller : Search Repo Controller"
	3.  Click on service "GET /search/repositories"
	4.  Pass the details to get the result
        Language: Programming language in which repository is created
        records_per_page:   Number of records you want to fetach in one request
        page:  The page you want to retrieve based on records_per_page             
Note:

  ⦁  language is mandatory field
  
  ⦁  records_per_page max value is as configured in application.properties with key "github.max.records.per.page" which can be set upto 100, Defualt value is 30
  
  ⦁  You can retrive first 1000 records



Configuration: src\main\resources\application.properties


Below properties are used for GitHub authentication and they are optional if you don't provide then you can run max 10 requests per minute otherwise 30 requests per minute

github.username : GitHub login user name

github.personal.access.token : your GitHub Personal Access Token

