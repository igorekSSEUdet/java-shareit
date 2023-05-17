package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utills.HttpHeaders;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @Autowired
    public ItemController(@Qualifier("itemServiceImpl") ItemService service) {
        this.service = service;
    }

    @PostMapping
    public Optional<ItemDto> createItem(@RequestBody @Valid Item item, @RequestHeader(HttpHeaders.USER_ID) int userId) {
        return service.createItem(item, userId);
    }

    @GetMapping("/{itemId}")
    public Optional<ItemDto> getItemById(@PathVariable int itemId, @RequestHeader(HttpHeaders.USER_ID) int userId) {
        return service.getItemById(itemId, userId);

    }

    @GetMapping
    public List<Item> getAllItemsByUserId(@RequestHeader(HttpHeaders.USER_ID) int userId) {
        return service.getAllItemsByUserId(userId);

    }

    @PatchMapping("/{id}")
    public Optional<ItemDto> updateItem(@RequestBody Item item, @PathVariable int id,
                                        @NotEmpty @RequestHeader(HttpHeaders.USER_ID) int userId) {
        return service.updateItem(item, id, userId);

    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam("text") String text) {
        return service.searchItem(text);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable int id, @RequestHeader(HttpHeaders.USER_ID) int userId) {
        service.deleteItem(id, userId);
    }
}
