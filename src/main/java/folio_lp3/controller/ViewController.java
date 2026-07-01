package folio_lp3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "html/lector/evidencias.html";
    }

    @GetMapping("/evidencias")
    public String evidencias() {
        return "html/lector/evidencias.html";
    }

    @GetMapping("/login")
    public String login() {
        return "html/login.html";
    }

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "redirect:/html/admin/login.html";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "html/admin/dashboard.html";
    }

    @GetMapping("/admin/upload")
    public String upload() {
        return "html/admin/upload.html";
    }

    @GetMapping("/admin/ia")
    public String ia() {
        return "html/admin/ia.html";
    }

    @GetMapping("/admin/alerts")
    public String alerts() {
        return "html/admin/alerts.html";
    }
}
