package vakhmistrov.grigorii.xpath.controllers;

import org.apache.commons.jxpath.FunctionLibrary;
import org.apache.commons.jxpath.JXPathContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

@RequestMapping("/")
@Controller
public class IndexController {
    @GetMapping
    public String index() {
        return "index";
    }


    // correct XPath example /ValCurs/@Date
    // incorrect XPath example java.lang.System.exit(42)
    // incorrect XPath example java.lang.Thread.sleep(10000)
    @PostMapping
    public String saveFile(@RequestParam(value = "file") MultipartFile xmlFile,
                           @RequestParam(value = "xpath") String xpath,
                           HttpServletRequest req) {

        try {
            InputStream is = xmlFile.getInputStream();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(is);

            var pathContext = JXPathContext.newContext(document);

            // uncomment to secure version
            //
            // pathContext.setFunctions(new FunctionLibrary());

            req.setAttribute("result", pathContext.getValue(xpath));

            return "result";
        } catch (SAXException | IOException | ParserConfigurationException ignored) {
        }

        return "index";
    }
}
