package pk.edu.pl.PASiR_Sosin_Jakub.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pk.edu.pl.PASiR_Sosin_Jakub.model.AppInfo;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public String test() {
        return "Hello World";
    }
    @GetMapping("/api/info")
    public AppInfo getInfo() {
        return new AppInfo("Aplikacja Budżetowa","1.0", "Witaj w aplikacji budżetowej stworzonej ze Spring Boot!");
    }
}
