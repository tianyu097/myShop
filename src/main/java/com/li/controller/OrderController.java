package com.li.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.li.pojo.*;
import com.li.service.CardService;
import com.li.service.OrderService;
import com.li.service.SsoService;
import com.li.utils.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;
    @Reference
    private CardService cardService;
    @Reference
    private SsoService ssoService;

    @RequestMapping("/saveOrder")
    public String saveOrder(Model model, HttpServletRequest request){
        //从redis拿cart
        String username = CookieUtil.getValue(request, "username");
        String token = CookieUtil.getValue(request, "token");
        Cart cart = getCart(username);
        //从redis里拿user
        User user =ssoService.getUser(username,token);
        //创建order对象
        Orders order = new Orders();
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        order.setId(id);
        order.setName(user.getName());
        order.setPhone(user.getPhone());
        order.setAddr(user.getAddr());
        order.setOrdertime(new Date());
        order.setState(1);
        order.setUid(user.getId());
        order.setTotal(cart.getTotal());

        //orderItems
        Collection<CartItem> cartItems=cart.getCartItems();

        for (CartItem cartItem : cartItems) {
            Orderitem orderitem = new Orderitem();
            orderitem.setOrderId(id);
            orderitem.setProductId(cartItem.getProduct().getId());
            orderitem.setCount(cartItem.getCount());
            orderitem.setSubtotal(cartItem.getSubtotal());
            orderitem.setProduct(cartItem.getProduct());
            order.getItems().add(orderitem);
        }
        //db
        orderService.add(order);
        //从redis删除cart;
        cardService.delete(username);
        //将order放入作用域
        request.setAttribute("order",order);
        //请求转发到order模板页面
        return "order";
    }
    private Cart getCart(String username) {
        Cart cart=cardService.getCart(username);
        if (cart==null) {
            cart=new Cart();
        }
        return cart;
    }
    @RequestMapping("/payOrder")
    public String payOrder(String id, String name, String phone, String addr, HttpServletRequest request, Model model){
        orderService.payOrder(id,name,phone,addr);
        String username = CookieUtil.getValue(request, "username");
        model.addAttribute("username",username);
        return "orderConfirm";
    }
    @RequestMapping("/payMoney")
    public String payMoney(String id, HttpServletRequest request, Model model){
        orderService.updateState(id,2);
        String username = CookieUtil.getValue(request, "username");
        model.addAttribute("username",username);
        return "redirect:/order/list";
    }
    @RequestMapping("/receive")
    public String receive(String id, HttpServletRequest request, Model model){
        orderService.updateState(id,4);
        String username = CookieUtil.getValue(request, "username");
        model.addAttribute("username",username);
        return "redirect:/order/list";
    }

    @RequestMapping("/list")
    public String list(String curPage,HttpServletRequest request){
        String username = CookieUtil.getValue(request, "username");
        String token = CookieUtil.getValue(request, "token");
        User user = ssoService.getUser(username, token);
        int pageSize=6;
        Map<String,Object> map=orderService.list(user.getId(),curPage,pageSize);
        request.setAttribute("list",map.get("list"));
        request.setAttribute("page",map.get("page"));
        return "orderList";
    }

}
