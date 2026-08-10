# Source Register

Records the source, download method, checksum, and basic statistics for all raw data files.
This file corresponds to the Step 1 (Freeze Raw Data) deliverable — any cleaning or processing should be traceable back to the raw files registered here.

---

## sensor_location_raw.csv

- Source: City of Melbourne Open Data Portal
- URL: https://data.melbourne.vic.gov.au/explore/dataset/pedestrian-counting-system-sensor-locations/
- Download method: CSV Export (whole dataset)
- Download date: 2026-08-06
- SHA256: f191b2c8901a50750676a1fa99799c7b3cb51ff7e952198dc962871648132de2
- Row count: 135 (including header; 134 actual data rows)
- Notes: Static dataset

---

## pedestrian_hour_count_raw.csv

- Source: City of Melbourne Open Data Portal
- URL: https://data.melbourne.vic.gov.au/explore/dataset/pedestrian-counting-system-monthly-counts-per-hour/
- Download method: CSV Export (whole dataset)
- Download date: 2026-08-06
- SHA256: 44cba737dd3860867b0702f1cd67351997b75718ac6116d64b43489442f17bc1
- Row count: 1,613,801 (including header; 1,613,800 actual data rows)
- Time span: Full history from 2009 to present; a continuous 8-week window is to be extracted in Step 4
- Notes: Static dataset, large in size — be mindful of memory usage / batch reading during processing

---

## minute_count_full_export.csv

- Source: City of Melbourne Open Data Portal (API export)
- URL: https://data.melbourne.vic.gov.au/api/explore/v2.1/catalog/datasets/pedestrian-counting-system-past-hour-counts-per-minute/exports/csv
- Download method: API Export (CSV)
- Download date: 2026-08-06
- SHA256: 56f838f1950d16740338285bfe13d5503af282c81ea615bfa882b3f5f3db87e7
- Row count: 221,544 (including header; 221,543 actual data rows, exactly matching the `total_count` returned by this dataset's API)
- Time span (observed): 2026-08-02T13:55 to 2026-08-06T13:04 (approx. 4 days, UTC)
- Notes: The official documentation states this dataset only retains a "past hour" rolling window, but testing found the platform actually retains approximately 4 days of history. This behavior is undocumented and not guaranteed to be reproducible in future downloads — it only reflects the actual state at the time of this download. The `sensing_datetime` field is in UTC, while `sensing_date`/`sensing_time` are in Melbourne local time — the two differ by 10 hours (AEST winter time, UTC+10). Timezone conversion must be handled carefully during cleaning to avoid date misalignment from inferring the local date directly off the UTC timestamp.

---

## landmark_raw.csv

- Source: City of Melbourne Open Data Portal
- URL: https://data.melbourne.vic.gov.au/explore/dataset/landmarks-and-places-of-interest-including-schools-theatres-health-services-spor/
- Download method: CSV Export (whole dataset)
- Download date: 2026-08-06
- SHA256: 53cdc7ef87ab4c627a49b8f8a6ad081b7613f72bbc5a08e447ba90c6f393a008
- Row count: 243 (including header; 242 actual data rows)
- Notes: Static dataset; to be split into `landmark` and `landmark_category` tables in Step 6

---

## Summary

| File | Status | Row count (excl. header) | SHA256 recorded |
|---|---|---|---|
| sensor_location_raw.csv | ✅ Frozen | 134 | ✅ |
| pedestrian_hour_count_raw.csv | ✅ Frozen | 1,613,800 | ✅ |
| minute_count_full_export.csv | ✅ Frozen | 221,543 | ✅ |
| landmark_raw.csv | ✅ Frozen | 242 | ✅ |

Step 1 (Freeze Raw Data) is complete — all four datasets have been downloaded, checksummed, and recorded.
