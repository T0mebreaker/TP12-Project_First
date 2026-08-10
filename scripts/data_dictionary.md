# Data Dictionary

This document describes the structure of the five cleaned data files in `data/cleaned/`. For the full raw-data processing history and issues found, see `data_quality_report.md`. For the rationale behind the selected supported locations and time window, see `mvp_scope.md`.

---

## 1. sensor_location.csv (6 rows)

Sensor metadata table, containing the 6 supported locations selected for this project.

| Field | Type | Description |
|---|---|---|
| `location_id` | Integer | Unique sensor ID, **primary key** — other tables reference this field to identify a specific location |
| `sensor_description` | Text | Description of where the sensor is located, e.g. "Melbourne Central" |
| `sensor_name` | Text | Internal device code, for reference only — not recommended for user-facing display |
| `installation_date` | Date | Installation date |
| `note` | Text | Maintenance notes, may be empty |
| `location_type` | Text | Always "Outdoor" (all Indoor locations were excluded during filtering) |
| `status` | Text | Always "A" (Active, in use) |
| `direction_1` / `direction_2` | Text | The compass direction each of the two counting directions corresponds to (e.g. "North"/"South"). ⚠️ Note: these are **text values** here, unlike the same-named numeric fields in the two pedestrian-count tables below |
| `latitude` / `longitude` | Numeric | Coordinates |

---

## 2. pedestrian_hour_count.csv (8,050 rows)

Hourly pedestrian count data, covering 6 locations × 2025-09-15 to 2025-11-09 (56 days).

| Field | Type | Description |
|---|---|---|
| `location_id` | Integer | Foreign key referencing sensor_location.location_id |
| `sensing_date` | Date | Date of the record |
| `hour_of_day` | Integer (0–23) | Hour of the day (0 = the 12am–1am hour). Renamed from the original `HourDay` to avoid confusion with "day of week" |
| `direction_1` / `direction_2` | Integer | Pedestrian counts for each direction during that hour. ⚠️ These are **numeric values** here, unlike the same-named text fields in the sensor_location table |
| `total_of_directions` | Integer | = direction_1 + direction_2, total pedestrian count for that hour |
| `sensing_datetime` | Timezone-aware timestamp | Fully constructed local timestamp (Australia/Melbourne, daylight saving automatically handled) — can be used directly, no further timezone conversion needed |

**Known data gaps** (all under 0.4%, consistent with normal sporadic sensor faults, not data errors):
- Princes Bridge (5): 3 missing hours
- Melbourne Central (3): 1 missing hour
- Southern Cross Station (9): 5 missing hours
- QV Market (27): 3 missing hours
- Bourke Street Mall (1): 1 missing hour (2025-10-05, 2am)
- Collins Street (53): 1 missing hour

---

## 3. pedestrian_minute_count.csv (19,516 rows)

Minute-level pedestrian count data, covering 6 locations. ⚠️ **The time range differs from and does not overlap with the hourly table** — the minute-level data source only retains a rolling window of approximately 4 days prior to download time (this download covers approx. 2026-08-02 to 2026-08-06), and cannot be retrieved retroactively for the 2025 Sep–Nov window used in the hourly table. The two tables serve different purposes: the hourly table supports historical pattern analysis, while the minute table supports near-real-time/demo scenarios. **Do not join these two tables directly on timestamp.**

| Field | Type | Description |
|---|---|---|
| `location_id` | Integer | Foreign key referencing sensor_location.location_id |
| `sensing_datetime` | Timezone-aware timestamp | Full timestamp as provided by the source, already timezone-tagged (e.g. +10:00) — can be used directly |
| `sensing_date` | Date | Date portion, split out |
| `sensing_time` | Time | Time portion (HH:MM), split out |
| `direction_1` / `direction_2` | Integer | Pedestrian counts for each direction during that minute |
| `total_of_directions` | Integer | = direction_1 + direction_2 |

**Known limitation**: Princes Bridge (5) and QV Market (27) report at an actual frequency of roughly once every 5 minutes, rather than once per minute — about 5x coarser than the other 4 locations. For any "real-time refresh" feature, these two locations will have noticeably coarser update granularity; consider flagging this in the UI or standardizing display to a 5-minute granularity.

**Issue already handled**: The raw data contained 90 records where the same sensor had two different counts recorded at the exact same timestamp (concentrated at Location_ID 11 and 35, neither of which was selected for this project). During cleaning, the record with the larger `total_of_directions` was kept and the other discarded.

---

## 4. landmark_category.csv (2 rows)

Landmark category lookup table.

| Field | Type | Description |
|---|---|---|
| `category_id` | Integer | Unique category ID, **primary key** |
| `theme` | Text | High-level category, e.g. "Leisure/Recreation", "Place Of Assembly" |
| `sub_theme` | Text | Subcategory, see note below |

⚠️ **Important note**: The project originally called for four landmark categories — "Library / Park / Garden / Reserve" — but the City of Melbourne's official data has no separate Park, Garden, or Reserve categories; the platform merges all three into a single Sub Theme: **"Informal Outdoor Facility (Park/Garden/Reserve)"**. As a result, this table only has 2 records:
1. `category_id=1`: Informal Outdoor Facility (Park/Garden/Reserve) — corresponds to Park/Garden/Reserve
2. `category_id=2`: Library

If the frontend needs to further distinguish "park" vs. "garden" vs. "reserve" in the UI, the current data source cannot support this and an alternative solution needs to be discussed.

---

## 5. landmark.csv (38 rows)

Landmark detail table.

| Field | Type | Description |
|---|---|---|
| `landmark_id` | Integer | **Primary key**, a new auto-increment ID generated during cleaning (the raw data had no reliable unique ID) |
| `feature_name` | Text | Landmark name, e.g. "Federation Square". ⚠️ Note: the same name may correspond to multiple coordinate points in the raw data (e.g. a large venue with several entrances) — this table retains these as needed, it is not a duplication error |
| `category_id` | Integer | Foreign key referencing landmark_category.category_id |
| `latitude` / `longitude` | Numeric | Coordinates, split from the original concatenated string into separate numeric fields |

---

## Table Relationships at a Glance

```
sensor_location (6)  ──┬── location_id ──→ pedestrian_hour_count (8,050)
                        └── location_id ──→ pedestrian_minute_count (19,516)

landmark_category (2) ──── category_id ──→ landmark (38)
```

There is **no direct foreign-key relationship** between the `sensor_location`/`pedestrian_*` table group and the `landmark`/`landmark_category` table group — landmarks are geographic information independent of sensor locations. The two groups are displayed together on a map via coordinates, not linked at the database level.

---

## Key Things to Know Before Use

1. `pedestrian_hour_count` and `pedestrian_minute_count` cover different time ranges — do not join across these two tables on timestamp
2. `direction_1`/`direction_2` mean different things in `sensor_location` (compass text) versus the two pedestrian-count tables (numeric counts)
3. All timestamps have already been timezone-processed (Australia/Melbourne) — use them as-is
4. `landmark_category` only has 2 categories, not the originally planned 4 — UI design should be adjusted accordingly
