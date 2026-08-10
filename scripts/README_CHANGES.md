# What's Different in This Package

This is your teammate's full `FIT5120/data/` folder, same structure, with one
change: **Step 3 sensor selection was corrected to compare actual data instead
of picking sensors by name-matching alone.**

## What changed

| Landmark | Original | Corrected | Why |
|---|---|---|---|
| Melbourne Central | Sensor 3 | Sensor 3 (unchanged) | Confirmed correct — denser minute reporting than the alternative |
| Princes Bridge | Sensor 5 | Sensor 5 (unchanged) | Only candidate available |
| Southern Cross Station | Sensor 9 | Sensor 9 (unchanged) | Confirmed correct — only dense-reporting candidate of 4 nearby sensors |
| **QV Market** | Sensor 27 | **Sensor 51** | Sensor 27 reported sparsely (~1/5min); sensor 51 was a candidate that existed but was never compared, and reports densely (~1/min) |
| Collins Street | Sensor 53 | Sensor 53 (unchanged) | Only candidate available |
| **Bourke Street Mall** | Sensor 1 (North only) | **Averaged: sensors 1 + 2** | Both sensors are equally good quality — no data reason to prefer one, so both are now combined |

## Where to look
- **`data/mvp_scope_English.md`** — Step 3 has the full before/after table and reasoning
- **`data/data_dictionary_English.md`** — documents the new `source_sensor_ids` and `n_sensors_present` fields, and the new `candidate_sensor_locations.csv` file
- **`data/raw/data/cleaned/candidate_sensor_locations.csv`** — the actual evidence: every candidate sensor considered per landmark, with the metrics used to decide
- **`data/raw/Step2_Profile.ipynb`** — the executed notebook that produced all of this, with every step's output visible

## What's unchanged
- `data/source_register_English.md` — raw file registration, unaffected by sensor selection
- `data/data_quality_report_English.md` — Step 2 profiling, unaffected
- Landmark cleaning (Steps 8) — landmarks never depended on sensor selection
- The 4 raw source CSVs — untouched, exactly as downloaded
