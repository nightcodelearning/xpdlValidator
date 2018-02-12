package xpdl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import rules.Rule;
import rules.RuleFactory;
import rules.RuleFactoryMethod;
import rules.Validation;
import xpdl.storage.StorageFileNotFoundException;
import xpdl.storage.StorageService;

@Controller
public class FileUploadController {
	private List<Validation> validationList=new ArrayList<>();
    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));
       

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
    		try {
    			 storageService.store(file);
    			 validationList=new ArrayList<>();
    		        validateFile(file);
    		        
					 
    		        if(!validationList.isEmpty()) {
	    		        	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    		        	String json = gson.toJson(validationList);
	    		        	redirectAttributes.addFlashAttribute("validationError",json);
    		        	
    		        }else {
    		        	redirectAttributes.addFlashAttribute("validationError","Validation completed without errors");
    		        	
    		        }
    		        
    		        return "redirect:/";
    			
    		}catch(Exception e) {
    			e.printStackTrace();
    			redirectAttributes.addFlashAttribute("mensajeError","No se pudo realizar la validación");
		        return "redirect:/";
    		}
       
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 
     * @param file
     */
    public void validateFile(MultipartFile file) throws Exception{
    	
    		//read rules
    		Properties properties = new Properties();
    		InputStream inputStream = FileUploadController.class.getResourceAsStream("/rules.properties");
    		properties.load(inputStream);
    		inputStream.close();
    		
    		Enumeration<Object> keys = properties.keys();
    		List<Rule> rulesList=new ArrayList<>();
    		RuleFactoryMethod factory=new RuleFactory();
    		
    		File convFile = new File(file.getOriginalFilename());
    	    convFile.createNewFile(); 
    	    FileOutputStream fos = new FileOutputStream(convFile); 
    	    fos.write(file.getBytes());
    	    fos.close(); 
    		
    		
    		//load rules, call factory 
    		while (keys.hasMoreElements()){
     		   Object key = keys.nextElement();
     		  Rule rule=factory.createRule(properties.get(key).toString());
     		  rulesList.add(rule);
     		}

    	
    		for (Rule rule : rulesList) {
    			Validation val= rule.validation(convFile);
    			if(!val.getErrorList().isEmpty()) {
    				validationList.add(val);
    				}
			}
    			
    		

    	
    }
    
    public static String toPrettyFormat(String jsonString) 
    {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

}
