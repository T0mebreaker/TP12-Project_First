# Data Quality Report (Final Version)

Generated: 2026-08-07
Scope: All four raw datasets — sensor_location_raw.csv, pedestrian_hour_count_raw.csv, minute_count_full_export.csv, landmark_raw.csv
Status: Step 2 Profiling complete

---

# Part 1: Plain-Language Field Glossary (Quick Reference for Teammates)

## sensor_location_raw.csv — Sensor Location Table

| Field | What it is | What the values mean |
|---|---|---|
| `Location_ID` | ID number for each sensor | A number, e.g. 49, uniquely identifies one sensor |
| `Sensor_Description` | Text description of where the sensor is installed | e.g. "Melbourne Central" |
| `Sensor_Name` | Internal device code | e.g. "Swa295_T", mainly for device maintenance, not for user-facing display |
| `Installation_Date` | When the sensor was installed | Date |
| `Note` | Maintenance notes | Free text, e.g. "device has been replaced", mostly empty |
| `Location_Type` | Whether the sensor is indoor or outdoor | `Outdoor` (street/outdoor) or `Indoor` (library/inside a building) — **these two types have different data capabilities, see important note below** |
| `Status` | Whether the sensor is currently in active use | `A` = Active (in use); all 134 sensors in this project are currently "A" |
| `Direction_1` / `Direction_2` | The names of the two pedestrian-flow directions this sensor tracks | **In this table these are text values**, e.g. "North"/"South", meaning "Direction 1 = people walking north, Direction 2 = people walking south". Note: these same field names also appear in the two pedestrian-count tables below, but with a completely different meaning (see below) |
| `Latitude` / `Longitude` | Sensor coordinates | Numbers, used to plot the point on a map |
| `Location` | Latitude and longitude concatenated into a single string | Duplicates the content of Latitude/Longitude, no extra information — safe to ignore |

## pedestrian_hour_count_raw.csv — Hourly Pedestrian Count Table

| Field | What it is | What the values mean |
|---|---|---|
| `ID` | The official row identifier | **Not reliable — do not use as a unique key**, see the issues list below for why |
| `Location_ID` | Which sensor recorded this row | Corresponds to Location_ID in the sensor_location table |
| `Sensing_Date` | Which day | Date, e.g. 2026-08-05 |
| `HourDay` | Which hour of that day | **A number 0–23** (0 = the 12am–1am hour, 23 = the 11pm–12am hour). Despite the name, this has nothing to do with the day of the week — it simply means "hour of day" |
| `Direction_1` / `Direction_2` | Pedestrian counts in each direction during that hour | **In this table these are numbers**, e.g. 6 and 11, meaning "6 people walked in direction 1, 11 people walked in direction 2" (which direction corresponds to north/south etc. must be looked up in the sensor_location table) |
| `Total_of_Directions` | Total pedestrians for that hour | = Direction_1 + Direction_2 |
| `Sensor_Name` / `Location` | Same as in sensor_location table | Some rows are blank — this is expected, not an error |

## minute_count_full_export.csv — Minute-Level Pedestrian Count Table (finest granularity)

| Field | What it is | What the values mean |
|---|---|---|
| `Location_ID` | Which sensor | Same as above |
| `Sensing_DateTime` | Full timestamp, already timezone-tagged | e.g. `2026-08-05T02:55:00+10:00`, meaning "2:55am on 5 August 2026, Melbourne local time". The `+10:00` marks the Melbourne timezone offset — **this column can be used directly, no manual timezone conversion needed** |
| `Sensing_Date` | The date portion, split out | Same date info, just as its own column |
| `Sensing_Time` | The time portion, split out | e.g. `02:55`, convenient for filtering by time |
| `Direction_1` / `Direction_2` | Pedestrian counts in each direction during that minute | Same logic as the hourly table, numeric values |
| `Total_of_Directions` | Total pedestrians for that minute | = Direction_1 + Direction_2 — verified 100% consistent, no arithmetic errors found |

## landmark_raw.csv — Landmarks / Points of Interest Table

| Field | What it is | What the values mean |
|---|---|---|
| `Theme` | High-level category of the landmark | e.g. "Transport", "Community Use" — 16 categories in total |
| `Sub Theme` | More specific category | e.g. "Library", "Railway Station" — 47 subcategories in total |
| `Feature Name` | Name of the landmark | e.g. "Flagstaff Railway Station" — **the same name can appear multiple times**, representing multiple coordinate points for the same place (e.g. a station with several entrances), not a data error |
| `Co-ordinates` | Coordinates | Latitude and longitude concatenated into a string; needs to be split into two numeric fields before use |

---

# Part 2: Issue List for Each Dataset

## ① sensor_location_raw.csv

