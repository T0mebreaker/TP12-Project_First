# Demo Scenarios

## Data-driven path (preferred evidence)

Open `/home` with no query string.

Use:

- Origin: Princes Bridge (5)
- Destination: Melbourne Central (3)
- Departure: 2025-10-21 17:30

This uses exact cleaned hourly records and should produce a High primary route and Low alternative.

## Deterministic review states

The following are **illustrative overrides** for presentation/regression only and are clearly labelled in the UI:

| Query | Purpose |
|---|---|
| `?scenario=HIGH_LOW` | guaranteed High + Low alternative |
| `?scenario=NO_ALTERNATIVE` | all usable routes High; warning + two actions |
| `?scenario=DATA_UNAVAILABLE` | primary route has no usable pedestrian data |
| `?scenario=PREDICTION_HIGH` | next-hour higher-activity proactive alert |
| `?scenario=PREDICTION_UNAVAILABLE` | insufficient comparable history state |
| `?scenario=OUTSIDE_COVERAGE` | outside-coverage recovery state |

Do not use deterministic scenarios as evidence that the real dataset naturally produces those exact values. Use the data-driven default first, then use scenarios to demonstrate exception branches.
