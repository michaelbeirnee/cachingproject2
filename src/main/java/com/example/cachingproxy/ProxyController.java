//defines the folder / package this class eblongs to inside src / main / java 
package com.example.cachingproxy; 

//create from jakarta servlet 
//represents teh incomign HTTP request 
import jakrata.servlet.http.HttpServletRequest; 

//spring - use to red avlaue from application prerpty or command-line arguments
import org.springframework.beans.factory.annoation.Value; 

//spring - gives access to resposne eneitty - httpheaeder, http status code - httpmethod
import org.springframework.http.*; 

//SPRING WEBS 

//spring - maps http requests to controller functions
import org.springframework.web.bind.annotation.RequestMapping; 

//spring this class returns raw http responses
import org.springframework.web.bind.annotation.RestController; 

//used to send http requests to the origin server 
import org.springframework.web.client.RestTemplate;

//comes form java library 
//used because one header can have severl values
import java.util.List; 

//comes from java standard library - used for key-value pairs
import java.util.Map; 


//spring automation - this class handles web requests
@RestController 
public class ProxyController{

    //cacheserver object used to check and save cached response 
    private final CacheService cacheService; 

    //rest template object used to send request to the origin server
    private final RestTemplate resTemplate = new RestTemplate(); 

    @Value("${origin}")
    private String origin; 

    //construct injection - this gives controller the cacheService object
    public ProxyController(CacheService cacheService){
        //chache service stored so other ufnction can use it 
        this.cacheService = cacheService; 
    }


    //catches every route 
    @RequestMapping 
    public ResponseEntity<byte[]> proxyRequest(HttpServeletRequest request){
        
    }


}