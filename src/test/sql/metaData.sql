select col.TABLE_NAME
, TABLE_TYPE
, COLUMN_NAME
, ORDINAL_POSITION
 , IS_NULLABLE
, DATA_TYPE
, CHARACTER_MAXIMUM_LENGTH
, NUMERIC_SCALE
, COLLATION_NAME
, *
from INFORMATION_SCHEMA.COLUMNS col
inner join INFORMATION_SCHEMA.TABLES tab
on col.TABLE_NAME = tab.TABLE_NAME and col.TABLE_SCHEMA = tab.TABLE_SCHEMA
where TABLE_TYPE <> 'SYSTEM TABLE'
and tab.TABLE_SCHEMA not in ('SYSTEM TABLE', 'PG_CATALOG', 'INFORMATION_SCHEMA', 'pg_catalog', 'information_schema')