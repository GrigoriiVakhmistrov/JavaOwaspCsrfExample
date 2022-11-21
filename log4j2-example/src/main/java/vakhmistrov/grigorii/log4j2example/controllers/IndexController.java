package vakhmistrov.grigorii.log4j2example.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

@RequestMapping("/")
@Controller
public class IndexController {
    private static final Logger logger = LogManager.getLogger(IndexController.class);

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping
    public String saveFile(@RequestParam(value = "file") MultipartFile xmlFile) {

        try {
            InputStream is = xmlFile.getInputStream();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            logger.info(doc.getDocumentElement().getAttribute("Date"));
        } catch (SAXException | IOException | ParserConfigurationException ignored) {

        }

        return "result";
    }
}
