//defines the folder / package this class eblongs to inside src / main / java
package com.example.cachingproxy;

//create from jakarta servlet
//represents teh incomign HTTP request
import jakarta.servlet.http.HttpServletRequest;

//spring - use to red avlaue from application prerpty or command-line arguments
import org.springframework.beans.factory.annotation.Value;

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

//used to iterate over header names from the incoming request
import java.util.Enumeration;

//used for building the response headers map
import java.util.HashMap;


//spring automation - this class handles web requests
@RestController
public class ProxyController{

    //cacheserver object used to check and save cached response
    private final CacheService cacheService;

    //rest template object used to send request to the origin server
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${origin}")
    private String origin;

    //construct injection - this gives controller the cacheService object
    public ProxyController(CacheService cacheService){
        //chache service stored so other ufnction can use it
        this.cacheService = cacheService;
    }


    //catches every route
    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxyRequest(HttpServletRequest request) throws Exception {
        // build the full target URL from the origin + path + query string
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String url = origin + path + (queryString != null ? "?" + queryString : "");

        // check the cache - if we have a saved response, return it immediately
        CacheService.CachedResponse cached = cacheService.get(url);
        if(cached != null){
            HttpHeaders responseHeaders = new HttpHeaders();
            for(Map.Entry<String, List<String>> entry : cached.getHeaders().entrySet()){
                responseHeaders.put(entry.getKey(), entry.getValue());
            }
            // mark the response as a cache hit
            responseHeaders.set("X-Cache", "HIT");
            return new ResponseEntity<>(cached.getBody(), responseHeaders, HttpStatus.valueOf(cached.getStatusCode()));
        }

        // copy incoming request headers to forward to the origin server
        HttpHeaders requestHeaders = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        if(headerNames != null){
            while(headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement();
                requestHeaders.set(headerName, request.getHeader(headerName));
            }
        }

        // read the request body (used for POST, PUT, etc.)
        byte[] requestBody = request.getInputStream().readAllBytes();
        HttpEntity<byte[]> entity = new HttpEntity<>(requestBody.length > 0 ? requestBody : null, requestHeaders);

        // get the HTTP method (GET, POST, etc.) from the incoming request
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        // forward the request to the origin server
        ResponseEntity<byte[]> originResponse = restTemplate.exchange(url, method, entity, byte[].class);

        // save the origin's response into the cache
        HttpHeaders originHeaders = originResponse.getHeaders();
        Map<String, List<String>> headersMap = new HashMap<>(originHeaders);
        CacheService.CachedResponse toCache = new CacheService.CachedResponse(
            originResponse.getStatusCode().value(),
            headersMap,
            originResponse.getBody()
        );
        cacheService.put(url, toCache);

        // return the origin response and mark it as a cache miss
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.putAll(originHeaders);
        responseHeaders.set("X-Cache", "MISS");
        return new ResponseEntity<>(originResponse.getBody(), responseHeaders, originResponse.getStatusCode());
    }


}
