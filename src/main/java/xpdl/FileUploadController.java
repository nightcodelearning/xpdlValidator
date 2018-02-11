package xpdl;

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

import rules.Rule;
import rules.RuleFactory;
import rules.RuleFactoryMethod;
import rules.Style0115;
import xpdl.storage.StorageFileNotFoundException;
import xpdl.storage.StorageService;

@Controller
public class FileUploadController {

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
    		        validateFile(file);
    		        
    		        redirectAttributes.addFlashAttribute("message",
    		                "You successfully uploaded " + file.getOriginalFilename() + "!");
    		        
    		        redirectAttributes.addFlashAttribute("mensajeDario","hola mundo");
    		        return "redirect:/";
    			
    		}catch(Exception e) {
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
    public static void validateFile(MultipartFile file) throws Exception{
    	
    		//read rules
    		Properties properties = new Properties();
    		InputStream inputStream = FileUploadController.class.getResourceAsStream("/rules.properties");
    		properties.load(inputStream);
    		inputStream.close();
    		
    		Enumeration<Object> keys = properties.keys();
    		List<Rule> rulesList=new ArrayList<>();
    		RuleFactoryMethod factory=new RuleFactory();
    		
    		//load rules, call factory 
    		while (keys.hasMoreElements()){
     		   Object key = keys.nextElement();
     		   //System.out.println(properties.get(key).toString());
     		  Rule rule=factory.createRule(properties.get(key).toString());
     		  rulesList.add(rule);
     		}
    		
    		for (Rule rule : rulesList) {
				System.out.println(rule.validation());
			}
//    		File convFile = new File(file.getOriginalFilename());
//    	    convFile.createNewFile(); 
//    	    FileOutputStream fos = new FileOutputStream(convFile); 
//    	    fos.write(file.getBytes());
//    	    fos.close(); 
//    	    
//    	 // La expresion xpath de busqueda
//    		String xPathExpression = "//satelite[@nombre='Luna']";
//    		
//                    // Carga del documento xml
//     		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//     		DocumentBuilder builder = factory.newDocumentBuilder();
//     		Document documento = builder.parse(convFile);
//
//    		// Preparación de xpath
//     		XPath xpath = XPathFactory.newInstance().newXPath();
//
//     		// Consultas
//     		NodeList nodos = (NodeList) xpath.evaluate(xPathExpression, documento, XPathConstants.NODESET);
//
//    		for (int i=0;i<nodos.getLength();i++){
//    			System.out.println(nodos.item(i).getNodeName()+" : " +
//                               nodos.item(i).getAttributes().getNamedItem("nombre"));
//    		}
//    		
//		String content = new String(file.getBytes(), "UTF-8");
//		System.out.println(content);
    	
//    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//    	DocumentBuilder builder = factory.newDocumentBuilder(); 
//    	Document doc = builder.parse(file);
//    	XPathFactory xPathfactory = XPathFactory.newInstance();
//    	XPath xpath = xPathfactory.newXPath(); 
//    	XPathExpression expr = xpath.compile(<xpath_expression>);
    	
    }

}
