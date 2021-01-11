package com.li.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.li.pojo.Cart;
import com.li.pojo.CartItem;
import com.li.pojo.Product;
import com.li.service.CardService;
import com.li.service.ProductService;
import com.li.utils.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Reference
    private ProductService productService;
    @Reference
    private CardService cardService;
    @RequestMapping("/add")
    public String add(Integer id, Integer count, HttpServletRequest request, HttpServletResponse response){
       //id->product
        Product product = productService.get(id);
       // new CartItem
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setCount(count);

        String username = CookieUtil.getValue(request, "username");
        Cart cart = getCart(username);
        //add Item
        cart.addItem(cartItem);
        //将cart放到redis中
        cardService.setCart(username,cart,60);
        //将cart放到request的作用域中
        request.setAttribute("cart", cart);
        return "cart";

    }

    private Cart getCart(String username) {
        Cart cart=cardService.getCart(username);
        if (cart==null) {
            cart=new Cart();
        }
        return cart;
    }

    @RequestMapping("/removeCart")
    public String removeCart(Integer id,HttpServletRequest request,HttpServletResponse response){
        String username = CookieUtil.getValue(request, "username");
        Cart cart= getCart(username);
        cart.removeCart(id);
        //setCart
        cardService.setCart(username,cart,60);
        //将cart放到request的作用域中
        request.setAttribute("cart", cart);
        return "cart";
    }
    @RequestMapping("/clearCart")
    public String clearCart(HttpServletRequest request,HttpServletResponse response){
        String username = CookieUtil.getValue(request, "username");
        Cart cart= getCart(username);
        cart.clearCart();
        //redis清除cart
       cardService.delete( username );
        //将cart放到request的作用域中
        request.setAttribute("cart", cart);
        return "cart";
    }

}
