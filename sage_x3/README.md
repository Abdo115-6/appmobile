# YDEVISMOBILE - Sage X3 v11 Import/Export

## Object Information

| Property        | Value            |
|-----------------|------------------|
| Object Name     | YDEVISMOBILE     |
| Table           | YDEVISMOBILE     |
| Primary Keys    | YNUM_0 + YID_0   |
| CSV Delimiter   | `;` (semicolon)  |
| Record Types    | E = Header, L = Line |

## Files

| File                     | Description                          |
|--------------------------|--------------------------------------|
| `YDEVISMOBILE_IMP.def`   | Import template definition           |
| `YDEVISMOBILE_EXP.def`   | Export template definition           |
| `YDEVISMOBILE.imp`       | Import method (4GL)                  |
| `YDEVISMOBILE.exp`       | Export method (4GL)                  |
| `SAMPLE.csv`             | Sample CSV file with 2 documents     |

## CSV Format

### E Record (Header / En-tete)

Each E record represents one devis document. All L records that follow
belong to this document until the next E record.

```
E;YNUM;SITE;CLIENT_CODE;CLIENT_NAME;CLIENT_DISPLAY;CREUSR;CREDAT
```

| Position | Field        | Type       | Max Len | Required | Description                            |
|----------|--------------|------------|---------|----------|----------------------------------------|
| 1        | TYPE         | fixed      | 1       | Yes      | Must be "E"                            |
| 2        | YNUM_0       | char       | 20      | Yes      | Document number (unique)               |
| 3        | YSITE_0      | char       | 5       | Yes      | Site code: FBC or FMD                  |
| 4        | YBPCNUM_0    | char       | 20      | Yes      | Client code                            |
| 5        | YBPCNAM_0    | char       | 100     | No       | Client name                            |
| 6        | CREUSR_0     | char       | 20      | No       | Created by user (default: MOBILE)      |
| 7        | CREDATTIM_0  | char       | 25      | No       | Created date/time                      |

### L Record (Line / Ligne)

Each L record represents one article line within a devis document.

```
L;YID;ARTICLE_REF;ARTICLE_NAME;ARTICLE_DISPLAY;QTY;PRICE;COEFF;CARTONS
```

| Position | Field        | Type       | Max Len | Required | Description                         |
|----------|--------------|------------|---------|----------|-------------------------------------|
| 1        | TYPE         | fixed      | 1       | Yes      | Must be "L"                         |
| 2        | YID_0        | char       | 10      | Yes      | Line unique ID (auto-gen if empty)  |
| 3        | YITMREF_0    | char       | 30      | Yes      | Article reference                   |
| 4        | YITMDES_0    | char       | 200     | No       | Article description                 |
| 5        | YARTICLE_0   | char       | 24      | No       | Article display (auto-built)        |
| 6        | YQTY_0       | decimal    | -       | Yes      | Quantity (> 0)                      |
| 7        | YPRICE_0     | decimal    | -       | Yes      | Unit price (>= 0)                   |
| 8        | YCOEFF_0     | decimal    | -       | No       | Coefficient (default: 1)            |
| 9        | YCARTON_0    | integer    | -       | Yes      | Number of cartons (> 0)             |

## E/L Record Linking

The E and L records are linked by **position in the file**:

1. When the import method encounters an **E record**, it stores the header
   fields (YNUM, site, client, etc.) in memory as the "current header context"
2. All subsequent **L records** use this header context until the next E record
3. Each L record + the current header context forms one complete YDEVISMOBILE row

```
Visual representation:

  E;FBC-001;FBC;CLI01;Client A  ──┐  Header context
                                   │
  L;A1;REF01;Art A;;100;25;6;17   ├── Row 1 (header + line A1)
  L;A2;REF02;Art B;;50;15;12;5    ├── Row 2 (header + line A2)
                                   │
  E;FBC-002;FBC;CLI02;Client B  ──┤  New header context
                                   │
  L;B1;REF03;Art C;;200;30;10;20  └── Row 3 (header + line B1)
```

## Execution Flow (Import)

