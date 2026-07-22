-- =============================================================================
-- Migration: Add YMOBKEY_0 column to YDEVISMOBILE
-- Run this on SQL Server against the MALLZELLIJ schema
-- =============================================================================

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'MALLZELLIJ'
      AND TABLE_NAME = 'YDEVISMOBILE'
      AND COLUMN_NAME = 'YMOBKEY_0'
)
BEGIN
    ALTER TABLE MALLZELLIJ.YDEVISMOBILE ADD YMOBKEY_0 VARCHAR(20) NULL;
END

-- Backfill existing rows with MOB keys (using the numeric part of their position)
UPDATE MALLZELLIJ.YDEVISMOBILE
SET YMOBKEY_0 = 'MOB' + RIGHT('0000000' + CAST(ROWID AS VARCHAR(10)), 7)
WHERE YMOBKEY_0 IS NULL;
