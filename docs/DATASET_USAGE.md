# Dataset Usage Audit

The uploaded `FIT5120_corrected.zip` contains very large raw exports plus a compact corrected/cleaned dataset. The backend packages only the cleaned files needed by the product, not the ~136 MB raw exports.

## Packaged cleaned files

| File | Rows (excluding header) | Product use |
|---|---:|---|
| `sensor_location.csv` | 6 | Supported Melbourne CBD sensor locations and coordinates |
| `pedestrian_hour_count.csv` | 8,052 | Route classification, peak-hour evidence, predictions, historical trend |
| `pedestrian_minute_count.csv` | 18,589 | Latest available location activity |
| `landmark.csv` | 38 | Nearby public places |
| `landmark_category.csv` | 2 | Source category reference |
| `candidate_sensor_locations.csv` | 14 | Audit evidence for corrected sensor selection; not queried by the runtime API |

## Date ranges observed

- Hourly cleaned data: **2025-09-15 → 2025-11-09**
- Minute cleaned data: **2026-08-02 23:55 +10:00 → 2026-08-06 11:55 +10:00**

Because the two cleaned sources cover different time windows, the product must not present the packaged snapshot as live current state. Location Detail explicitly retains the latest timestamp and marks the snapshot as stale/historical.

## Corrected sensor scope

The corrected sensor file contains:

- 3 — Melbourne Central
- 5 — Princes Bridge
- 9 — Southern Cross Station
- 51 — QVM Franklin St North
- 53 — Collins Street North
- 9001 — Bourke Street Mall combined sensor location

The correction notes in the uploaded package explain that QV Market was changed from sensor 27 to 51 and Bourke Street Mall combines sensors 1 + 2.

## Route classification use

The backend converts hourly `total_of_directions` to pedestrians/minute by dividing by 60.

For the default review example (`2025-10-21 17:30`):

- Sensor 5 / Princes Bridge: 5,511 per hour → **91.85/min → High**
- Sensor 3 / Melbourne Central: 3,065 per hour → **51.08/min → Low**

That gives a data-backed High route plus a Low lower-stimulation alternative without using an illustrative override.

If the exact requested date/hour is not present, the backend uses the historical average for the same sensor, same hour, and same weekday/weekend type. It does not silently substitute an unrelated “latest” hour.

## Prediction use

For the next hour the backend selects comparable hourly records that have:

- same sensor
- same hour of day
- same day type (weekday or weekend)
- at least 4 readings

The predicted value is the average of those hourly counts divided by 60. The same >60 / <=60 threshold is then used for “Higher pedestrian activity likely” / “Lower pedestrian activity likely”.

For the default review example, the next-hour (18:00 weekday) sample has 40 comparable readings for both sensor 5 and sensor 3, so prediction is eligible.

## Minute-count interpretation

`pedestrian_minute_count.csv` is treated as a one-minute count dataset. A sensor can have sparse timestamps (for example, observations approximately every five minutes), but that is a **reporting-completeness/freshness issue**, not evidence that each row is a five-minute aggregate. The Location Detail therefore displays the latest row's `total_of_directions` directly as the latest available pedestrians/minute observation and keeps its timestamp visible.

## Nearby public places

The runtime allows only the approved product categories:

- Library
- Park
- Garden
- Reserve

`landmark.csv` category 2 is mapped to `Library`; category 1 is restricted by place name to Park/Garden/Reserve semantics. The backend returns the nearest three eligible places within the prototype radius and never fills missing results with unrelated categories.

## Raw files intentionally not packaged in the app

The ZIP also contains raw exports including a ~126 MB hourly file and ~10 MB minute file. They are useful for data-cleaning provenance, but packaging them into the web service would slow builds and duplicate the already-cleaned MVP data.
