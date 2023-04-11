package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void getItems() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepo.findAll()).thenReturn(items);
        final ResponseEntity<List<Item>> itemsResponse = itemController.getItems();

        Assert.assertNotNull(itemsResponse);
        Assert.assertEquals(200, itemsResponse.getStatusCodeValue());

        Assert.assertTrue(itemsResponse.getBody().size() == 1);
    }

    @Test
    public void getItemById() {

        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");

        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        final ResponseEntity<Item> itemResponse = itemController.getItemById(1L);

        Assert.assertNotNull(itemResponse);
        Assert.assertEquals(200, itemResponse.getStatusCodeValue());

        Item returnedItem = itemResponse.getBody();

        Assert.assertEquals(item.getName(), returnedItem.getName());
    }

    @Test
    public void getItemsByName() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepo.findByName("Round Widget")).thenReturn(items);
        final ResponseEntity<List<Item>> itemsResponse = itemController.getItemsByName("Round Widget");

        Assert.assertNotNull(itemsResponse);
        Assert.assertEquals(200, itemsResponse.getStatusCodeValue());

        Assert.assertTrue(itemsResponse.getBody().size() == 1);
    }
}