```
  Start
    |
    v
  Open CSV file
    |
    v
  Read next line
    |
    v
  Empty? ───── Yes ──> Skip
    |
    No
    v
  Parse into fields (handle quoted values)
    |
    v
  Record type?
    |
    ├── "E" ──> Validate header fields
    |             |
    |             ├── Invalid ──> Log error, skip
    |             |
    |             └── Valid ──> Store as current header context
    |                            Check YNUM uniqueness
    |                            |
    |                            ├── Duplicate ──> Log error
    |                            └── Unique ──> Continue
    |
    └── "L" ──> Validate header context exists
                  |
                  ├── No context ──> Log error, skip
                  |
                  └── Context exists ──> Validate line fields
                                          |
                                          ├── Invalid ──> Log error
                                          └── Valid ──> Create YDEVISMOBILE row
                                                         |
                                                         ├── Write success ──> Increment OK counter
                                                         └── Write fail ──> Log error
    |
    v
  More lines? ── Yes ──> Repeat read
    |
    No
    v
  Close file, report summary
    |
    v
  End
```

## Validation Rules

| Rule                          | Record | Error Message                              |
|-------------------------------|--------|--------------------------------------------|
| TYPE must be E or L           | Both   | "Unknown record type: X"                   |
| YNUM_0 is required            | E      | "Field 'YNUM' is required"                 |
| YNUM_0 must be unique         | E      | "Duplicate YNUM: X already exists"         |
| YSITE_0 is required           | E      | "Field 'Site' is required"                 |
| YSITE_0 must be FBC or FMD   | E      | "Site must be FBC or FMD"                  |
| YBPCNUM_0 is required         | E      | "Field 'ClientCode' is required"           |
| L without prior E             | L      | "L record without preceding valid E"       |
| YITMREF_0 is required         | L      | "Field 'ArticleRef' is required"           |
| YQTY_0 must be > 0            | L      | "Quantity must be > 0"                     |
| YCARTON_0 must be > 0         | L      | "Cartons must be > 0"                      |
| Minimum field count           | Both   | "insufficient fields (need X, got Y)"      |

## Error Handling

- **Field validation errors**: logged per-record, import continues
  (skipping the invalid record)
- **File errors**: import aborts with descriptive message
- **Database errors**: logged with the database error message
- **Summary**: after import, a message shows total headers, lines,
  created records, and errors

## Test Cases

### Test 1: Valid import with 2 documents
- Input: `SAMPLE.csv`
- Expected: 2 E records, 3 L records, 3 YDEVISMOBILE rows created

### Test 2: Duplicate YNUM
- Input: Two E records with the same YNUM
- Expected: First E record accepted, second rejected with
  "Duplicate YNUM" error

### Test 3: Missing required fields
- Input: E record without YSITE_0
- Expected: E record rejected with "Field 'Site' is required"

### Test 4: L without prior E
- Input: CSV starting with an L record
- Expected: L record rejected with "L record without preceding valid E"

### Test 5: Invalid site
- Input: E record with YSITE_0 = "INVALID"
- Expected: E record rejected with "Site must be FBC or FMD"

### Test 6: Empty file
- Input: Empty CSV
- Expected: No records created, summary shows 0/0/0

### Test 7: Quoted fields with semicolons
- Input: Field value containing ";"
- Expected: Value correctly parsed using CSV quoting rules

### Test 8: Negative quantity
- Input: L record with YQTY_0 = -5
- Expected: L record rejected with "Quantity must be > 0"

## Auto-generated Fields

When certain fields are missing, the import method auto-generates values:

| Missing Field      | Auto-generated Value                    |
|--------------------|-----------------------------------------|
| YID_0              | First 10 chars of a UUID                |
| YNUM_0 (on L)      | `{SITE}-{YID}`                          |
| YCLIENT_0          | `{YBPCNUM} - {YBPCNAM}` (trunc 15)    |
| YARTICLE_0         | `{YITMREF} - {YITMDES}` (trunc 24)    |
| CREUSR_0           | "MOBILE"                                 |
| CREDATTIM_0        | Current date + time                      |
| YCOEFF_0 (if <= 0) | 1                                        |
| YPRICE_0 (if < 0)  | 0                                        |
