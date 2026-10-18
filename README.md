# lmarihugh.github.io
# Lynnea Marihugh | CS 499 Capstone ePortfolio

Live site: https://lmarihugh.github.io 

This repository holds my ePortfolio for CS 499. It contains a professional self-assessment, a code review video, and two enhanced artifacts covering three capstone categories.

## Start here

- **Self-assessment:** https://lmarihugh.github.io/self-assessment.html
- **Code review video:** https://youtu.be/zig0HAYUpBU (also embedded on the site's Code Review page)

## Artifacts

| Artifact | Capstone category | Language |
| Event Tracking App (originally CS 360) | Software Design and Engineering | Java (Android) |
| Event Tracking App (same app as above) | Databases | Java, SQLite |
| Thermostat (originally CS 350) | Algorithms and Data Structures | Python |

### Event Tracking App
An Android app for tracking events, with user login. One app, two enhancements:

Software design and engineering: added an `Event` model, an `EventRepository`, a RecyclerView `EventAdapter`, and an `EventValidator` with input validation, plus unit tests.

Databases: added PBKDF2 password hashing, a foreign key from events to users with cascade delete, an index on username, a version 1 to version 2 migration, and repository tests.

### Thermostat
A Raspberry Pi smart thermostat state machine. The enhancement adds a rolling-window `TemperatureBuffer` to smooth noisy sensor readings, plus error handling around sensor and serial calls. A simulation compared window sizes from 1 to 15 readings, and I chose a window of 7.

## Repository structure

```
.
├── index.html                  Welcome page
├── self-assessment.html        Professional self-assessment
├── code-review.html            Code review video
├── style.css                   Site styles
├── event-tracking-app/
│   ├── index.html              Overview
│   ├── software-design-narrative-final.html / .docx
│   ├── databases-narrative-final.html / .docx
│   ├── original/               Code as submitted in CS 360
│   └── new/               Enhanced code, tests, runnable project zip
│   └── MilestoneTwoNarrative.docx      Original Narrative for Milestone Two
│   └── MilestoneFourNarrative.docx     Original Narrative for Milestone Four
└── thermostat/
│   └── index.html              Overview
│   └── algorithms-narrative-final.html / .docx
│   └── CS499MilestoneThreeNarrative.docx       Original Narrative for Milestone Three
│   └── images/                 screenshot of testing in terminal
│   └── original/               Thermostat.py as submitted in CS 350
│   └── new/               Enhanced Thermostat.py and window_size_simulation.py
```

## Running the code

Event Tracking App: unzip `event-tracking-app/enhanced/runnable-project/CS360EventTrackingAppLynneaM (3).zip`, open the folder in Android Studio, let Gradle sync, and run on an emulator.

Unit tests: `app/src/test` (`EventValidatorTest`, 10 tests)

Instrumented tests: `app/src/androidTest` (`EventRepositoryTest`, 6 tests, uses an in-memory database)

Thermostat: `thermostat/enhanced/Thermostat.py` is written for a Raspberry Pi with the CS 350 hardware. I tested the logic with simulations and Wokwi, not on physical hardware. To reproduce the window-size results, run `python window_size_simulation.py` from `thermostat/enhanced/`.

## Narratives

Each narrative is available as a web page and as a Word document in its artifact folder:
Software Design and Engineering

Databases

Algorithms and Data Structures
