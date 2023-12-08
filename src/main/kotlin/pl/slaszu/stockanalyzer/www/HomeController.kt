package pl.slaszu.stockanalyzer.www

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {

    @GetMapping("/")
    fun blog(model: Model): String {
        model["title"] = "Blog"
        return "home"
    }

}