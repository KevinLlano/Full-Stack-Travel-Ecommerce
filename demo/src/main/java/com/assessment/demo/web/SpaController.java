package com.assessment.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * SPA fallback controller: forwards all non-API routes (no dot, not starting with /api)
 * to index.html so Angular client-side routing works when directly hitting deep links.
 */
@Controller
public class SpaController {

    @GetMapping({"/", "/{path:[^\\.]*}", "/{path:^(?!api$).*$}/{subpath:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}
