//defines the folder / package this class belongs to inside src / main / java
package com.example.cachingproxy; 

//comes from spring boot
//used to start a spring boot application 
import org.springframework.boot.SpringApplication; 

//comes from spring boot - used to start a spring boot applications
import org.springframework.boot.autoconfigure.SpringBootApplication; 

//enables our auto-configuartion - component scanning - and Spring Boot setup 
@SpringBootApplication
public class CachingProxyApplication {
    //entry point of the java application 
    //string args atore command-line arguments 
    public static void main(String[] args){
        for(String arg : args){
            if(args.equals("--clear-cache")){
               //call the static function in CacheService that edletes the cache file 
                CacheService.clearCacheFile(); 
                System.out.println("Cache file cleared"); 
                return; 
            }
        }
        //start the spring boot serever 
        //this create the web server and loads controllers / services 
        SpringApplication.run(CachingProxyApplication.class, args); 
    }
}
