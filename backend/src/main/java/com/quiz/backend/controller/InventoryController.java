package com.quiz.backend.controller;

import com.quiz.backend.dto.InventoryRequest;
import com.quiz.backend.entity.Inventory;
import com.quiz.backend.repository.InventoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping
    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }

    @GetMapping("/article/{ref}")
    public List<Inventory> getByArticle(@PathVariable String ref) {
        return inventoryRepository.findByYitmref0(ref);
    }

    @GetMapping("/depot/{depot}")
    public List<Inventory> getByDepot(@PathVariable String depot) {
        return inventoryRepository.findByYdepot0(depot);
    }

    @PostMapping
    public ResponseEntity<Inventory> create(@RequestBody InventoryRequest request) {
        Inventory inv = new Inventory();
        inv.setUpdtick0(1);
        String ynum = request.getYnum0();
        if (ynum == null || ynum.isBlank()) {
            ynum = "INV-" + System.currentTimeMillis();
        }
        inv.setYnum0(ynum);
        inv.setYdepot0(request.getYdepot0());
        inv.setYequipe0(request.getYequipe0());
        inv.setYzone0(request.getYzone0());
        inv.setYitmref0(request.getYitmref0());
        inv.setYqtyplt0(request.getYqtyplt0());
        inv.setYqtycrt0(request.getYqtycrt0());
        inv.setYqtymtr0(request.getYqtymtr0());
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        inv.setAuuid0(bb.array());
        inv.setCreusr0(request.getCreusr0());
        inv.setCredattim0(LocalDateTime.now());
        inv.setUpdusr0(request.getCreusr0());
        inv.setUpddattim0(LocalDateTime.now());

        Inventory saved = inventoryRepository.save(inv);
        return ResponseEntity.ok(saved);
    }
}
