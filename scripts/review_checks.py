from __future__ import annotations

import csv
import json
import re
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def require(path: str) -> Path:
    p = ROOT / path
    if not p.exists():
        raise AssertionError(f"Missing: {path}")
    return p


def check() -> None:
    package = json.loads(require("frontend/package.json").read_text())
    assert package["dependencies"]["vue"]
    assert package["dependencies"]["axios"]
    assert package["dependencies"]["leaflet"]
    ET.parse(require("backend/pom.xml"))

    required_csv = {
        "sensor_location.csv": {"location_id", "sensor_description", "latitude", "longitude"},
        "pedestrian_hour_count.csv": {"location_id", "sensing_date", "hour_of_day", "total_of_directions"},
        "pedestrian_minute_count.csv": {"location_id", "sensing_datetime", "total_of_directions"},
        "landmark.csv": {"landmark_id", "feature_name", "category_id", "latitude", "longitude"},
    }
    for name, expected in required_csv.items():
        path = require(f"backend/src/main/resources/data/{name}")
        with path.open(newline="", encoding="utf-8") as handle:
            header = set(next(csv.reader(handle)))
        missing = expected - header
        assert not missing, f"{name} missing columns: {sorted(missing)}"

    route_service = require("backend/src/main/java/com/monash/sensoryaware/service/RouteService.java").read_text()
    assert "HIGH_THRESHOLD = 60.0" in route_service
    assert '"Higher pedestrian activity likely"' in route_service
    assert '"Prediction unavailable"' in route_service
    assert '"OUTSIDE_COVERAGE"' in route_service

    routes = require("frontend/src/router/index.ts").read_text()
    for path in ["/", "/login", "/home", "/location/:id", "/location/:id/history", "/location/:id/nearby"]:
        assert path in routes

    home = require("frontend/src/views/HomeView.vue").read_text()
    for phrase in ["High/Low threshold", "Accessible route results", "Review routes", "Data unavailable"]:
        assert phrase in home

    # Basic SFC envelope check (not a replacement for vue-tsc).
    for vue in (ROOT / "frontend/src").rglob("*.vue"):
        text = vue.read_text()
        assert len(re.findall(r"<template(?:\s|>)", text)) == text.count("</template>"), f"Unbalanced template tags: {vue}"
        if "<script setup" in text:
            assert text.count("</script>") == 1, f"Bad script envelope: {vue}"

    print("Static review checks: PASS")
    print("Note: this does not replace npm/vue-tsc or Maven compilation.")


if __name__ == "__main__":
    check()