- 134 sensors total; primary key `Location_ID` is clean, no duplicates
- **34 sensors are Indoor type**; these 34 have no direction data (Direction_1/2 are blank), because indoor counters are designed to only track total footfall, not direction
- The `Location` column is a redundant concatenation of lat/long — drop during cleaning
- The field names `Direction_1`/`Direction_2` are **text** in this table but **numeric** in the pedestrian-count tables — this distinction must be preserved when designing the database schema, don't use the same data type for both
- The `Note` column records device replacement history; historical data continuity for some points may be affected by hardware swaps

## ② pedestrian_hour_count_raw.csv

- 1.6M+ rows, covering 2009 to present
- **The original `ID` field is flawed and cannot be used as a primary key**: it is generated by concatenating `Location_ID + HourDay + Sensing_Date` as strings without fixed-width zero-padding, so different combinations can produce identical ID strings (e.g. Location_ID=11 + HourDay=0 and Location_ID=1 + HourDay=10 both produce the same ID). 66,343 such collisions were found (~4.1% of records). **The true unique identifier is the combination of `(Location_ID, Sensing_Date, HourDay)`**
- `HourDay` is misleadingly named — it actually just means "hour of the day," unrelated to day-of-week. Recommend renaming to something clearer like `hour` during cleaning
- ~1.6% of records (25,502 rows) are missing `Sensor_Name`/`Location`, but `Location_ID` itself is never missing, so these can be backfilled via a join with the sensor_location table
- Verified: timestamps in this table align exactly with the minute-count table (totals match for the same hour window), so no timezone conversion is needed

## ③ minute_count_full_export.csv

- 220K+ rows, actual observed coverage is approximately 4 days (2026-08-02 to 2026-08-06)
- The official documentation states this dataset "only retains the past hour," but testing showed the platform actually retains a rolling ~4-day window. This is undocumented behavior and there is no guarantee the same window length will be available in future downloads
- `Sensing_DateTime` is already a full, timezone-tagged timestamp and can be used as-is
- Direction_1 + Direction_2 = Total_of_Directions, 100% consistent — no arithmetic mismatches found
- No negative values, no abnormally large values
- **90 records show two different counts recorded for the same sensor at the same timestamp**, concentrated mainly on Location_ID 11 and 35 — this looks like the device itself triggering two independent readings within the same minute (not a simple duplicate download). How to aggregate these (sum, keep one) needs to be decided with the team
- **35 of the 134 sensors have zero minute-level data within this 4-day window**:
  - 34 of them = all of the Indoor-type sensors, because indoor counters are not connected to this minute-level API
  - The remaining 1 (Location_ID=108) is an outdoor sensor, but has a wiring-update note from the same period, suggesting a temporary gap rather than a systemic issue
  - **Implication: any feature showing real-time minute-level pedestrian activity can only use Outdoor-type sensors — indoor points like libraries or visitor centres cannot be used**

## ④ landmark_raw.csv

- 242 rows, no missing values, no fully duplicate rows
- **Does not match the category scheme required by the operations guide**: the guide specifies keeping only Library/Park/Garden/Reserve categories, but the actual data has no separate Park, Garden, or Reserve categories — the platform merges all three into a single Sub Theme: `Informal Outdoor Facility (Park/Garden/Reserve)`. This needs to be discussed with the team — either use this merged category as-is, or find another way to further subdivide it
- `Feature Name` has 9 "duplicate" entries, but all represent legitimate business cases (e.g. a large station with multiple entrance coordinates) rather than data errors — no fix needed, but `Feature Name` alone cannot serve as a unique primary key. When building the database, use either the combination of (Feature Name + Co-ordinates) or generate a new auto-increment ID
- `Co-ordinates` is a concatenated lat/long string and needs to be split into two separate columns for consistency with the other tables
- A few address spelling inconsistencies were found (e.g. variant spellings of "Elizabeth Street") — if address matching is needed downstream, these should be standardized

---

# Part 3: Cross-Table Issues (Core Content for data_dictionary.md)

1. **`Direction_1`/`Direction_2` share the same field name across tables but mean different things**: text (compass direction) in sensor_location, numeric (pedestrian count) in the hourly/minute pedestrian-count tables
2. **Every table has a redundant `Location`/`Co-ordinates` concatenated string column**, duplicating the separate latitude/longitude fields — drop these during cleaning and keep only the independent numeric lat/long columns
3. **Sensors with `Location_Type = Indoor` only have hourly data — no minute-level data and no direction breakdown.** These must be excluded when selecting supported locations for any real-time pedestrian activity feature
4. **Neither the original `ID` (hourly table) nor `Feature Name` (landmark table) can be used directly as a primary key** — both require a composite key or a newly generated auto-increment ID
