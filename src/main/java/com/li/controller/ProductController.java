package com.li.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.li.pojo.Category;
import com.li.pojo.Product;
import com.li.service.CategoryService;
import com.li.service.ProductService;
import com.li.service.SecondCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Reference
    private ProductService productService;
    @Reference
    private CategoryService categoryService;
    @Reference
    private SecondCategoryService secondCategoryService;
    @RequestMapping("/list")
    public  String list(Integer cid, Integer scid, String curPage, Model model){
        Map<String,Object> map=productService.list(cid,scid,curPage,8);
        List<Category> categories = categoryService.listAll();
        model.addAttribute("list",map.get("list"));
        model.addAttribute("page",map.get("page"));
        model.addAttribute("cid",cid);
        model.addAttribute("scid",scid);
        model.addAttribute("categories",categories);
        return "/productList";
    }

    @RequestMapping("/show/{id}")
    public  String show(@PathVariable("id")  int id, Model model){
        Product product = productService.get(id);
        model.addAttribute("product",product);
        return "product";
    }
}
