package com.quiz.backend.controller;

import com.quiz.backend.dto.ArticleResponse;
import com.quiz.backend.dto.ArticleStockResponse;
import com.quiz.backend.entity.ItmMaster;
import com.quiz.backend.repository.ItmMasterRepository;
import com.quiz.backend.repository.ItmMvtRepository;
import com.quiz.backend.repository.SprcListRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private static final List<String> SITES = List.of("FBC", "FMD");

    private final ItmMasterRepository itmMasterRepository;
    private final ItmMvtRepository itmMvtRepository;
    private final SprcListRepository sprcListRepository;

    public ArticleController(ItmMasterRepository itmMasterRepository, ItmMvtRepository itmMvtRepository, SprcListRepository sprcListRepository) {
        this.itmMasterRepository = itmMasterRepository;
        this.itmMvtRepository = itmMvtRepository;
        this.sprcListRepository = sprcListRepository;
    }

    @GetMapping
    public List<ArticleResponse> getAll() {
        Map<String, Integer> qtyMap = buildQuantityMap();
        return itmMasterRepository.findAll().stream()
                .map(m -> toResponse(m, qtyMap))
                .toList();
    }

    @GetMapping("/search")
    public List<ArticleResponse> search(@RequestParam String q) {
        Map<String, Integer> qtyMap = buildQuantityMap();
        return itmMasterRepository.findByItmdes10ContainingIgnoreCase(q).stream()
                .map(m -> toResponse(m, qtyMap))
                .toList();
    }

    @GetMapping("/barcode/{ean}")
    public ResponseEntity<ArticleResponse> getByBarcode(@PathVariable String ean) {
        String ref = ean.trim().split("\\s+")[0];
        return itmMasterRepository.findByItmref0(ref)
                .map(m -> {
                    BigDecimal sum = itmMvtRepository.sumPhyall0ByItmref0AndSites(m.getItmref0(), SITES);
                    Integer qty = sum != null ? sum.intValue() : 0;
                    return ResponseEntity.ok(new ArticleResponse(m.getRowid(), m.getItmdes10(), qty));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{rowid}/stocks")
    public List<ArticleStockResponse> getStocks(@PathVariable Long rowid) {
        ItmMaster master = itmMasterRepository.findById(rowid).orElseThrow();
        BigDecimal total = itmMvtRepository.sumPhyall0ByItmref0AndSites(master.getItmref0(), SITES);
        Integer quantiteALouer = total != null ? total.intValue() : 0;
        BigDecimal price = sprcListRepository.findPriceByArticle("T11", "SPL26-0001", master.getItmref0());
        BigDecimal prix = price != null ? price : BigDecimal.ZERO;
        return itmMvtRepository.findByItmref0AndSites(master.getItmref0(), SITES).stream()
                .map(mvt -> new ArticleStockResponse(
                        0L,
                        mvt.getStofcy0(),
                        mvt.getAvcbasqty0() != null ? mvt.getAvcbasqty0().intValue() : 0,
                        quantiteALouer,
                        prix))
                .toList();
    }

    private Map<String, Integer> buildQuantityMap() {
        Map<String, Integer> map = new HashMap<>();
        for (Object[] row : itmMvtRepository.sumPhyall0GroupedByItmref0(SITES)) {
            String ref = (String) row[0];
            BigDecimal sum = (BigDecimal) row[1];
            map.put(ref, sum != null ? sum.intValue() : 0);
        }
        return map;
    }

    private ArticleResponse toResponse(ItmMaster m, Map<String, Integer> qtyMap) {
        Integer qty = qtyMap.getOrDefault(m.getItmref0(), 0);
        return new ArticleResponse(m.getRowid(), m.getItmdes10(), qty);
    }
}
