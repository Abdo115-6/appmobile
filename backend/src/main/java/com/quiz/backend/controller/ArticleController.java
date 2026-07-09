package com.quiz.backend.controller;

import com.quiz.backend.dto.ArticleResponse;
import com.quiz.backend.dto.ArticleStockResponse;
import com.quiz.backend.entity.ItmMaster;
import com.quiz.backend.repository.ItmMasterRepository;
import com.quiz.backend.repository.ItmMvtRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ItmMasterRepository itmMasterRepository;
    private final ItmMvtRepository itmMvtRepository;

    public ArticleController(ItmMasterRepository itmMasterRepository, ItmMvtRepository itmMvtRepository) {
        this.itmMasterRepository = itmMasterRepository;
        this.itmMvtRepository = itmMvtRepository;
    }

    @GetMapping
    public List<ArticleResponse> getAll() {
        return itmMasterRepository.findAll().stream()
                .map(m -> new ArticleResponse(m.getRowid(), m.getItmdes10()))
                .toList();
    }

    @GetMapping("/search")
    public List<ArticleResponse> search(@RequestParam String q) {
        return itmMasterRepository.findByItmdes10ContainingIgnoreCase(q).stream()
                .map(m -> new ArticleResponse(m.getRowid(), m.getItmdes10()))
                .toList();
    }

    @GetMapping("/barcode/{ean}")
    public ResponseEntity<ArticleResponse> getByBarcode(@PathVariable String ean) {
        String ref = ean.trim().split("\\s+")[0];
        return itmMasterRepository.findByItmref0(ref)
                .map(m -> ResponseEntity.ok(new ArticleResponse(m.getRowid(), m.getItmdes10())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{rowid}/stocks")
    public List<ArticleStockResponse> getStocks(@PathVariable Long rowid) {
        ItmMaster master = itmMasterRepository.findById(rowid).orElseThrow();
        List<String> sites = List.of("FBC", "FMD");
        return itmMvtRepository.findByItmref0AndSites(master.getItmref0(), sites).stream()
                .map(mvt -> new ArticleStockResponse(
                        0L,
                        mvt.getStofcy0(),
                        mvt.getAvcbasqty0() != null ? mvt.getAvcbasqty0().intValue() : 0))
                .toList();
    }
}
