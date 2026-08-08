from __future__ import annotations

import csv
from collections import defaultdict
from datetime import datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "backend" / "src" / "main" / "resources" / "data"


def rows(name: str):
    with (DATA / name).open(newline="", encoding="utf-8") as handle:
        yield from csv.DictReader(handle)


def main() -> None:
    sensors = list(rows("sensor_location.csv"))
    hourly = list(rows("pedestrian_hour_count.csv"))
    minute = list(rows("pedestrian_minute_count.csv"))
    landmarks = list(rows("landmark.csv"))

    print(f"Sensors: {len(sensors)}")
    print(f"Hourly rows: {len(hourly)}")
    print(f"Minute rows: {len(minute)}")
    print(f"Landmarks: {len(landmarks)}")
    print(f"Hourly date range: {min(r['sensing_date'] for r in hourly)} -> {max(r['sensing_date'] for r in hourly)}")
    print(f"Minute time range: {min(r['sensing_datetime'] for r in minute)} -> {max(r['sensing_datetime'] for r in minute)}")

    demo = {
        int(r["location_id"]): float(r["total_of_directions"]) / 60.0
        for r in hourly
        if r["sensing_date"] == "2025-10-21" and r["hour_of_day"] == "17" and int(r["location_id"]) in (3, 5)
    }
    print("Default demo exact records:")
    for sensor_id in (5, 3):
        value = demo.get(sensor_id)
        status = "High" if value is not None and value > 60 else "Low" if value is not None else "Data unavailable"
        print(f"  sensor {sensor_id}: {value:.2f}/min -> {status}" if value is not None else f"  sensor {sensor_id}: missing")

    comparable = defaultdict(list)
    for r in hourly:
        if int(r["location_id"]) not in (3, 5) or int(r["hour_of_day"]) != 18:
            continue
        date = datetime.strptime(r["sensing_date"], "%Y-%m-%d")
        if date.weekday() < 5:
            comparable[int(r["location_id"])].append(float(r["total_of_directions"]) / 60.0)
    print("Next-hour weekday 18:00 comparable readings:")
    for sensor_id in (5, 3):
        values = comparable[sensor_id]
        avg = sum(values) / len(values)
        print(f"  sensor {sensor_id}: n={len(values)}, avg={avg:.2f}/min")


if __name__ == "__main__":
    main()
