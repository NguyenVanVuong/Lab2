package hello;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import Veikimas.Gija;
import java.io.BufferedOutputStream;


import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import java.util.Iterator;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PreDestroy;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;


import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class FileUploadController {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Drive API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/drive-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(DriveScopes.DRIVE_METADATA);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
        FileUploadController.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = null;
		try {
			credential = new AuthorizationCodeInstalledApp(
			    flow, new LocalServerReceiver()).authorize("user");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    



    
    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }
    
    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("name") String name, 
            @RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = 
                        new BufferedOutputStream(new FileOutputStream(new java.io.File("./dir/",name)));
                stream.write(bytes);
                stream.close();
                
                
                Drive service = getDriveService();
                File fileMetadata = new File();
                fileMetadata.setTitle(file.getOriginalFilename());
                java.io.File filePath = new java.io.File("./dir/"+file.getOriginalFilename());
                FileContent mediaContent = new FileContent(file.getContentType(), filePath);
                File f =  service.files().insert(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
                
                return "You successfully uploaded " + name + "!";
                
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }
    
     @RequestMapping(value="/deleteFiles", method=RequestMethod.GET)
     public @ResponseBody String deleteFiles()
     {
         try
         {
            java.io.File dir = new java.io.File("./dir");
            FileUtils.cleanDirectory(dir);
            return ("All files have been deleted");
         }
         catch (Exception e) {         
            return ("Error");
         }
     }
     
      @RequestMapping(value="/run", method=RequestMethod.GET)
    public @ResponseBody String runScan() {
        final Map<String,Integer> queryCounts =  Collections.synchronizedMap(new HashMap<String,Integer>(1000)); 
        java.io.File dir = new java.io.File("./dir");
        java.io.File[] directoryListing = dir.listFiles();
        
         if (directoryListing != null) {
         
	List<Thread> threads = new ArrayList<Thread>();   
             for (java.io.File child : directoryListing) {
               Gija a = new Gija();
               threads.add(a);
                a.run(child,queryCounts);
        
            }
         for (Thread curThread : threads) {
	    try {
		// starting from the first wait for each one to finish.
		curThread.join();
	    } catch (InterruptedException e) {
	
	    }
	}
        try
        {
        PrintWriter writerAG = new PrintWriter("a-g.txt", "UTF-8");
        char[] charArrayAG = new char[] {'a','b','c','d','e','f','g'};
        PrintWriter writerHN = new PrintWriter("h-n.txt", "UTF-8");
        char[] charArrayHN = new char[] {'h','i','j','k','l','m','n'};
        PrintWriter writerOU = new PrintWriter("o-u.txt", "UTF-8");
        char[] charArrayOU = new char[] {'o','p','r','s','t','u'};
        PrintWriter writerVZ = new PrintWriter("v-z.txt", "UTF-8");
        char[] charArrayVZ = new char[] {'v','z'};
        
        
        
        
        

        Iterator it = queryCounts.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry entry = (Map.Entry)it.next();
       
            
            for (char c : charArrayAG)
            {
                if (entry.getKey().toString().startsWith(String.valueOf(c))) writerAG.write(entry.getValue() + " " + entry.getKey());
            }
            for (char c : charArrayHN)
            {
                if (entry.getKey().toString().startsWith(String.valueOf(c))) writerHN.write(entry.getValue() + " " + entry.getKey());
            }
            for (char c : charArrayOU)
            {
                if (entry.getKey().toString().startsWith(String.valueOf(c))) writerOU.write(entry.getValue() + " " + entry.getKey());
            }
            for (char c : charArrayVZ)
            {
                if (entry.getKey().toString().startsWith(String.valueOf(c))) writerVZ.write(entry.getValue() + " " + entry.getKey());
            }
            
        }
    writerAG.close();
    writerHN.close();
    writerOU.close();
    writerVZ.close();
        }
        catch (Exception e)
        {
            
        }


        
        
        
         
         }
         else return "not right";
         return "done";
    
}

}
