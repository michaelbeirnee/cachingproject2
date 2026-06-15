//defines the folder / package this class belongs to inside - src/main/java
package com.example.cachingproxy;

//comes from jackson
//used to deswibe the typewe want when reading JSON into Jaa object
import com.fasterxml.jackson.core.type.TypeReference;

//comes from jackson - used to read and write JSON
import com.fasterxml.jackson.databind.ObjectMapper;

//spring - tells spring to create this class as a service object
import org.springframework.stereotype.Service;

//comes from java standard library - used to represent a file on your computer
import java.io.File;

//usaed for a key-value datastructure
import java.util.HashMap;

//comes from java standard library
//used because one header can have servferal values
import java.util.List;

//comes from java standard library - used for key-value pairs
import java.util.Map;

@Service
public class CacheService{
    //name of the file where cacched rewsponse will be saved
    private static final String CACHE_FILE_NAME = ".cache.json";

    //jackson object used to ready and write JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    //in memeory cache
    //key : requested URL
    //Value : saved response
    private Map<String, CachedResponse> cache = new HashMap<>();

    //constructor runs when spring create cacheserver
    public CacheService(){
        loadCache();
    }

    //return the cached response for a URL, or null if not cached
    public CachedResponse get(String url){
        return cache.get(url);
    }

    //save a response to the cache and persist to disk
    public void put(String url, CachedResponse response){
        cache.put(url, response);
        saveCache();
    }

    //load cache from json file
    private void loadCache(){
        try{
            //create a file pointing to .cache.json; name from json file
            File file = new File(CACHE_FILE_NAME);

            //if the cahc efile does not exist -0 start with an empty cache
            if(!file.exists()){
                cache = new HashMap<>();
                return;    //stops this function early
            }
            //read .cache.json - convert it into a Map<String, CachedResponse>
            cache = objectMapper.readValue(file, new TypeReference<Map<String, CachedResponse>>());

        }catch (Exception exception){
            cache = new HashMap<>();
        }
    }

    //saved the current cache onto .cache.json
    private void saveCache(){
        try{
            //convert the cache map into JSON and write it to the file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(CACHE_FILE_NAME), cache);
        }catch(Exception exception){
            //throw an error if cache cannot be saved
            throw new RuntimeException("could not be saved" , exception);
        }
    }

    //static fucnction so it can be called before spring fully starts
    public static void clearCacheFile(){
        File file = new File(CACHE_FILE_NAME);
        if(file.exists()){
            file.delete();
        }
    }

    //inner class that rperesnts one saved HTTP response
    public static class CachedResponse{
        //store our HTTP code
        private int statusCode;

        //stores response headers
        //header name maps to a list of values
        private Map<String, List<String> > headers;

        //stores the response body as raw bytes
        private byte[] body;

        //empty constructor needed by jackson when reading json
        public CachedResponse(){
        }

        //constructor used when created a cached repsone manually
        public CachedResponse(int statusCode, Map<String, List<String>> headers, byte[] body){
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }

        public int getStatusCode(){
            return statusCode;
        }

        public void setStatusCode(int statusCode){
            this.statusCode = statusCode;
        }

        public Map<String, List<String> > getHeaders(){
            return headers;
        }

        public void setHeaders(Map< String, List<String> > headers ){
            this.headers = headers;
        }

        public byte[] getBody(){
            return body;
        }

        public void setBody(byte[] body){
            this.body = body;
        }

    }
}
