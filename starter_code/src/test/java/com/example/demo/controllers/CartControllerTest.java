package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class CartControllerTest {
    private CartController cartController;

    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        userController = new UserController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void addToCart() {
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

        u.setCart(cart);


        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepo.save(Mockito.any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);
        final ResponseEntity<Cart> cartResponse = cartController.addTocart(c);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, cartResponse.getStatusCodeValue());

        Assert.assertTrue(cartResponse.getBody().getId() == 1);
    }

    @Test
    public void removeFromCart() {
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

        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        final ResponseEntity<Cart> submitToCartResponse = cartController.addTocart(c);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, submitToCartResponse.getStatusCodeValue());

        final ResponseEntity<Cart> removeFromCartResponse = cartController.removeFromcart(c);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, removeFromCartResponse.getStatusCodeValue());

        Cart cart = removeFromCartResponse.getBody();

        Assert.assertFalse(cart.getItems().contains(item));
    }
}
