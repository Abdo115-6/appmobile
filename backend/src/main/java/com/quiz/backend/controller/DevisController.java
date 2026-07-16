package com.quiz.backend.controller;

import com.quiz.backend.dto.BpCustomerResponse;
import com.quiz.backend.dto.DevisRequest;
import com.quiz.backend.entity.YdevisMobile;
import com.quiz.backend.repository.BpCustomerRepository;
import com.quiz.backend.repository.YdevisMobileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devis")
public class DevisController {

    private final YdevisMobileRepository ydevisMobileRepository;
    private final BpCustomerRepository bpCustomerRepository;

    public DevisController(YdevisMobileRepository ydevisMobileRepository, BpCustomerRepository bpCustomerRepository) {
        this.ydevisMobileRepository = ydevisMobileRepository;
        this.bpCustomerRepository = bpCustomerRepository;
    }

    @PostMapping
    public ResponseEntity<YdevisMobile> create(@RequestBody DevisRequest request) {
        YdevisMobile devis = new YdevisMobile();
        devis.setYid0(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        devis.setYnum0(truncate(request.getSite(), 5) + "-" + truncate(String.valueOf(System.currentTimeMillis()), 14));
        devis.setYclient0(concatWithTruncation(request.getClientCode(), request.getClientName(), " - ", 15));
        devis.setYsite0(truncate(request.getSite(), 5));
        devis.setYarticle0(concatWithTruncation(request.getArticleRef(), request.getArticleName(), " - ", 24));
        devis.setYqty0(request.getQuantity());
        devis.setYprice0(request.getPrice());
        devis.setYcoeff0(request.getCoefficient());
        devis.setYcarton0(request.getCartons());
        devis.setYbpcnum0(truncate(request.getClientCode(), 20));
        devis.setYbpcnam0(truncate(request.getClientName(), 100));
        devis.setYitmref0(truncate(request.getArticleRef(), 30));
        devis.setYitmdes0(truncate(request.getArticleName(), 200));
        devis.setCreusr0(truncate(request.getCreusr0(), 20));
        devis.setCredattim0(LocalDateTime.now());
        devis.setUpddattim0(LocalDateTime.now());
        devis.setUpdtick0(0);
        devis.setUpdusr0(truncate(request.getCreusr0(), 20));
        devis.setAuuid0(toBytes(UUID.randomUUID()));

        YdevisMobile saved = ydevisMobileRepository.save(devis);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/clients")
    public List<BpCustomerResponse> searchClients(@RequestParam String q) {
        return bpCustomerRepository.search(q).stream()
                .map(c -> new BpCustomerResponse(c.getBpcnum0(), c.getBpcnam0(), c.getBpcsho0()))
                .toList();
    }

    private byte[] toBytes(UUID uuid) {
        ByteBuffer buf = ByteBuffer.allocate(16);
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        return buf.array();
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen);
    }

    private String concatWithTruncation(String part1, String part2, String separator, int maxLen) {
        String p1 = part1 != null ? part1 : "";
        String p2 = part2 != null ? part2 : "";
        String full = p1 + separator + p2;
        if (full.length() <= maxLen) return full;

        int sepLen = separator.length();
        int half = (maxLen - sepLen) / 2;
        return truncate(p1, half) + separator + truncate(p2, maxLen - half - sepLen);
    }
}
