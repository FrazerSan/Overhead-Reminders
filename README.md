# Overhead Reminders

RuneLite reminders for low prayer and expiring boosts, stacked above your character so simultaneous warnings remain visible.

## Features

- Low-prayer warning with a configurable threshold, text, colour, and display duration.
- Independent, vertically stacked messages with optional remaining display seconds.
- Reminders for Overload, Smelling Salts, Divine potions, Saturated Heart, Prayer Regeneration, Prayer Enhance, Thralls, Death Charge, Mark of Darkness, Ward of Arceuus, Stamina, Venom resistance, Antipoison, Antifire, Soulflame Horn, and Surge potions.
- Separate enable, text, and colour settings for each reminder type.
- Duplicate events do not add extra rows or restart an existing message timer.
- Successful thrall, Mark of Darkness, Ward of Arceuus, and Death Charge recasts clear their own reminder. Restoring prayer above the threshold clears Low Prayer and rearms it. Other active messages remain visible.

## Settings

**Overhead Display Time** controls how long messages remain visible (default 3 seconds). Low Prayer has an optional duration override; 0 uses the shared duration. Soulflame Horn uses six seconds and clears when Attack, Strength, or Defence changes.

Low Prayer defaults to enabled at threshold 20. It warns once when prayer reaches or falls below the threshold, including login with low prayer. Prayer must rise above the threshold before another warning can occur. Other reminder types default to disabled.

**Show remaining seconds** displays the time until a message disappears; it does not predict prayer drain or buff expiry. **Height above player** adjusts placement. Messages stack upward, oldest at the bottom. Expiring messages disappear independently.

A blank message suppresses that reminder. `{0}` inserts the reminder name. Messages use plain text and their configured colour. Settings are separate from the original plugins and are not imported automatically.

## Installation

Plugin Hub submission is being prepared; the plugin is not listed yet. Once RuneLite has reviewed and accepted the submission, install **Overhead Reminders** from the Plugin Hub. Until then, use the development client described below.

Disable **Overhead Notifications** and **Prayer Overhead Text** when using this combined plugin to avoid separate duplicate warnings.

## Development and testing

Requires a Java 17 JDK for the local build. Plugin source targets Java 11. From the project directory:

```powershell
.\dev.cmd build
.\dev.cmd run
```

Alternatively, set `JAVA_HOME` to your JDK and run `gradlew.bat build` or `gradlew.bat run`. The Windows helper can also use the portable JDK installed beside this project in `../.tools/jdk17`.

The development client registers the plugin directly. For Jagex Accounts, follow [RuneLite's development-client login guide](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).

There are 24 automated tests covering thresholds, rearming, stacking, independent expiry, duplicate suppression, successful/failed recasts with live-client message formatting, lifecycle cleanup, Death Charge timing, and rendering. The user has also confirmed gameplay behavior, including low-prayer restoration and thrall/Ward recast clearing.

On Windows, the development run task disables Java 2D interface acceleration to avoid the [known sidebar artifacting issue](https://github.com/runelite/runelite/wiki/Disable-Hardware-Acceleration). This leaves the game's GPU/117 HD plugins available.

See [development notes](docs/DEVELOPMENT.md) for detailed local setup and manual checks.

## Credits and licensing

Based on [Shuddie's Overhead Notifications](https://github.com/Shuddie/overhead-notifications), with low-prayer behavior adapted from [Xta-Dev's Prayer Overhead Text](https://github.com/Xta-Dev/prayer-notif).

Original BSD-2-Clause licenses are retained in [LICENSE](LICENSE) and [LICENSE-prayer-notif](LICENSE-prayer-notif), and included in the plugin resources. The Death Charge timer also retains its embedded InfernoStats license notice. The combined version adds stacked rendering, keyed expiry and duplicate suppression, a prayer threshold latch, configurable prayer duration, and clearing after successful recasts.
