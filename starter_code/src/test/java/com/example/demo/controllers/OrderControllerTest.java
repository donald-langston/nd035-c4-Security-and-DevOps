package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private UserController userController;

    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);

    private OrderRepository orderRepo = mock(OrderRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        userController = new UserController();
        cartController = new CartController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void submitOrder() {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();

        when(userRepo.findByUsername("test")).thenReturn(u);
        final ResponseEntity<User> foundUserResponse = userController.findByUserName(u.getUsername());

        Assert.assertNotNull(foundUserResponse);
        Assert.assertEquals(200, foundUserResponse.getStatusCodeValue());

        User f = foundUserResponse.getBody();

        ModifyCartRequest c = new ModifyCartRequest();
        c.setItemId(1);
        c.setUsername("test");
        c.setQuantity(1);

        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(f);
        List<Item> items = new ArrayList<>();
        items.add(item);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(1L));

        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        final ResponseEntity<Cart> cartResponse = cartController.addTocart(c);

        Assert.assertNotNull(cartResponse);
        Assert.assertEquals(200, cartResponse.getStatusCodeValue());

        //Cart foundCart = cartResponse.getBody();

        u.setCart(cart);

        when(userRepo.findByUsername("test")).thenReturn(u);
        final ResponseEntity<UserOrder>  userOrderResponse = orderController.submit("test");

        Assert.assertNotNull(userOrderResponse);
        Assert.assertEquals(200, userOrderResponse.getStatusCodeValue());

        UserOrder submittedUserOrder = userOrderResponse.getBody();
        Assert.assertEquals(submittedUserOrder.getUser().getUsername(), u.getUsername());
        Assert.assertEquals(submittedUserOrder.getItems(), u.getCart().getItems());
    }

    @Test
    public void getOrdersForUser() {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();

        when(userRepo.findByUsername("test")).thenReturn(u);
        final ResponseEntity<User> foundUserResponse = userController.findByUserName(u.getUsername());

        Assert.assertNotNull(foundUserResponse);
        Assert.assertEquals(200, foundUserResponse.getStatusCodeValue());

        User f = foundUserResponse.getBody();

        ModifyCartRequest c = new ModifyCartRequest();
        c.setItemId(1);
        c.setUsername("test");
        c.setQuantity(1);

        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(f);
        List<Item> items = new ArrayList<>();
        items.add(item);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(1L));

        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        final ResponseEntity<Cart> cartResponse = cartController.addTocart(c);

        Assert.assertNotNull(cartResponse);
        Assert.assertEquals(200, cartResponse.getStatusCodeValue());

        //Cart foundCart = cartResponse.getBody();

        u.setCart(cart);

        when(userRepo.findByUsername("test")).thenReturn(u);
        final ResponseEntity<UserOrder>  userOrderResponse = orderController.submit("test");

        Assert.assertNotNull(userOrderResponse);
        Assert.assertEquals(200, userOrderResponse.getStatusCodeValue());

        List<UserOrder> orders = new ArrayList<>();
        orders.add(UserOrder.createFromCart(cart));

        when(orderRepo.findByUser(u)).thenReturn(orders);
        final ResponseEntity<List<UserOrder>> getOrdersResponse = orderController.getOrdersForUser("test");

        Assert.assertNotNull(getOrdersResponse);
        Assert.assertEquals(200, userOrderResponse.getStatusCodeValue());

        List<UserOrder> userOrders = getOrdersResponse.getBody();
        UserOrder userOrder = userOrders.get(0);

        Assert.assertTrue(userOrder.getUser() == u);
    }
}
