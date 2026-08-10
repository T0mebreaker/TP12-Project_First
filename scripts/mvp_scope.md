# MVP Scope

Records the core decisions defining the data scope (supported locations, time window) and the rationale behind them, for reference during future development, analysis, or scope expansion. For detailed cleaning issues, see `data_quality_report.md`; for field descriptions of each table, see `data_dictionary.md`.

---

## Step 3: Supported Locations

### Selection criteria
1. `Location_Type` must be `Outdoor` (Indoor-type sensors have no minute-level data and no direction breakdown)
2. Must not be on the "missing minute data" list (of 134 sensors, 35 had no minute-level data within the 4-day capture window — 34 of these are Indoor, and 1 is Outdoor but has a known wiring-related outage)
3. Prioritize well-known Melbourne CBD landmarks with high foot traffic, spread across different areas (stations, markets, shopping streets, malls) to avoid excessive geographic clustering
4. For each landmark, select only one representative sensor (some landmarks have multiple nearby sensors — only the one with the most direct name and lowest ID number is selected)

### Final list (6 locations)

| Landmark | Location_ID | Sensor_Description | Notes |
|---|---|---|---|
| Melbourne Central | 3 | Melbourne Central | Complete data, no known issues |
| Princes Bridge | 5 | Princes Bridge | Complete data; minute-level sampling frequency is approx. once every 5 minutes (other locations report approx. once per minute) |
| Southern Cross Station | 9 | Southern Cross Station | Complete data, no known issues |
| QV Market | 27 | QV Market-Peel St | Complete data; minute-level sampling frequency approx. once every 5 minutes |
| Bourke Street Mall | 1 | Bourke Street Mall (North) | Historically (across the full 730-day range) has 239 days of missing hourly data, scattered across several short periods; within the 56-day window selected in Step 4, only 1 hour is missing |
| Collins Street | 53 | Collins Street (North) | Historically missing 14 days; only 1 hour missing within the Step 4 window |

(Coordinates can be looked up in `sensor_location.csv` by Location_ID and are not repeated here.)

### Known limitations to communicate to frontend/backend
- **Princes Bridge (5), QV Market (27)**: minute-level data reporting frequency is roughly 1/5 that of the other 4 locations. For any "real-time refresh" feature, these two locations will have coarser update granularity — consider noting this in the UI or standardizing display to a 5-minute granularity
- **Bourke Street Mall**: has the lowest historical data completeness of the six (239 of 730 days missing); usage is restricted to the verified clean window. If a longer historical range is needed in future, this location must be re-checked point-by-point

### Exclusions
- Of the 134 sensors, all 34 Indoor-type sensors (libraries, visitor centres, etc.) were excluded, as they have no minute-level data and no direction breakdown
- Location_ID=108 (Outdoor, William St – Little Lonsdale St West) was excluded due to a recent wiring-related data gap

---

## Step 4: Time Window

### Window range
**2025-09-15 to 2025-11-09 (56 consecutive days / 8 weeks)**

### Selection method
1. Systematically searched the full 730-day history for the most recent 56-day window in which all 6 supported locations had at least one record every day, initially identifying 2025-09-16 to 2025-11-10
2. Verified completeness at the hourly level and found that Bourke Street Mall had an 18-hour continuous gap in the afternoon/evening of the window's final day — indicating the window boundary happened to land on a single-day outage for that sensor
3. Shifted the entire window back by 1 day to 2025-09-15 to 2025-11-09, after which the gap was substantially reduced

### Completeness results (verified hourly; 56 days × 24 hours = 1,344 hours = full)

| Location_ID | Landmark | Missing hours | Missing % |
|---|---|---|---|
| 5 | Princes Bridge | 3 | 0.22% |
| 3 | Melbourne Central | 1 | 0.07% |
| 9 | Southern Cross Station | 5 | 0.37% |
| 27 | QV Market | 3 | 0.22% |
| 1 | Bourke Street Mall | 1 | 0.07% |
| 53 | Collins Street | 1 | 0.07% |

All locations have a missing rate below 0.4%, consistent with normal sporadic sensor faults and meeting the "reasonably complete" standard.

### Known gap details
- Princes Bridge (5): 3 missing hours
- Melbourne Central (3): 1 missing hour
- Southern Cross Station (9): 5 missing hours
- QV Market (27): 3 missing hours
- Bourke Street Mall (1): 1 missing hour (2025-10-05, 2am, isolated incident)
- Collins Street (53): 1 missing hour

### Minute-data window note
The minute-level dataset covers only approximately 4 days (this download covers approx. 2026-08-02 to 2026-08-06), used to support near-real-time demo scenarios such as "Latest Activity" and "Stale Data." This is not the same period as, and does not need to align with, the 56-day historical window in this section (used for historical pattern analysis). The two tables should not be directly joined on timestamp — see `data_dictionary.md`.

---

## Step 5–8: Cleaning Results Summary

| Table | Before cleaning | After cleaning | Scope |
|---|---|---|---|
| sensor_location | 134 rows | 6 rows | Only the 6 supported locations retained |
| pedestrian_hour_count | 1,613,800 rows | 8,050 rows | 6 locations × 2025-09-15 to 2025-11-09 |
| pedestrian_minute_count | 221,543 rows | 19,516 rows | 6 locations × observed ~4-day window |
| landmark_category | – | 2 rows | Library / Informal Outdoor Facility (Park/Garden/Reserve) |
| landmark | 242 rows | 38 rows | Only the above two categories retained |

Main issues addressed during cleaning: dropped redundant concatenated lat/long columns; fixed the flawed original ID field design (replaced with a composite key / newly generated auto-increment ID); standardized timezone to Australia/Melbourne (daylight saving handled automatically); removed 90 duplicate-timestamp readings in the minute data; split the raw landmark table into two normalized tables. See `data_quality_report.md` for the full issue list.
