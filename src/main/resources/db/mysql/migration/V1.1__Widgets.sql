start transaction;

ALTER TABLE galoshesdb.widgets ADD EXPIRY DATETIME(6) NOT NULL DEFAULT "1234-12-31 23:59:59.000000";
commit;