package com.quiz.backend.controller;

import com.quiz.backend.dto.BpCustomerResponse;
import com.quiz.backend.dto.DevisRequest;
import com.quiz.backend.entity.YdevisMobile;
import com.quiz.backend.repository.BpCustomerRepository;
import com.quiz.backend.repository.YdevisMobileRepository;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        String yid = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        devis.setYid0(yid);
        devis.setYnum0(truncate(request.getSite(), 5) + "-" + yid);
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

        // Set or auto-generate mobile key
        if (request.getMobileKey() != null && !request.getMobileKey().isEmpty()) {
            devis.setYmobkey0(request.getMobileKey());
        } else {
            devis.setYmobkey0(getNextMobileKey());
        }

        YdevisMobile saved = ydevisMobileRepository.save(devis);
        return ResponseEntity.ok(saved);
    }

    private synchronized String getNextMobileKey() {
        Integer maxNum = ydevisMobileRepository.findMaxYmobkey0Num();
        int nextNum = (maxNum != null ? maxNum : 0) + 1;
        return "MOB" + String.format("%07d", nextNum);
    }

    @GetMapping("/clients")
    public List<BpCustomerResponse> searchClients(@RequestParam String q) {
        return bpCustomerRepository.search(q).stream()
                .map(c -> new BpCustomerResponse(c.getBpcnum0(), c.getBpcnam0(), c.getBpcsho0()))
                .toList();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        List<YdevisMobile> all = ydevisMobileRepository.findAll();
        StringBuilder csv = new StringBuilder();
        DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyyMMdd");

        // Group by mobile key for E/L structure
        Map<String, List<YdevisMobile>> grouped = new LinkedHashMap<>();
        for (YdevisMobile d : all) {
            String mk = d.getYmobkey0() != null ? d.getYmobkey0() : "NOKEY";
            grouped.computeIfAbsent(mk, k -> new java.util.ArrayList<>()).add(d);
        }

        for (Map.Entry<String, List<YdevisMobile>> entry : grouped.entrySet()) {
            List<YdevisMobile> lines = entry.getValue();
            YdevisMobile first = lines.get(0);
            String ymdStr = first.getCredattim0() != null
                    ? first.getCredattim0().format(ymd) : LocalDate.now().format(ymd);

            // E: ;TYPE;;SITE;SQN;;CLIENT_CODE;DATE;;SITE;CURRENCY;MOBILE_KEY
            csv.append("E;;")
               .append(escapeCsv(first.getYsite0())).append(";")
               .append("SQN").append(";;")  // SQN = fixed
               .append(escapeCsv(first.getYbpcnum0())).append(";")
               .append(ymdStr).append(";;")
               .append(escapeCsv(first.getYsite0())).append(";")
               .append("MAD;")
               .append(escapeCsv(entry.getKey())).append("\n");

            for (YdevisMobile d : lines) {
                // L: ;TYPE;;ARTICLE_REF;UN;QTY;PRICE;MOBILE_KEY
                csv.append("L;;")
                   .append(escapeCsv(d.getYitmref0())).append(";")
                   .append("UN;")
                   .append(d.getYqty0()).append(";")
                   .append(d.getYprice0()).append(";")
                   .append(escapeCsv(entry.getKey())).append("\n");
            }
        }

        byte[] bytes = csv.toString().getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("devis_export.csv").build());
        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(";") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
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
