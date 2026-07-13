package com.quiz.backend.controller;

import com.quiz.backend.dto.InventoryRequest;
import com.quiz.backend.entity.Inventory;
import com.quiz.backend.repository.InventoryRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) String depot,
            @RequestParam(required = false) String equipe,
            @RequestParam(required = false) String zone,
            @RequestParam(defaultValue = "csv") String format) {

        List<Inventory> list = inventoryRepository.findFiltered(depot, equipe, zone);

        if ("xlsx".equalsIgnoreCase(format)) {
            return exportXlsx(list);
        }
        return exportCsv(list);
    }

    private ResponseEntity<byte[]> exportCsv(List<Inventory> list) {
        String header = "YNUM_0,YDEPOT_0,YEQUIPE_0,YZONE_0,YITMREF_0,YQTYPLT_0,YQTYCRT_0,YQTYMTR_0,CREUSR_0,CREDATTIM_0";
        String body = list.stream()
                .map(i -> String.join(",",
                        safe(i.getYnum0()),
                        safe(i.getYdepot0()),
                        safe(i.getYequipe0()),
                        safe(i.getYzone0()),
                        safe(i.getYitmref0()),
                        fmt(i.getYqtyplt0()),
                        fmt(i.getYqtycrt0()),
                        fmt(i.getYqtymtr0()),
                        safe(i.getCreusr0()),
                        i.getCredattim0() != null ? i.getCredattim0().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : ""
                ))
                .collect(Collectors.joining("\n"));

        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] content = (header + "\n" + body).getBytes(StandardCharsets.UTF_8);
        byte[] csv = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, csv, 0, bom.length);
        System.arraycopy(content, 0, csv, bom.length, content.length);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=utf-8"));
        headers.setContentDispositionFormData("attachment", "inventory.csv");

        return ResponseEntity.ok().headers(headers).body(csv);
    }

    private ResponseEntity<byte[]> exportXlsx(List<Inventory> list) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Inventory");
            Row headerRow = sheet.createRow(0);
            String[] cols = {"YNUM_0","YDEPOT_0","YEQUIPE_0","YZONE_0","YITMREF_0","YQTYPLT_0","YQTYCRT_0","YQTYMTR_0","CREUSR_0","CREDATTIM_0"};
            for (int i = 0; i < cols.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(boldStyle(wb));
            }

            int r = 1;
            for (Inventory inv : list) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(safeStr(inv.getYnum0()));
                row.createCell(1).setCellValue(safeStr(inv.getYdepot0()));
                row.createCell(2).setCellValue(safeStr(inv.getYequipe0()));
                row.createCell(3).setCellValue(safeStr(inv.getYzone0()));
                row.createCell(4).setCellValue(safeStr(inv.getYitmref0()));
                row.createCell(5).setCellValue(inv.getYqtyplt0() != null ? inv.getYqtyplt0().setScale(2, RoundingMode.HALF_UP).doubleValue() : 0);
                row.createCell(6).setCellValue(inv.getYqtycrt0() != null ? inv.getYqtycrt0().setScale(2, RoundingMode.HALF_UP).doubleValue() : 0);
                row.createCell(7).setCellValue(inv.getYqtymtr0() != null ? inv.getYqtymtr0().setScale(2, RoundingMode.HALF_UP).doubleValue() : 0);
                row.createCell(8).setCellValue(safeStr(inv.getCreusr0()));
                row.createCell(9).setCellValue(inv.getCredattim0() != null ? inv.getCredattim0().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);
            wb.write(baos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "inventory.xlsx");
            return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private CellStyle boldStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private String safeStr(String s) {
        return s != null ? s : "";
    }

    private String safe(String s) {
        return s != null ? "\"" + s.replace("\"", "\"\"") + "\"" : "";
    }

    private String fmt(BigDecimal n) {
        return n != null ? n.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00";
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
