package com.li.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.li.pojo.Category;
import com.li.pojo.Product;
import com.li.service.CategoryService;
import com.li.service.ProductService;
import com.li.utils.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {
    @Reference
    private ProductService productService;
    @Reference
    private CategoryService categoryService;
    @RequestMapping("/")
    public  String index(Model model, HttpServletRequest request){
        List<Product> hotlist=productService.hotList();
        List<Product> newslist=productService.newsList();
        List<Category> categories = categoryService.listAll();
        model.addAttribute("hotlist",hotlist);
        model.addAttribute("newslist",newslist);
        model.addAttribute("categories",categories);
        String username = CookieUtil.getValue(request, "username");
        model.addAttribute("username",username);
        return  "/list";
    }
}
