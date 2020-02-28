package link.thingscloud.simple.database.designer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author : zhouhailin
 */
@Controller
public class HomeController {

    @Value("${appVersion}")
    private String appVersion;
    @Value("${appName}")
    private String appName;
    @Value("${appUrl}")
    private String appUrl;

    @GetMapping({"", "/", "index"})
    public String index(Model model) {
        model.addAttribute("appUrl", appUrl);
        model.addAttribute("appName", appName);
        model.addAttribute("appVersion", appVersion);
        return "index";
    }
}
